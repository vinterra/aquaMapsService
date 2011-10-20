package org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis;

import it.cnr.isti.geoserverInteraction.GeoserverCaller;
import it.cnr.isti.geoserverInteraction.bean.BoundsRest;
import it.cnr.isti.geoserverInteraction.bean.FeatureTypeRest;
import it.cnr.isti.geoserverInteraction.bean.GroupRest;
import it.cnr.isti.geoserverInteraction.engine.MakeStyle;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.BadRequestException;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.gis.datamodel.enhanced.WMSContextInfo;
import org.gcube.common.gis.datamodel.types.LayersType;
import org.gcube.common.gis.datamodel.utils.ReadTemplate;

public class GISUtils {

	private static GCUBELog logger= new GCUBELog(GISUtils.class);
	public static char delimiter=',';
	public static boolean hasHeader=false;
	
	private static String cSquareCodeDefinition=HCAF_SFields.csquarecode+" varchar(10)";
	
	private static final String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
	"AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
	"AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";

	private static long DB_WAIT_TIME;
	private static long GEO_SERVER_WAIT_TIME;
	
	static{
		try{
			DB_WAIT_TIME=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_WAIT_FOR_DB_MS);
			GEO_SERVER_WAIT_TIME=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_WAIT_FOR_FT);
		}catch(Exception e){
			logger.fatal("UNABLE TO LOAD GIS CONFGURATION",e);
		}
	}
	
	
	
	public static LayerInfo generateLayer(LayerGenerationRequest request)throws Exception{
		DBSession session=null;
		String layerTable=null;
		String appTableName=null;
		boolean generatedLayer=false;
		try{
			//***** Create Layer table in postGIS
			long start=System.currentTimeMillis();
			logger.debug("Generating layer..");
			session=DBSession.getPostGisDBSession();
			session.disableAutoCommit();
			logger.debug("Importing data..");
			appTableName=GISUtils.importLayerData(request.getCsvFile(), request.getFeatureLabel(),request.getFeatureDefinition(),session);
			logger.debug("Created "+appTableName+" in "+(System.currentTimeMillis()-start));
			layerTable=GISUtils.createLayerTable(appTableName, request.getMapName(), request.getFeatureLabel(), session);
			logger.debug("Created "+layerTable+" in "+(System.currentTimeMillis()-start));
			session.dropTable(appTableName);
			session.commit();
			logger.debug("Committed session");
			//**** Needed wait for data synch 
			//**** POSTGIS - GEOServer limitation
			try {
				logger.debug("Waiting for postgis-geoserver sync..");
				Thread.sleep(DB_WAIT_TIME);
			} catch (InterruptedException e) {}

			//***** update GeoServerBox

			//****** Styles generation

			for(StyleGenerationRequest styleReq : request.getToGenerateStyles())				
				//					String linearStyle=layerTable+"_linearStyle";
				//					if(generateStyle(linearStyle, request.getFeatureLabel(), request.getMinValue(), request.getMaxValue(), request.getNClasses(), request.getColor1(), request.getColor2()));
				if(GISUtils.generateStyle(styleReq))request.getToAssociateStyles().add(styleReq.getNameStyle());
				else logger.warn("Style "+styleReq.getNameStyle()+" was not generated!!");

			//**** Layer Generation

			if(request.getToAssociateStyles().size()==0)
				throw new BadRequestException("No style to associate wtih Layer "+request.getMapName());

			generatedLayer=GISUtils.createLayer(layerTable, request.getMapName(), (ArrayList<String>)request.getToAssociateStyles(), request.getDefaultStyle());
			if(!generatedLayer)	throw new Exception("Unable to generate Layer "+request.getMapName());

			String url = ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL)+"/wms/"+layerTable;
			logger.trace("Layer url : "+url);
			
			logger.debug("GIS GENERATOR request served in "+(System.currentTimeMillis()-start));
			
			return GISUtils.getLayer(request.getMapType(), layerTable, request.getMapName(), "NO DESCRIPTION", request.getToAssociateStyles(), request.getDefaultStyle());
		}catch(Exception e){			
			if(appTableName!=null){
				try{
				session.dropTable(appTableName);
				}catch(Exception e1){logger.warn("Unable to drop table "+appTableName);}
				if(layerTable!=null){
					try{
						session.dropTable(layerTable);
					}catch(Exception e1){logger.warn("Unable to drop table "+appTableName);}
				if(generatedLayer){
					//TODO delete Layer
				}
				}
			}			
			
			
			throw e;}
		finally{if(session!=null)session.close();}
	}
	
	public static WMSContextInfo generateWMSContext(GroupGenerationRequest request)throws Exception{
		logger.trace("Generating group "+request.getToGenerateGroupName());		
		if(request.getGeoLayersAndStyles()==null||request.getGeoLayersAndStyles().size()==0) throw new Exception("Unable to generate group "+request.getToGenerateGroupName()+", No Layer selected");
		GroupRest group=GISUtils.createGroupOnGeoServer(request.getGeoLayersAndStyles().keySet(), request.getGeoLayersAndStyles(), request.getToGenerateGroupName());
		WMSContextInfo wms=ReadTemplate.getWMSContextTemplate();
		wms.getLayers().addAll(request.getPublishedLayersId());
		wms.setName(request.getToGenerateGroupName());
		return wms;
	}
	
	public static void deleteLayer(LayerInfo toDelete)throws Exception{
		logger.trace("Deleting layer "+toDelete.getName());
		if(deleteLayer(toDelete.getName()))
			if(deleteFeatureType(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_WORKSPACE),
					ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_NAME), toDelete.getName()))
				deleteLayerTable(toDelete.getName());
			else throw new Exception("Unable to delete Feature Type "+toDelete.getName());
		else throw new Exception("Unable to delete layer "+toDelete.getName());
	}
	
	public static void deleteWMSContext(WMSContextInfo toDelete)throws Exception{
		logger.trace("DELETING wms context "+toDelete.getName());
		if(!deleteGroup(toDelete.getName()))throw new Exception("Unable to delete group "+toDelete.getName());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//************************** ROUTINES 
	private static GeoserverCaller getCaller()throws Exception{
		return new GeoserverCaller(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_USER),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_PASSWORD));
	}
	
	
	
	/**
	 * imports layer data in postGIS db
	 * 
	 * @param fileName the csv File containing data
	 * @param featureLabel the feature label
	 * @param featureDefinition the feature definition eg : double / int 
	 * @return the generated table
	 */

	private static String importLayerData(final String fileName, String featureLabel,String featureDefinition,DBSession session)throws Exception{		
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(delimiter);
		processor.setHasHeader(hasHeader);
		logger.trace("Reading from file "+fileName); 
		String tableName=ServiceUtils.generateId("app", "");//"app"+(uuidGen.nextUUID()).replaceAll("-", "_");

		session.createTable(tableName, new String[]{
				cSquareCodeDefinition,
				featureLabel+" "+featureDefinition
		});

		List<Field> toInsertFields= new ArrayList<Field>();
		toInsertFields.add(new Field(HCAF_SFields.csquarecode+"","",FieldType.STRING));
		toInsertFields.add(new Field(featureLabel,"",FieldType.STRING));

		final PreparedStatement ps = session.getPreparedStatementForInsert(toInsertFields, tableName);
		Reader reader= new InputStreamReader(new FileInputStream(fileName), Charset.defaultCharset());

		final ArrayList<Integer> count=new ArrayList<Integer>();
		count.add(0);
		long start=System.currentTimeMillis();
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				try{
					ps.setString(1, arg1.get(0));				
					ps.setFloat(2, Float.parseFloat(arg1.get(1)));
					count.set(0, (count.get(0))+ps.executeUpdate());
				}catch(Exception e){
					logger.warn("Unable to complete insertion from file "+fileName, e);
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {}});
		logger.debug("Inserted "+count.get(0)+" in "+(System.currentTimeMillis()-start));
		return tableName;
	}


	/**
	 * Creates a layer table as an inner join of a csquareCode-feature table with the world table 
	 * 
	 * @param appTableName 	the csquareCode-feature table
	 * @param layerName		the layer name used for generating the table id
	 * @param featureLabel 	the feature label
	 * @param session		the session
	 * @return				the generated layer table name
	 * @throws Exception    
	 */

	private static String createLayerTable(String appTableName,String layerName,String featureLabel,DBSession session)throws Exception{

		String featureTable=ServiceUtils.generateId("L"+layerName, "").replaceAll(" ", "").replaceAll("_","").replaceAll("-","").toLowerCase();
		String worldTable=ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_WORLD_TABLE);
		logger.trace("Creating table "+featureTable);
		session.executeUpdate("Create table "+featureTable+" AS (Select "+
				worldTable+".*, app."+featureLabel+
				" FROM "+appTableName+" AS app inner join "+worldTable+
				" ON app."+HCAF_SFields.csquarecode+"="+worldTable+"."+HCAF_SFields.csquarecode+")");
		logger.trace(featureTable+" created");
		logger.trace("going do drop appTable "+appTableName);
		session.dropTable(appTableName);
		return featureTable;
	}


	/**
	 * Generate the style with the given parameters and sends it to GeoServer
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */

	private static boolean generateStyle(StyleGenerationRequest req)throws Exception{
		logger.trace("Generating style "+req.getNameStyle()+" attribute :"+req.getAttributeName()+" min "+req.getMin()+" max "+req.getMax()+" N classes "+req.getNClasses());
		GeoserverCaller caller= getCaller();
		String style;
		if(req.getTypeValue()==Integer.class){
			switch(req.getClusterScaleType()){
			case logarithmic : style=MakeStyle.createStyleLog(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Integer.parseInt(req.getMax()), Integer.parseInt(req.getMin()));
			break;
			default 	: style=MakeStyle.createStyle(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Integer.parseInt(req.getMax()), Integer.parseInt(req.getMin()));
			break;

			}
		}
		else if(req.getTypeValue()==Float.class){
			switch(req.getClusterScaleType()){
			case logarithmic : style=MakeStyle.createStyleLog(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Float.parseFloat(req.getMax()), Float.parseFloat(req.getMin()));
			break;
			default : 	style=MakeStyle.createStyle(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Float.parseFloat(req.getMax()), Float.parseFloat(req.getMin()));
			break;
			}
		}
		else throw new BadRequestException("Invalid type class : "+req.getTypeValue());
		logger.trace("Submitting style "+req.getNameStyle());
		boolean toReturn=false;
		toReturn=caller.sendStyleSDL(style);
		logger.trace("Submitting style result : "+toReturn);
		return toReturn;
	}

	/**
	 * Create featureType and Layer based on an existing layer table 
	 * 
	 * @param featureTable
	 * @param layerName
	 * @param styles
	 * @param defaultStyleIndex
	 * @return
	 * @throws Exception
	 */
	
	 private static boolean createLayer(String featureTable,String layerName, ArrayList<String> styles, int defaultStyleIndex) throws Exception{		
		GeoserverCaller caller= getCaller();
		FeatureTypeRest featureTypeRest=new FeatureTypeRest();
		featureTypeRest.setDatastore(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_NAME));
		featureTypeRest.setEnabled(true);
		featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
		featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
		featureTypeRest.setName(featureTable);
		featureTypeRest.setNativeName(featureTable);
		featureTypeRest.setProjectionPolicy("FORCE_DECLARED");
		featureTypeRest.setSrs("EPSG:4326");
		featureTypeRest.setNativeCRS(crs);
		featureTypeRest.setTitle(featureTable);
		featureTypeRest.setWorkspace(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_WORKSPACE)); 
		logger.debug("Invoking Caller for registering layer : ");
		logger.debug("featureTypeRest.getNativeName : "+featureTypeRest.getNativeName());
		logger.debug("featureTypeRest.getTitle : "+featureTypeRest.getTitle());
		if (caller.addFeatureType(featureTypeRest)){
			logger.debug("Add feature type returned true .. waiting 6 secs..");
			try {
				Thread.sleep(GEO_SERVER_WAIT_TIME);
			} catch (InterruptedException e) {}	
			boolean setLayerValue= caller.setLayer(featureTypeRest, styles.get(defaultStyleIndex), styles);
			logger.debug("Set layer returned "+setLayerValue);
			return setLayerValue;
		}else return false;
	}


	 
	 /**
	  * Creates a wmsContext on geoserver
	  * 
	  * @param layers
	  * @param styles
	  * @param groupName
	  * @return
	  * @throws Exception
	  */
	private static GroupRest createGroupOnGeoServer(Set<String> layers,Map<String,String> styles, String groupName)throws Exception{	 
		logger.trace("Creating group on geo server...");
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_USER),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_PASSWORD));
		logger.trace("Getting template group : "+ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_TEMPLATE_GROUP));
		GroupRest g=caller.getLayerGroup(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_TEMPLATE_GROUP));
		//		g.setBounds(new BoundsRest(-180.0,180.0,-90.0,90.0,"EPSG:4326"));
		//        g.setLayers(list);
		//        g.setStyles(styles);
		logger.trace("Adding layers to template copy...");
		for(String l:layers){
			logger.trace("Added layer "+l);
			g.addLayer(l);
			g.addStyle(l, styles.get(l));
		}
		g.setName(groupName);
		logger.trace("Setted group name "+groupName);
		if(!caller.addLayersGroup(g)) throw new Exception ("GEOSERVER REST CALL RETURNED FALSE FOR GROUP ID : "+groupName);
		
		return g;
	}
	
	
	/**
	 * Forms a LayerInfo object from a template
	 * 
	 * @param type
	 * @param name
	 * @param title
	 * @param abstractDescription
	 * @param styles
	 * @param defaultStyleIndex
	 * @return
	 * @throws Exception
	 */
	
	private static LayerInfo getLayer(LayersType type, String name, String title, String abstractDescription,List<String> styles, int defaultStyleIndex)throws Exception{
		LayerInfo layer=ReadTemplate.getLayerTemplate(type);
		
		layer.setType(type);
		layer.setName(name);
        layer.setTitle(title);
        layer.set_abstract(abstractDescription);
        //GEOSERVER
        layer.setUrl(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL));
        layer.setServerProtocol("OGC:WMS");
        layer.setServerLogin(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_USER));
        layer.setServerPassword(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_PASSWORD));
        layer.setServerType("geoserver");
        layer.setSrs("EPSG:4326");
        
        layer.setOpacity(1.0);
        layer.setStyles(new ArrayList<String>());
        layer.getStyles().addAll(styles);
        layer.setDefaultStyle(styles.get(defaultStyleIndex));
        //TODO Transect Info
		return layer;
	}
	
	/**
	 * deletes Layer from GeoServer
	 * 
	 * @param layerName
	 * @return
	 * @throws Exception
	 */
	private static boolean deleteLayer(String layerName)throws Exception{
		GeoserverCaller caller=getCaller();
		return caller.deleteLayer(layerName);
	}
	
	private static boolean deleteFeatureType(String workspaceName,String dataStore,String featureType)throws Exception{
		GeoserverCaller caller=getCaller();
		return caller.deleteFeatureTypes(workspaceName, dataStore, featureType);
	}
	
	private static boolean deleteLayerTable(String layerTable)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getPostGisDBSession();
			session.dropTable(layerTable);
			return true;
		}catch(Exception e){
			logger.error("Unable to delete Layer table", e);
			return false;
		}finally{
			if(session!=null)session.close();
		}
	}
	
	private static boolean deleteGroup(String groupName)throws Exception{
		GeoserverCaller caller=getCaller();
		return caller.deleteLayersGroup(groupName);
	}
}

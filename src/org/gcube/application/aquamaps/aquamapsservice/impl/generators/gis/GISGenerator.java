package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

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
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.BadRequestException;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.Generator;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.LayerInfoType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.WMSContextInfoType;
import org.json.JSONException;

public class GISGenerator implements Generator{

	private static GCUBELog logger= new GCUBELog(GISGenerator.class);
	public static char delimiter=',';
	public static boolean hasHeader=false;


	private static final String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
	"AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
	"AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";


	private GISGenerationRequest request=null;


	private static String cSquareCodeDefinition=HCAF_SFields.csquarecode+" varchar(10)";


	public GenerationRequest getRequest() {
		return request;
	}

	public boolean getResponse() throws Exception {
		if(request==null) throw new Exception("No request setted");
		else if (request instanceof LayerGenerationRequest) return generateLayer((LayerGenerationRequest)request);
		else if (request instanceof GroupGenerationRequest) return generateGroup((GroupGenerationRequest)request);
		else return generateStyle((StyleGenerationRequest)request);
	}

	public void setRequest(GenerationRequest theRequest)
	throws BadRequestException {
		if(theRequest instanceof GISGenerationRequest)
			request=(GISGenerationRequest) theRequest;
		else throw new BadRequestException();
	}


	//************************* DATA Generation with Publisher synchronization


	private static synchronized boolean generateLayer(LayerGenerationRequest request) throws Exception{
		//***** Check if existing
		LayerInfoType published= PublisherImpl.getPublisher().getExistingLayer(
				request.getSpeciesCoverage(), 
				request.getHcafId(), 
				request.getHspenId(),
				request.getEnvelopeCustomization(), 
				request.getEnvelopeWeights(), request.getSelectedAreas(), request.getBb(),request.getThreshold());

		if(published==null){
			DBSession session=null;
			try{
				//***** Create Layer table in postGIS
				session=DBSession.getPostGisDBSession();
				session.disableAutoCommit();
				String appTableName=importLayerData(request.getCsvFile(), request.getFeatureLabel(),request.getFeatureDefinition(),session);			
				String layerTable=createLayerTable(appTableName, request.getMapName(), request.getFeatureLabel(), session);
				session.dropTable(appTableName);

				//**** Needed wait for data synch 
				//**** POSTGIS - GEOServer limitation
				try {
					Thread.sleep(4*1000);
				} catch (InterruptedException e) {}

				//***** update GeoServerBox

				//****** Styles generation

				ArrayList<String> generatedStyles= new ArrayList<String>();

				for(StyleGenerationRequest styleReq : request.getToGenerateStyles())				
					//					String linearStyle=layerTable+"_linearStyle";
					//					if(generateStyle(linearStyle, request.getFeatureLabel(), request.getMinValue(), request.getMaxValue(), request.getNClasses(), request.getColor1(), request.getColor2()));
					if(generateStyle(styleReq))generatedStyles.add(styleReq.getNameStyle());
					else logger.warn("Style "+styleReq.getNameStyle()+" was not generated!!");

				//**** Layer Generation

				generatedStyles.addAll(request.getToAssociateStyles());
				if(generatedStyles.size()==0)
					throw new BadRequestException("No style to associate wtih Layer "+request.getMapName());

				if(!createLayer(layerTable, request.getMapName(), generatedStyles, 0))
					throw new Exception("Unable to generate Layer "+request.getMapName());

				request.setGeServerLayerId(layerTable);

				//***** create reference in Publisher
				published=PublisherImpl.getPublisher().publishNewLayer(
						request.getSpeciesCoverage(), 
						request.getHcafId(), 
						request.getHspenId(),
						request.getEnvelopeCustomization(), 
						request.getEnvelopeWeights(), request.getSelectedAreas(), request.getBb(),request.getThreshold(),
						request.getMapName(), generatedStyles,0);
				session.commit();
			}catch (Exception e ){
				logger.error("Unable to create Layer ", e);
				throw e;
			}finally {
				session.close();
			}
		}


		//***** insert reference in request
		request.setGeneratedLayer(published);
		return true;
	}


	private static synchronized boolean generateGroup(GroupGenerationRequest request)throws Exception{
		WMSContextInfoType published=PublisherImpl.getPublisher().getExistingWMSContext(request.getPublishedLayer());
		if(published==null){
			if(!createGroupOnGeoServer(request.getGeoServerLayers(), request.getStyles(), request.getToCreateGroupName()))
				throw new Exception("Unable to create group "+request.getToCreateGroupName()+"on GEOServer");
			published=PublisherImpl.getPublisher().publishNewWMSContext(request.getToCreateGroupName(),request.getPublishedLayer());
		}
		request.setAssociatedContext(published);
		return true;
	}


	private static synchronized boolean generateAndPublishStyle(StyleGenerationRequest request)throws Exception{
		throw new Exception("NOT IMPLEMENTED");
	}



	// ***************** Routines

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

		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				try{
					ps.setString(1, arg1.get(0));				
					ps.setFloat(2, Float.parseFloat(arg1.get(1)));
					ps.executeUpdate();
				}catch(Exception e){
					logger.warn("Unable to complete insertion from file "+fileName, e);
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {}});
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

		String featureTable=ServiceUtils.generateId("layerName", "");

		logger.trace("Creating table "+featureTable);
		session.executeUpdate("Create table "+featureTable+" AS (Select "+
				ServiceContext.getContext().getWorldTable()+".*, app."+featureLabel+
				" FROM "+appTableName+" AS app inner join "+ServiceContext.getContext().getWorldTable()+
				" ON app."+HCAF_SFields.csquarecode+"="+ServiceContext.getContext().getWorldTable()+"."+HCAF_SFields.csquarecode+")");
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

	public static boolean generateStyle(StyleGenerationRequest req)throws Exception{
		logger.trace("Generating style "+req.getNameStyle()+" attribute :"+req.getAttributeName()+" min "+req.getMin()+" max "+req.getMax()+" N classes "+req.getNClasses());
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
		String style;
		if(req.getTypeValue()==Integer.class)
			style=MakeStyle.createStyle(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Integer.parseInt(req.getMax()), Integer.parseInt(req.getMin()));
		else if(req.getTypeValue()==Float.class)
			style=MakeStyle.createStyle(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Float.parseFloat(req.getMax()), Float.parseFloat(req.getMin()));
		else throw new BadRequestException();
		logger.trace("Submitting style "+req.getNameStyle());
		boolean toReturn=false;
		toReturn=caller.sendStyleSDL(style);
		logger.trace("Submitting style result : "+toReturn);
		return toReturn;
	}


	private static boolean createLayer(String featureTable,String layerName, ArrayList<String> styles, int defaultStyleIndex) throws JSONException{		
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
		FeatureTypeRest featureTypeRest=new FeatureTypeRest();
		featureTypeRest.setDatastore("aquamapsdb");
		featureTypeRest.setEnabled(true);
		featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
		featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
		featureTypeRest.setName(featureTable);
		featureTypeRest.setNativeName(featureTable);
		featureTypeRest.setProjectionPolicy("FORCE_DECLARED");
		featureTypeRest.setSrs("EPSG:4326");
		featureTypeRest.setNativeCRS(crs);
		featureTypeRest.setTitle(layerName);
		featureTypeRest.setWorkspace("aquamaps");   	
		if (caller.addFeatureType(featureTypeRest)){
			try {
				Thread.sleep(6*1000);
			} catch (InterruptedException e) {}			
			return caller.setLayer(featureTypeRest, styles.get(defaultStyleIndex), styles);			
		}else return false;
	}


	private static boolean createGroupOnGeoServer(Set<String> layers,Map<String,String> styles, String groupName)throws Exception{	 
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());		
		GroupRest g=caller.getLayerGroup(ServiceContext.getContext().getTemplateGroup());
		//		g.setBounds(new BoundsRest(-180.0,180.0,-90.0,90.0,"EPSG:4326"));
		//        g.setLayers(list);
		//        g.setStyles(styles);

		for(String l:layers){
			g.addLayer(l);
			g.addStyle(l, styles.get(l));
		}		
		g.setName(groupName);
		return caller.addLayersGroup(g);
	}

}

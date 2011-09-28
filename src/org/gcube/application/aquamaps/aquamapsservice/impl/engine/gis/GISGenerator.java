package org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.BadRequestException;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.GenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.Generator;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.LayerLockManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.LayerLockManager.Ticket;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;
import org.gcube.common.gis.dataModel.types.LayersType;


public class GISGenerator implements Generator{

	private static GCUBELog logger= new GCUBELog(GISGenerator.class);
	public static char delimiter=',';
	public static boolean hasHeader=false;

	private static Publisher publisher=ServiceContext.getContext().getPublisher();

	private static Integer insertedLineFromCSV=0;


	private static final String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
	"AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
	"AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";


	private GISRequest request=null;

	private static String cSquareCodeDefinition=HCAF_SFields.csquarecode+" varchar(10)";


	public GenerationRequest getRequest() {
		return request;
	}

	public boolean getResponse() throws Exception {
		if(request==null) throw new Exception("No request setted");
		else if (request instanceof LayerGenerationRequest) return generateLayer((LayerGenerationRequest) request);
		else if (request instanceof GroupGenerationRequest) return generateGroup((GroupGenerationRequest) request);
		else if (request instanceof RemovalRequest) return removeGISData((RemovalRequest) request);
		else return generateStyle((StyleGenerationRequest) request);
	}

	public void setRequest(GenerationRequest theRequest)
	throws BadRequestException {
		if((theRequest==null)||(theRequest instanceof GISRequest))
			request=(GISRequest) theRequest;
		else throw new BadRequestException();
	}

	
	
	//************************* DATA Generation with Publisher synchronization

	private boolean removeGISData(RemovalRequest request)throws Exception{
		//TODO IMPLEMENT
		throw new Exception("Not yet Implemented");
	}

	private boolean generateLayer(LayerGenerationRequest request) throws Exception{
		//***** Check if existing
		LayerInfo published= getExisting(request);
		
		if(published==null){
			Ticket ticket=LayerLockManager.isLayerGenerationBooked(request);
			if(ticket.isBooked()){
				published = getExisting(request);
				//***** insert reference in request
				request.setGeneratedLayer(published.getId());
				request.setGeServerLayerId(published.getUrl());
				logger.trace("Found Published layer for request "+request);
				return true;
			}else{				
				logger.trace("Layer not found for request : "+request);
				DBSession session=null;
				String layerTable=null;
				try{
					//***** Create Layer table in postGIS
					long start=System.currentTimeMillis();
					logger.debug("Generating layer..");
					session=DBSession.getPostGisDBSession();
					session.disableAutoCommit();
					logger.debug("Importing data..");
					String appTableName=importLayerData(request.getCsvFile(), request.getFeatureLabel(),request.getFeatureDefinition(),session);
					logger.debug("Created "+appTableName+" in "+(System.currentTimeMillis()-start));
					layerTable=createLayerTable(appTableName, request.getMapName(), request.getFeatureLabel(), session);
					logger.debug("Created "+layerTable+" in "+(System.currentTimeMillis()-start));
					session.dropTable(appTableName);
					session.commit();
					logger.debug("Committed session");
					//**** Needed wait for data synch 
					//**** POSTGIS - GEOServer limitation
					try {
						logger.debug("Waiting for postgis-geoserver sync..");
						Thread.sleep(4*1000);
					} catch (InterruptedException e) {}

					//***** update GeoServerBox

					//****** Styles generation

					for(StyleGenerationRequest styleReq : request.getToGenerateStyles())				
						//					String linearStyle=layerTable+"_linearStyle";
						//					if(generateStyle(linearStyle, request.getFeatureLabel(), request.getMinValue(), request.getMaxValue(), request.getNClasses(), request.getColor1(), request.getColor2()));
						if(generateStyle(styleReq))request.getToAssociateStyles().add(styleReq.getNameStyle());
						else logger.warn("Style "+styleReq.getNameStyle()+" was not generated!!");

					//**** Layer Generation

					if(request.getToAssociateStyles().size()==0)
						throw new BadRequestException("No style to associate wtih Layer "+request.getMapName());

					if(!createLayer(layerTable, request.getMapName(), (ArrayList<String>)request.getToAssociateStyles(), request.getDefaultStyle()))
						throw new Exception("Unable to generate Layer "+request.getMapName());

					String url = ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL)+"/wms/"+layerTable;
					logger.trace("Layer url : "+url);

					request.setGeServerLayerId(layerTable);


					//***** create reference in Publisher
					logger.trace("invoking publisher");
					request.setGeneratedLayer(publishLayer(request));
					LayerLockManager.releaseLocks(ticket);
					logger.debug("GIS GENERATOR request served in "+(System.currentTimeMillis()-start));
					return true;
				}catch (Exception e ){
					logger.error("Unable to create Layer ", e);
					throw e;
				}finally {
					if(session!=null) session.close();
				}
			}
			}else{
				//***** insert reference in request
				request.setGeneratedLayer(published.getId());
				request.setGeServerLayerId(published.getUrl());
				logger.trace("Found Published layer for request "+request);
				return true;
			}
	}


	private static boolean generateGroup(GroupGenerationRequest request)throws Exception{
		logger.trace("Generating group "+request.getToGenerateGroupName());
		Publisher publisher=ServiceContext.getContext().getPublisher();
		List<LayerInfo> layers=new ArrayList<LayerInfo>();
		Map<String,String> layersAndStyles=new HashMap<String, String>();
		logger.trace("loading layer infos..");
		for(Entry<String, ObjectType> layer:request.getLayers().entrySet()){
//			LayerInfo found=null;
//			switch(layer.getValue()){
//			case Biodiversity :	found=publisher.getLayerByIdAndType(layer.getKey(), LayersType.Biodiversity);
//			break;
//			default : found=publisher.getLayerByIdAndType(layer.getKey(), LayersType.NativeRange);
//			}
			LayerInfo found=publisher.getLayerById(layer.getKey());
			if(found!=null){
				layersAndStyles.put(found.getName(),found.getDefaultStyle());
				layers.add(found);
			}else logger.warn("Layer "+layer.getKey()+" , "+layer.getValue()+" not found");
		}
		logger.trace("loaded "+layers.size()+" layers");
		if(layers.size()>0)
			//		request.setWMSContextInfoType(publisher.publishWMSContext(request.getToGenerateGroupName(), layers);
			return createGroupOnGeoServer(layersAndStyles.keySet(), layersAndStyles, request.getToGenerateGroupName());
		else return false;
	}


	private static boolean generateAndPublishStyle(StyleGenerationRequest request)throws Exception{
		//TODO IMPLEMENT
		throw new Exception("NOT IMPLEMENTED");
	}


	private static LayerInfo getExisting(LayerGenerationRequest request)throws Exception{
		LayerInfo toReturn=null;
			switch(request.getMapType()){
			case Biodiversity: {
				PredictionLayerGenerationRequest req=(PredictionLayerGenerationRequest) request;
				toReturn=publisher.getBiodiversityLayer(req.getSpeciesCoverage(), 
						req.getHcaf(), req.getHspen(), req.getSelectedAreas(), req.getEnvelopeCustomization(), req.getEnvelopeWeights(), req.getBb(), req.getThreshold());
				break;
			}
			case Environment:{
				EnvironmentalLayerGenerationRequest req=(EnvironmentalLayerGenerationRequest)request;
				toReturn=publisher.getEnvironmentalLayer(req.getParameter(), req.getHcaf());
				break;
			}
			case PointMap:{
				PointMapGenerationRequest req=(PointMapGenerationRequest) request;
				toReturn=publisher.getPointMapLayer(req.getSpecies().getId());
				break;
			}
			default : {
				PredictionLayerGenerationRequest req=(PredictionLayerGenerationRequest) request;
				toReturn=publisher.getDistributionLayer(req.getSpeciesCoverage().iterator().next(), req.getHcaf(), req.getHspen(), req.getSelectedAreas(), 
						req.getEnvelopeCustomization(), req.getEnvelopeWeights(), req.getBb(), AlgorithmType.valueOf(req.getMapType()+""));
				break;
			}
			}
		return toReturn;
	}

	private static String publishLayer(LayerGenerationRequest request)throws Exception{
		switch(request.getMapType()){
		case PointMap : {
			PointMapGenerationRequest req=(PointMapGenerationRequest) request;
			return publisher.publishPointMapLayer(req.getSpecies().getId(), req.getToAssociateStyles(), req.getDefaultStyle(), req.getMapName(), req.getGeServerLayerId());
		}
		case Environment : {
			EnvironmentalLayerGenerationRequest req=(EnvironmentalLayerGenerationRequest)request;
			return publisher.publishEnvironmentalLayer(req.getHcaf(), req.getParameter(), req.getToAssociateStyles(), req.getDefaultStyle(), req.getMapName(), req.getGeServerLayerId());
		}
		default :{
			PredictionLayerGenerationRequest req= (PredictionLayerGenerationRequest) request;
			return publisher.publishLayer(req.getObjectId(), request.getMapType(), req.getToAssociateStyles(), req.getDefaultStyle(), req.getMapName(), req.getGeServerLayerId());
		}
		}
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

		insertedLineFromCSV=0;
		long start=System.currentTimeMillis();
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				try{
					ps.setString(1, arg1.get(0));				
					ps.setFloat(2, Float.parseFloat(arg1.get(1)));
					insertedLineFromCSV+=ps.executeUpdate();
				}catch(Exception e){
					logger.warn("Unable to complete insertion from file "+fileName, e);
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {}});
		logger.debug("Inserted "+insertedLineFromCSV+" in "+(System.currentTimeMillis()-start));
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

		String featureTable=ServiceUtils.generateId(layerName, "").replaceAll(" ", "").replaceAll("_","").toLowerCase();
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

	public static boolean generateStyle(StyleGenerationRequest req)throws Exception{
		logger.trace("Generating style "+req.getNameStyle()+" attribute :"+req.getAttributeName()+" min "+req.getMin()+" max "+req.getMax()+" N classes "+req.getNClasses());
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_USER),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_PASSWORD));
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
		else throw new BadRequestException();
		logger.trace("Submitting style "+req.getNameStyle());
		boolean toReturn=false;
		toReturn=caller.sendStyleSDL(style);
		logger.trace("Submitting style result : "+toReturn);
		return toReturn;
	}


	private static boolean createLayer(String featureTable,String layerName, ArrayList<String> styles, int defaultStyleIndex) throws Exception{		
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_USER),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_PASSWORD));
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
				Thread.sleep(6*1000);
			} catch (InterruptedException e) {}	
			boolean setLayerValue= caller.setLayer(featureTypeRest, styles.get(defaultStyleIndex), styles);
			logger.debug("Set layer returned "+setLayerValue);
			return setLayerValue;
		}else return false;
	}


	private static boolean createGroupOnGeoServer(Set<String> layers,Map<String,String> styles, String groupName)throws Exception{	 
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
		boolean toReturn=caller.addLayersGroup(g);
		logger.trace("(Caller) Add layersGroup returned : "+toReturn);
		return toReturn;
	}

}

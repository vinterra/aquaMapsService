package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.AquaMapsObjectData;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.BiodiversityObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.DistributionObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.GISUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.GroupGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.StyleGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis.StyleGenerationRequest.ClusterScaleType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HSPECFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.publisher.MetaInformations;
import org.gcube.application.aquamaps.publisher.impl.datageneration.ObjectManager;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.FileType;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.application.aquamaps.publisher.impl.model.WMSContext;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.gis.datamodel.enhanced.WMSContextInfo;
import org.gcube.common.gis.datamodel.types.LayersType;

public abstract class Generator<T> implements ObjectManager<T> {
	private static final GCUBELog logger = new GCUBELog(Generator.class);

	private static final String GENERATION_DATA_TABLE = "generationdata";
	private static final String GENERATION_ID = "id";
	private static final String GENERATION_csv = "csv";
	private static final String GENERATION_max = "max";
	private static final String GENERATION_min = "min";
	private static final String GENERATION_csq = "csq";
	private static final String GENERATION_path = "path";

	// ***********INSTANCE
	protected GenerationRequest request;

	public Generator(GenerationRequest request) {
		this.request = request;
	}

	@Override
	public T generate() throws Exception {
		return null;
	}
	@Override
	public void destroy(T toDestroy) throws Exception {
		// TODO Auto-generated method stub		
	}
	@Override
	public T update() throws Exception {
		return null;
	}

	// **************** T GENERATION

	protected static Layer generateLayer(AquaMapsObjectData data,
			Submitted object) throws Exception {
		List<StyleGenerationRequest> toGenerateStyle=new ArrayList<StyleGenerationRequest>();
		List<String> toAssociateStyleList=new ArrayList<String>();
		if(object.getType().equals(ObjectType.Biodiversity)){
			toGenerateStyle.add(StyleGenerationRequest.getBiodiversityStyle(data.getMin(), data.getMax(), ClusterScaleType.linear, object.getTitle()));
			toGenerateStyle.add(StyleGenerationRequest.getBiodiversityStyle(data.getMin(), data.getMax(), ClusterScaleType.logarithmic, object.getTitle()));
		}else{
			toAssociateStyleList.add(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DEFAULT_DISTRIBUTION_STYLE));
		}


		LayerInfo layerInfo = GISUtils
		.generateLayer(new LayerGenerationRequest(		
				data.getCsvFile(),
				object.getType().equals(ObjectType.Biodiversity) ? AquaMapsManager.maxSpeciesCountInACell: HSPECFields.probability + "",
				object.getType().equals(ObjectType.Biodiversity) ? "integer": "real",						
				object.getTitle(),
				object.getType().equals(ObjectType.Biodiversity) ?LayersType.Biodiversity:LayersType.Prediction,
				toGenerateStyle,
				toAssociateStyleList,
				0));
		return new Layer(layerInfo.getType(), object.getIsCustomized(),
				layerInfo, new CoverageDescriptor(object.getSourceHSPEC() + "",
						object.getSpeciesCoverage()), new MetaInformations(
								object.getAuthor(), "", ""));
	}

	protected static FileSet generateFileSet(AquaMapsObjectData data,
			Submitted object) throws Exception {
		List<org.gcube.application.aquamaps.publisher.impl.model.File> files = new ArrayList<org.gcube.application.aquamaps.publisher.impl.model.File>();
		for (Entry<String, String> toPublishEntry : FileSetUtils.generateFileMap(data.getCsq_str(), object.getTitle()).entrySet()) {
			files.add(new org.gcube.application.aquamaps.publisher.impl.model.File(FileType.Image, toPublishEntry.getValue(), toPublishEntry.getKey()));
		}
		if(files.size()>0){
		logger.debug("Generated FileSet, passed base path "+data.getPath());
		return new FileSet(files, new CoverageDescriptor(
				object.getSourceHSPEC() + "", object.getSpeciesCoverage()),
				data.getPath(),
				new MetaInformations(object.getAuthor(), "", ""));
		}else throw new Exception ("NO IMAGES WERE GENERATED FOR OBJECT "+object.getSearchId());
	}

	protected static WMSContext generateWMSContext(int jobId)throws Exception{
		WMSContext toReturn=null;
		logger.trace("Creating group for "+jobId);
		Submitted job= SubmittedManager.getSubmittedById(jobId);
		ArrayList<String> layerIds=new ArrayList<String>();
		 
		for(Submitted obj:JobManager.getObjects(jobId)){
			if(obj.getGisEnabled()&&obj.getStatus().equals(SubmittedStatus.Completed))
				layerIds.add(obj.getGisPublishedId());
		}
		if(layerIds.isEmpty()){
			logger.trace("No layer found, skipping group generation for job id : "+jobId);			
		}else{
			HashMap<String,String> geoserverLayersAndStyle=new HashMap<String, String>(); 
			
			for(String layerId:layerIds){
				Layer layer=ServiceContext.getContext().getPublisher().getById(Layer.class, layerId);
				geoserverLayersAndStyle.put(layer.getLayerInfo().getName(), layer.getLayerInfo().getDefaultStyle());
			}
			WMSContextInfo wmsContextInfo=GISUtils.generateWMSContext(new GroupGenerationRequest(geoserverLayersAndStyle, ServiceUtils.generateId("WMS_"+job.getTitle(), ""), layerIds));
			toReturn=new WMSContext(wmsContextInfo, layerIds);
		}
		return toReturn;
	}
	
	
	
	// *********************** DATA RETRIEVAL / STORE

	public static void cleanData(Submitted object)throws Exception{
		DBSession session=null;
		try{
			session = DBSession.getInternalDBSession();
			List<Field> filter = new ArrayList<Field>();
			filter.add(new Field(GENERATION_ID,object.getSearchId()+"",FieldType.INTEGER));
			ResultSet rs=session.executeFilteredQuery(filter, GENERATION_DATA_TABLE, GENERATION_ID, OrderDirection.ASC);
			if(rs.next()){
				logger.info("Deleting generation data for "+object.getSearchId());
				ServiceUtils.deleteFile(rs.getString(GENERATION_csq));				
				ServiceUtils.deleteFile(rs.getString(GENERATION_csv));
				for(String path:FileSetUtils.getTempFiles(object.getTitle()))					
					ServiceUtils.deleteFile(path);								
				session.deleteOperation(GENERATION_DATA_TABLE, filter);
			}else logger.info("Unable to detect generation data for submitted "+object.getSearchId());
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	
	protected static AquaMapsObjectData getData(
			AquaMapsObjectExecutionRequest request) throws Exception {
		DBSession session = null;
		try {
			session = DBSession.getInternalDBSession();
			List<Field> filter = new ArrayList<Field>();
			filter.add(new Field(GENERATION_ID, request.getObject()
					.getSearchId() + "", FieldType.INTEGER));
			ResultSet rs = session.executeFilteredQuery(filter,
					GENERATION_DATA_TABLE, GENERATION_ID, OrderDirection.ASC);
			if (rs.next()) {
				return new AquaMapsObjectData(rs.getInt(GENERATION_ID),
						rs.getString(GENERATION_csq),
						rs.getInt(GENERATION_max), rs.getInt(GENERATION_min),
						rs.getString(GENERATION_csv),
						rs.getString(GENERATION_path));
			} else {
				// ************* DATA NOT FOUND, GOING TO GENERATE
				session.close();
				AquaMapsObjectData toStore = null;
				if (request instanceof BiodiversityObjectExecutionRequest) {
					BiodiversityObjectExecutionRequest theRequest = (BiodiversityObjectExecutionRequest) request;
					toStore = getBiodiversityData(theRequest.getObject(),
							theRequest.getSelectedSpecies(),
							JobManager.getWorkingHSPEC(theRequest.getObject()
									.getJobId()), theRequest.getThreshold());
				} else {
					DistributionObjectExecutionRequest theRequest = (DistributionObjectExecutionRequest) request;
					toStore = getDistributionData(theRequest.getObject(),
							theRequest.getSelectedSpecies().iterator().next()
							.getId(),
							JobManager.getWorkingHSPEC(theRequest.getObject()
									.getJobId()));
				}
				return storeData(toStore);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	private static AquaMapsObjectData storeData(AquaMapsObjectData toStore)
	throws Exception {
		DBSession session = null;
		try {
			session = DBSession.getInternalDBSession();
			List<List<Field>> rows = new ArrayList<List<Field>>();
			List<Field> row = new ArrayList<Field>();
			row.add(new Field(GENERATION_ID, toStore.getSubmittedId() + "",FieldType.INTEGER));
			row.add(new Field(GENERATION_csq, toStore.getCsq_str(),FieldType.STRING));
			row.add(new Field(GENERATION_csv, toStore.getCsvFile(),FieldType.STRING));
			row.add(new Field(GENERATION_max, toStore.getMax() + "",FieldType.INTEGER));
			row.add(new Field(GENERATION_min, toStore.getMin() + "",FieldType.INTEGER));
			row.add(new Field(GENERATION_path,toStore.getPath()+"",FieldType.STRING));
			rows.add(row);
			session.insertOperation(GENERATION_DATA_TABLE, rows);
			return toStore;
		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	// ****************** DATA GENERATION

	private static AquaMapsObjectData getBiodiversityData(
			Submitted objectDescriptor, Set<Species> selectedSpecies,
			String hspecTable, float threshold) throws Exception {
		DBSession session = null;
		try {
			logger.debug("DISTRIBUTION DATA FOR "
					+ objectDescriptor.getSearchId() + ".... STARTED");
			session = DBSession.getInternalDBSession();

			String tableName = ServiceUtils.generateId("s", "");
			PreparedStatement prep = null;

			session.createTable(tableName,
					new String[] { SpeciesOccursumFields.speciesid
					+ " varchar(50) PRIMARY KEY" });

			JobManager.addToDropTableList(objectDescriptor.getJobId(),
					tableName);
			List<List<Field>> toInsertSpecies = new ArrayList<List<Field>>();
			for (Species s : selectedSpecies) {
				List<Field> row = new ArrayList<Field>();
				row.add(new Field(SpeciesOccursumFields.speciesid + "", s
						.getId(), FieldType.STRING));
				toInsertSpecies.add(row);
				;
			}
			session.insertOperation(tableName, toInsertSpecies);

			prep = session.preparedStatement(clusteringBiodiversityQuery(
					hspecTable, tableName));
			prep.setFloat(1, threshold);
			ResultSet rs = prep.executeQuery();

			if (rs.first()) {

				String path = SpeciesManager.getCommonTaxonomy(selectedSpecies);

				// ******PERL
				String clusterFile = FileSetUtils.createClusteringFile(
						objectDescriptor.getSearchId(),
						objectDescriptor.getJobId(),
						FileSetUtils.clusterize(rs, 2, 1, 2, true),
						objectDescriptor.getTitle());

				rs.first();
				Integer min = rs.getInt(2);
				rs.last();
				Integer max = rs.getInt(2);

				String csvFile = ServiceContext.getContext()
				.getPersistenceRoot()
				+ File.separator
				+ objectDescriptor.getJobId()
				+ File.separator
				+ objectDescriptor.getTitle() + ".csv";
				FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
				CSVUtils.resultSetToCSVFile(rs, csvFile, false);

				// ******GIS

				return new AquaMapsObjectData(objectDescriptor.getSearchId(),
						clusterFile, min, max, csvFile, path);
			} else
				return null;

		} catch (Exception e) {
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	private static AquaMapsObjectData getDistributionData(Submitted objectDescriptor,String speciesId, String hspecTable)throws Exception{
		DBSession session=null;
		try{
			logger.debug("DISTRIBUTION DATA FOR "+objectDescriptor.getSearchId()+".... STARTED");
			session=DBSession.getInternalDBSession();
			String clusteringQuery=clusteringDistributionQuery(hspecTable);
			PreparedStatement ps= session.preparedStatement(clusteringQuery);
			ps.setString(1,speciesId);
			ResultSet rs=ps.executeQuery();
			if(rs.next()){

				Species s=SpeciesManager.getSpeciesById(true, false, speciesId, 0);
				String path=objectDescriptor.getSourceHSPEC()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.kingdom+"").getValue()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.phylum+"").getValue()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.classcolumn+"").getValue()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.ordercolumn+"").getValue()+File.separator+
				s.getFieldbyName(SpeciesOccursumFields.familycolumn+"").getValue()+File.separator+
				s.getId();
				String clusterFile=FileSetUtils.createClusteringFile(objectDescriptor.getSearchId(),objectDescriptor.getJobId()
						,FileSetUtils.clusterize(rs, 2, 1, 2,true),objectDescriptor.getTitle());

				String csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+
				objectDescriptor.getJobId()+File.separator+objectDescriptor.getTitle()+".csv";
				FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
				CSVUtils.resultSetToCSVFile(rs, csvFile,false);

				logger.debug("DISTRIBUTION DATA FOR "+objectDescriptor.getSearchId()+".... COMPLETED");
				return new AquaMapsObjectData(objectDescriptor.getSearchId(), clusterFile, 0, 0, csvFile,path);
			}else return null;
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}

	// ************************ QUERIES

	public static String clusteringDistributionQuery(String hspecName) {
		String query = "Select " + HCAF_SFields.csquarecode + ", "
		+ HSPECFields.probability + "  FROM " + hspecName + " where "
		+ hspecName + "." + SpeciesOccursumFields.speciesid
		+ "=?  ORDER BY " + HSPECFields.probability + " DESC";
		logger.debug("clusteringDistributionQuery: " + query);
		return query;
	}

	private static String clusteringBiodiversityQuery(String hspecName,
			String tmpTable) {

		String query = "Select " + HCAF_SFields.csquarecode + ", count(k."
		+ SpeciesOccursumFields.speciesid + ") AS "
		+ AquaMapsManager.maxSpeciesCountInACell + " FROM " + hspecName
		+ " as k Where  k." + SpeciesOccursumFields.speciesid
		+ " in (select " + SpeciesOccursumFields.speciesid + " from "
		+ tmpTable + " ) and " + HSPECFields.probability
		+ " > ? GROUP BY " + HCAF_SFields.csquarecode + " order by "
		+ AquaMapsManager.maxSpeciesCountInACell + " DESC";

		logger.debug("clusteringBiodiversityQuery: " + query);
		return query;
	}

}

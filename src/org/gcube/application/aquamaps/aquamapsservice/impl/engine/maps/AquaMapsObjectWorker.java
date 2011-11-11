package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Generator;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.StoreConfiguration;
import org.gcube.application.aquamaps.publisher.StoreConfiguration.StoreMode;
import org.gcube.application.aquamaps.publisher.UpdateConfiguration;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AquaMapsObjectWorker extends Thread {


	private static final GCUBELog logger=new GCUBELog(AquaMapsObjectWorker.class);
	
	
	private AquaMapsObjectExecutionRequest request;

	public AquaMapsObjectWorker(AquaMapsObjectExecutionRequest requestSettings) {		
		request=requestSettings;
	}

	@Override
	public void run() {
		try{
			logger.debug("Started OBJECT "+request.getObject().getSearchId()+"Execution");
			SubmittedManager.setStartTime(request.getObject().getSearchId());
			request.setObject(SubmittedManager.getSubmittedById(request.getObject().getSearchId()));			
			Publisher publisher=ServiceContext.getContext().getPublisher();
			CoverageDescriptor descriptor=new CoverageDescriptor(request.getObject().getSourceHSPEC()+"", request.getObject().getSpeciesCoverage());
			String fileSetID=null;
			String layerID=null;
			Generator<FileSet> fileSetGenerator=new Generator<FileSet>(request) {
				@Override
				public FileSet generate() throws Exception {
					AquaMapsObjectExecutionRequest req=(AquaMapsObjectExecutionRequest) request;
					return generateFileSet(getData(req), req.getObject());
				}				
			};
			Generator<Layer> layerGenrator=new Generator<Layer>(request) {
				@Override
				public Layer generate() throws Exception {
					AquaMapsObjectExecutionRequest req=(AquaMapsObjectExecutionRequest) request;
					return generateLayer(getData(req), req.getObject());
				}
			};
			
			
			if(request.getObject().getIsCustomized()){
				descriptor.setCustomized(true);
					fileSetID=publisher.store(FileSet.class, fileSetGenerator,new StoreConfiguration(StoreMode.UPDATE_EXISTING, new UpdateConfiguration(true, true, true)) ,descriptor).getStoredId().getId();
					if(request.getObject().getGisEnabled())layerID=publisher.store(Layer.class, layerGenrator,new StoreConfiguration(StoreMode.UPDATE_EXISTING, new UpdateConfiguration(true, true, true)) ,descriptor).getStoredId().getId();
			}else {
				fileSetID=publisher.get(FileSet.class, fileSetGenerator, descriptor).get().getId();
				if(request.getObject().getGisEnabled())layerID=publisher.get(Layer.class, layerGenrator, descriptor).get().getId();
			}
			request.getObject().setFileSetId(fileSetID);
			request.getObject().setGisPublishedId(layerID);
			SubmittedManager.update(request.getObject());
			SubmittedManager.updateStatus(request.getObject().getSearchId(), SubmittedStatus.Completed);
		}catch(Exception e){
			logger.error("Failed Object execution "+request.getObject().getSearchId(),e);
			try {
				SubmittedManager.updateStatus(request.getObject().getSearchId(), SubmittedStatus.Error);
			} catch (Exception e1) {
				logger.error("Unexpected Error ",e1);
			}
		}
		finally{
			JobExecutionManager.alertJob(request.getObject().getSearchId(),request.getObject().getJobId());
			JobExecutionManager.cleanReferences(request.getObject());
		}
	}



//	private static void executeBiodiversity(BiodiversityObjectExecutionRequest request)throws Exception{
//		int aquamapsId=request.getObject().getSearchId();
//		int jobId=request.getObject().getJobId();
//		String aquamapsName=request.getObject().getTitle();
//		float threshold=request.getThreshold();
//		Set<Species> selectedSpecies=request.getSelectedSpecies();
//		boolean gisEnabled=request.getObject().getGisEnabled();
//
//
//		Map<String,Map<String,Perturbation>> envelopeCustomization=request.getEnvelopeCustomization();
//		Map<String,Map<EnvelopeFields,Field>> envelopeWeights= request.getEnvelopeWeights();
//		Set<Area> selectedAreas=request.getSelectedArea();
//		BoundingBox bb=request.getBb();
//
//
//			Set<String> species=new HashSet<String>();
//			for(Species s:selectedSpecies){
//				species.add(s.getId());
//			}
//			String fileSetId=request.getObject().getFileSetId();
//			String layerString=request.getObject().getField(SubmittedFields.gispublishedid).getValue();
//
//			//*********************************** GENERATION
//			boolean needStaticImages=(fileSetId==null||fileSetId.equalsIgnoreCase("")||fileSetId.equalsIgnoreCase("null"));
//			boolean needLayers=(layerString==null||layerString.equalsIgnoreCase("")||layerString.equalsIgnoreCase("null"));
//			
//			if(needStaticImages||needLayers){			
//			
//			AquaMapsObjectData toMapData=getBiodiversityData(request.getObject(), selectedSpecies, 
//					JobManager.getWorkingHSPEC(jobId),threshold);
//			if(toMapData==null) logger.trace(aquamapsName+"Empty selection, nothing to render");
//				else {
//					if(needStaticImages){
//						publisher.getFileSetByCoverage(request.getObject(), false);
//						
//						
//						Map<String,String> images=JobUtils.createImages(aquamapsId,toMapData.getCsq_str(),request.getObject().getPostponePublishing());
//						
//						if(request.getObject().getPostponePublishing()){
//							logger.debug("Postponing publishing..");
//							AquaMapsXStream.serialize(request.getObject().getSerializedPath(), images);
//						}else{
//							if(!ServiceContext.getContext().getPublisher().publishImages(aquamapsId, images))
//								throw new Exception("OBJECT ID "+aquamapsId+" WAS UNABLE TO PUBLISH IMG FILES");
//						}
//					}
//					/// *************************** GIS GENERATION
//					logger.trace("Object "+aquamapsName+" ID "+aquamapsId+" GIS is : "+gisEnabled);
//					if(gisEnabled){
//
//						Resource hcaf= new Resource (ResourceType.HCAF,SubmittedManager.getHCAFTableId(jobId));
//						Resource hspen=new Resource (ResourceType.HSPEN,SubmittedManager.getHSPENTableId(jobId));
//						ArrayList<String> layersId=new ArrayList<String>();
//						ArrayList<String> layersUri=new ArrayList<String>();
//
//						PredictionLayerGenerationRequest layerRequest= new PredictionLayerGenerationRequest(aquamapsId,species,hcaf,hspen,envelopeCustomization,
//								envelopeWeights,selectedAreas,bb,threshold,toMapData.getCsvFile(),aquamapsName,toMapData.getMin(),toMapData.getMax());
//						if(GeneratorManager.requestGeneration(layerRequest)){
//							layersId.add(layerRequest.getGeneratedLayer());
//							layersUri.add(layerRequest.getGeServerLayerId());
//						}else {
//							throw new Exception("Gis Generation returned false, request was "+layerRequest);
//						}
//						AquaMapsManager.updateGISReferences(aquamapsId, layersId, layersUri);
//					}
//
//				}	
//			}
//			}
//			
//
//	private static void executeDistribution(DistributionObjectExecutionRequest request)throws Exception{
//		
//		int aquamapsId=request.getObject().getSearchId();
//		String aquamapsName=request.getObject().getTitle();	
//
//		Set<Species> selectedSpecies=request.getSelectedSpecies();
//		int jobId=request.getObject().getJobId();	
//		boolean gisEnabled=request.getObject().getGisEnabled();
//
//
//		Map<String,Perturbation> envelopeCustomization=request.getEnvelopeCustomization();
//		Map<EnvelopeFields,Field> envelopeWeights=request.getEnvelopeWeights();
//		Set<Area> selectedAreas=request.getSelectedArea();
//		BoundingBox bb=request.getBb();
//		
//		
//		logger.trace(aquamapsName+" started");
//		
//
//			//*********************************** GENERATION
//
//				AquaMapsObjectData toMapData=getDistributionData(request.getObject(),
//						request.getSelectedSpecies().iterator().next().getId(),
//						JobManager.getWorkingHSPEC(jobId));
//
//			if(toMapData==null) logger.trace(aquamapsName+"Empty selection, nothing to render");
//			else {
//
//				///************************ PERL IMAGES GENERATION AND PUBBLICATION
//
//				Map<String,String> images=JobUtils.createImages(aquamapsId,toMapData.getCsq_str(),request.getObject().getPostponePublishing());
//				
//				if(request.getObject().getPostponePublishing()){
//					logger.debug("Postponing publishing..");
//					AquaMapsXStream.serialize(request.getObject().getSerializedPath(), images);
//				}else{
//					if(!ServiceContext.getContext().getPublisher().publishImages(aquamapsId, images))
//						throw new Exception("OBJECT ID "+aquamapsId+" WAS UNABLE TO PUBLISH IMG FILES");
//				}
//				
//				
//					
//				
//
//
//				/// *************************** GIS GENERATION
//				logger.trace("Object "+aquamapsName+" ID "+aquamapsId+" GIS is : "+gisEnabled);
//				if(gisEnabled){
//					
//					Resource hcaf= new Resource (ResourceType.HCAF,SubmittedManager.getHCAFTableId(jobId));
//					Resource hspen=new Resource (ResourceType.HSPEN,SubmittedManager.getHSPENTableId(jobId));
//					ArrayList<String> layersId=new ArrayList<String>();
//					ArrayList<String> layersUri=new ArrayList<String>();
//					
//					//TODO check algorithm
//					PredictionLayerGenerationRequest layerRequest= new PredictionLayerGenerationRequest(aquamapsId,aquamapsName, selectedSpecies.iterator().next(), hcaf, hspen, 
//							envelopeCustomization, envelopeWeights, selectedAreas, bb, toMapData.getCsvFile(), request.getAlgorithm());
//					
//
//					if(GeneratorManager.requestGeneration(layerRequest)){
//						layersId.add(layerRequest.getGeneratedLayer());
//						layersUri.add(layerRequest.getGeServerLayerId());
//					}else {
//						throw new Exception("Gis Generation returned false, request was "+layerRequest);
//					}
//					AquaMapsManager.updateGISReferences(aquamapsId, layersId, layersUri);
//				}
//
//			}	
//	}

//************************ UTILS
	
	
	

	
//	private static String generateCsvFile(ResultSet rs,int jobId,String aquamapsName)throws Exception{
//		String csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+jobId+File.separator+aquamapsName+".csv";
//		FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
//		GenerationUtils.ResultSetToCSVFile(rs, csvFile);
//		return csvFile;
//	}
//
//
//	private static ResultSet queryForProbabilities(DBSession session,int jobId,int aquamapsId,String speciesId)throws Exception{
//		String HSPECName=JobManager.getWorkingHSPEC(jobId);
//		session=DBSession.getInternalDBSession();
//
//		String clusteringQuery=clusteringDistributionQuery(HSPECName);
//		logger.trace("Gonna use query "+clusteringQuery);
//		PreparedStatement ps= session.preparedStatement(clusteringQuery);
//		ps.setString(1,speciesId);
//
//
//		return ps.executeQuery();
//	}


	


	
	
	
	
	
	
}

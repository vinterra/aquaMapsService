package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.PredictionLayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HSPECFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
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
			SubmittedManager.setStartTime(request.getObject().getSearchId());
			if(request instanceof BiodiversityObjectExecutionRequest) executeBiodiversity((BiodiversityObjectExecutionRequest) request);
			else executeDistribution((DistributionObjectExecutionRequest) request);
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



	private static void executeBiodiversity(BiodiversityObjectExecutionRequest request)throws Exception{
		int aquamapsId=request.getObject().getSearchId();
		int jobId=request.getObject().getJobId();
		String aquamapsName=request.getObject().getTitle();
		float threshold=request.getThreshold();
		Set<Species> selectedSpecies=request.getSelectedSpecies();
		boolean gisEnabled=request.getObject().getGisEnabled();


		Map<String,Map<String,Perturbation>> envelopeCustomization=request.getEnvelopeCustomization();
		Map<String,Map<EnvelopeFields,Field>> envelopeWeights= request.getEnvelopeWeights();
		Set<Area> selectedAreas=request.getSelectedArea();
		BoundingBox bb=request.getBb();


			Set<String> species=new HashSet<String>();
			for(Species s:selectedSpecies){
				species.add(s.getId());
			}


			//*********************************** GENERATION
			AquaMapsObjectData toMapData=getBiodiversityData(request.getObject(), selectedSpecies, 
					JobManager.getWorkingHSPEC(jobId),threshold);
			

				if(toMapData==null) logger.trace(aquamapsName+"Empty selection, nothing to render");
				else {
					Map<String,String> images=JobUtils.createImages(aquamapsId,toMapData.getCsq_str(),request.getObject().getPostponePublishing());
					
					if(request.getObject().getPostponePublishing()){
						logger.debug("Postponing publishing..");
						AquaMapsXStream.serialize(request.getObject().getSerializedPath(), images);
					}else{
						if(!ServiceContext.getContext().getPublisher().publishImages(aquamapsId, images))
							throw new Exception("OBJECT ID "+aquamapsId+" WAS UNABLE TO PUBLISH IMG FILES");
					}

					/// *************************** GIS GENERATION
					logger.trace("Object "+aquamapsName+" ID "+aquamapsId+" GIS is : "+gisEnabled);
					if(gisEnabled){

						Resource hcaf= new Resource (ResourceType.HCAF,SubmittedManager.getHCAFTableId(jobId));
						Resource hspen=new Resource (ResourceType.HSPEN,SubmittedManager.getHSPENTableId(jobId));
						ArrayList<String> layersId=new ArrayList<String>();
						ArrayList<String> layersUri=new ArrayList<String>();

						PredictionLayerGenerationRequest layerRequest= new PredictionLayerGenerationRequest(aquamapsId,species,hcaf,hspen,envelopeCustomization,
								envelopeWeights,selectedAreas,bb,threshold,toMapData.getCsvFile(),aquamapsName,toMapData.getMin(),toMapData.getMax());
						if(GeneratorManager.requestGeneration(layerRequest)){
							layersId.add(layerRequest.getGeneratedLayer());
							layersUri.add(layerRequest.getGeServerLayerId());
						}else {
							throw new Exception("Gis Generation returned false, request was "+layerRequest);
						}
						AquaMapsManager.updateGISReferences(aquamapsId, layersId, layersUri);
					}

				}	
			}
			

	private static void executeDistribution(DistributionObjectExecutionRequest request)throws Exception{
		
		int aquamapsId=request.getObject().getSearchId();
		String aquamapsName=request.getObject().getTitle();	

		Set<Species> selectedSpecies=request.getSelectedSpecies();
		int jobId=request.getObject().getJobId();	
		boolean gisEnabled=request.getObject().getGisEnabled();


		Map<String,Perturbation> envelopeCustomization=request.getEnvelopeCustomization();
		Map<EnvelopeFields,Field> envelopeWeights=request.getEnvelopeWeights();
		Set<Area> selectedAreas=request.getSelectedArea();
		BoundingBox bb=request.getBb();
		
		
		logger.trace(aquamapsName+" started");
		

			//*********************************** GENERATION

				AquaMapsObjectData toMapData=getDistributionData(request.getObject(),
						request.getSelectedSpecies().iterator().next().getId(),
						JobManager.getWorkingHSPEC(jobId));

			if(toMapData==null) logger.trace(aquamapsName+"Empty selection, nothing to render");
			else {

				///************************ PERL IMAGES GENERATION AND PUBBLICATION

				Map<String,String> images=JobUtils.createImages(aquamapsId,toMapData.getCsq_str(),request.getObject().getPostponePublishing());
				
				if(request.getObject().getPostponePublishing()){
					logger.debug("Postponing publishing..");
					AquaMapsXStream.serialize(request.getObject().getSerializedPath(), images);
				}else{
					if(!ServiceContext.getContext().getPublisher().publishImages(aquamapsId, images))
						throw new Exception("OBJECT ID "+aquamapsId+" WAS UNABLE TO PUBLISH IMG FILES");
				}
				
				
					
				


				/// *************************** GIS GENERATION
				logger.trace("Object "+aquamapsName+" ID "+aquamapsId+" GIS is : "+gisEnabled);
				if(gisEnabled){
					
					Resource hcaf= new Resource (ResourceType.HCAF,SubmittedManager.getHCAFTableId(jobId));
					Resource hspen=new Resource (ResourceType.HSPEN,SubmittedManager.getHSPENTableId(jobId));
					ArrayList<String> layersId=new ArrayList<String>();
					ArrayList<String> layersUri=new ArrayList<String>();
					
					//TODO check algorithm
					PredictionLayerGenerationRequest layerRequest= new PredictionLayerGenerationRequest(aquamapsId,aquamapsName, selectedSpecies.iterator().next(), hcaf, hspen, 
							envelopeCustomization, envelopeWeights, selectedAreas, bb, toMapData.getCsvFile(), request.getAlgorithm());
					

					if(GeneratorManager.requestGeneration(layerRequest)){
						layersId.add(layerRequest.getGeneratedLayer());
						layersUri.add(layerRequest.getGeServerLayerId());
					}else {
						throw new Exception("Gis Generation returned false, request was "+layerRequest);
					}
					AquaMapsManager.updateGISReferences(aquamapsId, layersId, layersUri);
				}

			}	
	}

//************************ UTILS
	
	
	private static String clusteringBiodiversityQuery(String hspecName, String tmpTable){
		
		String query="Select "+HCAF_SFields.csquarecode+", count(k."+SpeciesOccursumFields.speciesid+") AS "+AquaMapsManager.maxSpeciesCountInACell+
		" FROM "+hspecName+" as k Where  k."+SpeciesOccursumFields.speciesid+" in (select "+SpeciesOccursumFields.speciesid+" from "+tmpTable+" ) and "+HSPECFields.probability+
		" > ? GROUP BY "+HCAF_SFields.csquarecode+" order by "+AquaMapsManager.maxSpeciesCountInACell+" DESC";
		
//		
//		String query= "Select "+HCAF_SFields.csquarecode+", count("+hspecName+"."+SpeciesOccursumFields.speciesid+") AS "+AquaMapsManager.maxSpeciesCountInACell+" FROM "+hspecName+
//		" INNER JOIN "+tmpTable+" ON "+hspecName+"."+SpeciesOccursumFields.speciesid+" = "+tmpTable+"."+SpeciesOccursumFields.speciesid+" where probability > ? GROUP BY "+HCAF_SFields.csquarecode+" ORDER BY MaxSpeciesCountInACell DESC";
		logger.trace("clusteringBiodiversityQuery: "+query);
		return query;
	}

	
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


	public static String clusteringDistributionQuery(String hspecName){
		String query= "Select "+HCAF_SFields.csquarecode+", "+HSPECFields.probability+"  FROM "+hspecName+" where "+
		hspecName+"."+SpeciesOccursumFields.speciesid+"=?  ORDER BY "+HSPECFields.probability+" DESC";
		logger.trace("clusteringDistributionQuery: "+query);
		return query;
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
				AquaMapsObjectData data=new AquaMapsObjectData();
				//********** PERL		
				data.setCsq_str(JobUtils.clusterize(rs, 2, 1, 2,false));
				
				//********** GIS
				if(objectDescriptor.getGisEnabled()){
					String csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+
					objectDescriptor.getJobId()+File.separator+objectDescriptor.getTitle()+".csv";
					FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
					CSVUtils.resultSetToCSVFile(rs, csvFile,false);
					data.setCsvFile(csvFile);
				}
				logger.debug("DISTRIBUTION DATA FOR "+objectDescriptor.getSearchId()+".... COMPLETED");
				return data;
			}else return null;
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	private static AquaMapsObjectData getBiodiversityData(Submitted objectDescriptor,Set<Species> selectedSpecies,String hspecTable,float threshold)throws Exception{
		DBSession session=null;
		try{
			logger.debug("DISTRIBUTION DATA FOR "+objectDescriptor.getSearchId()+".... STARTED");
			session=DBSession.getInternalDBSession();
		
			String tableName=ServiceUtils.generateId("s", "");
			PreparedStatement prep=null;

			session.createTable(tableName, new String[]{
					SpeciesOccursumFields.speciesid+" varchar(50) PRIMARY KEY"
			});


			JobManager.addToDropTableList(objectDescriptor.getJobId(), tableName);	
			List<List<Field>> toInsertSpecies= new ArrayList<List<Field>>();
			for(Species s: selectedSpecies){
				List<Field> row=new ArrayList<Field>();
				row.add(new Field(SpeciesOccursumFields.speciesid+"",s.getId(),FieldType.STRING));
				toInsertSpecies.add(row);
				;}
			session.insertOperation(tableName, toInsertSpecies);

			
			prep=session.preparedStatement(clusteringBiodiversityQuery(hspecTable,tableName));				
			prep.setFloat(1,threshold);
			ResultSet rs=prep.executeQuery();
			
			if(rs.first()){
				AquaMapsObjectData data=new AquaMapsObjectData();
				
				//******PERL
				data.setCsq_str(JobUtils.clusterize(rs, 2, 1, 2,true));
				
				rs.first();
				data.setMax(rs.getInt(2));
				rs.last();
				data.setMin(rs.getInt(2));
				
				//******GIS
				
				if(objectDescriptor.getGisEnabled()){
					String csvFile=ServiceContext.getContext().getPersistenceRoot()+
						File.separator+objectDescriptor.getJobId()+File.separator+objectDescriptor.getTitle()+".csv";
					FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
					CSVUtils.resultSetToCSVFile(rs, csvFile,false);
					data.setCsvFile(csvFile);
				}
				return data;
			}else return null;
			
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	
	
}

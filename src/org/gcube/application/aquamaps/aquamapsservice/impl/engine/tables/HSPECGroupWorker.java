package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.CommonServiceLogic;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.EnvironmentalLogicManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.TableGenerationConfiguration;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SourceGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.dataModel.fields.SourceGenerationRequestFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class HSPECGroupWorker extends Thread {

	private SourceGenerationRequest request;
	private static final GCUBELog logger=new GCUBELog(HSPECGroupWorker.class);

	public HSPECGroupWorker(SourceGenerationRequest request) {
		this.request=request;
	}

	@Override
	public void run() {
		BatchGeneratorI batch = null;
		try{
			logger.trace("Starting execution for request ID "+request.getId());
			logger.debug("Request is "+request.toXML());
			SourceGenerationRequestsManager.setStartTime(request.getId());
			HashMap<ResourceType,Resource> sources=new HashMap<ResourceType, Resource>();

			sources.put(ResourceType.HCAF, SourceManager.getById(request.getHcafId()));
			if(sources.get(ResourceType.HCAF)==null) throw new Exception ("Unable to find hcaf with id "+request.getHcafId());
			sources.put(ResourceType.HSPEN, SourceManager.getById(request.getHspenId()));
			if(sources.get(ResourceType.HSPEN)==null) throw new Exception ("Unable to find hspen with id "+request.getHspenId());
			sources.put(ResourceType.OCCURRENCECELLS, SourceManager.getById(request.getOccurrenceCellId()));
			if(sources.get(ResourceType.OCCURRENCECELLS)==null) throw new Exception ("Unable to find occurrence cells  with id "+request.getOccurrenceCellId());


			batch=EnvironmentalLogicManager.getBatch();
			logger.debug("Got batch Id "+batch.getReportId());

			batch.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
					DBSession.getInternalCredentials());

			SourceGenerationRequestsManager.setReportId(batch.getReportId(),request.getId());
			ArrayList<Resource> generatedSources=new ArrayList<Resource>(); 
			SourceGenerationRequestsManager.setPhasePercent(0d, request.getId());
			for(String algorithmString:request.getAlgorithms()){
				try{
					AlgorithmType algorithmType=AlgorithmType.valueOf(algorithmString);

					Resource existing=getExisting(algorithmType, request.getHspenId(),request.getHcafId(),request.getOccurrenceCellId(),request.getId(),request.getLogic());

					if(existing==null){
						long startTime=System.currentTimeMillis();
						logger.trace("No Resources found, submitting generation..");	
						String generatedTable=batch.generateTable(
								new TableGenerationConfiguration(
										request.getLogic(), 
										AlgorithmType.valueOf(algorithmString), 
										sources, 
										request.getSubmissionBackend(), 
										request.getExecutionEnvironment(), 
										request.getBackendURL(), 
										request.getEnvironmentConfiguration(),
										request.getNumPartitions(), 
										request.getAuthor()));
						logger.trace("Generated Table "+generatedTable+" in "+(startTime-System.currentTimeMillis()));
						Resource toRegister=new Resource(request.getLogic().equals(LogicType.HSPEC)?ResourceType.HSPEC:ResourceType.HSPEN,0);
						toRegister.setAlgorithm(algorithmType);
						toRegister.setAuthor(request.getAuthor());
						toRegister.setGenerationTime(System.currentTimeMillis());
						toRegister.setDescription(request.getDescription());
						toRegister.setProvenance("Generated on AquaMaps VRE, submitted on "+request.getExecutionEnvironment());
						toRegister.setSourceHCAFId(sources.get(ResourceType.HCAF).getSearchId());
						toRegister.setSourceHCAFTable(sources.get(ResourceType.HCAF).getTableName());
						toRegister.setSourceHSPENId(sources.get(ResourceType.HSPEN).getSearchId());
						toRegister.setSourceHSPENTable(sources.get(ResourceType.HSPEN).getTableName());
						toRegister.setSourceOccurrenceCellsId(sources.get(ResourceType.OCCURRENCECELLS).getSearchId());
						toRegister.setSourceOccurrenceCellsTable(sources.get(ResourceType.OCCURRENCECELLS).getTableName());
						toRegister.setStatus("Completed");
						toRegister.setTableName(generatedTable);
						toRegister.setTitle(request.getGenerationname()+"_"+algorithmType);
						toRegister=SourceManager.registerSource(toRegister);
						logger.trace("Registered Resource with id "+toRegister.getSearchId());
						SourceGenerationRequestsManager.addGeneratedResource(toRegister.getSearchId(), request.getId());
						generatedSources.add(toRegister);
						TableGenerationExecutionManager.notifyGeneration(algorithmType,request.getLogic(),sources.get(ResourceType.HSPEN).getSearchId(),
								sources.get(ResourceType.HCAF).getSearchId(),sources.get(ResourceType.OCCURRENCECELLS).getSearchId());
					}else{
						logger.trace("Found Resource "+existing.toXML());
						generatedSources.add(existing);
					}
				}catch(Exception e){
					logger.error("Unable to generate data for  algorithm "+algorithmString,e);
				}
			}
			EnvironmentalLogicManager.leaveBatch(batch);
			logger.trace("Generated "+generatedSources.size()+" hspec table(s)");


			if(request.getLogic().equals(LogicType.HSPEC))
				if(request.getEnableimagegeneration()){
					logger.trace("Generating jobs for request "+request.getId());
					ArrayList<Integer> jobIds=new ArrayList<Integer>();
					SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.mapgeneration,request.getId());
					for(Resource hspec:generatedSources){
						logger.trace("Requesting job for hspec "+hspec.getTitle()+", ID :"+hspec.getSearchId());
						int jobID=CommonServiceLogic.generateMaps_Logic(hspec.getSearchId(), new ArrayList<Field>(), request.getAuthor(), request.getEnablelayergeneration());
						logger.trace("Job is "+jobID);
						SourceGenerationRequestsManager.addJobIds(jobID, request.getId());
						jobIds.add(jobID);
					}

					if(jobIds.size()>0){
						logger.trace("Generation "+request.getId()+" : submitted "+jobIds.size()+" job, waiting for them to complete..");
						Boolean completed=false;
						while(!completed){
							for(Integer id:jobIds){
								Submitted submittedJob=SubmittedManager.getSubmittedById(id);
								if(!submittedJob.getStatus().equals(SubmittedStatus.Completed)&&!submittedJob.getStatus().equals(SubmittedStatus.Error)){
									completed=false;
									break;
								}else completed=true;
							}
							if(!completed)
								try{
									Thread.sleep(10*1000);
								}catch(InterruptedException e){}
						}
					}
				}
			logger.trace("Generation "+request.getId()+" : All jobs complete!");

			SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.completed,request.getId());
		}catch(Exception e){
			logger.error("Unexpected Exception while executing request "+request.getId(), e);
			try{
				SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.error,request.getId());
			}catch(Exception e1){
				logger.error("Unable to update phase , request was "+request,e);
			}
		}finally{
			try{
				if(batch!=null) EnvironmentalLogicManager.leaveBatch(batch);
			}catch(Exception e){
				logger.error("Unable to leave generator",e);
			}
		}
	}



	private static Resource getExisting(AlgorithmType algorithm, Integer hspenId, Integer hcafId,Integer occurrenceCellsId, String currentRequestId, LogicType logic)throws Exception{
		logger.trace("Request "+currentRequestId+" looking for existing generated Table [Algorith : "+algorithm+" ; HSPEN ID : "+hspenId+" ; HCAF ID : "+hcafId+"]");
		ArrayList<Field> requestFilter= new ArrayList<Field>();
		requestFilter.add(new Field(SourceGenerationRequestFields.sourcehspenid+"",hspenId+"",FieldType.INTEGER));
		requestFilter.add(new Field(SourceGenerationRequestFields.sourcehcafid+"",hcafId+"",FieldType.INTEGER));
		requestFilter.add(new Field(SourceGenerationRequestFields.sourceoccurrencecellsid+"",occurrenceCellsId+"",FieldType.INTEGER));
		requestFilter.add(new Field(SourceGenerationRequestFields.logic+"",logic+"",FieldType.STRING));
		for(SourceGenerationRequest request: SourceGenerationRequestsManager.getList(requestFilter)){
			if(!request.getId().equals(currentRequestId)&&request.getAlgorithms().contains(algorithm)){
				if(request.getPhase().equals(SourceGenerationPhase.pending)||request.getPhase().equals(SourceGenerationPhase.datageneration)){
					logger.trace("Found existing request [PHASE : "+request.getPhase()+" ; ID : "+request.getId()+"], waiting for generation");
					TableGenerationExecutionManager.signForGeneration(algorithm,logic,hspenId,hcafId,occurrenceCellsId);
				}
			}
		}
		ArrayList<Field> resourceFilter= new ArrayList<Field>();
		resourceFilter.add(new Field(MetaSourceFields.algorithm+"",algorithm+"",FieldType.STRING));
		resourceFilter.add(new Field(MetaSourceFields.sourcehspen+"",hspenId+"",FieldType.INTEGER));
		resourceFilter.add(new Field(MetaSourceFields.sourcehcaf+"",hcafId+"",FieldType.INTEGER));
		resourceFilter.add(new Field(MetaSourceFields.sourceoccurrencecells+"",occurrenceCellsId+"",FieldType.INTEGER));
		Set<Resource> existing=SourceManager.getList(resourceFilter);
		if(existing.isEmpty()) return null;
		else return existing.iterator().next();	
	}

}

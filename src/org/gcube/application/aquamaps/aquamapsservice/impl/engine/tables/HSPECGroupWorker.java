package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.common.core.utils.logging.GCUBELog;

import edu.emory.mathcs.backport.java.util.Arrays;

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
			
			//****************** CHECKING SOURCES

			List<Resource> sources=getSelectedSources(request);
			
			//********** RETRIEVE BATCH GENERATION
			
			batch=EnvironmentalLogicManager.getBatch();
			logger.debug("Got batch Id "+batch.getReportId());

			batch.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
					DBSession.getInternalCredentials());

			//********** START PROCESS INIT
			SourceGenerationRequestsManager.setReportId(batch.getReportId(),request.getId());
			ArrayList<Resource> generatedSources=new ArrayList<Resource>(); 
			SourceGenerationRequestsManager.setPhasePercent(0d, request.getId());
			
			for(AlgorithmType algorithmType:request.getAlgorithms()){
				//***************** FOR EVERY ALGORITHM
				try{
					
					//************* CHECK EXISTING
					Set<Resource> existing=getExisting(algorithmType, sources,request.getId(),request.getLogic(),request.getParameters());

					if(existing.size()==0){
						long startTime=System.currentTimeMillis();
						logger.trace("No Resources found, submitting generation..");	
						//*********** Submitting generation for one algorithm
						//TODO Change to generated multiple tables (List?) 
						List<String> generatedTables=batch.generateTable(
								new TableGenerationConfiguration(
										request.getLogic(), 
										algorithmType, 
										sources,
										request.getSubmissionBackend(), 
										request.getExecutionEnvironment(), 
										request.getBackendURL(), 
										request.getEnvironmentConfiguration(),
										request.getNumPartitions(), 
										request.getAuthor(),request.getParameters()));
						logger.trace("Generated Tables "+Arrays.toString(generatedTables.toArray())+" in "+(startTime-System.currentTimeMillis()));

						//********REgistering source
						//TODO consider multiple generated sources
						ArrayList<String> unregisteredTables=new ArrayList<String>(); 
						for(int i=0;i<generatedTables.size();i++){
							String generatedTable=generatedTables.get(i);
							try{
							Resource toRegister=new Resource(ResourceType.valueOf(request.getLogic()+""),0);
							toRegister.setAlgorithm(algorithmType);
							toRegister.setAuthor(request.getAuthor());
							toRegister.setGenerationTime(System.currentTimeMillis());
							toRegister.setDescription(request.getDescription());
							toRegister.setProvenance("Generated on AquaMaps VRE, submitted on "+request.getExecutionEnvironment());

							for(Resource r:sources)
								toRegister.addSource(r);

							toRegister.setStatus(ResourceStatus.Completed);
							toRegister.setTableName(generatedTable);
							String title=(generatedTables.size()>1)?request.getGenerationname()+"_"+algorithmType+"_step"+i:request.getGenerationname()+"_"+algorithmType;
							toRegister.setTitle(title);

							//************* Checking row count
							Long count=0l;
							DBSession session=null;
							try{
								session=DBSession.getInternalDBSession();
								count=session.getTableCount(generatedTable);
							}catch(Exception e){
								logger.warn("Unable to evaluate generated table "+generatedTable+" row count",e);
							}finally{if (session!=null) session.close();}
							toRegister.setRowCount(count);
							toRegister=SourceManager.registerSource(toRegister);
							logger.trace("Registered Resource with id "+toRegister.getSearchId());


							SourceGenerationRequestsManager.addGeneratedResource(toRegister.getSearchId(), request.getId());
							generatedSources.add(toRegister);
							}catch(Exception e){
								//Registration failure, need to delete table
								unregisteredTables.add(generatedTable);								
								logger.error("Unable to register source table "+generatedTable,e);
								DBSession session=null;
								try{
									session=DBSession.getInternalDBSession();
									session.dropTable(generatedTable);
								}catch(Exception e1){
									logger.warn("Unable to delete table "+generatedTable,e);
								}finally{if (session!=null) session.close();}
							}
						}
						//*************** REMOVING INVALID TABLES
						if(unregisteredTables.size()>0)logger.debug("Service was unable to register : "+Arrays.toString(unregisteredTables.toArray()));
						generatedTables.removeAll(unregisteredTables);
						
						
						int[] sourcesIds=new int[sources.size()];
						for(int i=0;i<sourcesIds.length;i++)sourcesIds[i]=sources.get(i).getSearchId();
						
						TableGenerationExecutionManager.notifyGeneration(new Execution(algorithmType, request.getLogic(), sourcesIds,request.getField(SourceGenerationRequestFields.additionalparameters).getValue()));
					}else{
						logger.trace("Found "+existing.size()+" existing sources ");
						for(Resource r:existing)generatedSources.add(r);
					}
				}catch(Exception e){
					logger.error("Unable to generate data for  algorithm "+algorithmType,e);
				}
			}
			EnvironmentalLogicManager.leaveBatch(batch);
			logger.trace("Generated "+generatedSources.size()+" resources");


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


	private List<Resource> getSelectedSources(SourceGenerationRequest request) throws Exception{
		List<Resource> toReturn=new ArrayList<Resource>();
		switch(request.getLogic()){		
			case HSPEC : {
				Resource hcaf=SourceManager.getById(request.getHcafIds().get(0));
				if(hcaf==null) throw new Exception("HCAF not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(hcaf);
				Resource hspen=SourceManager.getById(request.getHspenIds().get(0));
				if(hspen==null) throw new Exception("HSPEN not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(hspen);
				break;
				}
			case HSPEN : { 
				Resource hcaf=SourceManager.getById(request.getHcafIds().get(0));
				if(hcaf==null) throw new Exception("HCAF not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(hcaf);
				Resource hspen=SourceManager.getById(request.getHspenIds().get(0));
				if(hspen==null) throw new Exception("HSPEN not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(hspen);
				Resource occurrence=SourceManager.getById(request.getOccurrenceCellIds().get(0));
				if(occurrence==null) throw new Exception("HSPEN not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(occurrence);
				break;
			}			 
			case HCAF  : {
				Resource hcaf=SourceManager.getById(request.getHcafIds().get(0));
				if(hcaf==null) throw new Exception("HCAF not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(hcaf);
				Resource hcaf2=SourceManager.getById(request.getHcafIds().get(1));
				if(hcaf2==null) throw new Exception("HCAF not found for request "+request.getId()+", logic was "+request.getLogic());
				toReturn.add(hcaf2);
				break;
			}
		}
		return toReturn;
	}

	//TODO change to a more generic logic (NB multiple sources) and multiple Resources 
	private static Set<Resource> getExisting(AlgorithmType algorithm, List<Resource> sources, String currentRequestId, LogicType logic, ArrayList<Field> parameters)throws Exception{
//		logger.trace("Request "+currentRequestId+" looking for existing generated Table [Algorith : "+algorithm+" ; HSPEN ID : "+hspenId+" ; HCAF ID : "+hcafId+"]");
		ArrayList<Field> requestFilter= new ArrayList<Field>();
		
		SourceGenerationRequest requestFilterModel=new SourceGenerationRequest();
		requestFilterModel.getAlgorithms().add(algorithm);
		for(Resource r: sources)requestFilterModel.addSource(r);
		requestFilterModel.setLogic(logic);
		requestFilterModel.setParameters(parameters);
		
		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.sourcehcafids));
		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.sourcehspenids));
		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.sourceoccurrencecellsids));
		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.logic));
		requestFilter.add(requestFilterModel.getField(SourceGenerationRequestFields.additionalparameters));
		
		
		for(SourceGenerationRequest request: SourceGenerationRequestsManager.getList(requestFilter)){
			if(!request.getId().equals(currentRequestId)&&request.getAlgorithms().contains(algorithm)){
				if(request.getPhase().equals(SourceGenerationPhase.pending)||request.getPhase().equals(SourceGenerationPhase.datageneration)){
					logger.trace("Found existing request [PHASE : "+request.getPhase()+" ; ID : "+request.getId()+"], waiting for generation");
					int[] sourcesIds=new int[sources.size()];
					for(int i=0;i<sourcesIds.length;i++)sourcesIds[i]=sources.get(i).getSearchId();
					TableGenerationExecutionManager.signForGeneration(new Execution(algorithm, logic, sourcesIds,requestFilterModel.getField(SourceGenerationRequestFields.additionalparameters).getValue()));
				}
			}
		}
		
		Resource filterModel=new Resource(ResourceType.valueOf(logic+""),0);
		filterModel.setAlgorithm(algorithm);
		filterModel.setParameters(parameters);
		for(Resource r: sources)filterModel.addSource(r);
		
		ArrayList<Field> resourceFilter= new ArrayList<Field>();
		resourceFilter.add(filterModel.getField(MetaSourceFields.algorithm));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourcehcafids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourcehspecids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourcehspenids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.sourceoccurrencecellsids));
		resourceFilter.add(filterModel.getField(MetaSourceFields.parameters));
		return SourceManager.getList(resourceFilter);
			
	}

}

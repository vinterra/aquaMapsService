package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.common.core.utils.logging.GCUBELog;

public class EnvironmentalStatusUpdateThread extends Thread {


	private static final GCUBELog logger=new GCUBELog(EnvironmentalStatusUpdateThread.class);


	private long millis;


	public EnvironmentalStatusUpdateThread(long millis) {
		super("Environmental status updater");
		this.millis=millis;
	}

	@Override
	public void run() {
		while(true){
			try{

				//Updating data generation percent...
				ArrayList<Field> filter=new ArrayList<Field>();
				filter.add(new Field(SourceGenerationRequestFields.phase+"",SourceGenerationPhase.datageneration+"",FieldType.STRING));
				for(SourceGenerationRequest request : SourceGenerationRequestsManager.getList(filter)){
					try{
						int completedSteps=request.getGeneratedSources().size()/(request.getToGenerateTableCount()/request.getEvaluatedComputationCount());
						Double percent=((double)completedSteps/request.getEvaluatedComputationCount())*100;
						StringBuilder logBuilder=new StringBuilder("completed steps = "+completedSteps);
						logBuilder.append("computation count = "+request.getEvaluatedComputationCount());
						for(Integer reportId:request.getReportID()){
							Double reportStatus=BatchGeneratorObjectFactory.getReport(reportId,false).getPercent();
							percent+=reportStatus/request.getEvaluatedComputationCount();
							logBuilder.append("reportId "+reportId+" status "+reportStatus);
						}
						logger.trace("Updateing reference "+request.getId()+", percent "+percent+", formula details "+logBuilder);
						SourceGenerationRequestsManager.setPhasePercent(percent, request.getId());
					}catch(Exception e){
						logger.warn("Skipping percent update for execution id "+request.getId()+", report id was "+request.getReportID(),e);
						logger.debug("Request data were : "
								+"Generated sources size : "+request.getGeneratedSources().size()
								+"To generate count : "+request.getToGenerateTableCount()
								+"Computation count : "+request.getEvaluatedComputationCount());
						}
				}
				//Updating map generation percent

					filter=new ArrayList<Field>();
					filter.add(new Field(SourceGenerationRequestFields.phase+"",SourceGenerationPhase.mapgeneration+"",FieldType.STRING));
					for(SourceGenerationRequest request : SourceGenerationRequestsManager.getList(filter)){
						try{
							long completedObjCount=0;
							long totalObjCount=0;
							ArrayList<Field> completedFilter=null;
							ArrayList<Field> toCompleteFilter=null;
							//checking jobIds
							boolean completed=true;
							SourceGenerationPhase toSetCompletedPhase=SourceGenerationPhase.completed;
							for(Integer id:request.getJobIds()){
								Submitted job=SubmittedManager.getSubmittedById(id);
								if(!job.getStatus().equals(SubmittedStatus.Completed)&&!job.getStatus().equals(SubmittedStatus.Error)) completed=false;
								if(job.getStatus().equals(SubmittedStatus.Error)) toSetCompletedPhase=SourceGenerationPhase.error;
							}
							if(completed){
								logger.info("All jobs completed for source generation "+request.getId()+", to set phase : "+toSetCompletedPhase);
								SourceGenerationRequestsManager.setPhase(toSetCompletedPhase,request.getId());
							}else{
								//percent calculation
								for(Integer id:request.getJobIds()){
									completedFilter=new ArrayList<Field>();
									completedFilter.add(new Field(SubmittedFields.jobid+"",id+"",FieldType.INTEGER));
									completedFilter.add(new Field(SubmittedFields.status+"",SubmittedStatus.Completed+"",FieldType.STRING));
									completedObjCount+=SubmittedManager.getCount(completedFilter);
									toCompleteFilter=new ArrayList<Field>();
									toCompleteFilter.add(new Field(SubmittedFields.jobid+"",id+"",FieldType.INTEGER));
									totalObjCount+=SubmittedManager.getCount(toCompleteFilter);
								}
								Double percent=100d*completedObjCount/totalObjCount;
								SourceGenerationRequestsManager.setPhasePercent(percent, request.getId());
							}
						}catch(Exception e){logger.warn("Skipping percent update for execution id "+request.getId(),e);}
					}
			}catch(Exception e){
				logger.error("Unexpected exception ",e);
			}finally{
				try{					
					Thread.sleep(millis);
				}catch(InterruptedException e){
					//Woken up
				}
			}
		}
	}
}

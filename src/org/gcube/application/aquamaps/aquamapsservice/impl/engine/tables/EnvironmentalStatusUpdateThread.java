package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
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
						if(request.getReportID()!=-1){
							EnvironmentalExecutionReportItem report=BatchGeneratorObjectFactory.getReport(request.getReportID(),false);
							Double percent=(100/request.getAlgorithms().size()*request.getGeneratedSources().size())+
										(report.getPercent()/request.getAlgorithms().size());
							SourceGenerationRequestsManager.setPhasePercent(percent, request.getId());
						}
					}catch(Exception e){logger.warn("Skipping percent update for execution id "+request.getId()+", report id was "+request.getReportID(),e);}
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

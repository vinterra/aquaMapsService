package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.HSPECGroupGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.environments.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.GroupGenerationRequestFields;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
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
				filter.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.datageneration+"",FieldType.STRING));
				for(HSPECGroupGenerationRequest request : HSPECGroupGenerationRequestsManager.getList(filter)){
					try{
						EnvironmentalExecutionReportItem report=BatchGeneratorObjectFactory.getReport(request.getReportID());
						Double percent=(100/request.getAlgorithms().size()*request.getGeneratedhspec().size())+
									(report.getPercent()/request.getAlgorithms().size());
						HSPECGroupGenerationRequestsManager.setPhasePercent(percent, request.getId());
					}catch(Exception e){logger.warn("Skipping percent update for execution id "+request.getId()+", report id was "+request.getReportID(),e);}
				}
				//Updating map generation percent

					filter=new ArrayList<Field>();
					filter.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.mapgeneration+"",FieldType.STRING));
					for(HSPECGroupGenerationRequest request : HSPECGroupGenerationRequestsManager.getList(filter)){
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
							HSPECGroupGenerationRequestsManager.setPhasePercent(percent, request.getId());
						}catch(Exception e){logger.warn("Skipping percent update for execution id "+request.getId(),e);}
					}
			}catch(Exception e){
				logger.error("Unexpected exception ",e);
			}finally{
				try{					
					Thread.sleep(millis);
				}catch(Exception e){}
			}
		}
	}
}

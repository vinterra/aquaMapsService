package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.AnalysisFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AnalysisUpdaterThread extends Thread{
	private static final GCUBELog logger=new GCUBELog(AnalysisUpdaterThread.class);


	private long millis;


	public AnalysisUpdaterThread(long millis) {
		super("Analysis status updater");
		this.millis=millis;
	}
	@Override
	public void run() {
		while(true){
			try{
				//Updating data generation percent...
				ArrayList<Field> filter=new ArrayList<Field>();
				filter.add(new Field(AnalysisFields.status+"",SubmittedStatus.Generating+"",FieldType.STRING));
				for(Analysis reference : AnalysisTableManager.getList(filter)){
					try{
						Double percent=((double)reference.getPerformedAnalysis().size()/reference.getType().size())*100;
						for(Integer reportId:reference.getReportID()){
							percent=percent+AnalyzerFactory.getReport(reportId,false).getPercent();
						}
						AnalysisTableManager.setPhasePercent(percent, reference.getId());
					}catch(Exception e){logger.warn("Skipping percent update for analysis id "+reference.getId()+", report id was "+reference.getReportID(),e);}
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

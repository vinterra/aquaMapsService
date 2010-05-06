package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AreaPerturbationThread extends Thread {
	private static GCUBELog logger= new GCUBELog(AreaPerturbationThread.class);
	
private int jobId;
public AreaPerturbationThread(ThreadGroup group,int jobId,String jobName) {
	super(group,"AreaPerturbation_"+jobName);
	this.jobId=jobId;	
}


		public void run() {
			//TODO implement perturbation and status handling
			//generationDetails.setAreaReady(true);
			try{				
			JobGenerationDetails.setHCAFTable(DBCostants.HCAF_D,jobId);
//			JobGenerationDetails.updateStatus(jobId, status)
			}catch(Exception e){				
				try{
				JobGenerationDetails.updateStatus(jobId, Status.Error);
				}catch(Exception e1){
					logger.error("Unable to continue", e1);
				}
			}
		}
}

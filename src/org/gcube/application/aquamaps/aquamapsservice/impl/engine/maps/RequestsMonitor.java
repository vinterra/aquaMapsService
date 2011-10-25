package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.common.core.utils.logging.GCUBELog;

public class RequestsMonitor extends Thread {
	private static final GCUBELog logger=new GCUBELog(RequestsMonitor.class);
	
	private boolean object;
	
	public RequestsMonitor(boolean object) {
		super((object?"OBJ":"JOB")+"_REQUESTS_MONITOR");
		this.object=object;
	}
	
	
	@Override
	public void run() {
		while(true){
			try{
				for(Submitted found:JobExecutionManager.getAvailableRequests(object)){
						logger.trace("Found pending "+(object?"OBJ ":"JOB ")+found.getTitle()+", ID : "+found.getSearchId());
						try{
							JobExecutionManager.start(found);
						}catch(Exception e){
							logger.error("Unable to execute "+found,e);
							if(found!=null){
								SubmittedManager.updateStatus(found.getSearchId(),SubmittedStatus.Error);
							}
						}
				}
			}catch(Exception e){
				logger.error("Unexpected exception", e);
			}
		}
	}
	
}

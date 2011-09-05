package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;
import org.gcube.common.core.utils.logging.GCUBELog;

public class HSPECGroupMonitor extends Thread {


	private static final GCUBELog logger=new GCUBELog(HSPECGroupMonitor.class);


	public HSPECGroupMonitor() throws Exception{
		super("HSPEC MONITOR");
	}


	@Override
	public void run() {
		while(true){
			try{
				for(SourceGenerationRequest request:TableGenerationExecutionManager.getAvailableRequests()){
					logger.trace("Found pending hspec request, ID : "+request.getId());
					TableGenerationExecutionManager.start(request);
				}
			}catch(Exception e){
				logger.error("Unexpected Exception", e);
			}
		}
	}
}

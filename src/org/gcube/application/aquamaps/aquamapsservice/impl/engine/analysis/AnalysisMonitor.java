package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AnalysisMonitor extends Thread{
	private static final GCUBELog logger=new GCUBELog(AnalysisMonitor.class);
	
	public AnalysisMonitor() throws Exception{
		super("ANALYSIS MONITOR");
	}
	
	@Override
	public void run() {
		while(true){
			try{
				for(Analysis request:AnalysisManager.getAvailableRequests()){
					logger.trace("Found pending hspec request, ID : "+request.getId());
					AnalysisManager.start(request);
				}
			}catch(Exception e){
				logger.error("Unexpected Exception", e);
			}
		}
	}
}

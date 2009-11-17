package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.Connection;
import java.sql.SQLException;

import org.gcube.common.core.utils.logging.GCUBELog;

public class DistributionThread extends Thread {

	JobGenerationDetails generationDetails;
	private static final GCUBELog logger=new GCUBELog(DistributionThread.class);
	int selectedAquaMap;
	
	public DistributionThread(ThreadGroup group,JobGenerationDetails details,int index) {
		super(group,"SAG_AquaMapObj:"+details.getToPerform().getAquaMapList().getAquaMapList(index).getName());	
		generationDetails=details;
		selectedAquaMap=index;
		
	}
	
	
	public void run() {
		// TODO implement generation - publishing
		try {
			generationDetails.setAquaMapStatus(JobGenerationDetails.Status.Completed, selectedAquaMap);
		} catch (Exception e) {
			logger.error(e.getMessage() );			
		}
	}
}

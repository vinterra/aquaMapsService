package org.gcube.application.aquamaps.impl.threads;

import org.gcube.application.aquamaps.impl.util.DBCostants;

public class AreaPerturbationThread extends Thread {
JobGenerationDetails generationDetails;
public AreaPerturbationThread(ThreadGroup group,JobGenerationDetails details) {
	super(group,"AreaPerturbation_"+details.getToPerform().getName());
	generationDetails=details;	
}


		public void run() {
			//TODO implement perturbation
			generationDetails.setAreaReady(true);
			generationDetails.setHcafTable(DBCostants.HCAF_D);
		}
}

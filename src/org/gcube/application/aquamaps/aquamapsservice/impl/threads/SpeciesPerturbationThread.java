package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;

public class SpeciesPerturbationThread extends Thread {
JobGenerationDetails generationDetails;
String toPerturbSpeciesId;

public SpeciesPerturbationThread(ThreadGroup group,JobGenerationDetails details,String speciesId) {
	super(group,"Species_"+speciesId+"_"+details.getToPerform().getName());
	toPerturbSpeciesId=speciesId;
	generationDetails=details;	
	
}

	public void run() {
		//TODO implement species perturbation
		
		
		generationDetails.getSpeciesHandling().put(toPerturbSpeciesId,JobGenerationDetails.SpeciesStatus.toGenerate);
		generationDetails.setHspenTable(DBCostants.HSPEN);
	}
}

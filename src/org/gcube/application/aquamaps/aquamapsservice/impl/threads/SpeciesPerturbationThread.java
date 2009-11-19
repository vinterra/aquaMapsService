package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.lang.Thread;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
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

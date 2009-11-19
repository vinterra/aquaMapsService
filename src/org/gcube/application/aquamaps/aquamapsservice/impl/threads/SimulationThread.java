package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.SQLException;

import org.gcube.application.aquamaps.aquamapsservice.impl.perturbation.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SimulationThread extends Thread {
	JobGenerationDetails generationDetails;
	//String speciesId;
	private static final GCUBELog logger=new GCUBELog(SimulationThread.class);
	public SimulationThread(ThreadGroup group,JobGenerationDetails details) {
		super(group,"Simulation_"+details.getToPerform().getName());
		generationDetails=details;
	}

	public void run() {
		// TODO Implement simulation data generation
		
		try{
			HSPECGenerator generator= new HSPECGenerator(generationDetails);
			System.out.println("table generated:"+generator.generate());
		}catch(Exception e){logger.error("Error in generating HSPEC", e);}
		
		
		
		generationDetails.setHspecTable(DBCostants.HSPEC);
		try{
			generationDetails.setHspecTable(JobUtils.filterByArea(generationDetails));
		}catch(SQLException e){
			logger.error(e.getMessage());
		}
		//generationDetails.getSpeciesHandling().put(speciesId, JobGenerationDetails.SpeciesStatus.Ready);
	
	}

}

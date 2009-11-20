package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.SQLException;
import java.util.Map.Entry;

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
			String hspec=generator.generate();
			System.out.println("table generated:"+hspec);
			generationDetails.setHspecTable(hspec);
			generationDetails.getToDropTableList().add(hspec);
			for(Entry<String,JobGenerationDetails.SpeciesStatus> entry:generationDetails.getSpeciesHandling().entrySet())
				entry.setValue(JobGenerationDetails.SpeciesStatus.Ready);
			
		}catch(Exception e){logger.error("Error in generating HSPEC", e);}
		
		
		
		
	/*	try{
			generationDetails.setHspecTable(JobUtils.filterByArea(generationDetails));
		}catch(SQLException e){
			logger.error(e.getMessage());
		}*/
		//generationDetails.getSpeciesHandling().put(speciesId, JobGenerationDetails.SpeciesStatus.Ready);
	
	}

}

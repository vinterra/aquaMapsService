package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.perturbation.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.EnvelopeWeightArray;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
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
			EnvelopeWeightArray weights=generationDetails.getToPerform().getWeights(); 
			PerturbationArray envelopePert=generationDetails.getToPerform().getEnvelopCustomization();
			PerturbationArray environPert=generationDetails.getToPerform().getEnvironmentCustomization();
			/*boolean needToGenerate=false;
			if((weights!=null)&&(weights.getWeightList()!=null)){
				for(Weight w:weights.getWeightList())
					if(w.getChosenWeight()<1) {
						needToGenerate=true;
						break;
					}
			}*/			
			if(((weights!=null)&&(weights.getEnvelopeWeightList()!=null)&&(weights.getEnvelopeWeightList().length>0))||
				((envelopePert!=null)&&(envelopePert.getPerturbationList()!=null)&&(envelopePert.getPerturbationList().length>0))||
				((environPert!=null)&&(environPert.getPerturbationList()!=null)&&(environPert.getPerturbationList().length>0))){
				HSPECGenerator generator= new HSPECGenerator(generationDetails);
				String hspec=generator.generate();
				System.out.println("table generated:"+hspec);				
				generationDetails.setHspecTable(hspec);
				generationDetails.getToDropTableList().add(hspec);
			}else{
				generationDetails.setHspecTable(DBCostants.HSPEC);
				generationDetails.setHspecTable(JobUtils.filterByArea(generationDetails));
				for(Entry<String,JobGenerationDetails.SpeciesStatus> entry:generationDetails.getSpeciesHandling().entrySet())
					entry.setValue(JobGenerationDetails.SpeciesStatus.Ready);				
			}			
		}catch(Exception e){logger.error("Error in generating HSPEC", e);}
		
		
		
		
	/*	try{
			generationDetails.setHspecTable(JobUtils.filterByArea(generationDetails));
		}catch(SQLException e){
			logger.error(e.getMessage());
		}*/
		//generationDetails.getSpeciesHandling().put(speciesId, JobGenerationDetails.SpeciesStatus.Ready);
	
	}

}

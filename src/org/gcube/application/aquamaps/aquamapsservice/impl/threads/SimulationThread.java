package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import org.gcube.application.aquamaps.aquamapsservice.impl.perturbation.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.AreasArray;
import org.gcube.application.aquamaps.stubs.EnvelopeWeightArray;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SimulationThread extends Thread {
	
	//String speciesId;
	private static final GCUBELog logger=new GCUBELog(SimulationThread.class);
	
	private EnvelopeWeightArray weights; 
	private PerturbationArray envelopePert;
	private PerturbationArray environPert;
	private int jobId;
	private AreasArray area;
	private String HCAF_D;
	public SimulationThread(ThreadGroup group,Job toPerform) throws Exception {
		super(group,"Simulation_"+toPerform.getName());
		this.weights=toPerform.getWeights();
		this.envelopePert=toPerform.getEnvelopCustomization();
		this.environPert=toPerform.getEnvironmentCustomization();
		this.jobId=Integer.parseInt(toPerform.getId());
		this.HCAF_D=JobGenerationDetails.getHCAFTable(jobId);
		area=toPerform.getSelectedAreas();
	}

	public void run() {
		// TODO Implement simulation data generation
		
		try{
					String hspec;
			if(((weights!=null)&&(weights.getEnvelopeWeightList()!=null)&&(weights.getEnvelopeWeightList().length>0))||
				((envelopePert!=null)&&(envelopePert.getPerturbationList()!=null)&&(envelopePert.getPerturbationList().length>0))||
				((environPert!=null)&&(environPert.getPerturbationList()!=null)&&(environPert.getPerturbationList().length>0))){				
				HSPECGenerator generator= new HSPECGenerator(jobId,HCAF_D,DBCostants.HCAF_S,JobGenerationDetails.getHSPENTable(jobId),weights);						
				hspec=generator.generate();
				System.out.println("table generated:"+hspec);				
				JobGenerationDetails.setHSPECTable(hspec,jobId);
				JobGenerationDetails.addToDropTableList(jobId,hspec);
			}else{
				hspec=DBCostants.HSPEC;
				JobGenerationDetails.setHSPECTable(DBCostants.HSPEC,jobId);							
			}			
			JobGenerationDetails.setHSPECTable(JobUtils.filterByArea(jobId,area,hspec),jobId);
			JobGenerationDetails.updateStatus(jobId, Status.Generating);
			JobGenerationDetails.updateSpeciesStatus(jobId,
					JobGenerationDetails.getSpeciesByStatus(jobId, SpeciesStatus.toGenerate), SpeciesStatus.Ready);	
		}catch(Exception e){logger.error("Error in generating HSPEC", e);}
		
		
		
		
	/*	try{
			generationDetails.setHspecTable(JobUtils.filterByArea(generationDetails));
		}catch(SQLException e){
			logger.error(e.getMessage());
		}*/
		//generationDetails.getSpeciesHandling().put(speciesId, JobGenerationDetails.SpeciesStatus.Ready);
	
	}

}

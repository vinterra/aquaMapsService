package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ThreadManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.common.core.utils.logging.GCUBELog;


public class JobSubmissionThread extends Thread {


	private static final GCUBELog logger=new GCUBELog(JobSubmissionThread.class);
	private static final int waitTime=10*1000;

	private Job toPerform;


	Map<String,File> toPublishPaths=new HashMap<String, File>();
	ThreadGroup waitingGroup;

	public JobSubmissionThread(Job toPerform) throws Exception{
		super(toPerform.getName()+"_thread");
		this.setPriority(MIN_PRIORITY+1);		
		logger.trace("JobSubmissionThread created for job: "+toPerform.getName());
		waitingGroup=new ThreadGroup(toPerform.getName());
		this.toPerform=JobManager.insertNewJob(toPerform);
		
	}

	public int getJobId(){return (toPerform!=null)?toPerform.getId():0;}

	public void run() {
		try{
			if(SubmittedManager.getStatus(toPerform.getId()).equals(SubmittedStatus.Completed))
				logger.trace("Job "+toPerform.getId()+" doesn't need any processing..");
			else{
				try{
					logger.trace("Starting Job "+toPerform.getId()+" processing ");

					if(JobManager.getSpeciesByStatus(toPerform.getId(), SpeciesStatus.toCustomize).length>0){
						logger.trace("Found customizations, going to filter and schedule working HSPEN creation");
						SpeciesPerturbationThread specThread=new SpeciesPerturbationThread(waitingGroup,toPerform.getName(),toPerform.getId(),toPerform.getEnvelopeCustomization());
						specThread.setPriority(MIN_PRIORITY);
						ThreadManager.getExecutor().execute(specThread);	
						ServiceContext.getContext().setScope(specThread, ServiceContext.getContext().getScope());
					}else{
						logger.trace("No customized species found");
					}


					//Create and run Simulation Thread

					while((JobManager.getSpeciesByStatus(toPerform.getId(), SpeciesStatus.toCustomize).length>0)&&(JobManager.getStatus(toPerform.getId())!=SubmittedStatus.Error))
					{
						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException e) {}
						logger.trace(this.getName()+" waiting for species filtering and customization process ");			
						logger.trace(waitingGroup.toString());
					}

					if((JobManager.getStatus(toPerform.getId())==SubmittedStatus.Error))
						throw new Exception("Job "+toPerform.getId()+" failed perturbation phase");

					SubmittedManager.updateStatus(toPerform.getId(), SubmittedStatus.Simulating);
					SimulationThread simT=new SimulationThread(waitingGroup,toPerform);
					simT.setPriority(MIN_PRIORITY);
					ServiceContext.getContext().setScope(simT,ServiceContext.getContext().getScope());
					ThreadManager.getExecutor().execute(simT);

					while(JobManager.getStatus(toPerform.getId()).equals(SubmittedStatus.Simulating)&&(JobManager.getStatus(toPerform.getId())!=SubmittedStatus.Error)){
						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException e) {}
						logger.trace(this.getName()+" waiting for simulation process ");			
						logger.trace(waitingGroup.toString());
					}
					if(JobManager.getStatus(toPerform.getId()).equals(SubmittedStatus.Error)){
						for(AquaMapsObject aquaMapObj:toPerform.getAquaMapsObjectList())
							SubmittedManager.updateStatus(aquaMapObj.getId(), SubmittedStatus.Error);				
						throw new Exception("Job "+toPerform.getId()+" failed simulation phase");
						
					}else {
						logger.trace(this.getName()+" Launching maps generation");
					}

					//Create and run Suitable area map generation
					for(AquaMapsObject aquaMapObj:toPerform.getAquaMapsObjectList()){

						if(aquaMapObj.getStatus().equals(SubmittedStatus.Completed))
							logger.trace("Object "+aquaMapObj.getName()+" "+aquaMapObj+getId()+" is already Completed, skipping generation");
						else
							if((aquaMapObj.getSelectedSpecies().size()>0)){
								Thread t;

								if(aquaMapObj.getType().equals(ObjectType.Biodiversity)){
									t=new BiodiversityThread(waitingGroup,toPerform.getId(),aquaMapObj.getId(),aquaMapObj.getName(),aquaMapObj.getThreshold(),
											toPerform.getSelectedAreas(),aquaMapObj.getBoundingBox());						
									((BiodiversityThread)t).setRelatedSpeciesList(aquaMapObj.getSelectedSpecies(),toPerform.getEnvelopeCustomization(),toPerform.getEnvelopeWeights());
									((BiodiversityThread)t).setGis(aquaMapObj.getGis());
								}else{
									t=new DistributionThread(waitingGroup,toPerform.getId(),aquaMapObj.getId(),aquaMapObj.getName(),
											toPerform.getSelectedAreas(),aquaMapObj.getBoundingBox());
									((DistributionThread)t).setRelatedSpeciesId(aquaMapObj.getSelectedSpecies().iterator().next(),toPerform.getEnvelopeCustomization(),toPerform.getEnvelopeWeights());
									((DistributionThread)t).setGis(aquaMapObj.getGis());
								}					
								t.setPriority(MIN_PRIORITY);
								ServiceContext.getContext().setScope(t,ServiceContext.getContext().getScope());
								ThreadManager.getExecutor().execute(t);
							}else{
								SubmittedManager.updateStatus(aquaMapObj.getId(), SubmittedStatus.Error);
								logger.trace("Skipping obj "+aquaMapObj.getName()+", no species found");
							}
					}
					while(!JobManager.isJobComplete(toPerform.getId())){
						try {
							Thread.sleep(waitTime);
						} catch (InterruptedException e) {}
						logger.trace(this.getName()+" waiting for  generation Process(es) ");			
						logger.trace(waitingGroup.toString());
					}


					if(ServiceContext.getContext().isGISMode())
						if(toPerform.getIsGis())
							JobManager.createGroup(toPerform.getId());



					logger.warn("Job should be complete here");
					SubmittedManager.updateStatus(toPerform.getId(), SubmittedStatus.Completed);


					//			session.commit();
					logger.trace(this.getName()+" job "+toPerform.getName()+" completed");
				}catch (SQLException e) {
					try {
						SubmittedManager.updateStatus(toPerform.getId(), SubmittedStatus.Error);	
					} catch (Exception e1) {logger.error("Unaxpected Error",e);}
					logger.error("SQLException Occurred while performing Job "+toPerform.getName(), e);
					//				rollback();
				} catch (InstantiationException e) {
					logger.error("Unable to instatiate JDBCDriver",e);			
				} catch (IllegalAccessException e) {
					logger.error("Unable to instatiate JDBCDriver",e);			
				} catch (ClassNotFoundException e) {
					logger.error("Unable to instatiate JDBCDriver",e);			
				} catch (Exception e) {
					logger.error("unable to Publish maps",e);
					try {
						SubmittedManager.updateStatus(toPerform.getId(), SubmittedStatus.Error);				
					} catch (Exception e1) {logger.error("Unaxpected Error",e);}
				}

				finally{
					cleanTmp();			
				}
			}
		}catch(Exception e){
			logger.fatal("Cannot read job ("+toPerform.getId()+") status ",e);
		}

	}


	public void cleanTmp(){
		try{
			JobManager.cleanTemp(toPerform.getId());
		}catch(Exception e){
			logger.error("Unable to clean temp tables for toPerform.getId() "+toPerform.getId(), e);
		}
	}


}

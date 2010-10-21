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
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedStatus;

import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

public class JobSubmissionThread extends Thread {

	
	private static final GCUBELog logger=new GCUBELog(JobSubmissionThread.class);
	private static final int waitTime=10*1000;
	
	private Job toPerform;
	private int jobId;
	
	private GCUBEScope actualScope;

	Map<String,File> toPublishPaths=new HashMap<String, File>();
	ThreadGroup waitingGroup;

	public JobSubmissionThread(Job toPerform,GCUBEScope scope) throws Exception{
		super(toPerform.getName()+"_thread");
		this.setPriority(MIN_PRIORITY+1);
		this.toPerform=toPerform;
		logger.trace("JobSubmissionThread created for job: "+toPerform.getName());
		waitingGroup=new ThreadGroup(toPerform.getName());
		logger.trace("Passed scope : "+scope.toString());

		this.actualScope=scope;
	}

	public int getJobId(){return jobId;}

	public void run() {	
		try{
			this.jobId=JobManager.insertNewJob(toPerform);

			if(JobManager.getSpeciesByStatus(jobId, SpeciesStatus.toCustomize).length>0){

				SpeciesPerturbationThread specThread=new SpeciesPerturbationThread(waitingGroup,toPerform.getName(),jobId,toPerform.getEnvelopCustomization());
				specThread.setPriority(MIN_PRIORITY);
				ThreadManager.getExecutor().execute(specThread);				
			}


			//Create and run Simulation Thread
			
			while((JobManager.getSpeciesByStatus(jobId, SpeciesStatus.toCustomize).length>0))
			{
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}
				logger.trace(this.getName()+" waiting for species filtering and customization process ");			
				logger.trace(waitingGroup.toString());
			}

			SubmittedManager.updateStatus(jobId, SubmittedStatus.Simulating);
			SimulationThread simT=new SimulationThread(waitingGroup,toPerform);
			simT.setPriority(MIN_PRIORITY);
			ThreadManager.getExecutor().execute(simT);
			
			while(JobManager.getStatus(jobId).equals(SubmittedStatus.Simulating)){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}
				logger.trace(this.getName()+" waiting for simulation process ");			
				logger.trace(waitingGroup.toString());
			}
			if(JobManager.getStatus(jobId).equals(SubmittedStatus.Error)) 
				throw new Exception(this.getName()+"Something went wrong, check Log. JobId :"+jobId);
			else {
				logger.trace(this.getName()+" Launching maps generation");
			}

			//Create and run Suitable area map generation
			for(AquaMap aquaMapObj:toPerform.getAquaMapList().getAquaMapList()){
				int objId=Integer.parseInt(aquaMapObj.getId());

				if((aquaMapObj.getSelectedSpecies()!=null)&&(aquaMapObj.getSelectedSpecies().getSpeciesList()!=null)&&
						(aquaMapObj.getSelectedSpecies().getSpeciesList().length>0)){
					Thread t;
					String[] species=new String[aquaMapObj.getSelectedSpecies().getSpeciesList().length];
					for(int i=0;i<species.length;i++){
						species[i]=aquaMapObj.getSelectedSpecies().getSpeciesList(i).getId();
					}
					if(aquaMapObj.getType().equalsIgnoreCase(AquaMapsObject.Type.Biodiversity.toString())){
						t=new BiodiversityThread(waitingGroup,jobId,objId,aquaMapObj.getName(),aquaMapObj.getThreshold(),actualScope);						
						((BiodiversityThread)t).setRelatedSpeciesList(species);
						((BiodiversityThread)t).setGis(aquaMapObj.isGis());
					}else{
						t=new DistributionThread(waitingGroup,jobId,objId,aquaMapObj.getName(),actualScope);
						((DistributionThread)t).setRelatedSpeciesId(species);
						((DistributionThread)t).setGis(aquaMapObj.isGis());
					}					
					t.setPriority(MIN_PRIORITY);
					ThreadManager.getExecutor().execute(t);
				}else{
					SubmittedManager.updateStatus(objId, SubmittedStatus.Error);
					logger.trace("Skipping obj "+aquaMapObj.getName()+", no species found");
				}
			}
			while(!JobManager.isJobComplete(jobId)){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}
				logger.trace(this.getName()+" waiting for  generation Process(es) ");			
				logger.trace(waitingGroup.toString());
			}


			if(ServiceContext.getContext().isGISMode())
				JobManager.createGroup(jobId);



			logger.warn("Job should be complete here");
			SubmittedManager.updateStatus(jobId, SubmittedStatus.Completed);


			//			session.commit();
			logger.trace(this.getName()+" job "+toPerform.getName()+" completed");
		}catch (SQLException e) {
			try {
				SubmittedManager.updateStatus(jobId, SubmittedStatus.Error);	
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing Job "+toPerform.getName(), e);
//			rollback();
		} catch (InstantiationException e) {
			logger.error("Unable to instatiate JDBCDriver",e);			
		} catch (IllegalAccessException e) {
			logger.error("Unable to instatiate JDBCDriver",e);			
		} catch (ClassNotFoundException e) {
			logger.error("Unable to instatiate JDBCDriver",e);			
		} catch (Exception e) {
			logger.error("unable to Publish maps",e);
			try {
				SubmittedManager.updateStatus(jobId, SubmittedStatus.Error);				
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
		}

		finally{
			cleanTmp();			
		}
	}



	

	public void cleanTmp(){
		try{
			JobManager.cleanTemp(jobId);
		}catch(Exception e){
			logger.error("Unable to clean temp tables for jobId "+jobId, e);
		}
	}

//	public void rollback(){
//		//TODO implement rollback operations	
//	}




}

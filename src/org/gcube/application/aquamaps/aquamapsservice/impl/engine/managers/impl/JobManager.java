package org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.impl;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.JobManagerI;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.SpeciesStatus;
import org.gcube.common.core.utils.logging.GCUBELog;

public class JobManager extends SubmittedManager implements JobManagerI {

private static GCUBELog logger= new GCUBELog(JobManager.class);
	
	protected JobManager(){}
	private static JobManager instance =new JobManager();
	public static JobManagerI get(){return instance;}
	
	
	public String[] getSpeciesByStatus(int jobId, SpeciesStatus status)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isJobComplete(int jobId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSpeciesListReady(int jobId, String[] toCheck)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateSpeciesStatus(int jobId, String[] speciesId,
			SpeciesStatus status) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
		public void delete(int submittedId) throws Exception {
			// TODO Auto-generated method stub
			super.delete(submittedId);
		}
	
}

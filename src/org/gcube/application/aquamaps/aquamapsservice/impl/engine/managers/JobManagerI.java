package org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers;

import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.SpeciesStatus;

public interface JobManagerI extends SubmittedManagerI{

	public void updateSpeciesStatus(int jobId,String speciesId[],SpeciesStatus status)throws Exception;
	public String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception;
	public boolean isJobComplete(int jobId) throws Exception;
	public boolean isSpeciesListReady(int jobId,String[] toCheck)throws Exception;
}

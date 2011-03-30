package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.dataModel.enhanced.*;
import org.gcube.application.aquamaps.dataModel.Types.*;
import org.gcube.application.aquamaps.dataModel.fields.*;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesPerturbationThread extends Thread {



	private static final GCUBELog logger=new GCUBELog(SpeciesPerturbationThread.class);

	private int jobId;	
	private Map<String,Map<String,Perturbation>> toPerformPerturbations; 


	public SpeciesPerturbationThread(ThreadGroup group,String jobName,int jobId, Map<String,Map<String,Perturbation>> perturbations) throws Exception {
		super(group,"Species_Pert_"+jobName);

		this.jobId=jobId;
		
		this.toPerformPerturbations=perturbations;
	}



	public void run() {		
		DBSession session=null;
		try{
			
			String sourceHSPEN=SourceManager.getSourceName(ResourceType.HSPEN, JobManager.getHSPENTableId(jobId));
			Set<Species> toInsert=new HashSet<Species>();
			
			for(String speciesId : JobManager.getSpeciesByStatus(jobId, null))
				toInsert.add(new Species(speciesId));
			String HSPENName=SpeciesManager.getFilteredHSPEN(sourceHSPEN, toInsert);
			
			
			logger.trace("Going to Perturb filtered HSPEN "+HSPENName);
			session=DBSession.getInternalDBSession();
			for(String speciesId : JobManager.getSpeciesByStatus(jobId, SpeciesStatus.toCustomize)){
				if(toPerformPerturbations.containsKey(speciesId)){
					String query=null;
					try{
						query=SpeciesManager.perturbationUpdate(HSPENName,
								toPerformPerturbations.get(speciesId), speciesId);
						session.executeUpdate(query);						
					}catch(SQLException e){
						logger.error("Unable to perturb speciesId: "+speciesId+" queryString :"+query,e);
					}catch(Exception e){
						logger.error("Unable to create perturbation query for speciesId: "+speciesId,e);
					}
				}else
					logger.warn("Unable to find perturbation for species "+speciesId+" which was "+SpeciesStatus.toCustomize);
				JobManager.updateSpeciesStatus(jobId,new String[]{speciesId}, SpeciesStatus.toGenerate);
			}			
			session.close();
			

		}catch (Exception e){
			try {
				SubmittedManager.updateStatus(jobId, SubmittedStatus.Error);				
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing JobId:"+jobId, e);
		}finally{
			try{
				session.close();
				}catch(Exception e){
					logger.error("Unexpected Error, unable to close session");
				}
			}


	}


	
	
}

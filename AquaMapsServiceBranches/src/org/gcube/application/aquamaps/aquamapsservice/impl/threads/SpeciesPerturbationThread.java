package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesPerturbationThread extends Thread {



	private static final GCUBELog logger=new GCUBELog(SpeciesPerturbationThread.class);

	private int jobId;	
	private Map<String,List<Perturbation>> toPerformPerturbations=new HashMap<String, List<Perturbation>>(); 


	public SpeciesPerturbationThread(ThreadGroup group,String jobName,int jobId,PerturbationArray perturbationArray) throws Exception {
		super(group,"Species_Pert_"+jobName);

		//		this.perturbationArray=perturbationArray;
		this.jobId=jobId;
		String[] toCustomizeSpecies=JobManager.getSpeciesByStatus(jobId, SpeciesStatus.toCustomize);
		if((toCustomizeSpecies!=null)&&(toCustomizeSpecies.length>0)){
			for(String speciesId:toCustomizeSpecies){
				toPerformPerturbations.put(speciesId, new ArrayList<Perturbation>());			
			}

			for(Perturbation pert:perturbationArray.getPerturbationList()){
				String specId=pert.getToPerturbId();
				toPerformPerturbations.get(specId).add(pert);
			}
		}
	}



	public void run() {		
		DBSession session=null;
		try{

			logger.trace("Filtering species...");
			String HSPENName=ServiceUtils.generateId("H", "");
//			int HSPENid=SourceManager.registerSource(SourceType.HSPEN, HSPENName, "filtered selection", JobManager.getAuthor(jobId), SourceManager.getDefaultId(SourceType.HSPEN), SourceType.HSPEN);
			JobManager.setWorkingHSPEN(jobId,HSPENName);
			JobManager.addToDropTableList(jobId, HSPENName);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			String defaultHSPEN=SourceManager.getSourceName(SourceType.HSPEN, SourceManager.getDefaultId(SourceType.HSPEN));
			session.createLikeTable(HSPENName, defaultHSPEN);
			PreparedStatement ps=session.preparedStatement("INSERT INTO "+HSPENName+" (Select * from "+defaultHSPEN+" where "+DBCostants.SpeciesID+"=?)");
			for(String speciesId : JobManager.getSpeciesByStatus(jobId, null)){
				ps.setString(1, speciesId);
				ps.executeUpdate();
				if(toPerformPerturbations.containsKey(speciesId)){
					String query=null;
					try{
						query=DBCostants.perturbationUpdate(HSPENName,
								toPerformPerturbations.get(speciesId), speciesId);
						session.executeUpdate(query);						
					}catch(SQLException e){
						logger.error("Unable to perturb speciesId: "+speciesId+" queryString :"+query,e);
					}catch(Exception e){
						logger.error("Unable to create perturbation query for speciesId: "+speciesId,e);
					}
					JobManager.updateSpeciesStatus(jobId,new String[]{speciesId}, SpeciesStatus.toGenerate);
				}
			}			
			session.close();
			

		}catch (Exception e){
			try {
				SubmittedManager.updateStatus(jobId, SubmittedStatus.Error);				
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing JobId:"+jobId, e);
			//rollback();
		}finally{
			try{
				session.close();
				}catch(Exception e){
					logger.error("Unexpected Error, unable to close session");
				}
			}


	}


}

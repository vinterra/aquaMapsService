package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesPerturbationThread extends Thread {

	//JobGenerationDetails generationDetails;

	private static final GCUBELog logger=new GCUBELog(SpeciesPerturbationThread.class);
//	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	//	private PerturbationArray perturbationArray;
	private int jobId;	
	private Map<String,List<Perturbation>> toPerformPerturbations=new HashMap<String, List<Perturbation>>(); 


	public SpeciesPerturbationThread(ThreadGroup group,String jobName,int jobId,PerturbationArray perturbationArray) throws Exception {
		super(group,"Species_Pert_"+jobName);

		//		this.perturbationArray=perturbationArray;
		this.jobId=jobId;
		String[] toCustomizeSpecies=JobGenerationDetails.getSpeciesByStatus(jobId, SpeciesStatus.toCustomize);
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
			String HSPENName=ServiceUtils.generateId("H", "");//"H"+(uuidGen.nextUUID()).replaceAll("-", "_");
			JobGenerationDetails.setHSPENTable(HSPENName,jobId);
			//			String speciesListTable="s"+(uuidGen.nextUUID()).replaceAll("-", "_");			
			//
			//			String creationSQL="CREATE TABLE  "+speciesListTable+" ("+DBCostants.SpeciesID+" varchar(50) PRIMARY KEY )";
			//
			//			stmt.execute(creationSQL);
			//			generationDetails.getToDropTableList().add(speciesListTable);
			//			StringBuilder insertingQuery=new StringBuilder("Insert into "+speciesListTable+" values ");
			//			Specie[] species=generationDetails.getToPerform().getSelectedSpecies().getSpeciesList();
			//			for(int i= 0;i<species.length;i++)
			//				insertingQuery.append("('"+species[i].getId()+"')"+((i<species.length-1)?" , ":""));
			//			logger.trace("Inserting query : "+insertingQuery.toString());
			//			stmt.execute(insertingQuery.toString());		

			//			stmt.execute("Create table "+HSPENName+" AS Select "+DBCostants.HSPEN+".* from "+DBCostants.HSPEN+","+speciesListTable+" where "+
			//					DBCostants.HSPEN+"."+DBCostants.SpeciesID+" = "+speciesListTable+"."+DBCostants.SpeciesID);
			//			generationDetails.getToDropTableList().add(generationDetails.getHspenTable());
			//			logger.trace("Filtering complete");

			// ***************** Perturbation
			//int progressCount=0;	
			session=DBSession.openSession(PoolManager.DBType.mySql);
			session.createLikeTable(HSPENName, DBCostants.HSPEN);
			PreparedStatement ps=session.preparedStatement("INSERT INTO "+HSPENName+" (Select * from "+DBCostants.HSPEN+" where "+DBCostants.SpeciesID+"=?)");
			for(String speciesId : JobGenerationDetails.getSpeciesByStatus(jobId, null)){
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
					JobGenerationDetails.updateSpeciesStatus(jobId,new String[]{speciesId}, SpeciesStatus.toGenerate);
				}
			}			
			session.close();
			
//			Statement stmt=session.getConnection().createStatement();
//			for(Entry<String,List<Perturbation>> entry:toPerformPerturbations.entrySet()){
//				String query=null;
//				try{
//					query=DBCostants.perturbationUpdate(HSPENName,
//							entry.getValue(), entry.getKey());
//					stmt.executeUpdate(query);
//				}catch(SQLException e){
//					logger.error("Unable to perturb speciesId: "+entry.getKey()+" queryString :"+query,e);
//				}catch(Exception e){
//					logger.error("Unable to create perturbation query for speciesId: "+entry.getKey(),e);
//				}				
//			}
//			
//			JobGenerationDetails.updateSpeciesStatus(jobId, toPerformPerturbations.keySet().toArray(new String[toPerformPerturbations.keySet().size()]), SpeciesStatus.Ready);

		}catch (Exception e){
			try {
				JobGenerationDetails.updateStatus(jobId,JobGenerationDetails.Status.Error);				
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

		//generationDetails.setHspenTable(DBCostants.HSPEN);
	}


}

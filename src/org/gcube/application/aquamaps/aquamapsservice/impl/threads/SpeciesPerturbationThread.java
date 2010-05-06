package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

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
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesPerturbationThread extends Thread {

	//JobGenerationDetails generationDetails;

	private static final GCUBELog logger=new GCUBELog(SpeciesPerturbationThread.class);
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	//	private PerturbationArray perturbationArray;
	private int jobId;	
	private Map<String,List<Perturbation>> toPerformPerturbations=new HashMap<String, List<Perturbation>>(); 


	public SpeciesPerturbationThread(ThreadGroup group,String jobName,int jobId,PerturbationArray perturbationArray) throws Exception {
		super(group,"Species_Pert_"+jobName);

		//		this.perturbationArray=perturbationArray;
		this.jobId=jobId;
		String[] toGenerateSpecies=JobGenerationDetails.getSpeciesByStatus(jobId, SpeciesStatus.toCustomize);
		if((toGenerateSpecies!=null)&&(toGenerateSpecies.length>0)){
			for(String speciesId:toGenerateSpecies){
				toPerformPerturbations.put(speciesId, new ArrayList<Perturbation>());			
			}

			for(Perturbation pert:perturbationArray.getPerturbationList()){
				String specId=pert.getToPerturbId();
				toPerformPerturbations.get(specId).add(pert);
			}
		}
	}



	public void run() {		

		try{

			logger.trace("Filtering species...");
			String HSPENName="H"+(uuidGen.nextUUID()).replaceAll("-", "_");
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
			DBSession session=DBSession.openSession();
			Statement stmt=session.getConnection().createStatement();
			for(Entry<String,List<Perturbation>> entry:toPerformPerturbations.entrySet()){
				String query=null;
				try{
					query=DBCostants.perturbationUpdate(HSPENName,
							entry.getValue(), entry.getKey());
					stmt.executeUpdate(query);
				}catch(SQLException e){
					logger.error("Unable to perturb speciesId: "+entry.getKey()+" queryString :"+query);
				}catch(Exception e){
					logger.error("Unable to create perturbation query for speciesId: "+entry.getKey());
				}				
			}
			session.close();
			JobGenerationDetails.updateSpeciesStatus(jobId, toPerformPerturbations.keySet().toArray(new String[toPerformPerturbations.keySet().size()]), SpeciesStatus.Ready);

		}catch (Exception e){
			try {
				JobGenerationDetails.updateStatus(jobId,JobGenerationDetails.Status.Error);				
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing JobId:"+jobId, e);
			//rollback();
		}

		//generationDetails.setHspenTable(DBCostants.HSPEN);
	}


}

package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesPerturbationThread extends Thread {
	
JobGenerationDetails generationDetails;

private static final GCUBELog logger=new GCUBELog(SpeciesPerturbationThread.class);
private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

public SpeciesPerturbationThread(ThreadGroup group,JobGenerationDetails details) {
	super(group,"Species_Pert_"+details.getToPerform().getName());

	generationDetails=details;	
	
}

	public void run() {
		//TODO implement species perturbation
		logger.trace("Analyzing customizations.. "+this.getName());
		PerturbationArray perturbationArray=generationDetails.getToPerform().getEnvelopCustomization();
		Map<String,List<Perturbation>> toPerformPerturbations=new HashMap<String, List<Perturbation>>(); 
		if((perturbationArray!=null)&&(perturbationArray.getPerturbationList()!=null))
			for(Perturbation pert:perturbationArray.getPerturbationList()){
				String specId=pert.getToPerturbId();
				if(!toPerformPerturbations.containsKey(specId)) {
					generationDetails.getSpeciesHandling().put(specId,JobGenerationDetails.SpeciesStatus.toCustomize);
					toPerformPerturbations.put(specId, new ArrayList<Perturbation>());
				}
				toPerformPerturbations.get(specId).add(pert);
			}
		logger.trace("found "+toPerformPerturbations.size()+" / "+generationDetails.getSpeciesHandling().keySet().size()+" species to perturb");
		try{
		filterSpecies();
		
		// ***************** Perturbation
		//int progressCount=0;		
		Statement stmt=generationDetails.getConnection().getConnection().createStatement();
		for(String speciesId : generationDetails.getSpeciesHandling().keySet()){
			if(toPerformPerturbations.containsKey(speciesId)){ 
				//Species to Perturb
				String query=null;
				try{
					query=DBCostants.perturbationUpdate(generationDetails.getHspenTable(),
							toPerformPerturbations.get(speciesId), speciesId);
					stmt.executeUpdate(query);
				}catch(SQLException e){
					logger.error("Unable to perturb speciesId: "+speciesId+" queryString :"+query);
				}catch(Exception e){
					logger.error("Unable to create perturbation query for speciesId: "+speciesId);
				}
			}
			generationDetails.getSpeciesHandling().put(speciesId,JobGenerationDetails.SpeciesStatus.toGenerate);
		}
		
		}catch (SQLException e){
			try {
				generationDetails.setStatus(JobGenerationDetails.Status.Error);				
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing Job "+generationDetails.getToPerform().getName(), e);
			//rollback();
		}
		
		//generationDetails.setHspenTable(DBCostants.HSPEN);
	}
	
	public void filterSpecies() throws SQLException{
		logger.trace("Filtering species...");
		generationDetails.setHspenTable("H"+(uuidGen.nextUUID()).replaceAll("-", "_"));
		String speciesListTable="s"+(uuidGen.nextUUID()).replaceAll("-", "_");
		Statement stmt=generationDetails.getConnection().getConnection().createStatement();
		
		String creationSQL="CREATE TABLE  "+speciesListTable+" ("+DBCostants.SpeciesID+" varchar(50) PRIMARY KEY )";
		
		stmt.execute(creationSQL);
		generationDetails.getToDropTableList().add(speciesListTable);
		StringBuilder insertingQuery=new StringBuilder("Insert into "+speciesListTable+" values ");
		Specie[] species=generationDetails.getToPerform().getSelectedSpecies().getSpeciesList();
		for(int i= 0;i<species.length;i++)
			insertingQuery.append("('"+species[i].getId()+"')"+((i<species.length-1)?" , ":""));
		logger.trace("Inserting query : "+insertingQuery.toString());
		stmt.execute(insertingQuery.toString());		
		
		stmt.execute("Create table "+generationDetails.getHspenTable()+" AS Select "+DBCostants.HSPEN+".* from "+DBCostants.HSPEN+","+speciesListTable+" where "+
				DBCostants.HSPEN+"."+DBCostants.SpeciesID+" = "+speciesListTable+"."+DBCostants.SpeciesID);
		generationDetails.getToDropTableList().add(generationDetails.getHspenTable());
		logger.trace("Filtering complete");
	}
}

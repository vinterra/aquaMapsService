package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.common.core.utils.logging.GCUBELog;

public class JobSubmissionThread extends Thread {

	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	private static final GCUBELog logger=new GCUBELog(JobSubmissionThread.class);
	private static final int waitTime=10*1000;
	//private static final float defaultAlgorithmWeight=1;

	
	JobGenerationDetails generationStatus;
	
	//String speciesTmpTable=null;
	//String areaTmpTable=null;
	//String simulationDataTable=DBCostants.HSPEC; //Default HSPEC
	Connection conn;
	//String jobID=null;
	Map<String,File> toPublishPaths=new HashMap<String, File>();
	ThreadGroup waitingGroup;
	
	public JobSubmissionThread(Job toPerform) {
		super(toPerform.getName()+"_thread");
		generationStatus=new JobGenerationDetails(toPerform);
		logger.trace("JobSubmissionThread created for job: "+toPerform.getName()+
				" with "+generationStatus.getToPerformBiodiversity().size()+" Biodiversity AquaMaps and "+
				generationStatus.getToPerformDistribution().size()+" Distribution AquaMaps");
		waitingGroup=new ThreadGroup(toPerform.getName());		
	}


	public void run() {	
		try{
			logger.trace("Creating connection to DB");
			Class.forName(DBCostants.JDBCClassName).newInstance();
			conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			conn.setAutoCommit(false);
			generationStatus.setConnection(conn);
			insertNewJob();
			
			// Create and run Area Perturbation Thread
			if((generationStatus.getToPerform().getEnvironmentCustomization()!=null) && 
					(generationStatus.getToPerform().getEnvironmentCustomization().getPerturbationList()!=null)){
					AreaPerturbationThread areaThread=new AreaPerturbationThread(waitingGroup,generationStatus);
					areaThread.start();
			}else{
				generationStatus.setAreaReady(true);
				generationStatus.setHcafTable(DBCostants.HCAF_D);
			}
				
			//Create and run Species envelop perturbationThreads for specified customization 
			
			//SpeciesPerturbationThread
			
			filterSpecies();
			
			//generationStatus.setHspenTable(DBCostants.HSPEN);
			for(Entry<String,JobGenerationDetails.SpeciesStatus> entry:generationStatus.getSpeciesHandling().entrySet())
				entry.setValue(JobGenerationDetails.SpeciesStatus.toGenerate);
			
	/*		Perturbation[] enveloptPerturbation=null;
			if(generationStatus.getToPerform().getEnvelopCustomization()!=null)
					enveloptPerturbation=generationStatus.getToPerform().getEnvelopCustomization().getPerturbationList();
			for(String speciesId:generationStatus.getSpeciesHandling().keySet()){
				boolean isSpeciesToCustom=false;
				if((enveloptPerturbation!=null)&&(enveloptPerturbation.length>0))
					for(Perturbation pert: enveloptPerturbation)
						if(pert.getToPerturbId().equals(speciesId)) {
							isSpeciesToCustom=true;
							break;
						}
				if(isSpeciesToCustom)						
					{
					SpeciesPerturbationThread speciesThread=new SpeciesPerturbationThread(waitingGroup,generationStatus,speciesId);
					speciesThread.start();
					}
				else{
					generationStatus.getSpeciesHandling().put(speciesId,JobGenerationDetails.SpeciesStatus.toGenerate);
					generationStatus.setHspenTable(DBCostants.HSPEN);
				}
			}
		*/	
			//Create and run Simulation Thread for every selected species
			
			boolean speciesOk=false;
			
			while(!speciesOk){
				speciesOk=true;
				for(Entry<String,JobGenerationDetails.SpeciesStatus> entry:generationStatus.getSpeciesHandling().entrySet())
					if(!entry.getValue().equals(JobGenerationDetails.SpeciesStatus.toGenerate)){
						speciesOk=false;
						break;
					}
			}
			
			
				SimulationThread simT=new SimulationThread(waitingGroup,generationStatus);
				simT.start();
			
//			JobUtils.updateStatus(JobStatus.Generating, jobID, DBCostants.UNASSIGNED, conn);
			
			//Create and run Suitable area map generation
			for(int index:generationStatus.getToPerformDistribution().keySet()){					
					DistributionThread t=new DistributionThread(waitingGroup,generationStatus,index);
					t.start();
				}
			//Create and run biodiversity map generation
			for(int index:generationStatus.getToPerformBiodiversity().keySet()){					
				BiodiversityThread t=new BiodiversityThread(waitingGroup,generationStatus,index);
				t.start();
			}
			
			
			while(waitingGroup.activeCount()>0){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}
				logger.trace(this.getName()+" waiting for "+waitingGroup.activeCount()+" generation Process(es) ");			
				logger.trace(waitingGroup.toString());
			}
			generationStatus.setStatus(JobGenerationDetails.Status.Completed);
			

			conn.commit();
			logger.trace(this.getName()+" job "+generationStatus.getToPerform().getName()+" completed");
		}catch (SQLException e) {
			try {
				generationStatus.setStatus(JobGenerationDetails.Status.Error);	
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing Job "+generationStatus.getToPerform().getName(), e);
			rollback();
		} catch (InstantiationException e) {
			logger.error("Unable to instatiate JDBCDriver",e);			
		} catch (IllegalAccessException e) {
			logger.error("Unable to instatiate JDBCDriver",e);			
		} catch (ClassNotFoundException e) {
			logger.error("Unable to instatiate JDBCDriver",e);			
		} catch (Exception e) {
			logger.error("unable to Publish maps",e);
			try {
				generationStatus.setStatus(JobGenerationDetails.Status.Error);				
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
		}

		finally{
			cleanTmp();
		}
	}



	/**Creates a new entry in Job table and AquaMap table (for every aquamap object in job)
	 * 
	 * @return new job id
	 */
	public String insertNewJob() throws Exception{
		logger.trace("Creating new pending Job");
		
		Calendar cal = new GregorianCalendar();
		int giorno = cal.get(Calendar.DAY_OF_MONTH);
		int mese = cal.get(Calendar.MONTH);
		int anno = cal.get(Calendar.YEAR);
		
		Statement stmt=conn.createStatement();
		
		String myData = String.valueOf(anno)+"-"+String.valueOf(mese)+"-"+String.valueOf(giorno);
		String myJob = "INSERT INTO submitted(title, author, date, status,isAquaMap) VALUES('"+
							generationStatus.getToPerform().getName()+"', '"+
							generationStatus.getToPerform().getAuthor()+"', '"+
							myData+"', '"+generationStatus.getStatus().toString()+"', "+false+")";
		logger.trace("Going to execute : "+myJob);
		stmt.execute(myJob, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs=stmt.getGeneratedKeys();
		rs.first();
		String jobId=String.valueOf(rs.getInt(1));
		generationStatus.getToPerform().setId(jobId);
		stmt.close();	
		JobUtils.updateProfile(generationStatus.getToPerform().getName(), generationStatus.getToPerform().getId(), JobUtils.makeJobProfile(generationStatus.getToPerform()),
				generationStatus.getFirstLevelDirName(), generationStatus.getSecondLevelDirName(), generationStatus.getConnection());
		try{
		for(AquaMap aquaMapObj:generationStatus.getToPerform().getAquaMapList().getAquaMapList()){
			String myAquaMapObj="INSERT INTO submitted(title, author, date, status,jobId,type,isAquaMap) VALUES('"+
							aquaMapObj.getName()+"', '"+
							aquaMapObj.getAuthor()+"', '"+
							myData+"', '"+JobGenerationDetails.Status.Pending+"', '"+
							jobId+"', '"+ aquaMapObj.getType()+"', "+true+")";
			Statement aquaStatement=conn.createStatement();
			logger.trace("Going to execute : "+myAquaMapObj);
			aquaStatement.execute(myAquaMapObj,Statement.RETURN_GENERATED_KEYS);
			ResultSet rsA=aquaStatement.getGeneratedKeys();
			rsA.first();
			aquaMapObj.setId(String.valueOf(rsA.getInt(1)));
			aquaStatement.close();
			JobUtils.updateProfile(aquaMapObj.getName(), aquaMapObj.getId(), JobUtils.makeAquaMapProfile(aquaMapObj),
					generationStatus.getFirstLevelDirName(), generationStatus.getSecondLevelDirName(), generationStatus.getConnection());
		}
		conn.commit();
		
		}catch(NullPointerException e ){
			logger.error("Exception while inserting aquamaps object(s) ",e);
			throw new Exception("Unable to insert aquamaps Object(s)");
			
		}
		logger.trace("New Job created with Id "+jobId);
		return jobId;
	}

	public void cleanTmp(){
		for(String tableName:generationStatus.getToDropTableList()){
			try{
				Statement stmt=generationStatus.getConnection().createStatement();
				stmt.execute("Drop table "+tableName);
			}catch(SQLException e){
				logger.error("Unable to drop table : "+tableName);
			}
		}
	}

	public void rollback(){
		//TODO implement rollback operations	
	}

	public void filterSpecies() throws SQLException{
		logger.trace("Filtering species...");
		generationStatus.setHspenTable("H"+(uuidGen.nextUUID()).replaceAll("-", "_"));
		String speciesListTable="s"+(uuidGen.nextUUID()).replaceAll("-", "_");
		Statement stmt=generationStatus.getConnection().createStatement();
		
		String creationSQL="CREATE TABLE  "+speciesListTable+" ("+DBCostants.SpeciesID+" varchar(50) PRIMARY KEY )";
		
		stmt.execute(creationSQL);
		generationStatus.getToDropTableList().add(speciesListTable);
		StringBuilder insertingQuery=new StringBuilder("Insert into "+speciesListTable+" values ");
		Specie[] species=generationStatus.getToPerform().getSelectedSpecies().getSpeciesList();
		for(int i= 0;i<species.length;i++)
			insertingQuery.append("('"+species[i].getId()+"')"+((i<species.length-1)?" , ":""));
		logger.trace("Inserting query : "+insertingQuery.toString());
		stmt.execute(insertingQuery.toString());		
		
		stmt.execute("Create table "+generationStatus.getHspenTable()+" AS Select "+DBCostants.HSPEN+".* from "+DBCostants.HSPEN+","+speciesListTable+" where "+
				DBCostants.HSPEN+"."+DBCostants.SpeciesID+" = "+speciesListTable+"."+DBCostants.SpeciesID);
		generationStatus.getToDropTableList().add(generationStatus.getHspenTable());
		logger.trace("Filtering complete");
	}
	
	
}

package org.gcube.application.aquamaps.impl.threads;

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

import org.gcube.application.aquamaps.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.common.core.utils.logging.GCUBELog;

public class JobSubmissionThread extends Thread {

	//private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
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
			Perturbation[] enveloptPerturbation=null;
			if(generationStatus.getToPerform().getEnvelopCustomization()!=null)
					enveloptPerturbation=generationStatus.getToPerform().getEnvelopCustomization().getPerturbationList();
			for(String speciesId:generationStatus.getSpeciesHandling().keySet()){
				boolean isSpeciesToCustom=false;
				if((enveloptPerturbation!=null)&&(enveloptPerturbation.length>0))
					for(Perturbation pert: enveloptPerturbation)
						if(pert.getToPerturbId().equals(speciesId)) isSpeciesToCustom=true;
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
			
			//Create and run Simulation Thread for every selected species
			
			for(String speciesId:generationStatus.getSpeciesHandling().keySet()){
				SimulationThread t=new SimulationThread(waitingGroup,generationStatus,speciesId);
				t.start();
			}
			
			
		 



			

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
			} catch (SQLException e1) {logger.error("Unaxpected Error",e);}
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
			} catch (SQLException e1) {logger.error("Unaxpected Error",e);}
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
		String myJob = "INSERT INTO JOBS(title, author, date, status) VALUES('"+
							generationStatus.getToPerform().getName()+"', '"+
							generationStatus.getToPerform().getAuthor()+"', "+
							myData+"', '"+generationStatus.getStatus().toString()+"')";
		stmt.execute(myJob, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs=stmt.getGeneratedKeys();
		rs.first();
		String jobId=String.valueOf(rs.getInt(1));
		generationStatus.getToPerform().setId(jobId);
		stmt.close();	
		try{
		for(AquaMap aquaMapObj:generationStatus.getToPerform().getAquaMapList().getAquaMapList()){
			String myAquaMapObj="INSERT INTO AquaMaps(title, author, date, status,jobId) VALUES('"+
							aquaMapObj.getName()+"', '"+
							aquaMapObj.getAuthor()+"', "+
							myData+"', '"+JobGenerationDetails.Status.Pending+
							jobId+"')";
			Statement aquaStatement=conn.createStatement();
			aquaStatement.execute(myAquaMapObj);
			ResultSet rsA=stmt.getGeneratedKeys();
			rsA.first();
			aquaMapObj.setId(String.valueOf(rsA.getInt(1)));
			aquaStatement.close();
		}
		conn.commit();
		
		}catch(NullPointerException e ){
			throw new Exception("No AquaMap objects found");
			
		}
		logger.trace("New Job created with Id "+jobId);
		return jobId;
	}

	public void cleanTmp(){
		//TODO implement cleaning
	}

	public void rollback(){
		//TODO implement rollback operations	
	}

/*	public void publish()throws Exception{
		String basePath=JobUtils.publish(request.getHspec().getName(), getName().replaceAll(" ", ""), new ArrayList<File>(toPublishPaths.values()));
		logger.trace(this.getName()+" files moved to public access location, inserting information in DB");
		PreparedStatement ps =conn.prepareStatement(DBCostants.mapInsertion);
		for(String mapName:toPublishPaths.keySet()){
			ps.setString(1, jobID);
			ps.setString(2, toPublishPaths.get(mapName).getName());
			ps.setString(3,mapName);
			ps.execute();
		}
		logger.trace(this.getName()+" "+toPublishPaths.size()+" file information inserted in DB");
		JobUtils.updateStatus(JobStatus.Completed, jobID, basePath, conn);
		logger.trace(this.getName()+" Job status updated");
	}
*/
}

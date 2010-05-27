package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ThreadManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.common.core.utils.logging.GCUBELog;

public class JobSubmissionThread extends Thread {


	private static final GCUBELog logger=new GCUBELog(JobSubmissionThread.class);
	private static final int waitTime=10*1000;
	//private static final float defaultAlgorithmWeight=1;
	private Job toPerform;
	private int jobId;
	//	JobGenerationDetails generationStatus;


	Map<String,File> toPublishPaths=new HashMap<String, File>();
	ThreadGroup waitingGroup;

	public JobSubmissionThread(Job toPerform) throws Exception{
		super(toPerform.getName()+"_thread");
		this.setPriority(MIN_PRIORITY+1);
		this.toPerform=toPerform;
		logger.trace("JobSubmissionThread created for job: "+toPerform.getName());
		waitingGroup=new ThreadGroup(toPerform.getName());
		
	}

	public int getJobId(){return jobId;}
	
	public void run() {	
		try{
			insertNewJob();
//			DBSession session=DBSession.openSession();
//			session.disableAutoCommit();
			//			generationStatus.setConnection(session);

			//			filterSpecies();
			// Create and run Area Perturbation Thread
			if((toPerform.getEnvironmentCustomization()!=null) && 
					(toPerform.getEnvironmentCustomization().getPerturbationList()!=null)){
				AreaPerturbationThread areaThread=new AreaPerturbationThread(waitingGroup,jobId,toPerform.getName());
				areaThread.setPriority(MIN_PRIORITY);
				ThreadManager.getExecutor().execute(areaThread);
			}else{
				//				generationStatus.setAreaReady(true);
				JobGenerationDetails.setHCAFTable(DBCostants.HCAF_D,jobId);
			}

			//Create and run Species envelop perturbationThreads for specified customization 

			SpeciesPerturbationThread specThread=
				new SpeciesPerturbationThread(waitingGroup,toPerform.getName(),jobId,
						toPerform.getEnvelopCustomization());
			specThread.setPriority(MIN_PRIORITY);
			ThreadManager.getExecutor().execute(specThread);




			//Create and run Simulation Thread for every selected species


			int speciesCount=toPerform.getSelectedSpecies().getSpeciesList().length;
			while(!(JobGenerationDetails.getSpeciesByStatus(jobId, JobGenerationDetails.SpeciesStatus.Ready).length==speciesCount))
			{
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}
				logger.trace(this.getName()+" waiting for species filtering and customization process ");			
				logger.trace(waitingGroup.toString());
			}

			JobGenerationDetails.updateStatus(jobId, Status.Simulating);
			SimulationThread simT=new SimulationThread(waitingGroup,toPerform);
			simT.setPriority(MIN_PRIORITY);
			ThreadManager.getExecutor().execute(simT);

			//			JobUtils.updateStatus(JobStatus.Generating, jobID, DBCostants.UNASSIGNED, conn);

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
						t=new BiodiversityThread(waitingGroup,jobId,objId,aquaMapObj.getName(),aquaMapObj.getThreshold());
						((BiodiversityThread)t).setRelatedSpeciesList(species);
					}else{
						t=new DistributionThread(waitingGroup,jobId,objId,aquaMapObj.getName());
						((DistributionThread)t).setRelatedSpeciesId(species);
					}
					t.setPriority(MIN_PRIORITY);
					ThreadManager.getExecutor().execute(t);
				}else{
					JobUtils.updateAquaMapStatus(objId, Status.Error);
					logger.trace("Skipping obj "+aquaMapObj.getName()+", no species found");
				}
			}
			while(!JobGenerationDetails.isJobComplete(jobId)){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}
				logger.trace(this.getName()+" waiting for  generation Process(es) ");			
				logger.trace(waitingGroup.toString());
			}
			
			
			if(ServiceContext.getContext().isGISMode())
				JobGenerationDetails.createGroup(jobId);
			
			
			
			logger.warn("Job should be complete here");
			JobGenerationDetails.updateStatus(jobId,JobGenerationDetails.Status.Completed);


//			session.commit();
			logger.trace(this.getName()+" job "+toPerform.getName()+" completed");
		}catch (SQLException e) {
			try {
				JobGenerationDetails.updateStatus(jobId,JobGenerationDetails.Status.Error);	
			} catch (Exception e1) {logger.error("Unaxpected Error",e);}
			logger.error("SQLException Occurred while performing Job "+toPerform.getName(), e);
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
				JobGenerationDetails.updateStatus(jobId,JobGenerationDetails.Status.Error);				
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
	public int insertNewJob() throws Exception{
		logger.trace("Creating new pending Job");

		Calendar cal = new GregorianCalendar();
		int giorno = cal.get(Calendar.DAY_OF_MONTH);
		int mese = cal.get(Calendar.MONTH);
		int anno = cal.get(Calendar.YEAR);



		String myData = String.valueOf(anno)+"-"+String.valueOf(mese)+"-"+String.valueOf(giorno);
		String myJob = "INSERT INTO submitted(title, author, date, status,isAquaMap) VALUES('"+
		toPerform.getName()+"', '"+
		toPerform.getAuthor()+"', '"+
		myData+"', '"+JobGenerationDetails.Status.Pending+"', "+false+")";
		logger.trace("Going to execute : "+myJob);
		DBSession session=null;
		try{
		session=DBSession.openSession(PoolManager.DBType.mySql);
		session.disableAutoCommit();
		Statement stmt =session.getConnection().createStatement();
		stmt.execute(myJob, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs=stmt.getGeneratedKeys();
		rs.first();
		jobId=rs.getInt(1);
		toPerform.setId(String.valueOf(jobId));
		stmt.close();	
		//		JobUtils.updateProfile(toPerform.getName(), toPerform.getId(), JobUtils.makeJobProfile(toPerform),
		//				toPerform.getHspec(), generationStatus.getSecondLevelDirName(), generationStatus.getConnection());
		
			for(AquaMap aquaMapObj:toPerform.getAquaMapList().getAquaMapList()){
				String myAquaMapObj="INSERT INTO submitted(title, author, date, status,jobId,type,isAquaMap) VALUES('"+
				aquaMapObj.getName()+"', '"+
				aquaMapObj.getAuthor()+"', '"+
				myData+"', '"+JobGenerationDetails.Status.Pending+"', '"+
				jobId+"', '"+ aquaMapObj.getType()+"', "+true+")";
				Statement aquaStatement=session.getConnection().createStatement();
				logger.trace("Going to execute : "+myAquaMapObj);
				aquaStatement.execute(myAquaMapObj,Statement.RETURN_GENERATED_KEYS);
				ResultSet rsA=aquaStatement.getGeneratedKeys();
				rsA.first();
				aquaMapObj.setId(String.valueOf(rsA.getInt(1)));
				aquaStatement.close();
				//TODO make profile
				
							JobUtils.updateProfile(aquaMapObj.getName(), aquaMapObj.getId(), JobUtils.makeAquaMapProfile(aquaMapObj),
									DBCostants.HSPEC, String.valueOf(jobId));
			}
			
			
			
			if((toPerform.getSelectedSpecies()!=null)&&(toPerform.getSelectedSpecies().getSpeciesList()!=null)&&
					(toPerform.getSelectedSpecies().getSpeciesList().length>0)){
				
				boolean hasPerturbation=false;
				if((toPerform.getEnvelopCustomization()!=null)&&(toPerform.getEnvelopCustomization().getPerturbationList()!=null)&&
						(toPerform.getEnvelopCustomization().getPerturbationList().length>0)) hasPerturbation=true;
				
				boolean hasWeight=false;
				if((toPerform.getWeights()!=null)&&(toPerform.getWeights().getEnvelopeWeightList()!=null)&&
						(toPerform.getWeights().getEnvelopeWeightList().length>0)) hasWeight=true;
				
				PreparedStatement ps=session.preparedStatement("Insert into "+DBCostants.selectedSpecies+" (jobId,speciesId,status) value(?,?,?)");
				ps.setInt(1, jobId);
				for(Specie s:toPerform.getSelectedSpecies().getSpeciesList()){
					String status=JobGenerationDetails.SpeciesStatus.Ready.toString();
					if(hasWeight){
						for(int i=0;i<toPerform.getWeights().getEnvelopeWeightList().length;i++){
							String sId=toPerform.getWeights().getEnvelopeWeightList(i).getSpeciesId();
							if(sId.equalsIgnoreCase(s.getId())){
								status=JobGenerationDetails.SpeciesStatus.toGenerate.toString();
								break;
							}
						}
					}
					if(hasPerturbation){
						for(int i=0;i<toPerform.getEnvelopCustomization().getPerturbationList().length;i++){
							String sId=toPerform.getEnvelopCustomization().getPerturbationList(i).getToPerturbId();
							if(sId.equalsIgnoreCase(s.getId())){
								status=JobGenerationDetails.SpeciesStatus.toCustomize.toString();
								break;
							}
						}
					}
					ps.setString(2, s.getId());
					ps.setString(3, status);
					ps.execute();
				}
			}else throw new Exception("Invalid job, no species found");
			session.commit();
			session.close();
		logger.trace("New Job created with Id "+jobId);
		return jobId;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public void cleanTmp(){
		try{
		JobGenerationDetails.cleanTemp(jobId);
		}catch(Exception e){
			logger.error("Unable to clean temp tables for jobId "+jobId, e);
		}
	}

	public void rollback(){
		//TODO implement rollback operations	
	}




}

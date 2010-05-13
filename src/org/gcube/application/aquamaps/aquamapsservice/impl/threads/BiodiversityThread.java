package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BiodiversityThread extends Thread {	
	private static final GCUBELog logger=new GCUBELog(BiodiversityThread.class);
	private static final int waitTime=10*1000;
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	private int aquamapsId;
	private int jobId;
	private String aquamapsName;
	private float threshold;
	private String HSPECName;	
	private String[] species;
	private DBSession session;


	public BiodiversityThread(ThreadGroup group,int jobId,int aquamapsId,String aquamapsName,float threshold) {
		super(group,"BioD_AquaMapObj:"+aquamapsName);	
		this.threshold=threshold;
		this.aquamapsId=aquamapsId;
		this.aquamapsName=aquamapsName;
		this.jobId=jobId;
	}

	public void setRelatedSpeciesList(String[] ids){
		species=ids;
	}


	public void run() {
		logger.trace(this.getName()+" started");
		try {
			//Waiting for needed simulation data
			while(!JobGenerationDetails.isSpeciesListReady(jobId,species)){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}			
				logger.trace("waiting for selected species to be ready");
			}


			session=DBSession.openSession();
			JobUtils.updateAquaMapStatus(aquamapsId, Status.Generating);
			String tableName="S"+(uuidGen.nextUUID()).replaceAll("-", "_");
			PreparedStatement prep=null;
			String creationSQL="CREATE TABLE "+tableName+" ("+DBCostants.SpeciesID+" varchar(50) PRIMARY KEY )";
			logger.trace("Going to execute query : "+creationSQL);

			session.executeUpdate(creationSQL);

			JobGenerationDetails.addToDropTableList(jobId, tableName);	
			for(String specId: species){
				session.executeUpdate("INSERT INTO "+tableName+" VALUES('"+specId+"')");
				logger.trace("INSERT INTO "+tableName+" VALUES('"+specId+"')");
				;}
			logger.trace(this.getName()+" species temp table filled, gonna select relevant HSPEC records");
			HSPECName=JobGenerationDetails.getHSPECTable(jobId);
			JobUtils.updateAquaMapStatus(aquamapsId, Status.Simulating);
			prep=session.preparedStatement(DBCostants.clusteringBiodiversityQuery(HSPECName,tableName));				
			prep.setFloat(1,threshold);
			ResultSet rs=prep.executeQuery();

			String header=jobId+"_"+aquamapsName;
			String header_map = header+"_maps";
			StringBuilder[] csq_str;
			csq_str=JobUtils.clusterize(rs, 2, 1, 2,true);
			
			
			rs.close();
			session.close();
			
			
			
			if(csq_str==null) logger.trace(this.getName()+"Empty selection, nothing to render");
			else {
				String clusterFile=JobUtils.createClusteringFile(aquamapsName, csq_str, header, header_map, jobId+File.separator+aquamapsName+"_clustering");
				logger.trace(this.getName()+"Clustering completed, gonna call perl with file " +clusterFile);
				JobUtils.updateAquaMapStatus(aquamapsId, Status.Publishing);
				int result=GeneratorManager.requestGeneration(new ImageGeneratorRequest(clusterFile));
//				JobUtils.generateImages(clusterFile);
				logger.trace(this.getName()+" Perl execution exit message :"+result);		
				if(result!=0) logger.error("No images were generated");
				else {
					Map<String,String> app=JobUtils.getToPublishList(System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/",header);


					logger.trace(this.getName()+" found "+app.size()+" files to publish");
					if(app.size()>0){
						String basePath=JobUtils.publish(HSPECName, String.valueOf(jobId), app.values());
						logger.trace(this.getName()+" files moved to public access location, inserting information in DB");
						session=DBSession.openSession();
						PreparedStatement ps =session.preparedStatement(DBCostants.fileInsertion);

						for(String mapName:app.keySet()){
							File f= new File(app.get(mapName));
							ps.setBoolean(1,true);
							ps.setString(2,mapName);
							ps.setString(3, basePath+f.getName());
							ps.setString(4,"IMG");
							ps.setInt(5,aquamapsId);
							ps.execute();
						}

//						session.close();
						logger.trace(this.getName()+" "+app.size()+" file information inserted in DB");
					}
				}
			}	

			//		session.executeUpdate("DROP TABLE "+tableName);		
			JobUtils.updateAquaMapStatus(aquamapsId, Status.Completed);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			try {
				JobUtils.updateAquaMapStatus(aquamapsId, Status.Error);
			} catch (Exception e1) {
				logger.error("Unable to handle previous error! : "+e1.getMessage());
			}
		}finally{
			try{
			session.close();
			}catch(Exception e){
				logger.error("Unexpected Error, unable to close session");
			}
		}
	}
}

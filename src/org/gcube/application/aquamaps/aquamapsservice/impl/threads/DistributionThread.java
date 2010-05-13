package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DistributionThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(DistributionThread.class);
	private static final int waitTime=10*1000;

	private int aquamapsId;
	private String aquamapsName;	
	private String HSPECName;
	private String[] speciesId;
	private DBSession session;
	private int jobId;	


	public DistributionThread(ThreadGroup group,int jobId,int aquamapsId,String aquamapsName) {
		super(group,"SAD_AquaMapObj:"+aquamapsName);
		this.aquamapsId=aquamapsId;
		this.aquamapsName=aquamapsName;
		this.jobId=jobId;
	}
	public void setRelatedSpeciesId(String[] species){
		speciesId=species;
	}

	public void run() {
		logger.trace(this.getName()+" started");
		try {
			while(!JobGenerationDetails.isSpeciesListReady(jobId, speciesId)){
				try{
					Thread.sleep(waitTime);
				}catch(InterruptedException e){}
				logger.trace("waiting for "+speciesId[0]+" to Be ready");
			}

			HSPECName=JobGenerationDetails.getHSPECTable(jobId);
			JobUtils.updateAquaMapStatus(aquamapsId,Status.Simulating);
			session=DBSession.openSession();
			
			String clusteringQuery=DBCostants.clusteringDistributionQuery(HSPECName);
			logger.trace("Gonna use query "+clusteringQuery);
			PreparedStatement ps= session.preparedStatement(clusteringQuery);
			ps.setString(1,speciesId[0]);
			//		ps.setFloat(2,toPerform.getThreshold());

			ResultSet rs=ps.executeQuery();

			String header=jobId+"_"+aquamapsName;
			String header_map = header+"_maps";
			StringBuilder[] csq_str;
			csq_str=JobUtils.clusterize(rs, 2, 1, 2,false);
			rs.close();
			session.close();
			
			if(csq_str==null) logger.trace(this.getName()+"Empty selection, nothing to render");
			else {
				String clusterFile=JobUtils.createClusteringFile(aquamapsName, csq_str, header, header_map, jobId+File.separator+aquamapsName+"_clustering");
				logger.trace(this.getName()+"Clustering completed, gonna call perl with file " +clusterFile);
				JobUtils.updateAquaMapStatus(aquamapsId,Status.Publishing);
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
						PreparedStatement pps =session.preparedStatement(DBCostants.fileInsertion);

						for(String mapName:app.keySet()){
							File f= new File(app.get(mapName));
							pps.setBoolean(1,true);
							pps.setString(2,mapName);
							pps.setString(3, basePath+f.getName());
							pps.setString(4,"IMG");
							pps.setInt(5,aquamapsId);
							pps.execute();
						}
						session.close();

						logger.trace(this.getName()+" "+app.size()+" file information inserted in DB");
					}
				}
			}	

			JobUtils.updateAquaMapStatus(aquamapsId,Status.Completed);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			try {
				JobUtils.updateAquaMapStatus(aquamapsId, Status.Error);
			} catch (Exception e1) {
				logger.error("Unable to handle previous error! : "+e1.getMessage());
			}		
		}
	}
}

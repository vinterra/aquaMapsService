package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DistributionThread extends Thread {

	JobGenerationDetails generationDetails;
	private static final GCUBELog logger=new GCUBELog(DistributionThread.class);
	int selectedAquaMap;
	private static final int waitTime=10*1000;
	
	public DistributionThread(ThreadGroup group,JobGenerationDetails details,int index) {
		super(group,"SAG_AquaMapObj:"+details.getToPerform().getAquaMapList().getAquaMapList(index).getName());	
		generationDetails=details;
		selectedAquaMap=index;
		
	}
	
	
	public void run() {
		logger.trace(this.getName()+" started");
		AquaMap toPerform=generationDetails.getToPerform().getAquaMapList().getAquaMapList(selectedAquaMap);
		Specie species=toPerform.getSelectedSpecies().getSpeciesList(0);
		
		while(!generationDetails.getSpeciesHandling().get(species.getId()).equals(JobGenerationDetails.SpeciesStatus.Ready)){
			try{
				Thread.sleep(waitTime);
			}catch(InterruptedException e){}
			logger.trace("waiting for "+species.getId()+" to Be ready, current status :"+generationDetails.getSpeciesHandling().get(species.getId()).toString());
		}
		
		try {
			String clusteringQuery=DBCostants.clusteringDistributionQuery(generationDetails.getHspecTable());
			logger.trace("Gonna use query "+clusteringQuery);
			PreparedStatement ps= generationDetails.getConnection().prepareStatement(clusteringQuery);
			ps.setString(1,species.getId());
			ps.setFloat(2,toPerform.getThreshold());
			
			ResultSet rs=ps.executeQuery();
			
			String header=toPerform.getName();
			String header_map = header+"_maps";
			StringBuilder[] csq_str;
			csq_str=JobUtils.clusterize(rs, 2, 1, 2,true);
			if(csq_str==null) logger.trace(this.getName()+"Empty selection, nothing to render");
			else {
				File clusterFile=JobUtils.createClusteringFile(toPerform, csq_str, header, header_map, generationDetails.getToPerform()+File.separator+"clustering");
				logger.trace(this.getName()+"Clustering completed, gonna call perl with file " +clusterFile.getAbsolutePath());
				int result=JobUtils.generateImages(clusterFile.getAbsolutePath());
				logger.trace(this.getName()+" Perl execution exit message :"+result);
				if(result!=0) logger.error("No images were generated");
				else {
					Map<String,File> app=JobUtils.getToPublishList(System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/",toPerform.getName());
								
					
					logger.trace(this.getName()+" found "+app.size()+" files to publish");
					if(app.size()>0){
						String basePath=JobUtils.publish(generationDetails.getFirstLevelDirName(), generationDetails.getSecondLevelDirName(), app.values());
						logger.trace(this.getName()+" files moved to public access location, inserting information in DB");
						PreparedStatement pps =generationDetails.getConnection().prepareStatement(DBCostants.fileInsertion);
						
						for(String mapName:app.keySet()){
							pps.setBoolean(1,true);
							pps.setString(2,mapName);
							pps.setString(3, basePath+app.get(mapName).getName());
							pps.setString(4,"IMG");
							pps.setString(5,toPerform.getId());
							pps.execute();
						}
						
						
						logger.trace(this.getName()+" "+app.size()+" file information inserted in DB");
					}
				}
			}	
			
			generationDetails.setAquaMapStatus(JobGenerationDetails.Status.Completed, selectedAquaMap);
		} catch (Exception e) {
			logger.error(e.getMessage() );			
		}
	}
}

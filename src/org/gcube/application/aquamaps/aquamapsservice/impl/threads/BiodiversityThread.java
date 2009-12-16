package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BiodiversityThread extends Thread {
	JobGenerationDetails generationDetails;
	private static final GCUBELog logger=new GCUBELog(BiodiversityThread.class);
	int selectedAquaMap;
	private static final int waitTime=10*1000;
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	public BiodiversityThread(ThreadGroup group,JobGenerationDetails details,int index) {
		super(group,"BioD_AquaMapObj:"+details.getToPerform().getAquaMapList().getAquaMapList(index).getName());	
		generationDetails=details;
		selectedAquaMap=index;

	}


	public void run() {
		logger.trace(this.getName()+" started");
		AquaMap toPerform=generationDetails.getToPerform().getAquaMapList().getAquaMapList(selectedAquaMap);

		ArrayList<String> speciesIds=new ArrayList<String>(); 
		for(Specie spec:toPerform.getSelectedSpecies().getSpeciesList()) speciesIds.add(spec.getId());

		//Waiting for needed simulation data
		while(!generationDetails.isSpeciesListReady(speciesIds)){
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {}			
			logger.trace("waiting for selected species to be ready");
		}
	
		try {
	Statement stmt = generationDetails.getConnection().createStatement();
	String tableName="S"+(uuidGen.nextUUID()).replaceAll("-", "_");
	PreparedStatement prep=null;
	String creationSQL="CREATE TABLE "+tableName+" ("+DBCostants.SpeciesID+" varchar(50) PRIMARY KEY )";
	logger.trace("Going to execute query : "+creationSQL);
	stmt.execute(creationSQL);
	generationDetails.getToDropTableList().add(tableName);
	stmt.close();
	for(String specId: speciesIds){	
		stmt = generationDetails.getConnection().createStatement();
		stmt.execute("INSERT INTO "+tableName+" VALUES('"+specId+"')");
		logger.trace("INSERT INTO "+tableName+" VALUES('"+specId+"')");
		stmt.close();}
	logger.trace(this.getName()+" species temp table filled, gonna select relevant HSPEC records");
	
	prep=generationDetails.getConnection().prepareStatement(DBCostants.clusteringBiodiversityQuery(generationDetails.getHspecTable(),tableName));				
	prep.setFloat(1,toPerform.getThreshold());
	ResultSet rs=prep.executeQuery();
	
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
				PreparedStatement ps =generationDetails.getConnection().prepareStatement(DBCostants.fileInsertion);
				
				for(String mapName:app.keySet()){
					ps.setBoolean(1,true);
					ps.setString(2,mapName);
					ps.setString(3, basePath+app.get(mapName).getName());
					ps.setString(4,"IMG");
					ps.setString(5,toPerform.getId());
					ps.execute();
				}
				
				
				logger.trace(this.getName()+" "+app.size()+" file information inserted in DB");
			}
		}
	}	
		stmt = generationDetails.getConnection().createStatement();
		stmt.execute("DROP TABLE "+tableName);
		stmt.close();
		generationDetails.setAquaMapStatus(JobGenerationDetails.Status.Completed, selectedAquaMap);
	} catch (Exception e) {
		logger.error(e.getMessage());
		try {
			generationDetails.setAquaMapStatus(JobGenerationDetails.Status.Error, selectedAquaMap);
		} catch (Exception e1) {
			logger.error("Unable to handle previous error! : "+e1.getMessage());
		}
	}
}
}

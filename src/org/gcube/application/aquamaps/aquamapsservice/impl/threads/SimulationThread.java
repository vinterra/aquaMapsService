package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.perturbation.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.Area;
import org.gcube.application.aquamaps.stubs.AreasArray;
import org.gcube.application.aquamaps.stubs.EnvelopeWeightArray;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SimulationThread extends Thread {
	
	//String speciesId;
	private static final GCUBELog logger=new GCUBELog(SimulationThread.class);
	
	private EnvelopeWeightArray weights; 
	private PerturbationArray envelopePert;
	private PerturbationArray environPert;
	private int jobId;
	private AreasArray area;
//	private String HCAF_D;
	public SimulationThread(ThreadGroup group,Job toPerform) throws Exception {
		super(group,"Simulation_"+toPerform.getName());
		this.weights=toPerform.getWeights();
		this.envelopePert=toPerform.getEnvelopCustomization();
		this.environPert=toPerform.getEnvironmentCustomization();
		this.jobId=Integer.parseInt(toPerform.getId());
//		this.HCAF_D=SourceManager.getSourceName(SourceType.HCAF, JobManager.getHCAFTableId(jobId));
		
		area=toPerform.getSelectedAreas();
	}

	public void run() {
		
		
		try{
			boolean needToGenerate=((weights!=null)&&(weights.getEnvelopeWeightList()!=null)&&(weights.getEnvelopeWeightList().length>0))||
			((envelopePert!=null)&&(envelopePert.getPerturbationList()!=null)&&(envelopePert.getPerturbationList().length>0))||
			((environPert!=null)&&(environPert.getPerturbationList()!=null)&&(environPert.getPerturbationList().length>0));
			
			boolean filteredArea=((area!=null)&&(area.getAreasList()!=null)&&(area.getAreasList().length>0));
			
	
					
					
			if(filteredArea&&needToGenerate){
				String filteredHcaf=filterByArea(jobId, area, SourceType.HCAF, JobManager.getHCAFTableId(jobId));
				JobManager.setWorkingHCAF(jobId,filteredHcaf);
				String generatedHSPEC=generateHSPEC(jobId, weights, true);
				JobManager.setWorkingHSPEC(jobId,generatedHSPEC);		
				
			}else if (filteredArea){
				JobManager.setWorkingHSPEC(jobId,filterByArea(jobId, area, SourceType.HSPEC, SubmittedManager.getHSPECTableId(jobId)));
			}else if (needToGenerate){
				String generatedHSPEC=generateHSPEC(jobId,  weights,true);
				JobManager.setWorkingHSPEC(jobId,generatedHSPEC);		
			}else{
				JobManager.setWorkingHSPEC(jobId, SourceManager.getSourceName(SourceType.HSPEC, JobManager.getHSPECTableId(jobId)));
			}
						
			JobManager.updateSpeciesStatus(jobId,JobManager.getSpeciesByStatus(jobId, SpeciesStatus.toGenerate), SpeciesStatus.Ready);
			SubmittedManager.updateStatus(jobId, SubmittedStatus.Generating);
		}catch(Exception e){
			logger.error("Error in generating HSPEC", e);
			try {
				SubmittedManager.updateStatus(jobId, SubmittedStatus.Error);
			} catch (Exception e1) {
				logger.error("Unexpected Error", e1);
			}
			}
		
	
	}
	
	
	

	/**
	 * returns a new source table name filtering selected source table against Area selection
	 * 
	 * @param details
	 * @return
	 * @throws SQLException
	 */
	
	
	private static String filterByArea(int jobId,AreasArray areaSelection,SourceType tableType,int sourceId)throws Exception{
			
		logger.trace(" filtering on area selection for jobId:"+jobId);

		DBSession conn =null;
		try{
			
		conn = DBSession.openSession(PoolManager.DBType.mySql);
		String filteredTable=ServiceUtils.generateId(tableType.toString(), "");
		String sourceTableName=SourceManager.getSourceName(tableType, sourceId);
		conn.createLikeTable(filteredTable, sourceTableName);
		
		JobManager.addToDropTableList(jobId, filteredTable);
		

		PreparedStatement psFAO=conn.preparedStatement(DBCostants.filterCellByFaoAreas(filteredTable, sourceTableName));
		PreparedStatement psLME=conn.preparedStatement(DBCostants.filterCellByLMEAreas(filteredTable, sourceTableName));
		PreparedStatement psEEZ=conn.preparedStatement(DBCostants.filterCellByEEZAreas(filteredTable, sourceTableName));
		
		long startTime = System.currentTimeMillis(); 
		
		for(Area area: areaSelection.getAreasList()){
			if(area.getType().equalsIgnoreCase("LME")){
				psLME.setString(1, area.getCode());
				psLME.executeUpdate();
			}else if(area.getType().equalsIgnoreCase("FAO")){
				psFAO.setString(1, area.getCode());
				psFAO.executeUpdate();
			} else if(area.getType().equalsIgnoreCase("EEZ")){
				psEEZ.setString(1, area.getCode());
				psEEZ.executeUpdate();
			} else logger.warn(" Invalid area type , skipped selection : code = "+area.getCode()+"; type = "+area.getType()+"; name = "+area.getName());
		}
		logger.trace("Completed area filtering in "+(System.currentTimeMillis()-startTime)+" ms");

		
//		return SourceManager.registerSource(tableType, filteredTable, sourceTableName+" filtered by Area ", JobManager.getAuthor(jobId), sourceId, tableType);
		return filteredTable;
		}catch (Exception e){
			throw e;
		}finally{
			if((conn!=null)&&(!conn.getConnection().isClosed())){
				conn.close();
			}
		}
		
	}
	
	private static String generateHSPEC(int jobId,EnvelopeWeightArray weights,boolean makeTemp)throws Exception{
		String HCAF_DName=JobManager.getWorkingHCAF(jobId);		
		String HSPENName=JobManager.getWorkingHSPEN(jobId);
		HSPECGenerator generator= new HSPECGenerator(jobId,HCAF_DName,DBCostants.HCAF_S,HSPENName,weights);
		String generatedHspecName=generator.generate();
		System.out.println("table generated:"+generatedHspecName);
		if (makeTemp)JobManager.addToDropTableList(jobId,generatedHspecName);
//		return SourceManager.registerSource(SourceType.HSPEC, generatedHspecName, "generated HSPEC", JobManager.getAuthor(jobId), HCAF_id, SourceType.HCAF);
		return generatedHspecName;
	}
	
}

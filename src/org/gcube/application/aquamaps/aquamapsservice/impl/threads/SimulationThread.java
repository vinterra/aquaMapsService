package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.perturbation.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SimulationThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(SimulationThread.class);

	private Map<String,Map<EnvelopeFields,Field>> weights; 
	private Map<String,Map<String,Perturbation>> envelopePert;
	private int jobId;
	private Set<Area> area;
	public SimulationThread(ThreadGroup group,Job toPerform) throws Exception {
		super(group,"Simulation_"+toPerform.getName());
		this.weights=toPerform.getEnvelopeWeights();
		this.envelopePert=toPerform.getEnvelopeCustomization();		
		this.jobId=toPerform.getId();
		area=toPerform.getSelectedAreas();
	}

	public void run() {


		try{
			boolean needToGenerate=((weights.size()>0)||(envelopePert.size()>0));

			boolean filteredArea=(area.size()>0);

			if(filteredArea&&needToGenerate){
				String filteredHcaf=filterByArea(jobId, area, ResourceType.HCAF, JobManager.getHCAFTableId(jobId));
				JobManager.setWorkingHCAF(jobId,filteredHcaf);
				String generatedHSPEC=generateHSPEC(jobId, weights, true);
				String toUseHSPEC=filterByArea(jobId,area,ResourceType.HSPEC,generatedHSPEC);
				JobManager.setWorkingHSPEC(jobId,toUseHSPEC);	
				JobManager.addToDropTableList(jobId, generatedHSPEC);

			}else if (filteredArea){
				JobManager.setWorkingHSPEC(jobId,filterByArea(jobId, area, ResourceType.HSPEC, SubmittedManager.getHSPECTableId(jobId)));
			}else if (needToGenerate){				
				String generatedHSPEC=generateHSPEC(jobId,  weights,true);
				JobManager.setWorkingHSPEC(jobId,generatedHSPEC);		
			}else{
				JobManager.setWorkingHSPEC(jobId, SourceManager.getSourceName(ResourceType.HSPEC, JobManager.getHSPECTableId(jobId)));
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


	private static String filterByArea(int jobId,Set<Area> areaSelection,ResourceType tableType,int sourceId)throws Exception{

		return filterByArea(jobId,areaSelection,tableType,SourceManager.getSourceName(tableType, sourceId));

	}
	@Deprecated
	private static String filterByArea(int jobId,Set<Area> areaSelection,ResourceType tableType,String sourceTable)throws Exception{
		logger.trace(" filtering on area selection for jobId:"+jobId);

		DBSession conn =null;
		try{

			conn = DBSession.openSession(PoolManager.DBType.mySql);
			String filteredTable=ServiceUtils.generateId(tableType.toString(), "");
			String sourceTableName=sourceTable;
			conn.createLikeTable(filteredTable, sourceTableName);

			JobManager.addToDropTableList(jobId, filteredTable);


			PreparedStatement psFAO=conn.preparedStatement(CellManager.filterCellByFaoAreas(filteredTable, sourceTableName));
			PreparedStatement psLME=conn.preparedStatement(CellManager.filterCellByLMEAreas(filteredTable, sourceTableName));
			PreparedStatement psEEZ=conn.preparedStatement(CellManager.filterCellByEEZAreas(filteredTable, sourceTableName));


			for(Area area: areaSelection){
				switch(area.getType()){
				case LME: 	{psLME.setString(1, area.getCode());
								psLME.executeUpdate();
								break;
				}
				case FAO:   {psFAO.setString(1, area.getCode());
								psFAO.executeUpdate();
								break;
				}
				case EEZ:	{psEEZ.setString(1, area.getCode());
								psEEZ.executeUpdate();
								break;
				}
				default: logger.warn(" Invalid area type , skipped selection : code = "+area.getCode()+"; type = "+area.getType()+"; name = "+area.getName());
				}
			}
			
			return filteredTable;
		}catch (Exception e){
			throw e;
		}finally{
			if((conn!=null)&&(!conn.getConnection().isClosed())){
				conn.close();
			}
		}
	}


	private static String generateHSPEC(int jobId,Map<String,Map<EnvelopeFields,Field>> weights,boolean makeTemp)throws Exception{
		String HCAF_DName=JobManager.getWorkingHCAF(jobId);		
		String HSPENName=JobManager.getWorkingHSPEN(jobId);
		HSPECGenerator generator= new HSPECGenerator(jobId,HCAF_DName,DBCostants.HCAF_S,HSPENName,weights);
		String generatedHspecName=generator.generate();
		System.out.println("table generated:"+generatedHspecName);
		if (makeTemp)JobManager.addToDropTableList(jobId,generatedHspecName);
		//		return SourceManager.registerSource(ResourceType.HSPEC, generatedHspecName, "generated HSPEC", JobManager.getAuthor(jobId), HCAF_id, ResourceType.HCAF);
		return generatedHspecName;
	}

}

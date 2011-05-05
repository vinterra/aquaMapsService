package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.BatchGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.BatchGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.HSPECFields;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.mortbay.log.Log;

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
				Log.debug(" jobId "+jobId+" : Filter By Area and Re-generate");
				String filteredHcaf=filterByArea(jobId, area, ResourceType.HCAF, JobManager.getHCAFTableId(jobId));
				JobManager.setWorkingHCAF(jobId,filteredHcaf);
				String generatedHSPEC=generateHSPEC(jobId, weights, true);
				String toUseHSPEC=filterByArea(jobId,area,ResourceType.HSPEC,generatedHSPEC);
				JobManager.setWorkingHSPEC(jobId,toUseHSPEC);	

			}else if (filteredArea){
				Log.debug(" jobId "+jobId+" : Filter By Area");
				JobManager.setWorkingHSPEC(jobId,filterByArea(jobId, area, ResourceType.HSPEC, SubmittedManager.getHSPECTableId(jobId)));
			}else if (needToGenerate){				
				Log.debug(" jobId "+jobId+" : Re-generate");
				String generatedHSPEC=generateHSPEC(jobId,  weights,true);
				JobManager.setWorkingHSPEC(jobId,generatedHSPEC);		
			}else{
				Log.debug(" jobId "+jobId+" no needs");
				JobManager.setWorkingHSPEC(jobId, SourceManager.getSourceName(ResourceType.HSPEC, JobManager.getHSPECTableId(jobId)));
			}

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

			conn = DBSession.getInternalDBSession();
			String filteredTable=ServiceUtils.generateId(tableType.toString().toLowerCase(), "");
			String sourceTableName=sourceTable;
			conn.createLikeTable(filteredTable, sourceTableName);

			JobManager.addToDropTableList(jobId, filteredTable);


			PreparedStatement psFAO=conn.getFilterCellByAreaQuery(HSPECFields.faoaream,sourceTableName, filteredTable);
			PreparedStatement psLME=conn.getFilterCellByAreaQuery(HSPECFields.lme,sourceTableName, filteredTable);
			PreparedStatement psEEZ=conn.getFilterCellByAreaQuery(HSPECFields.eezall,sourceTableName, filteredTable);


			for(Area area: areaSelection){
				switch(area.getType()){
				case LME: 	{psLME.setInt(1, Integer.parseInt(area.getCode()));
								psLME.executeUpdate();
								break;
				}
				case FAO:   {psFAO.setInt(1, Integer.parseInt(area.getCode()));
								psFAO.executeUpdate();
								break;
				}
				case EEZ:	{psEEZ.setInt(1, Integer.parseInt(area.getCode()));
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

	/**
	 * Uses previos embedded generator
	 * @param jobId
	 * @param weights
	 * @param makeTemp
	 * @return
	 * @throws Exception
	 */
	private static String generateHSPEC(int jobId,Map<String,Map<EnvelopeFields,Field>> weights,boolean makeTemp)throws Exception{
		if(ServiceContext.getContext().isUseEnvironmentModelingLib()){
			logger.trace("HSPEC Generation with Environmental Modeling..");
			//************ fine processing
//			// TODO implement for less then (config param) species
//			//copy species from hspec
//			//for every species
//				//load species
//					//load cell
//					//insert probability
//			
			BatchGeneratorI generator=new BatchGenerator(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator,
					DBSession.getInternalCredentials());
			return generator.generateHSPECTable(JobManager.getWorkingHCAF(jobId), JobManager.getWorkingHSPEN(jobId), AlgorithmType.NativeRange, false);
		}else{
			logger.trace("Embedded HSPEC Generation");
			String HCAF_DName=JobManager.getWorkingHCAF(jobId);		
			String HSPENName=JobManager.getWorkingHSPEN(jobId);
			HSPECGenerator generator= new HSPECGenerator(jobId,HCAF_DName,CellManager.HCAF_S,HSPENName,weights);
			generator.generate();
			String generatedHspecName=generator.getNativeTable();
			System.out.println("table generated:"+generatedHspecName);
			if (makeTemp)JobManager.addToDropTableList(jobId,generatedHspecName);
			return generatedHspecName;
		}
	}

	
}

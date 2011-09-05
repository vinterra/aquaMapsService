package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.MyPooledExecutor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.SourceGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.SourceGenerationRequestFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class TableGenerationExecutionManager {

	private static final GCUBELog logger=new GCUBELog(TableGenerationExecutionManager.class);
	
	private static MyPooledExecutor pool=null;
	private static Semaphore insertedRequest=null;
	
	private static ConcurrentHashMap <Execution,Semaphore> subscribedGenerations=new ConcurrentHashMap<Execution, Semaphore>();
	
	
	
	public static void init(boolean purgeInvalid,int monitorInterval)throws Exception{
		logger.trace("Initializing pools..");
		pool=new MyPooledExecutor("HSPEC_WORKER", 
//				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.HSPEC_GROUP_PRIORITY),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.HSPEC_GROUP_MAX_WORKERS),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.HSPEC_GROUP_MIN_WORKERS),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.HSPEC_GROUP_INTERVAL_TIME));
		if(purgeInvalid){
			logger.trace("Purging pending requests...");
			ArrayList<Field> filter= new ArrayList<Field>();
			int count=0;
			for(SourceGenerationRequest request:SourceGenerationRequestsManager.getList(filter))
				if(!request.getPhase().equals(SourceGenerationPhase.completed)&&
						(!request.getPhase().equals(SourceGenerationPhase.error))){
					SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.error, request.getId());
					count++;
				}
			logger.trace("Purged "+count+" requests");
		}
		
		
		insertedRequest=new Semaphore(0);

		EnvironmentalStatusUpdateThread t4=new EnvironmentalStatusUpdateThread(monitorInterval*1000);
		t4.start();
		HSPECGroupMonitor monitor=new HSPECGroupMonitor();
		monitor.start();
		logger.trace("Monitors started");
	}
	
	
	public static String insertRequest(SourceGenerationRequest request)throws Exception{
		String toReturn=SourceGenerationRequestsManager.insertRequest(request);
		insertedRequest.release();
		return toReturn;
	}
	
	public static List<SourceGenerationRequest> getAvailableRequests()throws Exception{
		insertedRequest.acquire();
		ArrayList<Field> filter= new ArrayList<Field>();
		filter.add(new Field(SourceGenerationRequestFields.phase+"",SourceGenerationPhase.pending+"",FieldType.STRING));
		PagedRequestSettings settings=new PagedRequestSettings(1,0,SourceGenerationRequestFields.submissiontime+"",OrderDirection.DESC);
		return SourceGenerationRequestsManager.getList(filter,settings);
	}
	
	public static void start(SourceGenerationRequest request)throws Exception{
		SourceGenerationRequestsManager.setPhase(SourceGenerationPhase.datageneration, request.getId());
		HSPECGroupWorker thread=new HSPECGroupWorker(request);
		pool.execute(thread);
	}
	
	public static void signForGeneration(AlgorithmType algorithm,LogicType logic, Integer hspenId, Integer hcafId,Integer occurrenceCellsId)throws Exception{
		logger.trace("Signing up for generation [Algorith : "+algorithm+" ; LOGIC : "+logic+" ; HSPEN ID : "+hspenId+" ; HCAF ID : "+hcafId+" ; OCCURRENCE ID : "+occurrenceCellsId+"]");
		Execution identifier=new Execution(algorithm,hcafId,hspenId,occurrenceCellsId,logic);
		if(!subscribedGenerations.containsKey(identifier)) subscribedGenerations.put(identifier, new Semaphore(0));
		subscribedGenerations.get(identifier).wait();
	}
	
	public static void notifyGeneration(AlgorithmType algorithm,LogicType logic, Integer hspenId, Integer hcafId,Integer occurrenceCellsId) throws Exception{
		logger.trace("Notifying generation [Algorith : "+algorithm+" ; LOGIC : "+logic+" ; HSPEN ID : "+hspenId+" ; HCAF ID : "+hcafId+" ; OCCURRENCE ID : "+occurrenceCellsId+"]");
		Execution identifier=new Execution(algorithm,hcafId,hspenId,occurrenceCellsId,logic);
		if(subscribedGenerations.containsKey(identifier)){
			Semaphore sem=subscribedGenerations.get(identifier);
			logger.trace(sem.getQueueLength()+" execution are waiting..");
			sem.notifyAll();
			subscribedGenerations.remove(identifier);
		}
	}
}

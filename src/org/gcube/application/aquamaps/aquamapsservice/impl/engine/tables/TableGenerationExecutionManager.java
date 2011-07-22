package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.HSPECGroupGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.MyPooledExecutor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.environments.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.GroupGenerationRequestFields;
import org.gcube.common.core.utils.logging.GCUBELog;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

public class TableGenerationExecutionManager {

	private static final GCUBELog logger=new GCUBELog(TableGenerationExecutionManager.class);
	
	private static PooledExecutor pool=null;
	private static Semaphore insertedRequest=null;
	
	
	public static void init(boolean purgeInvalid)throws Exception{
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
			for(HSPECGroupGenerationRequest request:HSPECGroupGenerationRequestsManager.getList(filter))
				if(!request.getPhase().equals(HSPECGroupGenerationPhase.completed)&&
						(!request.getPhase().equals(HSPECGroupGenerationPhase.error))){
					HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.error, request.getId());
					count++;
				}
			logger.trace("Purged "+count+" requests");
		}
		
//		ArrayList<Field> filter= new ArrayList<Field>();
//		filter.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.pending+"",FieldType.STRING));
//		int count=HSPECGroupGenerationRequestsManager.getCount(filter);
		
		
		insertedRequest=new Semaphore(0);

		EnvironmentalStatusUpdateThread t4=new EnvironmentalStatusUpdateThread(4*1000);
		t4.start();
		HSPECGroupMonitor monitor=new HSPECGroupMonitor();
		monitor.start();
		logger.trace("Monitors started");
	}
	
	
	public static String insertRequest(HSPECGroupGenerationRequest request)throws Exception{
		String toReturn=HSPECGroupGenerationRequestsManager.insertRequest(request);
		insertedRequest.release();
		return toReturn;
	}
	
	public static List<HSPECGroupGenerationRequest> getAvailableRequests()throws Exception{
		insertedRequest.acquire();
		ArrayList<Field> filter= new ArrayList<Field>();
		filter.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.pending+"",FieldType.STRING));
		PagedRequestSettings settings=new PagedRequestSettings(1,0,GroupGenerationRequestFields.submissiontime+"",OrderDirection.DESC);
		return HSPECGroupGenerationRequestsManager.getList(filter,settings);
	}
	
	public static void start(HSPECGroupGenerationRequest request)throws Exception{
		HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.datageneration, request.getId());
		HSPECGroupWorker thread=new HSPECGroupWorker(request);
		pool.execute(thread);
	}
	
}

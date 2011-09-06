package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.utils.logging.GCUBELog;

public class JobExecutionManager {

	private static final GCUBELog logger=new GCUBELog(JobExecutionManager.class);


	private static MyPooledExecutor jobPool=null;
	private static MyPooledExecutor aqPool=null;

	private static String persistencePath=null;

	private static final ConcurrentHashMap<Integer, Semaphore> blockedJobs=new ConcurrentHashMap<Integer, Semaphore>();

	private static Semaphore insertedJobs=null;
	private static Semaphore insertedObjects=null;

	public static void init(boolean purgeinvalid)throws Exception{

		logger.trace("Initializing pools..");
		jobPool=new MyPooledExecutor("JOB_WORKER", 
				//					ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.JOB_PRIORITY),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.JOB_MAX_WORKERS),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.JOB_MIN_WORKERS),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.JOB_INTERVAL_TIME));

		aqPool=new MyPooledExecutor("AQ_WORKER", 
				//					ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.AQUAMAPS_OBJECT_PRIORITY),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.AQUAMAPS_OBJECT_MAX_WORKERS),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.AQUAMAPS_OBJECT_MIN_WORKERS),
				ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.AQUAMAPS_OBJECT_INTERVAL_TIME));





		persistencePath=ServiceContext.getContext().getPersistenceRoot().getAbsolutePath()+File.separator+"Serialized";
		File file=new File(persistencePath);
		file.mkdirs();
		logger.trace("Storing into "+persistencePath);


		List<Field> pendingObjFilter=new ArrayList<Field>();
		pendingObjFilter.add(new Field(SubmittedFields.isaquamap+"",true+"",FieldType.BOOLEAN));
		pendingObjFilter.add(new Field(SubmittedFields.status+"",SubmittedStatus.Generating+"",FieldType.STRING));
		if(purgeinvalid){
			logger.trace("Purging orphan objects requests...");
			int orphanCount=0;
			for(Submitted pendingObj:SubmittedManager.getList(pendingObjFilter)){
				if(pendingObj.getJobId()==null)SubmittedManager.updateStatus(pendingObj.getSearchId(), SubmittedStatus.Error);
				else { 
					Submitted job=SubmittedManager.getSubmittedById(pendingObj.getJobId());
					if((job!=null)&&(!job.getStatus().equals(SubmittedStatus.Pending))){
						SubmittedManager.updateStatus(pendingObj.getSearchId(), SubmittedStatus.Error);
						orphanCount++;
					}
				}
			}

			logger.trace("Purged "+orphanCount+" objects");
		}



		logger.trace("Looking for existing obj requests...");			
		int objCount=SubmittedManager.getCount(pendingObjFilter);
		insertedObjects=new Semaphore(objCount);
		logger.trace("Found "+objCount+" requests");


		logger.trace("Looking for existing job requests...");
		List<Field> jobfilter=new ArrayList<Field>();
		jobfilter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
		jobfilter.add(new Field(SubmittedFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
		int jobCount=SubmittedManager.getCount(jobfilter);
		insertedJobs=new Semaphore(jobCount);
		logger.trace("Found "+jobCount+" requests");






		RequestsMonitor jobMonitor=new RequestsMonitor(false);
		jobMonitor.start();
		RequestsMonitor objMonitor=new RequestsMonitor(true);
		objMonitor.start();


		logger.trace("Monitors started");
	}


	public static int insertJobExecutionRequest(Job toExecute,boolean postponePublishing)throws Exception{
		String file=persistencePath+File.separator+ServiceUtils.generateId("Job", ".xml");
		logger.debug("Serializing job "+toExecute.getName()+" to "+file);
		AquaMapsXStream.serialize(file, toExecute);
		Submitted toInsert=new Submitted(0);
		toInsert.setAuthor(toExecute.getAuthor());
		toInsert.setSubmissionTime(System.currentTimeMillis());
		toInsert.setGisEnabled(toExecute.getIsGis());
		toInsert.setIsAquaMap(false);
		toInsert.setJobId(0);
		toInsert.setSaved(false);
		toInsert.setSelectionCriteria("");
		toInsert.setSerializedPath(file);
		toInsert.setSourceHCAF(toExecute.getSourceHCAF().getSearchId());
		toInsert.setSourceHSPEC(toExecute.getSourceHSPEC().getSearchId());
		toInsert.setSourceHSPEN(toExecute.getSourceHSPEN().getSearchId());
		toInsert.setStatus(SubmittedStatus.Pending);
		toInsert.setTitle(toExecute.getName());
		toInsert.setPostponePublishing(postponePublishing);
		toInsert=SubmittedManager.insertInTable(toInsert);
		logger.trace("Assigned id "+toInsert.getSearchId()+" to Job "+toInsert.getTitle()+" [ "+toInsert.getAuthor()+" ]");


		insertedJobs.release(1);

		return toInsert.getSearchId();
	}

	public static void insertAquaMapsObjectExecutionRequest(List<AquaMapsObjectExecutionRequest> requests)throws Exception{
		for(AquaMapsObjectExecutionRequest request:requests){
			String file=persistencePath+File.separator+ServiceUtils.generateId("AQ", ".xml");
			logger.debug("Serializing object "+request.getObject().getTitle()+" to "+file);
			request.getObject().setSerializedPath(file);
			request.getObject().setStatus(SubmittedStatus.Generating);
			AquaMapsXStream.serialize(file, request);
		}
		int jobId=requests.get(0).getObject().getJobId();
		logger.trace("Creating "+requests.size()+" requests for objects execution for job "+jobId);
		blockedJobs.put(jobId, new Semaphore(-(requests.size()-1)));
		
		
		AquaMapsManager.insertRequests(requests);

		insertedObjects.release(requests.size());

		//************* BLOCKS current job
		((Semaphore)blockedJobs.get(jobId)).acquire();
		blockedJobs.remove(jobId);
	}

	private static void startJob(Submitted job)throws Exception{
		SubmittedManager.updateStatus(job.getSearchId(), SubmittedStatus.Simulating);
		Submitted submittedJob=SubmittedManager.getSubmittedById(job.getSearchId());
		Job toExecute=(Job) AquaMapsXStream.deSerialize(submittedJob.getSerializedPath());
		JobWorker worker=new JobWorker(toExecute,submittedJob);
		jobPool.execute(worker);
	}

	private static void startAquaMapsObject(Submitted object)throws Exception{
		SubmittedManager.updateStatus(object.getSearchId(), SubmittedStatus.Publishing);
		Submitted submittedObject=SubmittedManager.getSubmittedById(object.getSearchId());		
		AquaMapsObjectExecutionRequest toExecute=(AquaMapsObjectExecutionRequest) AquaMapsXStream.deSerialize(submittedObject.getSerializedPath());
		AquaMapsObjectWorker worker=new AquaMapsObjectWorker(toExecute);
		aqPool.execute(worker);
	}


	public static void start(Submitted toStart)throws Exception{
		if(toStart.getIsAquaMap()) startAquaMapsObject(toStart);
		else startJob(toStart);
	}



	public static void cleanReferences(Submitted toClean){
		try{
			if(!toClean.getIsAquaMap()){
				JobManager.cleanTemp(toClean.getSearchId());
			}
		}catch(Exception e){
			logger.error("Unexpected Error while trying to clean up submitted "+toClean.getSearchId()+" ["+(toClean.getIsAquaMap()?"OBJECT":"JOB")+"]",e);
		}
	}





	public static void alertJob(int objId,int jobId){
		try{
			if(blockedJobs.containsKey(jobId)){
				Semaphore sem=((Semaphore) blockedJobs.get(jobId));
				sem.release();
				logger.trace("Object "+objId+" released lock for job "+jobId+", still waiting for "+sem.availablePermits());
			}else logger.warn("Unable to find queued job "+jobId+", object was "+objId);
		}catch(Exception e){
			logger.warn("UNABLE TO RELEASE LOCK FOR JOB [ID : "+jobId+"]",e);
		}
	}


	

	public static List<Submitted> getAvailableRequests(boolean object)throws Exception {
		if(object) insertedObjects.acquire();
		else insertedJobs.acquire();

		List<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SubmittedFields.isaquamap+"",object+"",FieldType.BOOLEAN));
		filter.add(new Field(SubmittedFields.status+"",(object?SubmittedStatus.Generating:SubmittedStatus.Pending)+"",FieldType.STRING));
		PagedRequestSettings settings= new PagedRequestSettings(1,0,SubmittedFields.submissiontime+"",PagedRequestSettings.OrderDirection.DESC);
		return SubmittedManager.getList(filter, settings);
	}


}
package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.CommonServiceLogic;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.HSPECGroupGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.BatchGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.EnvironmentalLogicManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.common.core.utils.logging.GCUBELog;

public class HSPECGroupGenerationThread extends Thread {

	private HSPECGroupGenerationRequest request;
	private static final GCUBELog logger=new GCUBELog(HSPECGroupGenerationThread.class);

	public HSPECGroupGenerationThread(HSPECGroupGenerationRequest request) {
		this.request=request;
	}

	@Override
	public void run() {
		BatchGeneratorI batch = null;
		try{
			logger.trace("Starting execution for request ID "+request.getId());
			Resource hcaf=SourceManager.getById(ResourceType.HCAF, request.getHcafsearchid());
			if(hcaf == null ) throw new Exception ("Unable to find hcaf with id "+request.getHcafsearchid());
			Resource hspen=SourceManager.getById(ResourceType.HSPEN, request.getHspensearchid());
			if(hspen == null ) throw new Exception ("Unable to find hspen with id "+request.getHcafsearchid());
			batch=EnvironmentalLogicManager.getBatch();
			logger.debug("Got batch Id "+batch.getReportId());

			batch.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
					DBSession.getInternalCredentials());

			HSPECGroupGenerationRequestsManager.setReportId(batch.getReportId(),request.getId());
			ArrayList<Resource> generatedHspec=new ArrayList<Resource>(); 
			HSPECGroupGenerationRequestsManager.setPhasePercent(0d, request.getId());
			for(String algorithmString:request.getAlgorithms()){
				try{
					AlgorithmType algorithmType=AlgorithmType.valueOf(algorithmString);
					long startTime=System.currentTimeMillis();
					logger.trace("Found algorithm "+algorithmType+", submitting generation..");	
					String generatedHSPECTable=batch.generateHSPECTable(hcaf.getTableName(),hspen.getTableName(), algorithmType, 
							request.getIsCloud(),request.getBackend_url(),request.getResources());
					logger.trace("Generated Table "+generatedHSPECTable+" in "+(startTime-System.currentTimeMillis()));
					Resource toRegister=new Resource(ResourceType.HSPEC,0);
					toRegister.setAlgorithm(algorithmType);
					toRegister.setAuthor(request.getAuthor());
					toRegister.setDate(ServiceUtils.getTimeStamp());
					toRegister.setDescription(request.getDescription());
					toRegister.setProvenance("Generated on AquaMaps VRE, submitted on "+(request.getIsCloud()?"CLOUD":"Embedded Library"));
					toRegister.setSourceHCAFId(hcaf.getSearchId());
					toRegister.setSourceHCAFTable(hcaf.getTableName());
					toRegister.setSourceHSPENId(hspen.getSearchId());
					toRegister.setSourceHSPENTable(hspen.getTableName());
					toRegister.setStatus("Completed");
					toRegister.setTableName(generatedHSPECTable);
					toRegister.setTitle(request.getGenerationname()+"_"+algorithmType);
					toRegister=SourceManager.registerSource(toRegister);
					logger.trace("Registered HSPEC with id "+toRegister.getSearchId());
					HSPECGroupGenerationRequestsManager.addGeneratedHSPEC(toRegister.getSearchId(), request.getId());
					generatedHspec.add(toRegister);
				}catch(Exception e){
					logger.error("Unable to generate data for  algorithm "+algorithmString,e);
				}
			}
			EnvironmentalLogicManager.leaveBatch(batch);
			logger.trace("Generated "+generatedHspec.size()+" hspec table(s)");
			ArrayList<Integer> jobIds=new ArrayList<Integer>();
			if(request.getEnableimagegeneration()){
				HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.mapgeneration,request.getId());
				for(Resource hspec:generatedHspec){
					logger.trace("Requesting job for hspec "+hspec.getTitle()+", ID :"+hspec.getSearchId());
					int jobID=CommonServiceLogic.generateMaps_Logic(hspec.getSearchId(), new ArrayList<Field>(), request.getAuthor(), request.getEnablelayergeneration());
					logger.trace("Job is "+jobID);
					HSPECGroupGenerationRequestsManager.addJobIds(jobID, request.getId());
					jobIds.add(jobID);
				}
			}
			if(jobIds.size()>0){
				logger.trace("Generation "+request.getId()+" : submitted "+jobIds.size()+" job, waiting for them to complete..");
				Boolean completed=false;
				while(!completed){
					for(Integer id:jobIds)
						if(!JobManager.isJobComplete(id)){
							completed=false;
							break;
						}else completed=true;

					if(!completed)
						try{
							Thread.sleep(10*1000);
						}catch(InterruptedException e){}
				}
			}
			logger.trace("Generation "+request.getId()+" : All jobs complete!");

			HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.completed,request.getId());
			HSPECGroupGenerationRequestsManager.setPhasePercent(100, request.getId());
		}catch(Exception e){
			logger.error("Unexpected Exception while executing request "+request.getId(), e);
			try{
				HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.error,request.getId());
			}catch(Exception e1){
				logger.error("Unable to update phase , request was "+request,e);
			}
		}finally{
			try{
				if(batch!=null) EnvironmentalLogicManager.leaveBatch(batch);
			}catch(Exception e){
				logger.error("Unable to leave generator",e);
			}
		}
	}
}

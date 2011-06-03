package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.HSPECGroupGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.BatchGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.EnvironmentalLogicManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
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
			Resource hspen=SourceManager.getById(ResourceType.HSPEN, request.getHspensearchid());
			batch=EnvironmentalLogicManager.getBatch();
			logger.debug("Got batch Id "+batch.getReportId());
			//			return generator.generateHSPECTable(JobManager.getWorkingHCAF(jobId), JobManager.getWorkingHSPEN(jobId), AlgorithmType.NativeRange, false);

			batch.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
					DBSession.getInternalCredentials());

			HSPECGroupGenerationRequestsManager.setReportId(batch.getReportId(),request.getId());
			HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.DataGeneration,request.getId());
			ArrayList<Resource> generatedHspec=new ArrayList<Resource>(); 
			HSPECGroupGenerationRequestsManager.setPhasePercent(0d, request.getId());
			for(String algorithmString:request.getAlgorithms()){
				try{
					AlgorithmType algorithmType=AlgorithmType.valueOf(algorithmString);
					long startTime=System.currentTimeMillis();
					logger.trace("Found algorithm "+algorithmType+", submitting generation..");					
					String generatedHSPECTable=batch.generateHSPECTable(hcaf.getTableName(), hspen.getTableName(), algorithmType, request.getIsCloud());
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
					Double percent=(100d/request.getAlgorithms().size())*generatedHspec.size();
					HSPECGroupGenerationRequestsManager.setPhasePercent(percent, request.getId());
				}catch(Exception e){
					logger.error("Unable to generate data for  algorithm "+algorithmString,e);
				}
			}
			EnvironmentalLogicManager.leaveBatch(batch);
			logger.trace("Generated "+generatedHspec.size()+" hspec table(s)");
			if(request.getEnableimagegeneration()){
				HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.MapGeneration,request.getId());
				for(Resource hspec:generatedHspec){
					//TODO submit job generation
				}
			}



		}catch(Exception e){
			logger.error("Unexpected Exception while executing request "+request.getId(), e);
			try{
				HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.Error,request.getId());
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

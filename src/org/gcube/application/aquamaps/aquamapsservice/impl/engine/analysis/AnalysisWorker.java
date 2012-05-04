package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

import loci.formats.ImageTools;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils.AppZip;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AnalysisWorker extends Thread{
	
	private static final GCUBELog logger=new GCUBELog(AnalysisWorker.class);

	private Analysis toPerform;
	private AnalysisResponseDescriptor produced=new AnalysisResponseDescriptor();
	final Semaphore blocking=new Semaphore(0);
	
	
	public AnalysisWorker(Analysis analysis) {
		toPerform=analysis;
	}
	
	@Override
	public void run() {
		Analyzer analyzer=null;
		logger.trace("Starting execution for request ID "+toPerform.getId());
		logger.trace("Anaylisis to Perform : "+toPerform.toXML());
		try{
		AnalysisTableManager.setStartTime(toPerform.getId());
		AnalysisTableManager.setPhasePercent(0d, toPerform.getId());
		
		List<AnalysisRequest> requests=AnalysisRequest.getRequests(toPerform, this);
		
		//********** RETRIEVE BATCH GENERATION
		for(AnalysisRequest request:requests){
			try{
				analyzer=AnalyzerManager.getBatch();
				logger.debug("Got batch Id "+analyzer.getReportId());
				analyzer.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
						DBSession.getInternalCredentials());

				//********** START PROCESS INIT
				AnalysisTableManager.addReportId(analyzer.getReportId(),toPerform.getId());		 
				analyzer.produceImages(request);
			}catch(Exception e){throw e;}
		}
		
		logger.debug("Going to wait for "+requests.size()+" analyzers");
		blocking.acquire(requests.size());
		
		logger.debug("Woken up");
		for(Entry<AnalysisType,String> entry:produced.getMessages().entrySet()){
			logger.warn("Error message from execution, Analysis : "+entry.getKey()+", message : "+entry.getValue());
		}
		
		
		String path=archiveImages(produced,toPerform.getTitle()).getAbsolutePath();
		logger.trace("Generated archive file "+path);
		AnalysisTableManager.setArchivePath(toPerform.getId(), path);
		AnalysisTableManager.setStatus(SubmittedStatus.Completed, toPerform.getId());
		
		}catch(Exception e ){
			logger.error("Unexpected Exception while performing "+toPerform.toXML(),e);
			try {
				AnalysisTableManager.setStatus(SubmittedStatus.Error, toPerform.getId());
			} catch (Exception e1) {
				logger.fatal("Unable to update reference status for analysis",e1);
			}
		}
	}
	
	
	private static File archiveImages(AnalysisResponseDescriptor produced, String name)throws Exception{
		File directory=new File(ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS),ServiceUtils.generateId(name, ""));
		directory.mkdirs();
		for(Entry<String,ArrayList<ImageDescriptor>> entry :produced.getCategorizedImages().entrySet()){
			File subDir=new File(directory,entry.getKey());
			subDir.mkdirs();
			for(int i=0;i<entry.getValue().size();i++){
				Image image=entry.getValue().get(i).getImage();
				BufferedImage bi = ImageTools.makeBuffered(image);
				File outputfile = new File(subDir,entry.getValue().get(i).getName()+".png");
				ImageIO.write(bi, "png", outputfile);
			}			
		}
		
		File toReturn=new File(ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS),ServiceUtils.generateId("Analysis", ".zip"));
		AppZip zip=new AppZip(directory.getAbsolutePath());
		logger.trace("Zipped "+zip.zipIt(toReturn.getAbsolutePath())+" files to "+toReturn.getAbsolutePath());		
		ServiceUtils.deleteFile(directory.getAbsolutePath());
		return toReturn;		
	}
	
	
	public void notifyGenerated(AnalysisResponseDescriptor result, Analyzer analyzer){
		logger.debug("Releasing... analyzerId is "+analyzer.getReportId());
		produced.append(result);
		blocking.release();
		if(analyzer!=null)
			try {
				AnalyzerManager.leaveBatch(analyzer);
				AnalysisTableManager.removeReportId(analyzer.getReportId(),toPerform.getId());
			} catch (Exception e) {
				logger.fatal("Unable to leave analyzer",e);
			}
	}
}

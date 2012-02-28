package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import loci.formats.ImageTools;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils.ArchiveManager;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AnalysisWorker extends Thread{
	
	private static final GCUBELog logger=new GCUBELog(AnalysisWorker.class);

	private Analysis toPerform;
	
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
		
		AnalysisRequest request=checkAnalysisParameters(toPerform);
		//********** RETRIEVE BATCH GENERATION
		
		analyzer=AnalyzerManager.getBatch();
		logger.debug("Got batch Id "+analyzer.getReportId());
		analyzer.setConfiguration(ServiceContext.getContext().getFile("generator", false).getAbsolutePath()+File.separator, 
				DBSession.getInternalCredentials());
		
		//********** START PROCESS INIT
		AnalysisTableManager.setReportId(analyzer.getReportId(),toPerform.getId());		 
		AnalysisTableManager.setPhasePercent(0d, toPerform.getId());
		
		List<Image> produced=analyzer.produceImages(request);
		AnalyzerManager.leaveBatch(analyzer);
		logger.trace("Produced "+produced.size()+" image for Analysis "+toPerform.getId());
		
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
		}finally{
			if(analyzer!=null)
				try {
					AnalyzerManager.leaveBatch(analyzer);
				} catch (Exception e) {
					logger.fatal("Unable to leave analyzer",e);
				}
		}
	}
	
	
	private static AnalysisRequest checkAnalysisParameters(Analysis toCheck) throws Exception{
		ArrayList<ResourceType> toAvoidTypes=new ArrayList<ResourceType>();
		toAvoidTypes.add(ResourceType.HSPEN);
		toAvoidTypes.add(ResourceType.OCCURRENCECELLS);
		switch(toCheck.getType()){
		case HCAF: toAvoidTypes.add(ResourceType.HSPEN);
					break;
		case HSPEC : toAvoidTypes.add(ResourceType.HCAF);
		}
		ArrayList<String> foundHSPECTables=new ArrayList<String>();
		ArrayList<String> foundHCAFTables=new ArrayList<String>();
		for(Integer sourceId:toCheck.getSources()){
			Resource r=SourceManager.getById(sourceId);
			if(toAvoidTypes.contains(r.getType())) throw new Exception ("Invalid resource [ID = "+r.getSearchId()+" , Type = "+r.getType()+"]selected for current analysis type "+toCheck.getType());
			if(r.getType().equals(ResourceType.HCAF)) foundHCAFTables.add(r.getTableName());
			else if(r.getType().equals(ResourceType.HSPEC)) foundHSPECTables.add(r.getTableName());
		}
		return new AnalysisRequest(toCheck.getType(),
				foundHCAFTables.toArray(new String[foundHCAFTables.size()]),
				foundHSPECTables.toArray(new String[foundHSPECTables.size()]));
			
	}
	private static File archiveImages(List<Image> images, String name)throws Exception{
		ArrayList<File> toZipFiles=new ArrayList<File>();
		File directory=new File(ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS),name);
		directory.mkdirs();
		
		for(int i=0;i<images.size();i++){
			Image image=images.get(i);
			BufferedImage bi = ImageTools.makeBuffered(image);

			File outputfile = new File(directory,name+"_"+i+".png");
			ImageIO.write(bi, "png", outputfile);
			toZipFiles.add(outputfile);
		}
		File toReturn=new File(ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS),ServiceUtils.generateId("Analysis", ".tar.gz"));
		ArchiveManager.createTarGz(toReturn, toZipFiles);
		for(File f: toZipFiles) ServiceUtils.deleteFile(f.getAbsolutePath());
		return toReturn;		
	}
}

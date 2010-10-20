package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.SourceGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceGenerationThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(SourceGenerationThread.class);
	
	
	private static int requestId;
	
	public SourceGenerationThread(int id) {
		requestId=id;
	}
	
	@Override
	public void run() {	
		File dir=new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+"SourceGeneration");
		dir.mkdirs();
		String sourcesFileName=dir.getAbsolutePath()+File.separator+ServiceUtils.generateId("", ".txt");
		String csvFileName=ServiceUtils.generateId("", ".csv");
		String appTable=ServiceUtils.generateId("HCAFapp", "");
		DBSession session=null;
		try{
			logger.trace("Started generation Source for requestId : "+requestId);
			String[] sources=SourceGenerationManager.getSources(requestId).split(";");
			
			PrintStream ps= new PrintStream(new FileOutputStream(sourcesFileName));
			for(String s:sources){
				ps.println(s);
			}
			ps.close();
			logger.trace("Wroten "+sources.length+" sources to "+sourcesFileName);
			
			SourceGeneratorRequest req= new SourceGeneratorRequest(sourcesFileName,csvFileName,SourceType.HCAF);
			
			//*************** IMPORT
			
			logger.trace("Requesting generator");
			SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Importing);
			GeneratorManager.requestGeneration(req);
			//!!!!!!!!! Standalone application always adds ".txt" to outputfile
			csvFileName=System.getenv("GLOBUS_LOCATION")+File.separator+csvFileName+".txt";
			if(!(new File(csvFileName).exists())) throw new Exception("CSV Generation failed");
			
			
			
			// ************* LOADING CSV
			
			
			
			
			logger.trace("Creating appTable");
			
			session=DBSession.openSession(DBType.mySql);
			session.createTable(appTable, new String[]{
					"CsquareCode varchar(10)",
					"PrimProdMean int(11)",
					"primary key (CsquareCode)"
			}, DBSession.ENGINE.MyISAM);
			logger.trace("Importing data from "+csvFileName);
			long startImport=System.currentTimeMillis();
			importCSVToAPP(appTable, csvFileName);
			long insertedCount=session.getTableCount(appTable);
			logger.trace("Inserted "+insertedCount+" in "+(System.currentTimeMillis()-startImport)+" ms");
			
			
			//************** MERGING
			
			String targetHCAFName=SourceGenerationManager.getGeneratedHCAFName(requestId);
			String targetHCAFTable=ServiceUtils.generateId("HCAF", "");
			int sourceHCAFId=SourceGenerationManager.getSourceId(requestId);
			String sourceHCAFName=SourceManager.getSourceName(SourceType.HCAF, sourceHCAFId);
			
			logger.trace("Starting merging imported data with "+sourceHCAFName+" (id:"+sourceHCAFId+")");
			SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Merging);
			long startMerging=System.currentTimeMillis();
			
			session.createLikeTable(targetHCAFTable, sourceHCAFName);
			String insertionQuery="INSERT INTO "+targetHCAFTable+" (Select "+
				sourceHCAFName+".CsquareCode, "+
				sourceHCAFName+".DepthMin, "+		sourceHCAFName+".DepthMax, "+sourceHCAFName+".DepthMean, "+sourceHCAFName+".DepthSD, "+
				sourceHCAFName+".SSTAnMean, "+sourceHCAFName+".SSTAnSD, "+sourceHCAFName+".SSTMnMax, "+sourceHCAFName+".SSTMnMin, "+sourceHCAFName+".SSTMnRange, "+sourceHCAFName+".SBTAnMean, "+
				sourceHCAFName+".SalinityMean, "+	sourceHCAFName+".SalinitySD, "+sourceHCAFName+".SalinityMax, "+sourceHCAFName+".SalinityMin, "+sourceHCAFName+".SalinityBMean, "+
				appTable+".PrimProdMean, "+
				sourceHCAFName+".IceConAnn, "+		sourceHCAFName+".IceConSpr, "+sourceHCAFName+".IceConSum, "+sourceHCAFName+".IceConFal, "+sourceHCAFName+".IceConWin "+
				"from "+sourceHCAFName+" inner join "+appTable+" on "+sourceHCAFName+".CsquareCode = "+appTable+".CsquareCode )";
			logger.trace("Query is "+insertionQuery);
			session.executeUpdate(insertionQuery);
			long mergedCount=session.getTableCount(targetHCAFTable);
			logger.trace("merged "+mergedCount+" records in "+(System.currentTimeMillis()-startMerging)+" ms");
			
			//**************** Register
			logger.trace("Registering new HCAF table "+targetHCAFName);
			SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Registering);
			String author=SourceGenerationManager.getAuthor(requestId);
			int generatedHCAFId=SourceManager.registerSource(SourceType.HCAF, targetHCAFTable, "Imported Clorophyl data", author, sourceHCAFId, SourceType.HCAF);
			SourceManager.setTableTitle(SourceType.HCAF, generatedHCAFId, targetHCAFName);
			SourceGenerationManager.setGeneratedHCAFId(requestId, generatedHCAFId);
			SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Complete);
			logger.trace("Complete generation process for request "+requestId);		
		}catch(Exception e){
			try {
				SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Error);
				logger.error("Something went wrong ",e);
			} catch (Exception e1) {
				logger.error("unexpected error",e1);
			}	
		}finally{
//			File f=new File(sourcesFileName);
//			if(f.exists()) FileUtils.delete(f);
//			f=new File(csvFileName);
//			if(f.exists()) FileUtils.delete(f);
//			try{
//				session.dropTable(appTable);
//				session.close();
//			}catch(Exception e){logger.error("unable to close session and/or delete temp tables");}
		}
	}
	
	private void importCSVToAPP(String appTableName, final String csvFile)throws Exception{
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(',');
		processor.setHasHeader(true);		
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
		final PreparedStatement ps = session.getPreparedStatementForInsert(2, appTableName);				
		Reader reader= new InputStreamReader(new FileInputStream(csvFile), Charset.defaultCharset());
		processor.processStream(reader , new CSVLineProcessor(){
			int csquareCodeIndex=0;
			int meanValueIndex=0;
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {				
				try{
					ps.setString(1, arg1.get(csquareCodeIndex));				
					ps.setFloat(2, Float.parseFloat(arg1.get(meanValueIndex)));
					ps.executeUpdate();
				}catch(Exception e){
					logger.warn("Unable to complete insertion from file "+csvFile, e);					
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {
				for(int i=0;i<arg1.size();i++){
					if (arg1.get(i).equalsIgnoreCase("csquarecode")) csquareCodeIndex=i;
					if (arg1.get(i).equalsIgnoreCase("ClorophyllA_avg")) meanValueIndex=i;
				}
			}});
		
		}catch(Exception e){throw e;}
		finally{session.close();}	
		
	}
	
	
	
}

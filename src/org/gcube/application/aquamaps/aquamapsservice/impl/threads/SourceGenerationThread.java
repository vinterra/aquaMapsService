package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.SourceGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.common.core.utils.logging.GCUBELog;


public class SourceGenerationThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(SourceGenerationThread.class);
	
	
	private int requestId;
	
	public SourceGenerationThread(int id) {
		requestId=id;
	}
	
	@Override
	public void run() {	
		File dir=new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+"SourceGeneration");
		dir.mkdirs();
		String sourcesFileName=dir.getAbsolutePath()+File.separator+ServiceUtils.generateId("", ".txt");
		String outputFileName=ServiceUtils.generateId("", "");
		String csvFileName=outputFileName+".csv";
		String appTable=ServiceUtils.generateId("hcafapp", "");
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
			
			SourceGeneratorRequest req= new SourceGeneratorRequest(sourcesFileName,outputFileName,ResourceType.HCAF);
			
			//*************** IMPORT
			
			logger.trace("Requesting generator");
			SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Importing);
			
			logger.trace("Generation reuslt ="+GeneratorManager.requestGeneration(req));
			//!!!!!!!!! Standalone application always adds ".txt" to outputfile
			
			csvFileName=System.getenv("GLOBUS_LOCATION")+File.separator+csvFileName;
			outputFileName=System.getenv("GLOBUS_LOCATION")+File.separator+outputFileName;
			
			logger.trace("Expected output file : "+csvFileName);
			if(!(new File(csvFileName).exists())) throw new Exception("CSV Generation output file not found "+csvFileName);
			
			
			
			// ************* LOADING CSV
			
			
			
			
			logger.trace("Creating appTable");
			
			session=DBSession.getInternalDBSession();
			session.createTable(appTable, new String[]{
					"CsquareCode varchar(10)",
					"PrimProdMean int(11)",
					"primary key (CsquareCode)"
			});
			logger.trace("Importing data from "+csvFileName);
			long startImport=System.currentTimeMillis();
			importCSVToAPP(appTable, csvFileName);
			long insertedCount=session.getTableCount(appTable);
			logger.trace("Inserted "+insertedCount+" in "+(System.currentTimeMillis()-startImport)+" ms");
			
			
			//************** MERGING
			
			String targetHCAFName=SourceGenerationManager.getGeneratedHCAFName(requestId);
			String targetHCAFTable=ServiceUtils.generateId("hcaf", "");
			int sourceHCAFId=SourceGenerationManager.getSourceId(requestId);
			String sourceHCAFName=SourceManager.getSourceName(sourceHCAFId);
			
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
				"from "+sourceHCAFName+" INNER JOIN "+appTable+" on "+sourceHCAFName+".CsquareCode = "+appTable+".CsquareCode )";
			logger.trace("Query is "+insertionQuery);
			session.executeUpdate(insertionQuery);
			long mergedCount=session.getTableCount(targetHCAFTable);
			logger.trace("merged "+mergedCount+" records in "+(System.currentTimeMillis()-startMerging)+" ms");
			
			//**************** Register
			logger.trace("Registering new HCAF table "+targetHCAFName);
			SourceGenerationManager.setStatus(requestId, SourceGenerationStatus.Registering);
//			String author=SourceGenerationManager.getAuthor(requestId);
			
			//FIXME COMPLETE REGISTRATION
//			Resource resource= new Resource(ResourceType.HCAF,0);
//			resource.setTableName(targetHCAFTable);
//			resource.setTitle(targetHCAFName);
//			resource.setDescription("Imported Clorophyll data");
//			
//			int generatedHCAFId=SourceManager.registerSource(ResourceType.HCAF, targetHCAFTable, "Imported Clorophyl data", author, sourceHCAFId, ResourceType.HCAF);
//			SourceManager.setTableTitle(ResourceType.HCAF, generatedHCAFId, targetHCAFName);
//			SourceGenerationManager.setGeneratedHCAFId(requestId, generatedHCAFId);
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
			File f=new File(sourcesFileName);
			if(f.exists()) FileUtils.delete(f);
			f=new File(csvFileName);
			if(f.exists()) FileUtils.delete(f);
			f=new File(outputFileName);
			if(f.exists()) FileUtils.delete(f);
			try{
				session.dropTable(appTable);
				if(session!=null) session.close();
			}catch(Exception e){logger.error("unable to close session and/or delete temp tables");}
		}
	}
	
	private void importCSVToAPP(String appTableName, final String csvFile)throws Exception{
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(',');
		processor.setHasHeader(true);		
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> columns= new ArrayList<Field>();
			columns.add(new Field("csquarecode","",FieldType.STRING));
			columns.add(new Field("mean","",FieldType.STRING));
			
			
		final PreparedStatement ps = session.getPreparedStatementForInsert(columns, appTableName);				
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
					if (arg1.get(i).equalsIgnoreCase("mean")) meanValueIndex=i;
				}
			}});
		
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}	
		
	}
	
	
	
}

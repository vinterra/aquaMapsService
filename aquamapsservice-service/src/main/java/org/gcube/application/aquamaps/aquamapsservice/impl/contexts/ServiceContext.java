package org.gcube.application.aquamaps.aquamapsservice.impl.contexts;

import java.io.File;
import java.io.IOException;

import org.gcube.application.aquamaps.aquamapsservice.impl.config.Configuration;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.FileSetUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesReader;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.common.core.contexts.GCUBEServiceContext;



public class ServiceContext extends GCUBEServiceContext {

	public enum FOLDERS{
		SERIALIZED,CLUSTERS,IMPORTS,TABLES,ANALYSIS
	}
	
	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	
	private ServiceContext(){};
	
	
	
	/** {@inheritDoc} */
	@Override
	protected String getJNDIName() {return "gcube/application/aquamaps/aquamapsservice";}

	
		//********PUBLISHER
	private Publisher publisher;
	private Configuration configuration;
	
	@Override
	protected void onReady() throws Exception{
		
		super.onReady();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void onInitialisation()throws Exception{
		logger.trace("Initializing AquaMaps Service Context...");
		super.onInitialisation();
		
		//TODO move to deletion Thread
		try{
			ServiceUtils.deleteFile(FileSetUtils.getTempMapsFolder());
		}catch(Exception e){
			logger.fatal("Unable to clean temp maps folder",e);
			throw e;
		}
		
//		try{
////			//Monitoring
//			StatusMonitorThread t=new StatusMonitorThread(getPropertyAsInteger(PropertiesConstants.MONITOR_INTERVAL),
//					getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD));
//			logger.debug("Staring monitor thread: interval = "+getPropertyAsInteger(PropertiesConstants.MONITOR_INTERVAL)+
//					"; freespaceThreshold="+getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD));
//			t.start();
//		}catch(Exception e){
//			logger.fatal("Unable to start disk monitoring",e);
//			throw e;
//		}
//			
//		
//		try{
//			JobExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_OBJECTS));
//			TableGenerationExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_HSPEC_REQUESTS),getPropertyAsInteger(PropertiesConstants.PROGRESS_MONITOR_INTERVAL_SEC));
//			AnalysisManager.init(true,getPropertyAsInteger(PropertiesConstants.PROGRESS_MONITOR_INTERVAL_SEC));
//			SourceManager.checkTables();
//		}catch(Exception e){
//			logger.fatal("Unable to start managers",e);
//		}
//		
//		try{
//			DeletionMonitor t=new DeletionMonitor(5000);
//			t.start();
//			logger.info("Deletion Monitor started");
//		}catch(Exception e){
//			logger.fatal("Unable to start Deletion Monitor ",e);
//		}
		
	}
	
	
	@Override
    protected void onShutdown() throws Exception {
        PublisherManager.shutdown();
        super.onShutdown();
        
    }
    @Override
    protected void onFailure() throws Exception {
        // TODO Auto-generated method stub
        super.onFailure();
        
    }
    
    public String getProperty(String paramName)throws Exception{
    	return PropertiesReader.get(this.getFile("config.properties", false).getAbsolutePath()).getParam(paramName);
    }

    public Boolean getPropertyAsBoolean(String propertyName)throws Exception{
    	return Boolean.parseBoolean(getProperty(propertyName));
    }
    
    public Integer getPropertyAsInteger(String propertyName) throws Exception{
    	return Integer.parseInt(getProperty(propertyName));
    }
    public Double getPropertyAsDouble(String propertyName) throws Exception{
    	return Double.parseDouble(getProperty(propertyName));
    }
    
   public Publisher getPublisher() {
	return publisher;
   }
    
	
	public File getEcoligicalConfigDir(){
		return this.getFile("generator", false);
	}
	
	
	
	
	public String getFolderPath(FOLDERS folderName){
		String persistencePath = ServiceContext.getContext().getPersistenceRoot().getAbsolutePath()+File.separator+folderName;
		File f=new File(persistencePath);
		if(!f.exists()){
			logger.debug("Creating persistence folder "+persistencePath);
			f.mkdirs();
			try {
				Process proc=Runtime.getRuntime().exec("chmod -R 777 "+persistencePath);
				try{
					proc.waitFor();
				}catch(InterruptedException e){
					int exitValue=proc.exitValue();
					logger.debug("Permission execution exit value = "+exitValue);
				}
			} catch (IOException e) {
				logger.warn("Unexpected Exception", e);
			}
		}
		return persistencePath;
	}
	
	public Configuration getConfiguration(){
		return configuration;
	}
}

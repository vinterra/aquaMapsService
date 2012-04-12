package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis.AnalysisManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.TableGenerationExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.monitor.StatusMonitorThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesReader;
import org.gcube.application.aquamaps.enabling.Configuration;
import org.gcube.application.aquamaps.enabling.Impl.ConfigurationImpl;
import org.gcube.application.aquamaps.enabling.Impl.crawler.CrawlerMode;
import org.gcube.application.aquamaps.enabling.model.DBDescriptor;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.PublisherConfiguration;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.scope.GCUBEScope;



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
	private GCUBEScope configurationScope;
	
	@Override
	protected void onReady() throws Exception{
		
		
//		
//		//Monitoring
		StatusMonitorThread t=new StatusMonitorThread(getPropertyAsInteger(PropertiesConstants.MONITOR_INTERVAL),
				getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD));
		logger.debug("Staring monitor thread: interval = "+getPropertyAsInteger(PropertiesConstants.MONITOR_INTERVAL)+
				"; freespaceThreshold="+getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD));
		t.start();
		
//		logger.trace("Starting hspec group request monitor..");
//		HSPECGroupMonitor t3=new HSPECGroupMonitor(4*1000);
//		t3.start();
//		
//		
		
		JobExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_OBJECTS));
		TableGenerationExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_HSPEC_REQUESTS),getPropertyAsInteger(PropertiesConstants.PROGRESS_MONITOR_INTERVAL_SEC));
		AnalysisManager.init(true,getPropertyAsInteger(PropertiesConstants.PROGRESS_MONITOR_INTERVAL_SEC));
		
		SourceManager.checkTables();
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public void onInitialisation()throws Exception{
		
		try{
			int interval=Integer.parseInt(getProperty(PropertiesConstants.ISCRAWLER_INTERVAL_MINUTES));
			logger.debug("Interval time is "+interval);
			configuration=ConfigurationImpl.get(CrawlerMode.SERVICE, interval);			
			configurationScope=configuration.getAvailableScopes().iterator().next();
			if(configurationScope==null) throw new Exception ("NO valid scope found");
			logger.trace("Configuration Scope will be "+configurationScope);
		}catch (Exception e){
			logger.fatal("Unable to init configuration",e);
			throw e;
		}
		
		
		try{
			DBDescriptor publisherDB=configuration.getPublisherDataBase(configurationScope);
			logger.debug("Publisher Database is "+publisherDB);
			publisher=Publisher.getPublisher();
			PublisherConfiguration config= new PublisherConfiguration(
					publisherDB.getEntryPoint(),
					publisherDB.getUser(),
					publisherDB.getPassword(),
					getPersistenceRoot(),
					(String) this.getProperty("httpServerBasePath", true),
					Integer.parseInt((String)this.getProperty("httpServerPort",true))
					);			
			publisher.initialize(config);
			
		}catch(Exception e){
			logger.fatal("Unable to initiate Publisher library ",e);
			throw e;
		}
		
		
			
	}
	
	
	@Override
    protected void onShutdown() throws Exception {
        try{
        	publisher.shutdown();
        }catch(Exception e){
        	logger.fatal("Unable to shutdown publisher ",e);
        }
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
		if(!f.exists())f.mkdirs();
		return persistencePath;
	}
	
	public Configuration getConfiguration(){
		return configuration;
	}
	
	public GCUBEScope getConfigurationScope(){
		return configurationScope;
	}
}

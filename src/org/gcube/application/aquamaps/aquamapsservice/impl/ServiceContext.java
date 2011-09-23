package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.TableGenerationExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.monitor.StatusMonitorThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.ConnectedPublisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.DummyPublisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.EmbeddedPublisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesReader;
import org.gcube.common.core.contexts.GCUBEServiceContext;



public class ServiceContext extends GCUBEServiceContext {

	
	
	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
	
	
	
	/** {@inheritDoc} */
	protected String getJNDIName() {return "gcube/application/aquamaps/aquamapsservice";}

	
		//********PUBLISHER
	private Publisher publisher;

	
	

	protected void onReady() throws Exception{
		
		
		
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
		SourceManager.checkTables();
	}
	
	/**
     * {@inheritDoc}
     */
	public void onInitialisation(){
		
		
		try{
			if(getPropertyAsBoolean(PropertiesConstants.USE_DUMMY_PUBLISHER)){
				logger.trace("Publisher is DummyPublisher");			
				setPublisher(new DummyPublisher());
			}else if(getPropertyAsBoolean(PropertiesConstants.STANDALONE_MODE)){
				logger.trace("Publisher is Embedded");
				setPublisher(new EmbeddedPublisher(
						getPersistenceRoot().getAbsolutePath(),
						getPublisherConfigDir().getAbsolutePath()+File.separator,						
						(String) this.getProperty("httpServerBasePath", true),
						Integer.parseInt((String)this.getProperty("httpServerPort",true))));
				}else {
					logger.trace("Pubilsher is connected");
					setPublisher(ConnectedPublisher.getPublisher());
				}
			
		}catch(Exception e){
			logger.fatal("Unable to initiate Publisher library ",e);
		}
		
		
			
	}
	
	
	@Override
    protected void onShutdown() throws Exception {
        // TODO Auto-generated method stub
        super.onShutdown();
        if(!getPropertyAsBoolean(PropertiesConstants.USE_DUMMY_PUBLISHER)&&getPropertyAsBoolean(PropertiesConstants.STANDALONE_MODE))
        	EmbeddedPublisher.stop();
    }
    @Override
    protected void onFailure() throws Exception {
        // TODO Auto-generated method stub
        super.onFailure();
        if(!getPropertyAsBoolean(PropertiesConstants.USE_DUMMY_PUBLISHER)&&getPropertyAsBoolean(PropertiesConstants.STANDALONE_MODE))
        	EmbeddedPublisher.stop();
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
    
    public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public Publisher getPublisher() {
		return publisher;
	}
    
	public File getPublisherConfigDir(){
		return ServiceContext.getContext().getFile("publisher", false);
	}
	public File getEcoligicalConfigDir(){
		return ServiceContext.getContext().getFile("generator", false);
	} 
}

package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.EnvironmentalStatusUpdateThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.HSPECGroupMonitor;
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

//	private String httpServerBasePath; 
//	private int httpServerPort;
	
//	private String webServerUrl=null;
//	private String internaldbUsername;
//	private String internaldbPassword;
//	private DBType internalDBType;
//	private String internalDBName;
//	private String internalDBPort;
//	private String internalDBHost;
//	
//	
//	private int queueSize;
//	private int coreSize;
//	private int maxSize;
//	private long waitIdleTime;
//	
//	
//	private  String postGis_dbtype;
//	private  String postGis_host;
//	private  String postGis_port;
//	private  String postGis_database;
//	private  String postGis_user; 
//	private  String postGis_passwd;
//	
//	
//	private String worldTable;
//	private String geoServerUrl;
//	private String geoServerUser;
//	private String geoServerPwd;
//	private String geoServerWorkspace;
//
//	private String templateGroup;
//	
//	private String distributionDefaultStyle;
//	
//	private boolean GISMode;
//	private boolean standAloneMode;
//	private boolean useDummyPublisher;
//	private boolean useEnvironmentModelingLib;
//	private boolean enableScriptLogging;
//	private boolean postponeSubmission;
//	
//	private String defaultPublisherUrl;
//	
//	
//	
//	
//	
//	private long monitorInterval;
//	private long monitorThreshold;
//	
//	//************ ALGORITHM
//	
//	private boolean evaluateDepth;
//	private boolean evaluateTemperature;
//	private boolean evaluateSalinity;
//	private boolean evaluatePrimaryProduction;
//	private boolean evaluateIceConcentration;
//	private boolean evaluateLandDistance;
//	
//	
//	//************ DEFAULT VALUES FOR DB
//	
//	private String integerDefault=null;
//	private String doubleDefault=null;
//	private String booleanDefault=null;
//	
//	//************ DEFAULT SOURCES
//	
//	private Integer defaultHSPENID=null;
//	private Integer defaultHSPECID=null;
//	private Integer defaultHCAFID=null;
//	
//	
//	//************ Environmental library config
//	
//	private Integer BATCH_POOL_SIZE=null;
//	
	

	protected void onReady() throws Exception{
		
		
//		File serverPathDir= new File(this.getPersistenceRoot()+File.separator+httpServerBasePath);
//		if(!serverPathDir.exists())
//			serverPathDir.mkdirs();
//				
//		webServerUrl="http://"+GHNContext.getContext().getHostname()+":"+httpServerPort+"/";
//		logger.debug("WEBSERVER URL: "+this.webServerUrl);
//
//		//initializing jetty
//		Connector connector = new SelectChannelConnector();
//		connector.setPort(httpServerPort);
//		Server server = new Server(httpServerPort);
//		server.setConnectors(new Connector[]{connector});
//		ResourceHandler resourceHandler = new ResourceHandler();
//		resourceHandler.setResourceBase(serverPathDir.getAbsolutePath());
//		try {
//			logger.debug("HTTP Server Base Path : " + resourceHandler.getBaseResource().getFile().getAbsolutePath());
//		} catch (IOException e) {
//			logger.error(e);
//		}
//		server.setHandler(resourceHandler);
//		//starting the web server
//		server.start();
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
		TableGenerationExecutionManager.init(getPropertyAsBoolean(PropertiesConstants.PURGE_PENDING_HSPEC_REQUESTS));
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
						ServiceContext.getContext().getFile("publisher", false).getAbsolutePath()+File.separator,						
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
    
}

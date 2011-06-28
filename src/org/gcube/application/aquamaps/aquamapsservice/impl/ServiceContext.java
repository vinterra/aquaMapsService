package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.aquamapsservice.impl.monitor.StatusMonitorThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.ConnectedPublisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.DummyPublisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.EmbeddedPublisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.EnvironmentalStatusUpdateThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.HSPECGroupGenerationManagerThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.SubmittedMonitorThread;
import org.gcube.common.core.contexts.GCUBEServiceContext;



public class ServiceContext extends GCUBEServiceContext {

	
	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
	
	private String httpServerBasePath; 
	private int httpServerPort;
	
	
	/** {@inheritDoc} */
	protected String getJNDIName() {return "gcube/application/aquamaps/aquamapsservice";}

	
	private String webServerUrl=null;
	private String internaldbUsername;
	private String internaldbPassword;
	private DBType internalDBType;
	private String internalDBName;
	private String internalDBPort;
	private String internalDBHost;
	
	
	private int queueSize;
	private int coreSize;
	private int maxSize;
	private long waitIdleTime;
	
	
	private  String postGis_dbtype;
	private  String postGis_host;
	private  String postGis_port;
	private  String postGis_database;
	private  String postGis_user; 
	private  String postGis_passwd;
	
	
	private String worldTable;
	private String geoServerUrl;
	private String geoServerUser;
	private String geoServerPwd;
	private String geoServerWorkspace;

	private String templateGroup;
	
	private String distributionDefaultStyle;
	
	private boolean GISMode;
	private boolean standAloneMode;
	private boolean useDummyPublisher;
	private boolean useEnvironmentModelingLib;
	private boolean enableScriptLogging;
	private boolean postponeSubmission;
	
	private String defaultPublisherUrl;
	
	
	//********PUBLISHER
	private Publisher publisher;
	
	
	
	private long monitorInterval;
	private long monitorThreshold;
	
	//************ ALGORITHM
	
	private boolean evaluateDepth;
	private boolean evaluateTemperature;
	private boolean evaluateSalinity;
	private boolean evaluatePrimaryProduction;
	private boolean evaluateIceConcentration;
	private boolean evaluateLandDistance;
	
	
	//************ DEFAULT VALUES FOR DB
	
	private String integerDefault=null;
	private String doubleDefault=null;
	private String booleanDefault=null;
	
	//************ DEFAULT SOURCES
	
	private Integer defaultHSPENID=null;
	private Integer defaultHSPECID=null;
	private Integer defaultHCAFID=null;
	
	
	//************ Environmental library config
	
	private Integer BATCH_POOL_SIZE=null;
	
	
	public Integer getDefaultHSPENID() {
		return defaultHSPENID;
	}

	public void setDefaultHSPENID(Integer defaultHSPENID) {
		this.defaultHSPENID = defaultHSPENID;
	}

	public Integer getDefaultHSPECID() {
		return defaultHSPECID;
	}

	public void setDefaultHSPECID(Integer defaultHSPECID) {
		this.defaultHSPECID = defaultHSPECID;
	}

	public Integer getDefaultHCAFID() {
		return defaultHCAFID;
	}

	public void setDefaultHCAFID(Integer defaultHCAFID) {
		this.defaultHCAFID = defaultHCAFID;
	}

	public String getIntegerDefault() {
		return integerDefault;
	}

	public String getDoubleDefault() {
		return doubleDefault;
	}

	public String getBooleanDefault() {
		return booleanDefault;
	}

	public String getDefaultPublisherUrl() {
		return defaultPublisherUrl;
	}

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
		StatusMonitorThread t=new StatusMonitorThread(monitorInterval,monitorThreshold);
		logger.debug("Staring monitor thread: interval = "+monitorInterval+"; freespaceThreshold="+monitorThreshold);
		t.start();
		
		logger.trace("Starting submitted monitor..");
		SubmittedMonitorThread t2=new SubmittedMonitorThread(getPersistenceRoot()+File.separator+"JOBS", 4*1000);
		t2.start();
		logger.trace("Starting hspec group request monitor..");
		HSPECGroupGenerationManagerThread t3=new HSPECGroupGenerationManagerThread(4*1000);
		t3.start();
		
		logger.trace("Starting hspec group request status updater..");
		EnvironmentalStatusUpdateThread t4=new EnvironmentalStatusUpdateThread(2*1000);
		t4.start();
		
	}
	
	/**
     * {@inheritDoc}
     */
	public void onInitialisation(){
		
		try{
			Properties prop= new Properties();
			logger.debug("Loading configuration File...");
			prop.load(new FileInputStream(this.getFile("config.properties", false)));
			logger.debug("Found properties : "+prop.toString());
			
			this.internaldbUsername=prop.getProperty("internaldbusername","").trim();
			this.internaldbPassword=prop.getProperty("internaldbpassword","").trim();
			this.setInternalDBType(DBType.valueOf(prop.getProperty("internaldbType","mySql").trim()));
			this.setInternalDBName(prop.getProperty("internaldbName","").trim());
			this.setInternalDBHost(prop.getProperty("internaldbHost","").trim());
			this.setInternalDBPort(prop.getProperty("internaldbPort","").trim());
			
			this.coreSize=Integer.parseInt(prop.getProperty("coreSize","30").trim());
			this.queueSize=Integer.parseInt(prop.getProperty("queueSize","1000").trim());
			this.maxSize=Integer.parseInt(prop.getProperty("maxSize","50").trim());
			this.waitIdleTime=Long.parseLong(prop.getProperty("waitIdleTime","30000").trim());

			this.postGis_database=prop.getProperty("postGis_database","").trim();
			this.postGis_dbtype=prop.getProperty("postGis_dbtype","").trim();
			this.postGis_host=prop.getProperty("postGis_host","").trim();
			this.postGis_passwd=prop.getProperty("postGis_passwd","").trim();
			this.postGis_port=prop.getProperty("postGis_port","").trim();
			this.postGis_user=prop.getProperty("postGis_user","").trim();
			this.geoServerUrl=prop.getProperty("geoServerUrl", "").trim();
			this.geoServerUser=prop.getProperty("geoServerUser", "").trim();
			this.geoServerPwd=prop.getProperty("geoServerPwd", "").trim();
			this.worldTable=prop.getProperty("worldTable", "").trim();
			this.setGeoServerWorkspace(prop.getProperty("geoServerWorkspace").trim());
			this.templateGroup=prop.getProperty("templateGroup", "").trim();
			this.distributionDefaultStyle=prop.getProperty("distributionDefaultStyle","").trim();
			this.setGISMode(Boolean.parseBoolean(prop.getProperty("GISMode", "false").trim()));
			this.setStandAloneMode(Boolean.parseBoolean(prop.getProperty("StandAloneMode", "false").trim()));
			this.defaultPublisherUrl=prop.getProperty("DEFAULT_PUBLISHER_URL","").trim();
		
			this.httpServerBasePath =prop.getProperty("httpServerBasePath", "").trim();
					
			this.httpServerPort = Integer.parseInt(prop.getProperty("httpServerPort","").trim());
			
			this.monitorInterval=Long.parseLong(prop.getProperty("monitorInterval", "").trim());
			this.monitorThreshold=Long.parseLong(prop.getProperty("freeSpaceThreshold", "").trim());
			
			this.setEvaluateDepth(Boolean.parseBoolean(prop.getProperty("evaluateDepth","true").trim()));
			this.setEvaluateTemperature(Boolean.parseBoolean(prop.getProperty("evaluateTemperature","true").trim()));
			this.setEvaluateSalinity(Boolean.parseBoolean(prop.getProperty("evaluateSalinity","true").trim()));
			this.setEvaluatePrimaryProduction(Boolean.parseBoolean(prop.getProperty("evaluatePrimaryProduction","true").trim()));
			this.setEvaluateIceConcentration(Boolean.parseBoolean(prop.getProperty("evaluateIceConcentration","true").trim()));
			this.setEvaluateLandDistance(Boolean.parseBoolean(prop.getProperty("evaluateLandDistance","false").trim()));
			
			
			this.booleanDefault=(prop.getProperty("booleanDefaultValue").trim());
			this.doubleDefault=(prop.getProperty("doubleDefaultValue").trim());
			this.integerDefault=(prop.getProperty("integerDefaultValue").trim());
			
			
			this.setDefaultHCAFID(Integer.parseInt(prop.getProperty("defaultHCAFID").trim()));
			this.setDefaultHSPECID(Integer.parseInt(prop.getProperty("defaultHSPECID").trim()));
			this.setDefaultHSPENID(Integer.parseInt(prop.getProperty("defaultHSPENID").trim()));
			
			this.setUseDummyPublisher(Boolean.parseBoolean(prop.getProperty("useDummyPublisher").trim()));
			this.setUseEnvironmentModelingLib(Boolean.parseBoolean(prop.getProperty("useEnvironmentModelingLib").trim()));
			this.setEnableScriptLogging(Boolean.parseBoolean(prop.getProperty("enableScriptLogging").trim()));
			this.setPostponeSubmission(Boolean.parseBoolean(prop.getProperty("postponeSubmission").trim()));
			this.setBATCH_POOL_SIZE(Integer.parseInt(prop.getProperty("BATCH_POOL_SIZE").trim()));
		}catch(Exception e){
			logger.fatal("Unable to load properties ",e);
		}
			
		try{
			if(isUseDummyPublisher()){
				logger.trace("Publisher is DummyPublisher");			
				setPublisher(new DummyPublisher());
			}else if(isStandAloneMode()){
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
	
	
	public String getWebServiceURL(){
		return this.webServerUrl;
	}

	public String getHttpServerBasePath() {
		return httpServerBasePath;
	}

	public String getInternalDbUsername() {
		return internaldbUsername;
	}

	public String getInternalDbPassword() {
		return internaldbPassword;
	}

	/**
	 * @return the queueSize
	 */
	public int getQueueSize() {
		return queueSize;
	}

	/**
	 * @param queueSize the queueSize to set
	 */
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	/**
	 * @return the coreSize
	 */
	public int getCoreSize() {
		return coreSize;
	}

	/**
	 * @param coreSize the coreSize to set
	 */
	public void setCoreSize(int coreSize) {
		this.coreSize = coreSize;
	}

	/**
	 * @return the maxSize
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * @return the waitIdleTime
	 */
	public long getWaitIdleTime() {
		return waitIdleTime;
	}

	/**
	 * @param waitIdleTime the waitIdleTime to set
	 */
	public void setWaitIdleTime(long waitIdleTime) {
		this.waitIdleTime = waitIdleTime;
	}

	/**
	 * @return the postGis_dbtype
	 */
	public String getPostGis_dbtype() {
		return postGis_dbtype;
	}

	/**
	 * @param postGis_dbtype the postGis_dbtype to set
	 */
	public void setPostGis_dbtype(String postGis_dbtype) {
		this.postGis_dbtype = postGis_dbtype;
	}

	/**
	 * @return the postGis_host
	 */
	public String getPostGis_host() {
		return postGis_host;
	}

	/**
	 * @param postGis_host the postGis_host to set
	 */
	public void setPostGis_host(String postGis_host) {
		this.postGis_host = postGis_host;
	}

	/**
	 * @return the postGis_port
	 */
	public String getPostGis_port() {
		return postGis_port;
	}

	/**
	 * @param postGis_port the postGis_port to set
	 */
	public void setPostGis_port(String postGis_port) {
		this.postGis_port = postGis_port;
	}

	/**
	 * @return the postGis_database
	 */
	public String getPostGis_database() {
		return postGis_database;
	}

	/**
	 * @param postGis_database the postGis_database to set
	 */
	public void setPostGis_database(String postGis_database) {
		this.postGis_database = postGis_database;
	}

	/**
	 * @return the postGis_user
	 */
	public String getPostGis_user() {
		return postGis_user;
	}

	/**
	 * @param postGis_user the postGis_user to set
	 */
	public void setPostGis_user(String postGis_user) {
		this.postGis_user = postGis_user;
	}

	/**
	 * @return the postGis_passwd
	 */
	public String getPostGis_passwd() {
		return postGis_passwd;
	}

	/**
	 * @param postGis_passwd the postGis_passwd to set
	 */
	public void setPostGis_passwd(String postGis_passwd) {
		this.postGis_passwd = postGis_passwd;
	}

	public void setGeoServerUrl(String geoServerUrl) {
		this.geoServerUrl = geoServerUrl;
	}

	public String getGeoServerUrl() {
		return geoServerUrl;
	}

	/**
	 * @return the worldTable
	 */
	public String getWorldTable() {
		return worldTable;
	}

	/**
	 * @param worldTable the worldTable to set
	 */
	public void setWorldTable(String worldTable) {
		this.worldTable = worldTable;
	}

	/**
	 * @return the templateGroup
	 */
	public String getTemplateGroup() {
		return templateGroup;
	}

	/**
	 * @param templateGroup the templateGroup to set
	 */
	public void setTemplateGroup(String templateGroup) {
		this.templateGroup = templateGroup;
	}

	public void setDistributionDefaultStyle(String distributionDefaultStyle) {
		this.distributionDefaultStyle = distributionDefaultStyle;
	}

	public String getDistributionDefaultStyle() {
		return distributionDefaultStyle;
	}

	public void setGISMode(boolean gISMode) {
		GISMode = gISMode;
	}

	public boolean isGISMode() {
		return GISMode;
	}
	
	public String getGeoServerUser() {
		return geoServerUser;
	}

	public void setGeoServerUser(String geoServerUser) {
		this.geoServerUser = geoServerUser;
	}

	public String getGeoServerPwd() {
		return geoServerPwd;
	}

	public void setGeoServerPwd(String geoServerPwd) {
		this.geoServerPwd = geoServerPwd;
	}

	public void setStandAloneMode(boolean standAloneMode) {
		this.standAloneMode = standAloneMode;
	}

	public boolean isStandAloneMode() {
		return standAloneMode;
	}

	public void setInternalDBType(DBType internalDBType) {
		this.internalDBType = internalDBType;
	}

	public DBType getInternalDBType() {
		return internalDBType;
	}

	public void setInternalDBName(String intarnalDBName) {
		this.internalDBName = intarnalDBName;
	}

	public String getInternalDBName() {
		return internalDBName;
	}

	public void setInternalDBPort(String internalDBPort) {
		this.internalDBPort = internalDBPort;
	}

	public String getInternalDBPort() {
		return internalDBPort;
	}

	public void setInternalDBHost(String internalDBHost) {
		this.internalDBHost = internalDBHost;
	}

	public String getInternalDBHost() {
		return internalDBHost;
	}

	public boolean isEvaluateDepth() {
		return evaluateDepth;
	}

	public void setEvaluateDepth(boolean evaluateDepth) {
		this.evaluateDepth = evaluateDepth;
	}

	public boolean isEvaluateTemperature() {
		return evaluateTemperature;
	}

	public void setEvaluateTemperature(boolean evaluateTemperature) {
		this.evaluateTemperature = evaluateTemperature;
	}

	public boolean isEvaluateSalinity() {
		return evaluateSalinity;
	}

	public void setEvaluateSalinity(boolean evaluateSalinity) {
		this.evaluateSalinity = evaluateSalinity;
	}

	public boolean isEvaluatePrimaryProduction() {
		return evaluatePrimaryProduction;
	}

	public void setEvaluatePrimaryProduction(boolean evaluatePrimaryProduction) {
		this.evaluatePrimaryProduction = evaluatePrimaryProduction;
	}

	public boolean isEvaluateIceConcentration() {
		return evaluateIceConcentration;
	}

	public void setEvaluateIceConcentration(boolean evaluateIceConcentration) {
		this.evaluateIceConcentration = evaluateIceConcentration;
	}

	public boolean isEvaluateLandDistance() {
		return evaluateLandDistance;
	}

	public void setEvaluateLandDistance(boolean evaluateLandDistance) {
		this.evaluateLandDistance = evaluateLandDistance;
	}

	public void setUseDummyPublisher(boolean useDummyPublisher) {
		this.useDummyPublisher = useDummyPublisher;
	}

	public boolean isUseDummyPublisher() {
		return useDummyPublisher;
	}

	public void setUseEnvironmentModelingLib(boolean useEnvironmentModelingLib) {
		this.useEnvironmentModelingLib = useEnvironmentModelingLib;
	}

	public boolean isUseEnvironmentModelingLib() {
		return useEnvironmentModelingLib;
	}

	public void setEnableScriptLogging(boolean enableScriptLogging) {
		this.enableScriptLogging = enableScriptLogging;
	}

	public boolean isEnableScriptLogging() {
		return enableScriptLogging;
	}

	public void setPostponeSubmission(boolean postponeSubmission) {
		this.postponeSubmission = postponeSubmission;
	}

	public boolean isPostponeSubmission() {
		return postponeSubmission;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public Publisher getPublisher() {
		return publisher;
	}
		
	
	@Override
    protected void onShutdown() throws Exception {
        // TODO Auto-generated method stub
        super.onShutdown();
        if(!isUseDummyPublisher()&&isStandAloneMode())
        	EmbeddedPublisher.stop();
    }
    @Override
    protected void onFailure() throws Exception {
        // TODO Auto-generated method stub
        super.onFailure();
        if(!isUseDummyPublisher()&&isStandAloneMode())
        	EmbeddedPublisher.stop();
    }

	public void setGeoServerWorkspace(String geoServerWorkspace) {
		this.geoServerWorkspace = geoServerWorkspace;
	}

	public String getGeoServerWorkspace() {
		return geoServerWorkspace;
	}

	public void setBATCH_POOL_SIZE(Integer bATCH_POOL_SIZE) {
		BATCH_POOL_SIZE = bATCH_POOL_SIZE;
	}

	public Integer getBATCH_POOL_SIZE() {
		return BATCH_POOL_SIZE;
	}
	
	
}

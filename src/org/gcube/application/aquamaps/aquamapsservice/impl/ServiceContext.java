package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;



public class ServiceContext extends GCUBEServiceContext {

	
	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
	
	private String httpServerBasePath; 
	
	/** {@inheritDoc} */
	protected String getJNDIName() {return "gcube/application/aquamaps";}

	
	private String webServerUrl=null;
	private String dbUsername;
	private String dbPassword;
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
	

	private String templateGroup;
	
	private String distributionDefaultStyle;
	
	private boolean GISMode;
	
	
	private String defaultPublisherUrl;
	
	
	public String getDefaultPublisherUrl() {
		return defaultPublisherUrl;
	}

	protected void onReady() throws Exception{
		
		
		//taking jetty parameters
		httpServerBasePath =(String) this.getProperty("httpServerBasePath", true);
		logger.debug("HTTP Server Base path = " + httpServerBasePath);
		File serverPathDir= new File(this.getPersistenceRoot()+File.separator+httpServerBasePath);
		if(!serverPathDir.exists())
			serverPathDir.mkdirs();
				
		int httpServerPort = Integer.parseInt((String)this.getProperty("httpServerPort",true));
		logger.debug("HTTP Server port = " + httpServerPort);
		webServerUrl="http://"+GHNContext.getContext().getHostname()+":"+httpServerPort+"/";
		logger.debug("WEBSERVER URL: "+this.webServerUrl);
		//initializing jetty
		Connector connector = new SelectChannelConnector();
		connector.setPort(httpServerPort);
		Server server = new Server(httpServerPort);
		server.setConnectors(new Connector[]{connector});
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(serverPathDir.getAbsolutePath());
		try {
			logger.debug("HTTP Server Base Path : " + resourceHandler.getBaseResource().getFile().getAbsolutePath());
		} catch (IOException e) {
			logger.error(e);
		}
		server.setHandler(resourceHandler);
		//starting the web server
		server.start();
	
	}
	
	/**
     * {@inheritDoc}
     */
	public void onInitialisation(){
		try{
			Properties prop= new Properties();
			prop.load(new FileInputStream(this.getFile("dbprop.properties", false)));
			this.dbUsername=prop.getProperty("dbusername","");
			this.dbPassword=prop.getProperty("dbpassword","");
		}catch(Exception e){logger.error("error getting DB credential ",e);}
		try{
			Properties prop= new Properties();
			prop.load(new FileInputStream(this.getFile("pool.properties", false)));
			this.coreSize=Integer.parseInt(prop.getProperty("coreSize","30"));
			this.queueSize=Integer.parseInt(prop.getProperty("queueSize","1000"));
			this.maxSize=Integer.parseInt(prop.getProperty("maxSize","50"));
			this.waitIdleTime=Long.parseLong(prop.getProperty("waitIdleTime","30000"));
		}catch(Exception e){logger.fatal("error getting Thread Pool settings ",e);}
		try{
			Properties prop= new Properties();
			prop.load(new FileInputStream(this.getFile("geoserver.properties", false)));
			this.postGis_database=prop.getProperty("postGis_database","");
			this.postGis_dbtype=prop.getProperty("postGis_dbtype","");
			this.postGis_host=prop.getProperty("postGis_host","");
			this.postGis_passwd=prop.getProperty("postGis_passwd","");
			this.postGis_port=prop.getProperty("postGis_port","");
			this.postGis_user=prop.getProperty("postGis_user","");
			this.geoServerUrl=prop.getProperty("geoServerUrl", "");
			this.geoServerUser=prop.getProperty("geoServerUser", "");
			this.geoServerPwd=prop.getProperty("geoServerPwd", "");
			this.worldTable=prop.getProperty("worldTable", "");
			this.templateGroup=prop.getProperty("templateGroup", "");
			this.distributionDefaultStyle=prop.getProperty("distributionDefaultStyle","");
			this.setGISMode(Boolean.parseBoolean(prop.getProperty("GISMode", "false")));
		}catch(Exception e){logger.fatal("error getting GeoServer information",e);}
		try{
			Properties prop= new Properties();
			prop.load(new FileInputStream(this.getFile("publisher.properties", false)));
			this.defaultPublisherUrl=prop.getProperty("DEFAULT_PUBLISHER_URL","");
		}catch(Exception e){logger.fatal("error getting default Publisher information",e);}
	}
	
	public String getWebServiceURL(){
		return this.webServerUrl;
	}

	public String getHttpServerBasePath() {
		return httpServerBasePath;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
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
		
}

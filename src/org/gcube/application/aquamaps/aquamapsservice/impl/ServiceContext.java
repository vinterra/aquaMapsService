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
		
}

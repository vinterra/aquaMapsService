package org.gcube.application.aquamaps.aquamapsservice.impl.engine.request;

public abstract class RequestManager <T>{

	public abstract void serveRequest(T request) throws Exception;
	
	protected abstract void startMonitor()throws Exception;
	
}

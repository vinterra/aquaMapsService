package org.gcube.application.aquamaps.aquamapsservice.impl.engine;


public interface Generator {

	public void setRequest(GenerationRequest theRequest)throws BadRequestException;
	public boolean getResponse()throws Exception;
	public GenerationRequest getRequest();
}

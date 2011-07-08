package org.gcube.application.aquamaps.aquamapsservice.impl.engine.request;

import org.gcube.application.aquamaps.dataModel.enhanced.Field;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

public class Request<T> {

	private T theRequest=null;
	private boolean isSerialized=false;
	private Field referenceID=null;
	private Semaphore sempahore=null;
	
	public T getTheRequest() {
		return theRequest;
	}


	public void setTheRequest(T theRequest) {
		this.theRequest = theRequest;
	}


	public boolean isSerialized() {
		return isSerialized;
	}


	public void setSerialized(boolean isSerialized) {
		this.isSerialized = isSerialized;
	}


	public Field getReferenceID() {
		return referenceID;
	}


	public void setReferenceID(Field referenceID) {
		this.referenceID = referenceID;
	}


	public Request(T theRequest,Field referenceID) {
		setTheRequest(theRequest);
		setReferenceID(referenceID);
	}
	
	
}

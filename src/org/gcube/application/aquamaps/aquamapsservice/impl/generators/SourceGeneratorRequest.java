package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;

public class SourceGeneratorRequest implements GenerationRequest{
	
	private SourceType sourceType;
	private String parameterName;
	private int requestId;
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public SourceType getSourceType() {
		return sourceType;
	}
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	public SourceGeneratorRequest(int requestId){
		this.setRequestId(requestId);
	}
	
}

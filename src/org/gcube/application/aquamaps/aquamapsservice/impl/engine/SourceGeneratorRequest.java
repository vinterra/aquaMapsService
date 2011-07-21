package org.gcube.application.aquamaps.aquamapsservice.impl.engine;

import org.gcube.application.aquamaps.dataModel.Types.ResourceType;


public class SourceGeneratorRequest implements GenerationRequest{
	
	private ResourceType sourceType;
	private String inputFile;
	private String outputFile;
	
	public SourceGeneratorRequest(String inputFile,String outputFile,ResourceType type) {
		this.inputFile=inputFile;
		this.outputFile=outputFile;
		this.sourceType=type;
	}
	
	public String getInputFile() {
		return inputFile;
	}
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	public ResourceType getSourceType() {
		return sourceType;
	}
	public void setSourceType(ResourceType sourceType) {
		this.sourceType = sourceType;
	}	
	
}

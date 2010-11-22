package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;

public class SourceGeneratorRequest implements GenerationRequest{
	
	private SourceType sourceType;
	private String inputFile;
	private String outputFile;
	
	public SourceGeneratorRequest(String inputFile,String outputFile,SourceType type) {
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
	public SourceType getSourceType() {
		return sourceType;
	}
	public void setSourceType(SourceType sourceType) {
		this.sourceType = sourceType;
	}	
	
}

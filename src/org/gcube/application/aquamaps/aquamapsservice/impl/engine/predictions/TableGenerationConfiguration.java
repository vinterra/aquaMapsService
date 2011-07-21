package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.HashMap;

import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;

public class TableGenerationConfiguration {

	private LogicType logic;
	private AlgorithmType algorithm;
	private HashMap<ResourceType,Resource> sources; 
	
	
	private String submissionBackend;
	private String executionEnvironment;
	private String backendUrl;
	private HashMap<String,String> configuration;
	private int partitionsNumber;
	private String author;
	
	
	public TableGenerationConfiguration(LogicType logic,
			AlgorithmType algorithm,HashMap<ResourceType, Resource> sources, String submissionBackend,
			String executionEnvironment, String backendUrl,
			HashMap<String, String> configuration, int partitionsNumber,String author) {
		super();
		this.logic = logic;
		this.algorithm = algorithm;
		this.sources = sources;
		this.submissionBackend = submissionBackend;
		this.executionEnvironment = executionEnvironment;
		this.backendUrl = backendUrl;
		this.configuration = configuration;
		this.partitionsNumber = partitionsNumber;
		this.author=author;
	}

	public LogicType getLogic() {
		return logic;
	}


	public AlgorithmType getAlgorithm() {
		return algorithm;
	}

	public HashMap<ResourceType, Resource> getSources() {
		return sources;
	}

	public String getSubmissionBackend() {
		return submissionBackend;
	}

	public String getExecutionEnvironment() {
		return executionEnvironment;
	}

	public String getBackendUrl() {
		return backendUrl;
	}

	public HashMap<String, String> getConfiguration() {
		return configuration;
	}

	public int getPartitionsNumber() {
		return partitionsNumber;
	}

	public String getAuthor() {
		return author;
	}
	
	
}

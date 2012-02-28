package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;

public class TableGenerationConfiguration {

	private LogicType logic;
	private AlgorithmType algorithm;
	private HashMap<ResourceType,List<Resource>> sources=new HashMap<ResourceType, List<Resource>>(); 
	
	private String maxMinHspenTable;
	
	private String submissionBackend;
	private String executionEnvironment;
	private String backendUrl;
	private HashMap<String,String> configuration;
	private int partitionsNumber;
	private String author;
	private List<Field> additionalParameters;
	
	public TableGenerationConfiguration(LogicType logic,
			AlgorithmType algorithm,List<Resource> sources, String submissionBackend,
			String executionEnvironment, String backendUrl,
			HashMap<String, String> configuration, int partitionsNumber,String author,List<Field> additionalParams) {
		super();
		this.logic = logic;
		this.algorithm = algorithm;
		for(Resource r:sources){
			if(!this.sources.containsKey(r.getType())) this.sources.put(r.getType(), new ArrayList<Resource>());
			this.sources.get(r.getType()).add(r);
		}		
		this.submissionBackend = submissionBackend;
		this.executionEnvironment = executionEnvironment;
		this.backendUrl = backendUrl;
		this.configuration = configuration;
		this.partitionsNumber = partitionsNumber;
		this.author=author;
		if(this.sources.containsKey(ResourceType.HSPEN))maxMinHspenTable="maxminlat_"+this.sources.get(ResourceType.HSPEN).get(0).getTableName();
		this.additionalParameters=additionalParams;
	}
	
	public List<Field> getAdditionalParameters() {
		return additionalParameters;
	}
	public LogicType getLogic() {
		return logic;
	}


	public AlgorithmType getAlgorithm() {
		return algorithm;
	}

	public HashMap<ResourceType, List<Resource>> getSources() {
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
	public String getMaxMinHspenTable() {
		return maxMinHspenTable;
	}
	
}

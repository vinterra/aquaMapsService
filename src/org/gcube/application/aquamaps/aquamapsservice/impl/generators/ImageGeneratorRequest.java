package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISGenerationRequest;

public class ImageGeneratorRequest implements GenerationRequest{

	private String clusterfile;

	private GISGenerationRequest groupRequest;
	
	
	public void setClusterFile(String file) {
		this.clusterfile = file;
	}

	public String getClusterFile() {
		return clusterfile;
	}
	public ImageGeneratorRequest(String clusterfile){
		setClusterFile(clusterfile);
	}

	public void setGroupRequest(GISGenerationRequest groupRequest) {
		this.groupRequest = groupRequest;
	}

	public GISGenerationRequest getGroupRequest() {
		return groupRequest;
	}
	
	
}

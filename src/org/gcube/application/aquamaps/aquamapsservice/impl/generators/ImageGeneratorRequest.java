package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISRequest;

public class ImageGeneratorRequest implements GenerationRequest{

	private String clusterfile;

	private GISRequest groupRequest;
	
	
	public void setClusterFile(String file) {
		this.clusterfile = file;
	}

	public String getClusterFile() {
		return clusterfile;
	}
	public ImageGeneratorRequest(String clusterfile){
		setClusterFile(clusterfile);
	}

	public void setGroupRequest(GISRequest groupRequest) {
		this.groupRequest = groupRequest;
	}

	public GISRequest getGroupRequest() {
		return groupRequest;
	}
	
	
}

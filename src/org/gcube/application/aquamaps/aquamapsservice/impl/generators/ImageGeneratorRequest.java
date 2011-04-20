package org.gcube.application.aquamaps.aquamapsservice.impl.generators;


public class ImageGeneratorRequest implements GenerationRequest{

	private String clusterfile;
	
	public void setClusterFile(String file) {
		this.clusterfile = file;
	}

	public String getClusterFile() {
		return clusterfile;
	}
	public ImageGeneratorRequest(String clusterfile){
		setClusterFile(clusterfile);
	}

	
	
}

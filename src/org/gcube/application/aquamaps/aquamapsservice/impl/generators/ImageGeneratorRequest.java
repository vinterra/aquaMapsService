package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

public class ImageGeneratorRequest {

	private String file;

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}
	public ImageGeneratorRequest(String file){
		this.file=file;
	}
	
	
}

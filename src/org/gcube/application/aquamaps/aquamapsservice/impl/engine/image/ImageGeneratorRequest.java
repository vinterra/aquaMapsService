package org.gcube.application.aquamaps.aquamapsservice.impl.engine.image;

import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.GenerationRequest;


public class ImageGeneratorRequest implements GenerationRequest{

	
	private StringBuilder[] csq_str=null;
	
	private int objId=0;
	
	private Map<String,String> generatedImagesNameAndPath=new HashMap<String, String>(); 
	
	
	public ImageGeneratorRequest(StringBuilder[] strings, int objId) {
		this.csq_str=strings;
		this.objId=objId;
	}


	public Map<String, String> getGeneratedImagesNameAndPath() {
		return generatedImagesNameAndPath;
	}


	public void setGeneratedImagesNameAndPath(
			Map<String, String> generatedImagesNameAndPath) {
		this.generatedImagesNameAndPath = generatedImagesNameAndPath;
	}


	public StringBuilder[] getCsq_str() {
		return csq_str;
	}


	public int getObjId() {
		return objId;
	}
	
	
	
	
}

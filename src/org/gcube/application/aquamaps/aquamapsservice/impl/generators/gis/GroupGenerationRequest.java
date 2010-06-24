package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupGenerationRequest implements GISGenerationRequest {

	private ArrayList<String> layers=new ArrayList<String>();
	private Map<String,String> styles=new HashMap<String, String>(); 
	private String name;
	private String id;
	
	private int submittedId;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the layers
	 */
	public ArrayList<String> getLayers() {
		return layers;
	}
	/**
	 * @param layers the layers to set
	 */
	public void setLayers(ArrayList<String> layers) {
		this.layers = layers;
	}	
	/**
	 * @return the submittedId
	 */
	public int getSubmittedId() {
		return submittedId;
	}
	/**
	 * @param submittedId the submittedId to set
	 */
	public void setSubmittedId(int submittedId) {
		this.submittedId = submittedId;
	}
	public void setStyles(Map<String,String> styles) {
		this.styles = styles;
	}
	public Map<String,String> getStyles() {
		return styles;
	}
	
}

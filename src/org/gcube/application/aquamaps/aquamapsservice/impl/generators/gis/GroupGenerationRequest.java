package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.ArrayList;

public class GroupGenerationRequest implements GISGenerationRequest {

	private ArrayList<String> layers=new ArrayList<String>();
	private ArrayList<String> styles=new ArrayList<String>();
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
	 * @return the styles
	 */
	public ArrayList<String> getStyles() {
		return styles;
	}
	/**
	 * @param styles the styles to set
	 */
	public void setStyles(ArrayList<String> styles) {
		this.styles = styles;
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
	
}

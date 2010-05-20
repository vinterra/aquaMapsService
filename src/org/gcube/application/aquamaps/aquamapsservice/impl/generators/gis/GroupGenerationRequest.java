package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

public class GroupGenerationRequest implements GISGenerationRequest {

	private String[] layers;
	private String name;
	private String id;
	/**
	 * @return the layers
	 */
	public String[] getLayers() {
		return layers;
	}
	/**
	 * @param layers the layers to set
	 */
	public void setLayers(String[] layers) {
		this.layers = layers;
	}
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
	
}

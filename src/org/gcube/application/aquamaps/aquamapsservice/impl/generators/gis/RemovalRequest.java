package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.ArrayList;
import java.util.List;

public class RemovalRequest implements GISRequest {

	private List<String> layers=new ArrayList<String>();
	private List<String> groups=new ArrayList<String>();
	private List<String> styles=new ArrayList<String>();
	public void setLayers(List<String> layers) {
		this.layers = layers;
	}
	public List<String> getLayers() {
		return layers;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setStyles(List<String> styles) {
		this.styles = styles;
	}
	public List<String> getStyles() {
		return styles;
	}
}

package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.LayerInfoType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.WMSContextInfoType;

public class GroupGenerationRequest implements GISGenerationRequest {

	
	private Map<String,String> geoServerLayers=new HashMap<String, String>();
	private String toCreateGroupName;
	
	private List<LayerInfoType> publishedLayer=new ArrayList<LayerInfoType>();
	
	private WMSContextInfoType associatedContext;
	
	
	public Set<String> getGeoServerLayers(){return getGeoServerLayersMap().keySet();}
	public Map<String,String> getStyles(){return getGeoServerLayersMap();}
	
	
	public Map<String,String> getGeoServerLayersMap() {
		return geoServerLayers;
	}
	public void setGeoServerLayers(Map<String,String> geoServerLayers) {
		this.geoServerLayers = geoServerLayers;
	}
	public void setToCreateGroupName(String toCreateGroupName) {
		this.toCreateGroupName = toCreateGroupName;
	}
	public String getToCreateGroupName() {
		return toCreateGroupName;
	}
	public void setPublishedLayer(List<LayerInfoType> publishedLayer) {
		this.publishedLayer = publishedLayer;
	}
	public List<LayerInfoType> getPublishedLayer() {
		return publishedLayer;
	}
	public void setAssociatedContext(WMSContextInfoType associatedContext) {
		this.associatedContext = associatedContext;
	}
	public WMSContextInfoType getAssociatedContext() {
		return associatedContext;
	}
	
}

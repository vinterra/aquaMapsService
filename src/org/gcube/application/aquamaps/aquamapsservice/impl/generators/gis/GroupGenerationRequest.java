package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.HashMap;
import java.util.Map;

import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.common.gis.dataModel.WMSContextInfoType;

public class GroupGenerationRequest implements GISRequest {

	private Map<String,ObjectType> layers=new HashMap<String, ObjectType>();
	private String toGenerateGroupName;
	private WMSContextInfoType createdContextInfoType;
	
	
	
	
	public String getToGenerateGroupName() {
		return toGenerateGroupName;
	}
	public void setToGenerateGroupName(String toGenerateGroupName) {
		this.toGenerateGroupName = toGenerateGroupName;
	}
	public WMSContextInfoType getCreatedContextInfoType() {
		return createdContextInfoType;
	}
	public void setCreatedContextInfoType(WMSContextInfoType createdContextInfoType) {
		this.createdContextInfoType = createdContextInfoType;
	}
	public void setLayers(Map<String,ObjectType> layers) {
		this.layers = layers;
	}
	public Map<String,ObjectType> getLayers() {
		return layers;
	}
}

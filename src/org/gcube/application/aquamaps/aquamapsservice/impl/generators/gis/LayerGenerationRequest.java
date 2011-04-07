package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.gis.dataModel.types.LayersType;



public abstract class LayerGenerationRequest implements GISRequest {


	//************ Layer Generation details
	private String csvFile;
	private String featureLabel;
	private String FeatureDefinition;
	private String mapName;
	private LayersType mapType;
	
	private List<StyleGenerationRequest> toGenerateStyles=new ArrayList<StyleGenerationRequest>();
	private List<String> toAssociateStyles=new ArrayList<String>();
	private int defaultStyle=0;
		
	//************ Generated Layer references
	
	private String generatedLayer;
	private String geoServerLayerId;
	
	
	
	
	protected LayerGenerationRequest(String csvFile,String featureLabel,String featureDefinition,String mapName){
		this.setCsvFile(csvFile);
		this.setFeatureDefinition(featureDefinition);
		this.setFeatureLabel(featureLabel);
		this.setMapName(mapName);
	}

	
	
	//TODO getOccurrenceCells Request
	//TODO getEnvironmentalLayerRequest
	
	
	
	


	public String getCsvFile() {
		return csvFile;
	}




	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}




	public String getFeatureLabel() {
		return featureLabel;
	}




	public void setFeatureLabel(String featureLabel) {
		this.featureLabel = featureLabel;
	}




	public String getFeatureDefinition() {
		return FeatureDefinition;
	}




	public void setFeatureDefinition(String featureDefinition) {
		FeatureDefinition = featureDefinition;
	}




	public String getMapName() {
		return mapName;
	}




	public void setMapName(String mapName) {
		this.mapName = mapName;
	}




	public LayersType getMapType() {
		return mapType;
	}




	public void setMapType(LayersType mapType) {
		this.mapType = mapType;
	}




	public List<StyleGenerationRequest> getToGenerateStyles() {
		return toGenerateStyles;
	}




	public void setToGenerateStyles(List<StyleGenerationRequest> toGenerateStyles) {
		this.toGenerateStyles = toGenerateStyles;
	}




	public List<String> getToAssociateStyles() {
		return toAssociateStyles;
	}




	public void setToAssociateStyles(List<String> toAssociateStyles) {
		this.toAssociateStyles = toAssociateStyles;
	}




	




	public void setGeneratedLayer(String generatedLayer) {
		this.generatedLayer = generatedLayer;
	}




	public String getGeneratedLayer() {
		return generatedLayer;
	}

	public String getGeServerLayerId() {
		return geoServerLayerId;
	}

	public void setGeServerLayerId(String geServerLayerId) {
		this.geoServerLayerId = geServerLayerId;
	}



	public void setDefaultStyle(int defaultStyle) {
		this.defaultStyle = defaultStyle;
	}



	public int getDefaultStyle() {
		return defaultStyle;
	}



	@Override
	public String toString() {
		return "LayerGenerationRequest [mapName=" + mapName + ", mapType="
				+ mapType + ", generatedLayer=" + generatedLayer
				+ ", geoServerLayerId=" + geoServerLayerId + "]";
	}

	
	
}

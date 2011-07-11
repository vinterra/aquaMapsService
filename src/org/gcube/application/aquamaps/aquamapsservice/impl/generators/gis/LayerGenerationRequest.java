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



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((FeatureDefinition == null) ? 0 : FeatureDefinition
						.hashCode());
		result = prime * result + ((csvFile == null) ? 0 : csvFile.hashCode());
		result = prime * result + defaultStyle;
		result = prime * result
				+ ((featureLabel == null) ? 0 : featureLabel.hashCode());
		result = prime * result
				+ ((generatedLayer == null) ? 0 : generatedLayer.hashCode());
		result = prime
				* result
				+ ((geoServerLayerId == null) ? 0 : geoServerLayerId.hashCode());
		result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
		result = prime * result + ((mapType == null) ? 0 : mapType.hashCode());
		result = prime
				* result
				+ ((toAssociateStyles == null) ? 0 : toAssociateStyles
						.hashCode());
		result = prime
				* result
				+ ((toGenerateStyles == null) ? 0 : toGenerateStyles.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LayerGenerationRequest other = (LayerGenerationRequest) obj;
		if (FeatureDefinition == null) {
			if (other.FeatureDefinition != null)
				return false;
		} else if (!FeatureDefinition.equals(other.FeatureDefinition))
			return false;
		if (csvFile == null) {
			if (other.csvFile != null)
				return false;
		} else if (!csvFile.equals(other.csvFile))
			return false;
		if (defaultStyle != other.defaultStyle)
			return false;
		if (featureLabel == null) {
			if (other.featureLabel != null)
				return false;
		} else if (!featureLabel.equals(other.featureLabel))
			return false;
		if (generatedLayer == null) {
			if (other.generatedLayer != null)
				return false;
		} else if (!generatedLayer.equals(other.generatedLayer))
			return false;
		if (geoServerLayerId == null) {
			if (other.geoServerLayerId != null)
				return false;
		} else if (!geoServerLayerId.equals(other.geoServerLayerId))
			return false;
		if (mapName == null) {
			if (other.mapName != null)
				return false;
		} else if (!mapName.equals(other.mapName))
			return false;
		if (mapType != other.mapType)
			return false;
		if (toAssociateStyles == null) {
			if (other.toAssociateStyles != null)
				return false;
		} else if (!toAssociateStyles.equals(other.toAssociateStyles))
			return false;
		if (toGenerateStyles == null) {
			if (other.toGenerateStyles != null)
				return false;
		} else if (!toGenerateStyles.equals(other.toGenerateStyles))
			return false;
		return true;
	}

	
	
}

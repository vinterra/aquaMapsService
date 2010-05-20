package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;


public class LayerGenerationRequest implements GISGenerationRequest {

	private String csvFile;
	private String featureLabel;
	private String layerName;
	private String featureDefinition;
	private int featureSQLType;
	/**
	 * @return the csvFile
	 */
	public String getCsvFile() {
		return csvFile;
	}
	/**
	 * @param csvFile the csvFile to set
	 */
	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}
	/**
	 * @return the featureLabel
	 */
	public String getFeatureLabel() {
		return featureLabel;
	}
	/**
	 * @param featureLabel the featureLabel to set
	 */
	public void setFeatureLabel(String featureLabel) {
		this.featureLabel = featureLabel;
	}
	/**
	 * @return the layerName
	 */
	public String getLayerName() {
		return layerName;
	}
	/**
	 * @param layerName the layerName to set
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}
	/**
	 * @return the featureDefinition
	 */
	public String getFeatureDefinition() {
		return featureDefinition;
	}
	/**
	 * @param featureDefinition the featureDefinition to set
	 */
	public void setFeatureDefinition(String featureDefinition) {
		this.featureDefinition = featureDefinition;
	}
	/**
	 * @return the featureSQLType
	 */
	public int getFeatureSQLType() {
		return featureSQLType;
	}
	/**
	 * @param featureSQLType the featureSQLType to set
	 */
	public void setFeatureSQLType(int featureSQLType) {
		this.featureSQLType = featureSQLType;
	} 
	
	
	
	
}

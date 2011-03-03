package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.StyleGenerationRequest.ClusterScaleType;
import org.gcube.application.aquamaps.stubs.LayerInfoType;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HSPECFields;


public class LayerGenerationRequest implements GISGenerationRequest {


	//************ Layer Generation details
	private String csvFile;
	private String featureLabel;
	private String FeatureDefinition;
	private String mapName;
	private ObjectType mapType;
	
	private List<StyleGenerationRequest> toGenerateStyles=new ArrayList<StyleGenerationRequest>();
	private List<String> toAssociateStyles=new ArrayList<String>();
	
	//************ Layer Info Type identification parameters 
	
	private Set<String> speciesCoverage; 
	private int hcafId;
	private int hspenId; 
	private Map<String,Map<String,Perturbation>> envelopeCustomization;
	private Map<String,Map<EnvelopeFields,Field>> envelopeWeights;
	private Set<Area> selectedAreas;
	private BoundingBox bb;
	private float threshold;
	
	
	//************ Generated Layer references
	
	private LayerInfoType generatedLayer;
	private String geServerLayerId;
	
	
	public static LayerGenerationRequest getBioDiversityRequest(Set<String> speciesCoverage, int hcafId,int hspenId,
			Map<String,Map<String,Perturbation>> envelopeCustomization, Map<String,Map<EnvelopeFields,Field>> envelopeWeights,
			Set<Area> areaSelection, BoundingBox bb,float threshold, String csvFile,String objectName, int min,int max ){
		LayerGenerationRequest toReturn= new LayerGenerationRequest(hcafId,hspenId,areaSelection,bb,csvFile);
		toReturn.setMapName(objectName);
		toReturn.getSpeciesCoverage().addAll(speciesCoverage);
		toReturn.setEnvelopeCustomization(envelopeCustomization);
		toReturn.setEnvelopeWeights(envelopeWeights);
		toReturn.setFeatureLabel(AquaMapsManager.maxSpeciesCountInACell);
		toReturn.setFeatureDefinition("integer");
		toReturn.setMapType(ObjectType.Biodiversity);
		toReturn.setThreshold(threshold);
		toReturn.toGenerateStyles.add(
				StyleGenerationRequest.getBiodiversityStyle(min,max,ClusterScaleType.linear,objectName));
		return toReturn;
	}

	public static LayerGenerationRequest getSpeciesDistributionRequest(String speciesId, int hcafId,int hspenId,
			Map<String,Perturbation> envelopeCustomization, Map<EnvelopeFields,Field> envelopeWeights,
			Set<Area> areaSelection, BoundingBox bb, String csvFile){
		LayerGenerationRequest toReturn= new LayerGenerationRequest(hcafId,hspenId,areaSelection,bb,csvFile);
		toReturn.getSpeciesCoverage().add(speciesId);
		toReturn.getEnvelopeCustomization().put(speciesId, envelopeCustomization);
		toReturn.getEnvelopeWeights().put(speciesId, envelopeWeights);
		toReturn.setMapName(speciesId);
		toReturn.setMapType(ObjectType.SpeciesDistribution);
		toReturn.setFeatureLabel(HSPECFields.probability+"");
		toReturn.setFeatureDefinition("real");
		toReturn.getToAssociateStyles().add(ServiceContext.getContext().getDistributionDefaultStyle());
		return toReturn;
	}
	

	private LayerGenerationRequest(int hcafId,int hspenId,Set<Area> areaSelection, BoundingBox bb, String csvFile){
		this.setHcafId(hcafId);
		this.setHspenId(hspenId);
		this.setBb(bb);
		this.setSelectedAreas(areaSelection);
		this.csvFile=csvFile;
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




	public ObjectType getMapType() {
		return mapType;
	}




	public void setMapType(ObjectType mapType) {
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




	public Set<String> getSpeciesCoverage() {
		return speciesCoverage;
	}




	public void setSpeciesCoverage(Set<String> speciesCoverage) {
		this.speciesCoverage = speciesCoverage;
	}




	public int getHcafId() {
		return hcafId;
	}




	public void setHcafId(int hcafId) {
		this.hcafId = hcafId;
	}




	public int getHspenId() {
		return hspenId;
	}




	public void setHspenId(int hspenId) {
		this.hspenId = hspenId;
	}




	public Map<String, Map<String, Perturbation>> getEnvelopeCustomization() {
		return envelopeCustomization;
	}




	public void setEnvelopeCustomization(
			Map<String, Map<String, Perturbation>> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}




	public Map<String, Map<EnvelopeFields, Field>> getEnvelopeWeights() {
		return envelopeWeights;
	}




	public void setEnvelopeWeights(
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}




	public Set<Area> getSelectedAreas() {
		return selectedAreas;
	}




	public void setSelectedAreas(Set<Area> selectedAreas) {
		this.selectedAreas = selectedAreas;
	}




	public BoundingBox getBb() {
		return bb;
	}




	public void setBb(BoundingBox bb) {
		this.bb = bb;
	}




	public void setGeneratedLayer(LayerInfoType generatedLayer) {
		this.generatedLayer = generatedLayer;
	}




	public LayerInfoType getGeneratedLayer() {
		return generatedLayer;
	}

	public String getGeServerLayerId() {
		return geServerLayerId;
	}

	public void setGeServerLayerId(String geServerLayerId) {
		this.geServerLayerId = geServerLayerId;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return threshold;
	}
	
	
	
//	
//	
//	public void setStyleGenerationParameter(int maxValue,int minValue){
//		styleDefinition=new StyleGenerationRequest();
//		styleDefinition.setAttributeName(this.getFeatureLabel());
//		styleDefinition.setC1(Color.YELLOW);
//		styleDefinition.setC2(Color.RED);
//		styleDefinition.setMax(String.valueOf(maxValue));
//		styleDefinition.setMin(String.valueOf(minValue));					
//		styleDefinition.setNameStyle(ServiceUtils.generateId(this.getLayerName(), "style"));					
//		int Nclasses=((maxValue-minValue)>4)?5:maxValue-minValue;
//		styleDefinition.setNClasses(Nclasses);
//		styleDefinition.setTypeValue(Integer.class);
//	}
//	
}

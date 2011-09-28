package org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.StyleGenerationRequest.ClusterScaleType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.HSPECFields;
import org.gcube.common.gis.dataModel.types.LayersType;

public class PredictionLayerGenerationRequest extends LayerGenerationRequest {

	//*** Object bound layer
	private Set<String> speciesCoverage=new HashSet<String>(); 
	private Resource hcaf;
	private Resource hspen; 
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	private Map<String,Map<EnvelopeFields,Field>> envelopeWeights=new HashMap<String, Map<EnvelopeFields,Field>>();
	private Set<Area> selectedAreas= new HashSet<Area>();
	private BoundingBox bb;
	private float threshold;
	private int objectId;
	
	/**
	 * for Distribution Layers
	 * 
	 * @param species
	 * @param hcaf
	 * @param hspen
	 * @param envelopeCustomization
	 * @param envelopeWeights
	 * @param areaSelection
	 * @param bb
	 * @param csvFile
	 * @param isNative
	 */
	public PredictionLayerGenerationRequest(int objectId,String objectName,Species species, Resource hcaf,Resource hspen,
			Map<String,Perturbation> envelopeCustomization, Map<EnvelopeFields,Field> envelopeWeights,
			Set<Area> areaSelection, BoundingBox bb, String csvFile,AlgorithmType algorithm) throws Exception{
		super(csvFile,HSPECFields.probability+"","real",objectName);
		this.setMapType(LayersType.valueOf(algorithm+""));
		this.getSpeciesCoverage().add(species.getId());
		if(envelopeCustomization!=null)
			this.getEnvelopeCustomization().put(species.getId(), envelopeCustomization);
		if(envelopeWeights!=null)
			this.getEnvelopeWeights().put(species.getId(), envelopeWeights);
		this.setSelectedAreas(areaSelection);
		this.setBb(bb);
		this.setHcaf(hcaf);
		this.setHspen(hspen);
		this.setObjectId(objectId);
		this.getToAssociateStyles().add(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DEFAULT_DISTRIBUTION_STYLE));
		this.setDefaultStyle(0);
	}
	
	
	/**
	 * for Biodiversity Layer
	 * 
	 * @param speciesCoverage
	 * @param hcafId
	 * @param hspenId
	 * @param envelopeCustomization
	 * @param envelopeWeights
	 * @param areaSelection
	 * @param bb
	 * @param threshold
	 * @param csvFile
	 * @param objectName
	 * @param min
	 * @param max
	 */
	public PredictionLayerGenerationRequest(int objectid,Set<String> speciesCoverage, Resource hcaf,Resource hspen,
			Map<String,Map<String,Perturbation>> envelopeCustomization, Map<String,Map<EnvelopeFields,Field>> envelopeWeights,
			Set<Area> areaSelection, BoundingBox bb,float threshold, String csvFile,String objectName, int min,int max ) {
		super(csvFile,AquaMapsManager.maxSpeciesCountInACell,"integer",objectName);
		this.setMapType(LayersType.Biodiversity);
		this.setSpeciesCoverage(speciesCoverage);
		this.setEnvelopeCustomization(envelopeCustomization);
		this.setEnvelopeWeights(envelopeWeights);
		this.setSelectedAreas(areaSelection);
		this.setBb(bb);
		this.setHcaf(hcaf);
		this.setHspen(hspen);
		this.setObjectId(objectid);
		
		this.setThreshold(threshold);
		this.getToGenerateStyles().add(
				StyleGenerationRequest.getBiodiversityStyle(min,max,ClusterScaleType.linear,objectName));
		this.getToGenerateStyles().add(
				StyleGenerationRequest.getBiodiversityStyle(min,max,ClusterScaleType.logarithmic,objectName));
	}
	
	
	public Set<String> getSpeciesCoverage() {
		return speciesCoverage;
	}


	public Resource getHcaf() {
		return hcaf;
	}

	public void setHcaf(Resource hcaf) {
		this.hcaf = hcaf;
	}

	public Resource getHspen() {
		return hspen;
	}

	public void setHspen(Resource hspen) {
		this.hspen = hspen;
	}

	public void setSpeciesCoverage(Set<String> speciesCoverage) {
		this.speciesCoverage = speciesCoverage;
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


	public float getThreshold() {
		return threshold;
	}


	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}


	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}


	public int getObjectId() {
		return objectId;
	}



	
}

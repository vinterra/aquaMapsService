package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;

public class DistributionObjectExecutionRequest extends
		AquaMapsObjectExecutionRequest {

	
	private Set<Species> selectedSpecies=new HashSet<Species>();
	private Map<String,Perturbation> envelopeCustomization;
	private Map<EnvelopeFields,Field> envelopeWeights;
	
	public DistributionObjectExecutionRequest(Submitted object,
			Set<Area> selectedArea, BoundingBox bb,
			Set<Species> selectedSpecies,
			Map<String, Perturbation> envelopeCustomization,
			Map<EnvelopeFields, Field> envelopeWeights) {
		super(object, selectedArea, bb);
		this.selectedSpecies = selectedSpecies;
		this.envelopeCustomization = envelopeCustomization;
		this.envelopeWeights = envelopeWeights;
	}

	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}

	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}

	public Map<String, Perturbation> getEnvelopeCustomization() {
		return envelopeCustomization;
	}

	public void setEnvelopeCustomization(
			Map<String, Perturbation> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}

	public Map<EnvelopeFields, Field> getEnvelopeWeights() {
		return envelopeWeights;
	}

	public void setEnvelopeWeights(Map<EnvelopeFields, Field> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}
	
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.OccurrenceCellsFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;

public class PointMapGenerationRequest extends LayerGenerationRequest {

	
	private Species species;
	
	
	public PointMapGenerationRequest(Species species,String csvFile) {
		super(csvFile,OccurrenceCellsFields.goodcell+"","integer",species.getFieldbyName(SpeciesOccursumFields.english_name+"").getValue()+"_Points");
		this.setSpecies(species);		
	}


	public void setSpecies(Species species) {
		this.species = species;
	}


	public Species getSpecies() {
		return species;
	}
	
}

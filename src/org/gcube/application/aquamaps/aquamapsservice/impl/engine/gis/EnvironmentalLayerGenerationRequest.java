package org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis;

import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.common.gis.dataModel.types.LayersType;

public class EnvironmentalLayerGenerationRequest extends LayerGenerationRequest {

	
	//*** Environmental
	private EnvelopeFields parameter;
	private Resource hcaf;
	
	
	
	public EnvironmentalLayerGenerationRequest(String csvFile,Resource hcaf, EnvelopeFields parameter,String featureLabel,String featureDefintion){
		super(csvFile,featureLabel,featureDefintion,parameter+"");
		this.setMapType(LayersType.Environment);
		this.setHcaf(hcaf);
		this.setParameter(parameter);
	}



	public void setParameter(EnvelopeFields parameter) {
		this.parameter = parameter;
	}



	public EnvelopeFields getParameter() {
		return parameter;
	}



	public void setHcaf(Resource hcaf) {
		this.hcaf = hcaf;
	}



	public Resource getHcaf() {
		return hcaf;
	}
	
}

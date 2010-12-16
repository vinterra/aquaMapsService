package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Types.PerturbationType;


public class Perturbation {


	private PerturbationType type=PerturbationType.ASSIGN;
	private String perturbationValue;


	public Perturbation() {
		this(PerturbationType.ASSIGN, "0");
	}
	public Perturbation(PerturbationType toSetType,String perturbation) {
		type=toSetType;
		perturbationValue=perturbation;
	}
	public PerturbationType getType() {
		return type;
	}
	public String getPerturbationValue() {
		return perturbationValue;
	}

	public Perturbation (org.gcube.application.aquamaps.stubs.Perturbation toLoad){
		this.setPerturbationValue(toLoad.getValue());
		this.setType(PerturbationType.valueOf(toLoad.getType()));
	}

	public List<Perturbation> load(org.gcube.application.aquamaps.stubs.PerturbationArray toLoad){
		List<Perturbation> toReturn=new ArrayList<Perturbation>();
		if((toLoad!=null)&&(toLoad.getPerturbationList()!=null))
			for(org.gcube.application.aquamaps.stubs.Perturbation p:toLoad.getPerturbationList())
				toReturn.add(new Perturbation(p));
		return toReturn;
	}

	@Deprecated
	public org.gcube.application.aquamaps.stubs.Perturbation toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Perturbation toReturn= new org.gcube.application.aquamaps.stubs.Perturbation();
		toReturn.setValue(this.getPerturbationValue());
		toReturn.setType(this.getType().toString());
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.PerturbationArray toStubsVersion(List<Perturbation> toConvert){
		List<org.gcube.application.aquamaps.stubs.Perturbation> list=new ArrayList<org.gcube.application.aquamaps.stubs.Perturbation>();
		if(toConvert!=null)
			for(Perturbation obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.PerturbationArray(list.toArray(new org.gcube.application.aquamaps.stubs.Perturbation[list.size()]));
	}


	public void setType(PerturbationType type) {
		this.type = type;
	}
	public void setPerturbationValue(String perturbationValue) {
		this.perturbationValue = perturbationValue;
	}


}

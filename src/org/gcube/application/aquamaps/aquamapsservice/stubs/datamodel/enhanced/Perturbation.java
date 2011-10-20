package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.PerturbationType;

public class Perturbation extends DataModel{

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((perturbationValue == null) ? 0 : perturbationValue
						.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Perturbation other = (Perturbation) obj;
		if (perturbationValue == null) {
			if (other.perturbationValue != null)
				return false;
		} else if (!perturbationValue.equals(other.perturbationValue))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
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

	public Perturbation (org.gcube.application.aquamaps.datamodel.Perturbation toLoad){
		this.setPerturbationValue(toLoad.getValue());
		this.setType(PerturbationType.valueOf(toLoad.getType()));
	}

	public List<Perturbation> load(org.gcube.application.aquamaps.datamodel.PerturbationArray toLoad){
		List<Perturbation> toReturn=new ArrayList<Perturbation>();
		if((toLoad!=null)&&(toLoad.getPerturbationList()!=null))
			for(org.gcube.application.aquamaps.datamodel.Perturbation p:toLoad.getPerturbationList())
				toReturn.add(new Perturbation(p));
		return toReturn;
	}

	@Deprecated
	public org.gcube.application.aquamaps.datamodel.Perturbation toStubsVersion(){
		org.gcube.application.aquamaps.datamodel.Perturbation toReturn= new org.gcube.application.aquamaps.datamodel.Perturbation();
		toReturn.setValue(this.getPerturbationValue());
		toReturn.setType(this.getType().toString());
		return toReturn;
	}

	public static org.gcube.application.aquamaps.datamodel.PerturbationArray toStubsVersion(List<Perturbation> toConvert){
		List<org.gcube.application.aquamaps.datamodel.Perturbation> list=new ArrayList<org.gcube.application.aquamaps.datamodel.Perturbation>();
		if(toConvert!=null)
			for(Perturbation obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.datamodel.PerturbationArray(list.toArray(new org.gcube.application.aquamaps.datamodel.Perturbation[list.size()]));
	}


	public void setType(PerturbationType type) {
		this.type = type;
	}
	public void setPerturbationValue(String perturbationValue) {
		this.perturbationValue = perturbationValue;
	}


}

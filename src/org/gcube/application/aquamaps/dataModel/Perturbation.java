package org.gcube.application.aquamaps.dataModel;


public class Perturbation {
	
	public enum Type{
		ASSIGN,PERCENT,ADD
	}
	
private Type type;
private Float perturbationValue;
public Perturbation() {
	this(Type.ASSIGN, new Float(0));
}
	public Perturbation(Type toSetType,Float perturbation) {
		type=toSetType;
		perturbationValue=perturbation;
	}
	public Type getType() {
		return type;
	}
	public float getPerturbationValue() {
		return perturbationValue;
	}
	
}

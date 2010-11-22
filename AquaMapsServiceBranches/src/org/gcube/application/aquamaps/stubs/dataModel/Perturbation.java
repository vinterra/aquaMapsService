package org.gcube.application.aquamaps.stubs.dataModel;


public class Perturbation {
	
	public enum Type{
		ASSIGN,PERCENT,ADD
	}
	
private Type type;
private String perturbationValue;
public Perturbation() {
	this(Type.ASSIGN, "0");
}
	public Perturbation(Type toSetType,String perturbation) {
		type=toSetType;
		perturbationValue=perturbation;
	}
	public Type getType() {
		return type;
	}
	public String getPerturbationValue() {
		return perturbationValue;
	}
	
}

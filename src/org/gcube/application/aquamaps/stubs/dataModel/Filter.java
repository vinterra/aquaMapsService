package org.gcube.application.aquamaps.stubs.dataModel;


public class Filter {

	public enum Type  {
		is,
		contains,
		begins,
		ends	
	}
	
	private Type type;
	private Field field;
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}
	
}

package org.gcube.application.aquamaps.dataModel;


public class Field {

	public enum Type {INTEGER,BOOLEAN,STRING,DOUBLE} 
	
	private Type type;
	private String name;
	private String value;
	
	public Type getType() {
		return type;
	}
	
	public Field() {
		type=Type.STRING;
		name="DefaultFieldName";
		value="DefaultValue";
	}
	
	public Field(String name,String value) {
		this();
		this.name=name;
		this.value=value;
	}
	
	public void setType(Type type) {
		this.type = type;		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public String toXML(){
		StringBuilder doc=new StringBuilder();
		doc.append("<Field>");
		doc.append("<Type>"+type.toString()+"</Type>");
		doc.append("<Name>"+name+"</Name>");
		doc.append("<Value>"+value+"</Value>");
		doc.append("</Field>");
		return doc.toString();
	}
	
}

package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.util.XMLUtils;
import org.w3c.dom.Element;


public class Field {

	private FieldType type=FieldType.STRING;
	private String name;
	private String value;

	public FieldType getType() {
		return type;
	}

	public Field() {
		type=FieldType.STRING;
		name="DefaultFieldName";
		value="DefaultValue";
	}

	public Field(String name,String value) {
		this();
		this.name=name;
		this.value=value;
	}
	public Field(String name,String value,FieldType type){
		this(name,value);
		this.setType(type);
	}


	public void setType(FieldType type) {
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

	public static List<Field> load(org.gcube.application.aquamaps.stubs.FieldArray toLoad){
		List<Field> toReturn=new ArrayList<Field>();
		if((toLoad!=null)&&(toLoad.getFields()!=null))
			for(org.gcube.application.aquamaps.stubs.Field f:toLoad.getFields())toReturn.add(new Field(f));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.FieldArray toStubsVersion(Collection<Field> collection){
		List<org.gcube.application.aquamaps.stubs.Field> list=new ArrayList<org.gcube.application.aquamaps.stubs.Field>();
		if(collection!=null)
			for(Field obj:collection)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.FieldArray(list.toArray(new org.gcube.application.aquamaps.stubs.Field[list.size()]));
	}


	public Field(org.gcube.application.aquamaps.stubs.Field toLoad){
		super();
		this.setName(toLoad.getName());
		this.setType(FieldType.valueOf(toLoad.getType()));
		this.setValue(toLoad.getValue());
	}
	public org.gcube.application.aquamaps.stubs.Field toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Field toReturn=new org.gcube.application.aquamaps.stubs.Field();
		toReturn.setName(this.getName());
		toReturn.setType(this.getType().toString());
		toReturn.setValue(this.getValue());
		return toReturn;		
	}


	public String getOperator(){
		if(name.contains("min")) return ">=";
		else if(name.contains("max")) return "<=";
		else return "=";
	}

	public String toXML(){
		StringBuilder doc=new StringBuilder();
		doc.append("<Field>");
		doc.append("<FieldType>"+type.toString()+"</FieldType>");
		doc.append("<Name>"+name+"</Name>");
		doc.append("<Value>"+value+"</Value>");
		doc.append("</Field>");
		return doc.toString();
	}

	public Field (Element el){
		Element typeElement=(Element) el.getElementsByTagName("FieldType").item(0);
		this.setType(FieldType.valueOf(XMLUtils.getTextContent(typeElement)));
		Element nameElement=(Element) el.getElementsByTagName("Name").item(0);
		this.setName(XMLUtils.getTextContent(nameElement));
		Element valueElement=(Element) el.getElementsByTagName("Value").item(0);
		this.setValue(XMLUtils.getTextContent(valueElement));
	}
}

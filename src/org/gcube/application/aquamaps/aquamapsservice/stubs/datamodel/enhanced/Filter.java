package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli2.validation.InvalidArgumentException;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FilterType;

public class Filter extends DataModel{


	private FilterType type=FilterType.is;
	private Field field;

	public FilterType getType() {
		return type;
	}

	public void setType(FilterType type) {
		this.type = type;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public Filter(FilterType type,Field f){
		this.field=f;
		this.type=type;
	}
	
	
	public Filter (org.gcube.application.aquamaps.datamodel.Filter toLoad){
		Field field= new Field();
		field.setName(toLoad.getName());
		field.setValue(toLoad.getValue());
		field.setType(FieldType.valueOf(toLoad.getFieldType()));
		this.setField(field);
		this.setType(FilterType.valueOf(toLoad.getType()));
	}

	public static List<Filter> load(org.gcube.application.aquamaps.datamodel.FilterArray toLoad){
		List<Filter> toReturn=new ArrayList<Filter>();
		if((toLoad!=null)&&(toLoad.getFilterList()!=null))
			for(org.gcube.application.aquamaps.datamodel.Filter f: toLoad.getFilterList())toReturn.add(new Filter(f));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.datamodel.FilterArray toStubsVersion(List<Filter> toConvert){
		List<org.gcube.application.aquamaps.datamodel.Filter> list=new ArrayList<org.gcube.application.aquamaps.datamodel.Filter>();
		if(toConvert!=null)
			for(Filter obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.datamodel.FilterArray(list.toArray(new org.gcube.application.aquamaps.datamodel.Filter[list.size()]));
	}

	public org.gcube.application.aquamaps.datamodel.Filter toStubsVersion(){
		org.gcube.application.aquamaps.datamodel.Filter toReturn= new org.gcube.application.aquamaps.datamodel.Filter();
		toReturn.setName(this.getField().getName());
		toReturn.setType(this.getType().toString());
		toReturn.setValue(this.getField().getValue());
		toReturn.setFieldType(this.getField().getType()+"");
		return toReturn;
	}

	public String toSQLString() throws InvalidArgumentException{
		switch(type){
		case begins: return " like '"+field.getValue()+"%'";
		case contains: return " like '%"+field.getValue()+"%'";
		case ends: return " like '%"+field.getValue()+"'";
		case is: {
					switch(field.getType()){
					case STRING : return " = '"+field.getValue()+"'";
					case INTEGER : return " = "+field.getValueAsInteger();
					case DOUBLE : return " = "+field.getValueAsDouble();
					case LONG : return " = "+field.getValueAsLong();
					default : return " = "+(field.getValueAsBoolean()?"1":"0");
					}
				}
		case greater_then : return " >= "+field.getValue();
		case smaller_then : return " <= "+field.getValue();
		default : throw new InvalidArgumentException("invalid filter type");
		}
	}

}

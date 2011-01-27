package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli2.validation.InvalidArgumentException;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FilterType;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Filter")
public class Filter {


	@XStreamAlias("method")
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
	
	
	public Filter (org.gcube.application.aquamaps.stubs.Filter toLoad){
		Field field= new Field();
		field.setName(toLoad.getName());
		field.setValue(toLoad.getValue());
		this.setField(field);
		this.setType(FilterType.valueOf(toLoad.getType()));
	}

	public static List<Filter> load(org.gcube.application.aquamaps.stubs.FilterArray toLoad){
		List<Filter> toReturn=new ArrayList<Filter>();
		if((toLoad!=null)&&(toLoad.getFilterList()!=null))
			for(org.gcube.application.aquamaps.stubs.Filter f: toLoad.getFilterList())toReturn.add(new Filter(f));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.FilterArray toStubsVersion(List<Filter> toConvert){
		List<org.gcube.application.aquamaps.stubs.Filter> list=new ArrayList<org.gcube.application.aquamaps.stubs.Filter>();
		if(toConvert!=null)
			for(Filter obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.FilterArray(list.toArray(new org.gcube.application.aquamaps.stubs.Filter[list.size()]));
	}

	public org.gcube.application.aquamaps.stubs.Filter toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Filter toReturn= new org.gcube.application.aquamaps.stubs.Filter();
		toReturn.setName(this.getField().getName());
		toReturn.setType(this.getType().toString());
		toReturn.setValue(this.getField().getValue());
		return toReturn;
	}

	public String toSQLString() throws InvalidArgumentException{
		switch(type){
		case begins: return " like '"+field.getValue()+"%'";
		case contains: return " like '%"+field.getValue()+"%'";
		case ends: return " like '%"+field.getValue()+"'";
		case is: return " = '"+field.getValue()+"'";
		default : throw new InvalidArgumentException("invalid filter type");
		}
	}

}

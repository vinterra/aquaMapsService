package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.dataModel.Types.AreaType;



public class Area {


	public static final String TYPE="type";
	public static final String CODE="code";
	public static final String Name="name";

	private AreaType type=AreaType.FAO;
	private String name;
	private String code;
	public Map<String,Field> attributes= new HashMap<String, Field>();
	
	
	public AreaType getType() {
		return type;
	}

	public Area(AreaType type, String code){
		this.code=code;
		this.type=type;
	}

	public void setType(AreaType type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


	public Area(org.gcube.application.aquamaps.stubs.Area toLoad){
		super();
		this.setCode(toLoad.getCode());
		this.setName(toLoad.getName());
		this.setType(AreaType.valueOf(toLoad.getType()));
	}

	public org.gcube.application.aquamaps.stubs.Area toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Area toReturn= new org.gcube.application.aquamaps.stubs.Area();
		toReturn.setCode(this.getCode());
		toReturn.setName(this.getName());
		toReturn.setType(this.getType().toString());
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.AreasArray toStubsVersion(Set<Area> toConvert){
		List<org.gcube.application.aquamaps.stubs.Area> list=new ArrayList<org.gcube.application.aquamaps.stubs.Area>();
		if(toConvert!=null)
			for(Area obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.AreasArray(list.toArray(new org.gcube.application.aquamaps.stubs.Area[list.size()]));
	}


	public static Set<Area> load(org.gcube.application.aquamaps.stubs.AreasArray toLoad){
		Set<Area> toReturn= new HashSet<Area>();
		if((toLoad!=null)&&(toLoad.getAreasList()!=null))
			for(org.gcube.application.aquamaps.stubs.Area a:toLoad.getAreasList())
				toReturn.add(new Area(a));
		return toReturn;
	}

		public String toJSON(){
			StringBuilder toReturn=new StringBuilder();
			toReturn.append("{\""+CODE+"\":\""+code+"\"");
			toReturn.append(",\""+TYPE+"\":\""+type.toString()+"\"");
			toReturn.append(",\""+Name+"\":\""+name+"\"");
			for(String fieldName:attributes.keySet()){
				toReturn.append(" ,\""+fieldName+"\":\""+attributes.get(fieldName).getValue()+"\"");
			}
			toReturn.append("}");
			return toReturn.toString();
		}	
		
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Area>");
		toReturn.append("<"+CODE+">"+code+"</"+CODE+">");
		toReturn.append("<"+TYPE+">"+type.toString()+"</"+TYPE+">");
		toReturn.append("<"+Name+">"+name+"</"+Name+">");
		toReturn.append("<Attributes>");
		for(Field field:attributes.values())toReturn.append(field.toXML());
		toReturn.append("</Attributes>");
		toReturn.append("</Area>");
		return toReturn.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Area))
			return false;
		Area other = (Area) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	
	
}

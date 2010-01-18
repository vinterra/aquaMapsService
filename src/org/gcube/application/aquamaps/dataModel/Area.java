package org.gcube.application.aquamaps.dataModel;

import java.util.HashMap;
import java.util.Map;



public class Area {

	public static class Tags{
		public static final String TYPE="type";
		public static final String CODE="code";
	 	public static final String Name="name";
	}
	
	public enum Type {LME,EEZ,FAO}
	private Type type;
	private String name;
	private String code;
	public Map<String,Field> attributes= new HashMap<String, Field>();
	public Type getType() {
		return type;
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String toJSON(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("{\""+Tags.CODE+"\":\""+code+"\"");
		toReturn.append(",\""+Tags.TYPE+"\":\""+type.toString()+"\"");
		toReturn.append(",\""+Tags.Name+"\":\""+name+"\"");
		for(String fieldName:attributes.keySet()){
			toReturn.append(" ,\""+fieldName+"\":\""+attributes.get(fieldName).getValue()+"\"");
		}
		toReturn.append("}");
		return toReturn.toString();
	}	
	
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Area>");
		toReturn.append("<"+Tags.CODE+">"+code+"</"+Tags.CODE+">");
		toReturn.append("<"+Tags.TYPE+">"+type.toString()+"</"+Tags.TYPE+">");
		toReturn.append("<"+Tags.Name+">"+name+"</"+Tags.Name+">");
		toReturn.append("<Attributes>");
		for(Field field:attributes.values())toReturn.append(field.toXML());
		toReturn.append("</Attributes>");
		toReturn.append("</Area>");
		return toReturn.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
	
	/*public boolean equals(Object	toCheckobj) {
		Area toCheck;
		if(!(toCheckobj instanceof Area)) return false;
		else toCheck=(Area) toCheckobj;
		return ((this.getType().equals(toCheck.getType())&&(this.getCode().equals(toCheck.getCode()))));
	}*/
}

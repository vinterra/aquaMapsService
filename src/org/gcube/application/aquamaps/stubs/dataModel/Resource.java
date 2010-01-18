package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.HashMap;
import java.util.Map;

public class Resource{

	public enum Type {HSPEC,HSPEN,JOB,HCAF};
	
	public static class Tags{
		public static final String NAME="title";
		public static final String DESCRIPTION="description";
		public static final String DISCLAIMER="disclaimer";
		public static final String PROVENANCE="provenience";
		public static final String PARAMETERS="parameters";
		public static final String DATA="data";
		public static final String AUTHOR="author";
		public static final String SOURCE_Name="sourceName";
		public static final String SOURCE_ID="sourceId";
		public static final String CRITERION="selectionCriteria";
		public static final String RELATED="related";
		public static final String HCAF_ID="sourceHCafId";
		public static final String HSPEN_ID="sourceHSpenId";
		public static final String HCAF_Name="sourceHCAFName";
		public static final String HSPEN_Name="sourceHSPENName";
		public static final String STATUS="status";
		public static final String RESID="searchId";
	}
	
	
	private Type type;
	public Map<String,Field> attributes=new HashMap<String,Field>();
	
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	public Resource() {
		this(Type.HCAF);
	}
	public Resource(Type type) {		
		this.type=type;
		this.attributes.put(Tags.RESID, new Field(Tags.RESID,"1"));
		this.attributes.put(Tags.NAME	, new Field(Tags.NAME,"Default_"+type.toString()));
	}
	
	public String toXML(){
		StringBuilder doc=new StringBuilder();
		doc.append("<Resource>");
		doc.append("<Type>"+type.toString()+"</Type>");
		doc.append("<Attributes>");
		for(Field field:attributes.values())
			doc.append(field.toXML());
		doc.append("</Attributes>");		
		doc.append("</Resource>");
		return doc.toString();
	}
	public String toJSON(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("{\"type\" :\""+type.toString()+"\"");
		for(Field field:attributes.values()){
			toReturn.append(" ,\""+field.getName()+"\":\""+field.getValue()+"\"");
		}
		toReturn.append("}");
		return toReturn.toString();
	}
}

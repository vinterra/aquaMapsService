package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Resource")
public class Resource {

	@XStreamAsAttribute
	private ResourceType type=ResourceType.HCAF;
	
	@XStreamAsAttribute
	private int searchId=1;

	private String title;
	private String tableName;
	private String description;
	private String author;
	private String disclaimer;
	private String provenance;
	private String date;
	private Integer sourceId=0;
	private String parameters;
	private String status;
	private String sourceName;

	
	

	
	//********* instance values


	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getProvenience() {
		return provenance;
	}
	public void setProvenance(String provenience) {
		this.provenance = provenience;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	


	public void setType(ResourceType type) {
		this.type = type;
	}
	public ResourceType getType() {
		return type;
	}

	public Resource(ResourceType type,int searchId) {		
		this.type=type;
		this.searchId=searchId;
	}



	public Resource (org.gcube.application.aquamaps.stubs.Resource toLoad){
		this.setAuthor(toLoad.getAuthor());
		this.setDate(toLoad.getDate());
		this.setDescription(toLoad.getDescription());
		this.setDisclaimer(toLoad.getDisclaimer());
		this.setParameters(toLoad.getParameters());
		this.setProvenance(toLoad.getProvenance());
		this.setSearchId(toLoad.getSearchId());
		this.setSourceId(toLoad.getSourceId());
		this.setSourceName(toLoad.getSourceName());
		this.setStatus(toLoad.getStatus());
		this.setTableName(toLoad.getTableName());
		this.setTitle(toLoad.getTitle());
		this.setType(ResourceType.valueOf(toLoad.getType()));
	}

	public org.gcube.application.aquamaps.stubs.Resource toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Resource toReturn=new org.gcube.application.aquamaps.stubs.Resource();
		toReturn.setAuthor(this.getAuthor());
		toReturn.setDate(this.getDate());
		toReturn.setDescription(this.getDescription());
		toReturn.setDisclaimer(this.getDisclaimer());
		toReturn.setParameters(this.getParameters());
		toReturn.setProvenance(this.getProvenience());
		toReturn.setSearchId(this.getSearchId());
		toReturn.setSourceId(this.getSourceId());		
		toReturn.setSourceName(this.getSourceName());
		toReturn.setStatus(this.getStatus());
		toReturn.setTableName(this.getTableName());
		toReturn.setTitle(this.getTitle());
		toReturn.setType(this.getType().toString());
		return toReturn;
	}


	public static List<Resource> load(org.gcube.application.aquamaps.stubs.ResourceArray toLoad){
		List<Resource> toReturn=new ArrayList<Resource>();
		if((toLoad!=null)&&(toLoad.getResourceList()!=null))
			for(org.gcube.application.aquamaps.stubs.Resource f: toLoad.getResourceList())toReturn.add(new Resource(f));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.ResourceArray toStubsVersion(List<Resource> toConvert){
		List<org.gcube.application.aquamaps.stubs.Resource> list=new ArrayList<org.gcube.application.aquamaps.stubs.Resource>();
		if(toConvert!=null)
			for(Resource obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.ResourceArray(list.toArray(new org.gcube.application.aquamaps.stubs.Resource[list.size()]));
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + searchId;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Resource other = (Resource) obj;
		if (searchId != other.searchId)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}


	//	public String toXML(){
	//		StringBuilder doc=new StringBuilder();
	//		doc.append("<Resource>");
	//		doc.append("<Type>"+type.toString()+"</Type>");
	//		doc.append("<Attributes>");
	//		for(Field field:attributes.values())
	//			doc.append(field.toXML());
	//		doc.append("</Attributes>");		
	//		doc.append("</Resource>");
	//		return doc.toString();
	//	}
	//	public String toJSON(){
	//		StringBuilder toReturn=new StringBuilder();
	//		toReturn.append("{\"type\" :\""+type.toString()+"\"");
	//		for(Field field:attributes.values()){
	//			toReturn.append(" ,\""+field.getName()+"\":\""+field.getValue()+"\"");
	//		}
	//		toReturn.append("}");
	//		return toReturn.toString();
	//	}



}

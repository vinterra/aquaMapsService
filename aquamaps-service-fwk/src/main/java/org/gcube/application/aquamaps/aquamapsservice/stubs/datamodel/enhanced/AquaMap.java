package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.json.JSONException;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube_system.namespaces.application.aquamaps.types.Map;
import org.gcube_system.namespaces.application.aquamaps.types.MapArray;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("AquaMap")
public class AquaMap {

	private LayerInfo layer=null;
	private boolean gis=false;
	private String title="";
	private ObjectType mapType=ObjectType.SpeciesDistribution;
	private ArrayList<File> files=new ArrayList<File>();
	private Resource resource=new Resource(ResourceType.HSPEC, 0);
	private String coverage="";
	private Long creationDate=0l;
	private String author="";
	private String fileSetId="";
	private String layerId="";
	private String speciesCsvList="";
	private Boolean custom=false;
	
	public AquaMap() {
		// TODO Auto-generated constructor stub
	}
	
	public Map toStubsVersion() throws JSONException{
		Map toReturn=new Map();
		toReturn.setAuthor(author);
		toReturn.setCoverage(coverage);
		toReturn.setCreationDate(creationDate);
		toReturn.setFileSetIt(fileSetId);
		toReturn.setGis(gis);
		if(gis)	toReturn.setGisLayer(layer.toStubsVersion());
		toReturn.setLayerId(layerId);
		toReturn.setMapType(mapType+"");		
		toReturn.setResource(resource.toStubsVersion());
		toReturn.setSpeciesListCSV(speciesCsvList);
		toReturn.setStaticImages(File.toStubsVersion(files));
		toReturn.setTitle(title);
		toReturn.setCustom(isCustom());
		return toReturn;
	}
	
	
	
	
	

	
	public AquaMap(Map toLoad){
		setAuthor(toLoad.getAuthor());
		setCoverage(toLoad.getCoverage());
		setCreationDate(toLoad.getCreationDate());
		setFiles(File.load(toLoad.getStaticImages()));
		setFileSetId(toLoad.getFileSetIt());
		setGis(toLoad.isGis());
		if(isGis())setLayer(new LayerInfo(toLoad.getGisLayer()));
		setLayerId(toLoad.getLayerId());
		setMapType(ObjectType.valueOf(toLoad.getMapType()));
		setResource(new Resource(toLoad.getResource()));
		setSpeciesCsvList(toLoad.getSpeciesListCSV());
		setTitle(toLoad.getTitle());
		setCustom(toLoad.isCustom());
	}
	
	public static MapArray toStubsVersion(Collection<AquaMap> toConvert) throws JSONException{
		List<Map> list=new ArrayList<Map>();
		if(toConvert!=null)
			for(AquaMap a:toConvert)list.add(a.toStubsVersion());
		return new MapArray(list.toArray(new Map[list.size()]));
	}
	public static List<AquaMap> load(MapArray toLoad){
		List<AquaMap> toReturn=new ArrayList<AquaMap>();
		if((toLoad!=null)&&(toLoad.getMapList()!=null))
			for(Map m:toLoad.getMapList()) toReturn.add(new AquaMap(m));
		return toReturn;
	}

	public LayerInfo getLayer() {
		return layer;
	}

	public void setLayer(LayerInfo layer) {
		this.layer = layer;
	}

	public boolean isGis() {
		return gis;
	}

	public void setGis(boolean gis) {
		this.gis = gis;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ObjectType getMapType() {
		return mapType;
	}

	public void setMapType(ObjectType mapType) {
		this.mapType = mapType;
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public Resource getResource() {
		return resource;
	}
public Boolean isCustom() {
	return custom;
}
public void setCustom(Boolean custom) {
	this.custom = custom;
}
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getCoverage() {
		return coverage;
	}

	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFileSetId() {
		return fileSetId;
	}

	public void setFileSetId(String fileSetId) {
		this.fileSetId = fileSetId;
	}

	public String getLayerId() {
		return layerId;
	}

	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}

	public String getSpeciesCsvList() {
		return speciesCsvList;
	}

	public void setSpeciesCsvList(String speciesCsvList) {
		this.speciesCsvList = speciesCsvList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((coverage == null) ? 0 : coverage.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
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
		AquaMap other = (AquaMap) obj;
		if (coverage == null) {
			if (other.coverage != null)
				return false;
		} else if (!coverage.equals(other.coverage))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AquaMap [layer=");
		builder.append(layer);
		builder.append(", gis=");
		builder.append(gis);
		builder.append(", title=");
		builder.append(title);
		builder.append(", mapType=");
		builder.append(mapType);
		builder.append(", files=");
		builder.append(files);
		builder.append(", resource=");
		builder.append(resource);
		builder.append(", coverage=");
		builder.append(coverage);
		builder.append(", creationDate=");
		builder.append(creationDate);
		builder.append(", author=");
		builder.append(author);
		builder.append(", fileSetId=");
		builder.append(fileSetId);
		builder.append(", layerId=");
		builder.append(layerId);
		builder.append(", speciesCsvList=");
		builder.append(speciesCsvList);
		builder.append(", custom=");
		builder.append(custom);
		builder.append("]");
		return builder.toString();
	}

	

	

	
}

package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.datamodel.AquaMap;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.gis.datamodel.utils.Utils;


public class AquaMapsObject extends DataModel{

	public static final String CITATION="Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
	"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
	"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";
	
	private int id;	
	private String name;
	private String author;
	private SubmittedStatus status=SubmittedStatus.Pending;
	private Long date=0l;
	private Boolean gis=false;
	private ArrayList<LayerInfo> layers = new ArrayList<LayerInfo>();
	private ObjectType type=ObjectType.Biodiversity;
	private Set<Species> selectedSpecies=new TreeSet<Species>();
	private List<File> additionalFiles=new ArrayList<File>();
	private List<File> images=new ArrayList<File>();
	private float threshold=0.5f;	
	private BoundingBox boundingBox=new BoundingBox();
	
	private String projectCitation=	CITATION;

	private AlgorithmType algorithmType=AlgorithmType.NativeRange;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ObjectType getType() {
		return type;
	}
	public void setType(ObjectType type) {
		this.type = type;
	}
	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}
	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}
	
	public float getThreshold() {
		return threshold;
	}
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public int addSpecies(Collection<Species> toAdd){
		selectedSpecies.addAll(toAdd);
		return selectedSpecies.size();
	}

	public int removeSpecies(Collection<Species> toAdd){
		selectedSpecies.removeAll(toAdd);
		return selectedSpecies.size();
	}
	
	public void setStatus(SubmittedStatus status) {
		this.status = status;
	}
	public SubmittedStatus getStatus() {
		return status;
	}
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	public void setGis(Boolean gis) {
		this.gis = gis;
	}
	public Boolean getGis() {
		return gis;
	}

	public AquaMapsObject(AquaMap toLoad){
		this.setAuthor(toLoad.getAuthor());
		this.getBoundingBox().parse(toLoad.getBoundingBox());
		this.setDate(toLoad.getDate());
		this.getImages().addAll(File.load(toLoad.getImages()));
		this.getAdditionalFiles().addAll(File.load(toLoad.getAdditionalFiles()));
		this.getSelectedSpecies().addAll(Species.load(toLoad.getSelectedSpecies()));
		this.setGis(toLoad.isGis());
		this.setId(toLoad.getId());
		this.setName(toLoad.getName());
		this.setStatus(SubmittedStatus.valueOf(toLoad.getStatus()));
		this.setThreshold(toLoad.getThreshold());
		this.setType(ObjectType.valueOf(toLoad.getType()));
		this.setLayers(Utils.loadArray(toLoad.getLayers()));
		this.setAlgorithmType(AlgorithmType.valueOf(toLoad.getAlgorithmType()));
		
	}
	public ArrayList<LayerInfo> getLayers() {
		return layers;
	}
	public void setLayers(ArrayList<LayerInfo> layers) {
		this.layers = layers;
	}
	public List<File> getAdditionalFiles() {
		return additionalFiles;
	}
	public void setAdditionalFiles(List<File> additionalFiles) {
		this.additionalFiles = additionalFiles;
	}
	public List<File> getImages() {
		return images;
	}
	public void setImages(List<File> images) {
		this.images = images;
	}
	public static List<AquaMapsObject> load(org.gcube.application.aquamaps.datamodel.AquaMapArray toLoad){
		List<AquaMapsObject> toReturn= new ArrayList<AquaMapsObject>();
		if((toLoad!=null)&&(toLoad.getAquaMapList()!=null))
			for(org.gcube.application.aquamaps.datamodel.AquaMap a: toLoad.getAquaMapList())
				toReturn.add(new AquaMapsObject(a));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.datamodel.AquaMapArray toStubsVersion(List<AquaMapsObject> toConvert){
		List<org.gcube.application.aquamaps.datamodel.AquaMap> list=new ArrayList<org.gcube.application.aquamaps.datamodel.AquaMap>();
		if(toConvert!=null)
			for(AquaMapsObject obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.datamodel.AquaMapArray(list.toArray(new org.gcube.application.aquamaps.datamodel.AquaMap[list.size()]));
	}

	public org.gcube.application.aquamaps.datamodel.AquaMap toStubsVersion(){
		org.gcube.application.aquamaps.datamodel.AquaMap toReturn= new org.gcube.application.aquamaps.datamodel.AquaMap();
		toReturn.setAuthor(this.author);
		toReturn.setBoundingBox(this.boundingBox.toString());
		toReturn.setDate(this.date!=null?date:0);
		toReturn.setGis(this.gis);
		toReturn.setId(this.id);
		toReturn.setName(this.name);
		toReturn.setAdditionalFiles(File.toStubsVersion(this.additionalFiles));
		toReturn.setImages(File.toStubsVersion(this.images));
		toReturn.setSelectedSpecies(Species.toStubsVersion(this.selectedSpecies));
		toReturn.setStatus(this.status+"");
		toReturn.setThreshold(this.threshold);
		toReturn.setType(this.type.toString());
		toReturn.setLayers(Utils.loadArray(this.layers));
		toReturn.setAlgorithmType(this.getAlgorithmType()+"");
		return toReturn;
	}

	public AquaMapsObject(String name,int id, ObjectType type){
		this.setName(name);
		this.setId(id);
		this.setType(type);
	}
	
	
	
	public AquaMapsObject(){}
	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}
	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}
	
	public String getCompressedSpeciesCoverage(){
		return generateMD5(selectedSpecies,(type.equals(ObjectType.Biodiversity)?threshold+"":""));
	}
	
	public static String generateMD5(Set<Species> set,String toIncludeParameterList){
		StringBuilder concatSpeciesIds=new StringBuilder();
		for(Species s : set) concatSpeciesIds.append(s.getId()+",");
		concatSpeciesIds.deleteCharAt(concatSpeciesIds.lastIndexOf(","));		
		return DigestUtils.md5Hex(concatSpeciesIds.toString()+toIncludeParameterList);
	}
	public static String generateMD5fromIds(Set<String> set,String toIncludeParameterList){
		StringBuilder concatSpeciesIds=new StringBuilder();
		for(String s : set) concatSpeciesIds.append(s+",");
		concatSpeciesIds.deleteCharAt(concatSpeciesIds.lastIndexOf(","));		
		return DigestUtils.md5Hex(concatSpeciesIds.toString()+toIncludeParameterList);
	}
}

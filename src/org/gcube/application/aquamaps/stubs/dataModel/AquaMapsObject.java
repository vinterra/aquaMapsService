package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AquaMapsObject {
	
	public static class Tags{
		public static final String id="identifier";
		public static final String title="title";
		public static final String creator="creator";
		public static final String date="date.created";
		public static final String publisher="publisher";
		public static final String source="source";
		public static final String type="type";
		public static final String species="coverage.species";
		
	}
	public static String projectCitation=	"Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
										 	"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
										 	"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";
	
	
	public enum Type{
		Biodiversity,SpeciesDistribution
	}
	
	
	
	private String status;
	private String name="";
	private String author="";
	private String date="";
	private String id="";	
	private String creator="";
	private String publisher="";
	private String source="";
	private String profileUrl="";
	private Type type=Type.Biodiversity;
	private Set<Species> selectedSpecies=new HashSet<Species>();
	private Map<String,String> relatedResources=new HashMap<String, String>();
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	private Map<String,List<Field>> envelopeWeights=new HashMap<String, List<Field>>();
	private Set<Area> selectedAreas=new HashSet<Area>();
	private Set<Cell> excludedCells=new HashSet<Cell>();
	private Map<String,Map<String,Perturbation>> environmentCustomization=new HashMap<String, Map<String,Perturbation>>();
	private float threshold=0.5f;	
	private BoundingBox boundingBox=new BoundingBox();
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}	
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}
	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}
	public Map<String,String> getRelatedResources() {
		return relatedResources;
	}
	public void setRelatedResources(Map<String,String> relatedResources) {
		this.relatedResources = relatedResources;
	}
	public Map<String, Map<String, Perturbation>> getEnvelopeCustomization() {
		return envelopeCustomization;
	}
	public void setEnvelopeCustomization(
			Map<String, Map<String, Perturbation>> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}
	public Set<Area> getSelectedAreas() {
		return selectedAreas;
	}
	public void setSelectedAreas(Set<Area> selectedAreas) {
		this.selectedAreas = selectedAreas;
	}
	public Set<Cell> getExcludedCells() {
		return excludedCells;
	}
	public void setExcludedCells(Set<Cell> excludedCells) {
		this.excludedCells = excludedCells;
	}
	public Map<String, Map<String, Perturbation>> getEnvironmentCustomization() {
		return environmentCustomization;
	}
	public void setEnvironmentCustomization(
			Map<String, Map<String, Perturbation>> environmentCustomization) {
		this.environmentCustomization = environmentCustomization;
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
	
	public int addAreas(Collection<Area> toAdd){
		selectedAreas.addAll(toAdd);		
		return selectedAreas.size();
	}
	
	public int removeAreas(Collection<Area> toRemove){
		selectedAreas.removeAll(toRemove);		
		return selectedAreas.size();
	}
	public int removeSpecies(Collection<Species> toAdd){
		selectedSpecies.removeAll(toAdd);
		for(Species spec: toAdd) {
			envelopeWeights.remove(spec.getId());
			envelopeCustomization.remove(spec.getId());
		}
		return selectedSpecies.size();
	}
	public String toXML(){
		StringBuilder profileBuilder=new StringBuilder();
		profileBuilder.append("<AquaMap>");
		profileBuilder.append("<Name>"+name+"</Name>");
		profileBuilder.append("<Author>"+author+"</Author>");
		profileBuilder.append("<Source>"+source+"</Source>");
		profileBuilder.append("<Status>"+status+"</Status>");
		profileBuilder.append("<date>"+date+"</date>");
		profileBuilder.append("<SelectedSpecies>");
		for(Species spec:selectedSpecies) profileBuilder.append(spec.toXML());
		profileBuilder.append("</SelectedSpecies>");
		
		profileBuilder.append("<EnvelopeCustomization>");
		for(String specId:envelopeCustomization.keySet()){
			profileBuilder.append("<"+Species.Tags.ID+">"+specId+"</"+Species.Tags.ID+">");
			profileBuilder.append("<Weights>");
				for(Field field:envelopeWeights.get(specId)) profileBuilder.append(field.toXML());
			profileBuilder.append("</Weights>");
			profileBuilder.append("<Customizations>");
			for(String fieldName:envelopeCustomization.get(specId).keySet()){
				profileBuilder.append("<Customization>");
				profileBuilder.append("<FieldName>"+fieldName+"</FieldName>");
				profileBuilder.append("<Type>"+envelopeCustomization.get(specId).get(fieldName).getType()+"</Type>");
				profileBuilder.append("<Value>"+envelopeCustomization.get(specId).get(fieldName).getPerturbationValue()+"</Value>");
				profileBuilder.append("</Customization>");
			}
			profileBuilder.append("</Customizations>");
		}
		profileBuilder.append("</EnvelopeCustomization>");
		
		profileBuilder.append("<SelectedAreas>");
		for(Area area:selectedAreas) profileBuilder.append(area.toXML());
		profileBuilder.append("</SelectedAreas>");
		
		profileBuilder.append("<CellExclusion>");
		for(Cell cell: excludedCells) profileBuilder.append(cell.toXML());
		profileBuilder.append("</CellExclusion>");
		
		profileBuilder.append("<EnvironmentCustomization>");
		for(String cellId:environmentCustomization.keySet()){
			profileBuilder.append("<"+Cell.Tags.ID+">"+cellId+"</"+Cell.Tags.ID+">");
			profileBuilder.append("<Customizations>");
			for(String fieldName:environmentCustomization.get(cellId).keySet()){				
				profileBuilder.append("<FieldName>"+fieldName+"</FieldName>");
				profileBuilder.append("<Type>"+environmentCustomization.get(cellId).get(fieldName).getType()+"</Type>");
				profileBuilder.append("<Value>"+environmentCustomization.get(cellId).get(fieldName).getPerturbationValue()+"</Value>");
				profileBuilder.append("</Customization>");
			}
			profileBuilder.append("</Customizations>");
		}
		profileBuilder.append("</EnvironmentCustomization>");
		
		profileBuilder.append("<AlgorithmSettings>");		
		profileBuilder.append("<Threshold>"+threshold+"</Threshold>");
		profileBuilder.append("<Source>"+projectCitation+"</Source>");
		profileBuilder.append("</AlgorithmSettings>");
		
		profileBuilder.append("</AquaMap>");
		return profileBuilder.toString();
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus() {
		return status;
	}
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setEnvelopeWeights(Map<String,List<Field>> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}
	public Map<String,List<Field>> getEnvelopeWeights() {
		return envelopeWeights;
	}
	
	
}

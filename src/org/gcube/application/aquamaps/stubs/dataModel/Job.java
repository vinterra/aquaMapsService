package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Job {
	
	
	private String id="";
	private String name="";
	private List<AquaMapsObject> aquaMapsObjectList=new ArrayList<AquaMapsObject>();
	private String status="";
	private Resource sourceHSPEN=new Resource(Resource.Type.HSPEN);
	private Resource sourceHCAF=new Resource(Resource.Type.HCAF);
	private Resource sourceHSPEC=new Resource(Resource.Type.HSPEC);
	private Set<Species> selectedSpecies=new HashSet<Species> ();
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	private Map<String,List<Field>> envelopeWeights=new HashMap<String, List<Field>>();
	private Set<Area> selectedAreas=new HashSet<Area>();
	private Map<String,Map<String,Perturbation>> environmentCustomization= new HashMap<String, Map<String,Perturbation>>();
	private String author="";
	private String date="";	
	/**
	 * @param weights the weights to set
	 */
	
	private Set<Cell> cellExclusion=new HashSet<Cell>();
	
	private List<String> related=new ArrayList<String>();	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Resource getSourceHSPEN() {
		return sourceHSPEN;
	}
	public void setSourceHSPEN(Resource sourceHSPEN) {
		this.sourceHSPEN = sourceHSPEN;
	}
	public Resource getSourceHCAF() {
		return sourceHCAF;
	}
	public void setSourceHCAF(Resource sourceHCAF) {
		this.sourceHCAF = sourceHCAF;
	}
	public Resource getSourceHSPEC() {
		return sourceHSPEC;
	}
	public void setSourceHSPEC(Resource sourceHSPEC) {
		this.sourceHSPEC = sourceHSPEC;
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
	
	public String toXML(){
		StringBuilder profileBuilder=new StringBuilder();
		profileBuilder.append("<JOB>");
		profileBuilder.append("<Name>"+name+"</Name>");
		profileBuilder.append("<Author>"+author+"</Author>");
		profileBuilder.append("<Status>"+status+"</Status>");
		profileBuilder.append("<Sources>"+
								sourceHCAF.toXML()+
								sourceHSPEC.toXML()+
								sourceHSPEN.toXML()+"</Sources>");
		profileBuilder.append("<date>"+date+"</date>");
		profileBuilder.append("<SelectedSpecies>");
		for(Species spec:selectedSpecies) profileBuilder.append(spec.toXML());
		profileBuilder.append("</SelectedSpecies>");
		
		profileBuilder.append("<EnvelopeCustomization>");
		for(String specId:envelopeCustomization.keySet()){
			//TODO weights
			profileBuilder.append("<"+Species.Tags.ID+">"+specId+"</"+Species.Tags.ID+">");
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
		for(Cell cell: cellExclusion) profileBuilder.append(cell.toXML());
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
			//profileBuilder.append("<Threshold>"+threshold+"</Threshold>");
			profileBuilder.append("<Source>"+AquaMapsObject.projectCitation+"</Source>");
		profileBuilder.append("</AlgorithmSettings>");
		
		
		profileBuilder.append("<AquaMapsObjects>");
		for(AquaMapsObject obj:aquaMapsObjectList) profileBuilder.append(obj.toXML());
		profileBuilder.append("<AquaMapsObjects>");
		
		profileBuilder.append("<RelatedResources>");
		for(String val:related)profileBuilder.append("<Related>"+val+"</Related>");
		profileBuilder.append("</RelatedResources>");
		profileBuilder.append("</JOB>");
		
		
		return profileBuilder.toString();
	}
	
	
	public int addSpecies(Collection<Species> toAdd){
		selectedSpecies.addAll(toAdd);
		return selectedSpecies.size();
	}
	
	public int addAreas(Collection<Area> toAdd){
		selectedAreas.addAll(toAdd);
		for(AquaMapsObject obj:aquaMapsObjectList)obj.addAreas(toAdd);
		return selectedAreas.size();
	}
	
	public List<AquaMapsObject> getAquaMapsObjectList() {
		return aquaMapsObjectList;
	}
	public void setAquaMapsObjectList(List<AquaMapsObject> aquaMapsObjectList) {
		this.aquaMapsObjectList = aquaMapsObjectList;
	}
	public Set<Species> getSelectedSpecies() {
		return selectedSpecies;
	}
	
	public Map<String, Map<String, Perturbation>> getEnvelopeCustomization() {
		return envelopeCustomization;
	}
	
	public Set<Area> getSelectedAreas() {
		return selectedAreas;
	}
	
	public Map<String, Map<String, Perturbation>> getEnvironmentCustomization() {
		return environmentCustomization;
	}
	
	
	public Set<Cell> getCellExclusion() {
		return cellExclusion;
	}
	
	public List<String> getRelated() {
		return related;
	}
	
	public int removeAreas(Collection<Area> toRemove){
		selectedAreas.removeAll(toRemove);
		for(AquaMapsObject obj:aquaMapsObjectList)
			obj.removeAreas(toRemove);
		return selectedAreas.size();
	}
	
	//TODO Weights
	public int removeSpecies(Collection<Species> toAdd){
		selectedSpecies.removeAll(toAdd);
		for(Species spec: toAdd) {
			envelopeWeights.remove(spec.getId());
			envelopeCustomization.remove(spec.getId());
		}
		for(AquaMapsObject obj:aquaMapsObjectList)
			if(obj.getType().equals(AquaMapsObject.Type.Biodiversity)) {
				int specsNumber=obj.removeSpecies(toAdd);
				if(specsNumber==0) aquaMapsObjectList.remove(obj);
				else if(specsNumber==1) {
					obj.setType(AquaMapsObject.Type.SpeciesDistribution);
					obj.setThreshold(0);
				}
			}
			else aquaMapsObjectList.remove(obj);
		return selectedSpecies.size();
	}
	
	public int addAquaMapsObject(Collection<AquaMapsObject> toAdd){
		aquaMapsObjectList.addAll(toAdd);
		return aquaMapsObjectList.size();
	}
	
	public int removeAquaMapsObject(Collection<AquaMapsObject> toRemove){
		aquaMapsObjectList.removeAll(toRemove);
		return aquaMapsObjectList.size();
	}
	
	public AquaMapsObject addAquaMapsObject(AquaMapsObject.Type type){
		AquaMapsObject toReturn=new AquaMapsObject();
		toReturn.setAuthor(this.author);
		toReturn.setEnvelopeCustomization(this.envelopeCustomization);
		toReturn.setEnvironmentCustomization(environmentCustomization);
		toReturn.setExcludedCells(this.cellExclusion);
		toReturn.setSelectedAreas(this.selectedAreas);
		toReturn.setType(type);
		toReturn.setEnvelopeWeights(envelopeWeights);
		this.aquaMapsObjectList.add(toReturn);
		return toReturn;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setEnvelopeWeights(Map<String,List<Field>> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}
	public Map<String,List<Field>> getEnvelopeWeights() {
		return envelopeWeights;
	}
	
}

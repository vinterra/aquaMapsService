package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject.Tags;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;


public class Job {
	
	
	private int id;
	private String name;
	private String author;
	private SubmittedStatus status=SubmittedStatus.Pending;
	private String date;
	private List<AquaMapsObject> aquaMapsObjectList=new ArrayList<AquaMapsObject>();
	
	private Boolean isGis=false;
	private String wmsContextId;
	
	
	private Resource sourceHSPEN=new Resource(ResourceType.HSPEN,1);
	private Resource sourceHCAF=new Resource(ResourceType.HCAF,1);
	private Resource sourceHSPEC=new Resource(ResourceType.HSPEC,1);
	private Set<Species> selectedSpecies=new HashSet<Species> ();
	
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	private Map<String,Map<EnvelopeFields,Field>> envelopeWeights=new HashMap<String, Map<EnvelopeFields,Field>>();
	
	private Set<Area> selectedAreas=new HashSet<Area>();
	
		
	
	/**
	 * @param weights the weights to set
	 */
	
	
	
	
	private List<File> related=new ArrayList<File>();	
	
	
	
	public Job() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SubmittedStatus getStatus() {
		return status;
	}
	public void setStatus(SubmittedStatus status) {
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
	
	
	@Deprecated
	public String toXML(){
		StringBuilder profileBuilder=new StringBuilder();
		profileBuilder.append("<JOB>");
		profileBuilder.append("<Name>"+name+"</Name>");
		profileBuilder.append("<Author>"+author+"</Author>");
		profileBuilder.append("<Status>"+status+"</Status>");
//		profileBuilder.append("<Sources>"+
//								sourceHCAF.toXML()+
//								sourceHSPEC.toXML()+
//								sourceHSPEN.toXML()+"</Sources>");
		profileBuilder.append("<date>"+date+"</date>");
		profileBuilder.append("<SelectedSpecies>");
		for(Species spec:selectedSpecies) profileBuilder.append(spec.toXML());
		profileBuilder.append("</SelectedSpecies>");
		
		profileBuilder.append("<"+Tags.envelopeCustomization+">");
		for(Species spec:selectedSpecies){
			String specId=spec.getId();
			if((envelopeWeights.containsKey(specId))||(envelopeCustomization.containsKey(specId))){
				profileBuilder.append("<"+Tags.customizationSet+" "+SpeciesOccursumFields.speciesid+" =\""+specId+"\">");
				if(envelopeWeights.containsKey(specId)){
					profileBuilder.append("<Weights>");
					for(Field field:envelopeWeights.get(specId).values()) profileBuilder.append(field.toXML());
					profileBuilder.append("</Weights>");
				}
				if(envelopeCustomization.containsKey(specId))
					for(String fieldName:envelopeCustomization.get(specId).keySet()){
						profileBuilder.append("<"+Tags.customization+">");
						profileBuilder.append("<FieldName>"+fieldName+"</FieldName>");
						profileBuilder.append("<Type>"+envelopeCustomization.get(specId).get(fieldName).getType()+"</Type>");
						profileBuilder.append("<Value>"+envelopeCustomization.get(specId).get(fieldName).getPerturbationValue()+"</Value>");
						profileBuilder.append("</"+Tags.customization+">");
					}
				profileBuilder.append("</"+Tags.customizationSet+">");				
			}			
		}
		profileBuilder.append("</"+Tags.envelopeCustomization+">");
		
		profileBuilder.append("<SelectedAreas>");
		for(Area area:selectedAreas) profileBuilder.append(area.toXML());
		profileBuilder.append("</SelectedAreas>");
		
		
//		profileBuilder.append("<"+Tags.environementCustomization+">");
//		for(String cellId:environmentCustomization.keySet()){			
//			profileBuilder.append("<"+Tags.customizationSet+" "+Cell.Tags.ID+" = \""+cellId+"\">");
//			for(String fieldName:environmentCustomization.get(cellId).keySet()){
//				profileBuilder.append("<"+Tags.customization+">");
//				profileBuilder.append("<FieldName>"+fieldName+"</FieldName>");
//				profileBuilder.append("<Type>"+environmentCustomization.get(cellId).get(fieldName).getType()+"</Type>");
//				profileBuilder.append("<Value>"+environmentCustomization.get(cellId).get(fieldName).getPerturbationValue()+"</Value>");
//				profileBuilder.append("</"+Tags.customization+">");
//			}
//			profileBuilder.append("</"+Tags.customizationSet+">");
//		}
//		profileBuilder.append("</"+Tags.environementCustomization+">");
		
		
		
		profileBuilder.append("<AlgorithmSettings>");			
			//profileBuilder.append("<Threshold>"+threshold+"</Threshold>");
//			profileBuilder.append("<Source>"+AquaMapsObject.projectCitation+"</Source>");
		profileBuilder.append("</AlgorithmSettings>");
		
		
		profileBuilder.append("<AquaMapsObjects>");
		for(AquaMapsObject obj:aquaMapsObjectList) profileBuilder.append(obj.toXML());
		profileBuilder.append("<AquaMapsObjects>");
		
//		profileBuilder.append("<RelatedResources>");
//		for(String val:related)profileBuilder.append("<Related>"+val+"</Related>");
//		profileBuilder.append("</RelatedResources>");
		profileBuilder.append("</JOB>");
		
		
		return profileBuilder.toString();
	}
	
	
	public int addSpecies(Collection<Species> toAdd){
		selectedSpecies.addAll(toAdd);
		return selectedSpecies.size();
	}
	
	public int addAreas(Collection<Area> toAdd){
		selectedAreas.addAll(toAdd);
//		for(AquaMapsObject obj:aquaMapsObjectList)obj.addAreas(toAdd);
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
	
	
	
	public List<File> getRelated() {
		return related;
	}
	public void setRelated(List<File> related) {
		this.related = related;
	}
	public int removeAreas(Collection<Area> toRemove){
		selectedAreas.removeAll(toRemove);
//		for(AquaMapsObject obj:aquaMapsObjectList)
//			obj.removeAreas(toRemove);
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
			if(obj.getType().equals(ObjectType.Biodiversity)) {
				int specsNumber=obj.removeSpecies(toAdd);
				if(specsNumber==0) aquaMapsObjectList.remove(obj);
				else if(specsNumber==1) {
					obj.setType(ObjectType.SpeciesDistribution);
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
	
	public AquaMapsObject addAquaMapsObject(ObjectType type){
		AquaMapsObject toReturn=new AquaMapsObject();
		toReturn.setAuthor(this.author);
		toReturn.setType(type);
		this.aquaMapsObjectList.add(toReturn);
		return toReturn;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setEnvelopeWeights(Map<String,Map<EnvelopeFields,Field>> envelopeWeights) {
		this.envelopeWeights = envelopeWeights;
	}
	public Map<String,Map<EnvelopeFields,Field>> getEnvelopeWeights() {
		return envelopeWeights;
	}
	
	
	public Job (org.gcube.application.aquamaps.stubs.Job toLoad){
		this.setAuthor(toLoad.getAuthor());
		this.setDate(toLoad.getDate());
		
		if((toLoad.getWeights()!=null)&&(toLoad.getWeights().getEnvelopeWeightList()!=null))
			for(org.gcube.application.aquamaps.stubs.EnvelopeWeights weights:toLoad.getWeights().getEnvelopeWeightList()){
				String speciesID=weights.getSpeciesId();
				if(!envelopeWeights.containsKey(speciesID)) 
					envelopeWeights.put(speciesID, new HashMap<EnvelopeFields, Field>());
				for(Field f: Field.load(weights.getWeights()))
						envelopeWeights.get(speciesID).put(EnvelopeFields.valueOf(f.getName()), f);
			}
		if((toLoad.getEnvelopCustomization()!=null)&&(toLoad.getEnvelopCustomization().getPerturbationList()!=null))
			for(org.gcube.application.aquamaps.stubs.Perturbation pert:toLoad.getEnvelopCustomization().getPerturbationList()){
				String speciesID=pert.getToPerturbId();
				if(!envelopeCustomization.containsKey(speciesID))
					envelopeCustomization.put(speciesID, new HashMap<String, Perturbation>());
				envelopeCustomization.get(speciesID).put(pert.getField(), new Perturbation(pert));
			}

		this.setRelated(File.load(toLoad.getRelatedResources()));
		this.setSelectedAreas(Area.load(toLoad.getSelectedAreas()));
		this.setId(toLoad.getId());
		this.setName(toLoad.getName());
		this.setSourceHCAF(new Resource(toLoad.getHcaf()));
		this.setSourceHSPEN(new Resource(toLoad.getHspen()));
		this.setSourceHSPEC(new Resource(toLoad.getHspec()));
		this.setStatus(SubmittedStatus.valueOf(toLoad.getStatus()));
		
		this.setAquaMapsObjectList(AquaMapsObject.load(toLoad.getAquaMapList()));
		this.getSelectedSpecies().addAll(Species.load(toLoad.getSelectedSpecies()));
		
		
		this.setIsGis(toLoad.isGis());
		this.setWmsContextId(toLoad.getGroupId());
		
	}

	public org.gcube.application.aquamaps.stubs.Job toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Job toReturn= new org.gcube.application.aquamaps.stubs.Job();
		toReturn.setAquaMapList(AquaMapsObject.toStubsVersion(this.getAquaMapsObjectList()));
		toReturn.setAuthor(this.getAuthor());
		toReturn.setDate(this.getDate());
		
		List<org.gcube.application.aquamaps.stubs.Perturbation> pertList=new ArrayList<org.gcube.application.aquamaps.stubs.Perturbation>();
		for(String specId: this.envelopeCustomization.keySet())
			for(String field: this.envelopeCustomization.get(specId).keySet()){
				Perturbation p=this.envelopeCustomization.get(specId).get(field);
				pertList.add(new org.gcube.application.aquamaps.stubs.Perturbation(field,specId,p.getType().toString(),p.getPerturbationValue()));
			}
		toReturn.setEnvelopCustomization(
				new org.gcube.application.aquamaps.stubs.PerturbationArray(pertList.toArray(new org.gcube.application.aquamaps.stubs.Perturbation[pertList.size()])));
		
		toReturn.setHcaf(this.sourceHCAF.toStubsVersion());
		toReturn.setHspec(this.sourceHSPEC.toStubsVersion());
		toReturn.setHspen(this.sourceHSPEN.toStubsVersion());
		toReturn.setId(this.id);
		toReturn.setName(this.name);
		toReturn.setRelatedResources(File.toStubsVersion(this.related));
		toReturn.setSelectedAreas(Area.toStubsVersion(this.selectedAreas));
		toReturn.setSelectedSpecies(Species.toStubsVersion(this.selectedSpecies));
		toReturn.setStatus(this.status.toString());
		
		toReturn.setGis(this.isGis);
		toReturn.setGroupId(this.getWmsContextId());
		
		List<org.gcube.application.aquamaps.stubs.EnvelopeWeights> weightList= new ArrayList<org.gcube.application.aquamaps.stubs.EnvelopeWeights>();
		for(String specId:this.envelopeWeights.keySet()){
			weightList.add(new org.gcube.application.aquamaps.stubs.EnvelopeWeights(
					specId,Field.toStubsVersion(this.envelopeWeights.get(specId).values())));
		}
		toReturn.setWeights(new org.gcube.application.aquamaps.stubs.EnvelopeWeightArray(weightList.toArray(new org.gcube.application.aquamaps.stubs.EnvelopeWeights[weightList.size()])));
		return toReturn;
	}
	
	
	public void setSelectedSpecies(Set<Species> selectedSpecies) {
		this.selectedSpecies = selectedSpecies;
	}
	public void setEnvelopeCustomization(
			Map<String, Map<String, Perturbation>> envelopeCustomization) {
		this.envelopeCustomization = envelopeCustomization;
	}
	public void setSelectedAreas(Set<Area> selectedAreas) {
		this.selectedAreas = selectedAreas;
	}
	
	
	public void setCustomization(Species s, Field f, Perturbation p){
		Map<String, Perturbation> map=envelopeCustomization.get(s.getId());
		if(map==null)map=new HashMap<String, Perturbation>();
		map.put(f.getName(), p);
		envelopeCustomization.put(s.getId(), map);
	}
	
	public void setWeights(Species s,List<Field> weights){
		Map<EnvelopeFields,Field> map=envelopeWeights.get(s.getId());
		if(map==null) map=new HashMap<EnvelopeFields, Field>();
		for(Field f:weights){
			map.put(EnvelopeFields.valueOf(f.getName()), f);
		}
		envelopeWeights.put(s.getId(), map);
	}
	public Boolean getIsGis() {
		return isGis;
	}
	public void setIsGis(Boolean isGis) {
		this.isGis = isGis;
	}
	public String getWmsContextId() {
		return wmsContextId;
	}
	public void setWmsContextId(String wmsContextId) {
		this.wmsContextId = wmsContextId;
	}
}

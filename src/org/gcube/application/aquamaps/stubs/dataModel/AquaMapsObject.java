package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


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
		public static final String environementCustomization="EnvironmentCustomization";
		public static final String customization="Customization";
		public static final String customizationSet="CustomizationSet";
		public static final String envelopeCustomization="EnvelopeCustomization";
	}




	private int id;	
	private String name;
	private String author;
	private SubmittedStatus status=SubmittedStatus.Pending;
	private String date;
	
	private Boolean gis=false;
	private String layerId;
	private ObjectType type=ObjectType.Biodiversity;
	
	
	private Set<Species> selectedSpecies=new HashSet<Species>();
	private List<File> relatedResources=new ArrayList<File>();
	private float threshold=0.5f;	
	private BoundingBox boundingBox=new BoundingBox();
	
	public String projectCitation=	"Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
	"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
	"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";

	
	
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
	public List<File> getRelatedResources() {
		return relatedResources;
	}
	public void setRelatedResources(List<File> relatedResources) {
		this.relatedResources = relatedResources;
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
	
	@Deprecated
	public String toXML(){
		StringBuilder profileBuilder=new StringBuilder();
		profileBuilder.append("<AquaMap>");
		profileBuilder.append("<Name>"+name+"</Name>");
		profileBuilder.append("<Author>"+author+"</Author>");
		profileBuilder.append("<BoundingBox>"+boundingBox.toString()+"</BoundingBox>");
		profileBuilder.append("<Identifier>"+id+"</Identifier>");
		//		profileBuilder.append("<Source>"+source+"</Source>");
		profileBuilder.append("<Status>"+status+"</Status>");
		profileBuilder.append("<Type>"+type.toString()+"</Type>");
		profileBuilder.append("<date>"+date+"</date>");
		profileBuilder.append("<Gis>"+gis+"</Gis>");
		//		profileBuilder.append("<RelatedResources>");
		//		for(Entry<String,String> entry : relatedResources.entrySet()){
		//			profileBuilder.append("<Resource>");
		//			profileBuilder.append("<Name>"+entry.getKey()+"</Name>");
		//			profileBuilder.append("<Url>"+entry.getValue()+"</Url>");
		//			profileBuilder.append("</Resource>");
		//		}
		//		profileBuilder.append("<Resource>");
		//		profileBuilder.append("<Name>Profile</Name>");
		//		profileBuilder.append("<Url>"+profileUrl+"</Url>");
		//		profileBuilder.append("</Resource>");		
		//		profileBuilder.append("</RelatedResources>");



		profileBuilder.append("<SelectedSpecies>");
		for(Species spec:selectedSpecies) profileBuilder.append(spec.toXML());
		profileBuilder.append("</SelectedSpecies>");

		//		profileBuilder.append("<"+Tags.envelopeCustomization+">");
		//		for(Species spec:selectedSpecies){
		//			String specId=spec.getId();
		//			if((envelopeWeights.containsKey(specId))||(envelopeCustomization.containsKey(specId))){
		//				profileBuilder.append("<"+Tags.customizationSet+" "+SpeciesOccursumFields.SpeciesID+" =\""+specId+"\">");
		//				if(envelopeWeights.containsKey(specId)){
		//					profileBuilder.append("<Weights>");
		//					for(Field field:envelopeWeights.get(specId).values()) profileBuilder.append(field.toXML());
		//					profileBuilder.append("</Weights>");
		//				}
		//				if(envelopeCustomization.containsKey(specId))
		//					for(String fieldName:envelopeCustomization.get(specId).keySet()){
		//						profileBuilder.append("<"+Tags.customization+">");
		//						profileBuilder.append("<FieldName>"+fieldName+"</FieldName>");
		//						profileBuilder.append("<Type>"+envelopeCustomization.get(specId).get(fieldName).getType()+"</Type>");
		//						profileBuilder.append("<Value>"+envelopeCustomization.get(specId).get(fieldName).getPerturbationValue()+"</Value>");
		//						profileBuilder.append("</"+Tags.customization+">");
		//					}
		//				profileBuilder.append("</"+Tags.customizationSet+">");				
		//			}			
		//		}
		//		profileBuilder.append("</"+Tags.envelopeCustomization+">");

		//		profileBuilder.append("<SelectedAreas>");
		//		for(Area area:selectedAreas) profileBuilder.append(area.toXML());
		//		
		//		profileBuilder.append("</SelectedAreas>");


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
		profileBuilder.append("<Threshold>"+threshold+"</Threshold>");
		profileBuilder.append("<Source>"+projectCitation+"</Source>");
		profileBuilder.append("</AlgorithmSettings>");

		profileBuilder.append("</AquaMap>");
		return profileBuilder.toString();
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
	//	public void setProfileUrl(String profileUrl) {
	//		this.profileUrl = profileUrl;
	//	}
	//	public String getProfileUrl() {
	//		return profileUrl;
	//	}
	//	public void setEnvelopeWeights(Map<String,Map<EnvelopeFields,Field>> envelopeWeights) {
	//		this.envelopeWeights = envelopeWeights;
	//	}
	//	public Map<String,Map<EnvelopeFields,Field>> getEnvelopeWeights() {
	//		return envelopeWeights;
	//	}

	@Deprecated
	public AquaMapsObject(String profile) throws ParserConfigurationException{
		super();
		Document doc=XMLUtils.getDocumentGivenXML(profile);

		Element nameElement=(Element) doc.getElementsByTagName("Name").item(0);
		this.setName(XMLUtils.getTextContent(nameElement));
		Element idElement=(Element) doc.getElementsByTagName("Identifier").item(0);
		this.setId(Integer.parseInt(XMLUtils.getTextContent(idElement)));
		Element dateElement=(Element) doc.getElementsByTagName("Date").item(0);
		this.setDate(XMLUtils.getTextContent(dateElement));
		Element authorElement=(Element) doc.getElementsByTagName("Author").item(0);
		this.setAuthor(XMLUtils.getTextContent(authorElement));
		Element typeElement=(Element) doc.getElementsByTagName("Type").item(0);
		
		//FIXME Workaround for old profiles without Status value
		try{
		this.setType(ObjectType.valueOf(XMLUtils.getTextContent(typeElement)));
		Element statusElement=(Element) doc.getElementsByTagName("Status").item(0);
		this.setStatus(SubmittedStatus.valueOf(XMLUtils.getTextContent(statusElement)));
		}catch(Exception e){
			this.setStatus(SubmittedStatus.Completed);
		}
		
		try{
			Element gisElement=(Element) doc.getElementsByTagName("Gis").item(0);
			this.setGis(Boolean.parseBoolean(XMLUtils.getTextContent(gisElement)));
		}catch(Exception e){/* GIS TAG non trovato*/}			
		
		
		
		Element bbElement=(Element) doc.getElementsByTagName("BoundingBox").item(0);
		this.getBoundingBox().parse(XMLUtils.getTextContent(bbElement));
		Element thresholdElement=(Element) doc.getElementsByTagName("Threshold").item(0);
		this.setThreshold(Float.parseFloat(XMLUtils.getTextContent(thresholdElement)));
		try{
			Element gisElement=(Element) doc.getElementsByTagName("Threshold").item(0);
			this.setGis(Boolean.valueOf(XMLUtils.getTextContent(gisElement)));
		}catch(Exception e){
			//Backward compatibility
			this.setGis(false);
		}

		NodeList speciesNodes=doc.getElementsByTagName("Species");
		ArrayList<Species> specList=new ArrayList<Species>(); 
		for(int i=0;i<speciesNodes.getLength();i++){
			Element speciesEl=(Element) speciesNodes.item(i);
			specList.add(new Species(speciesEl));
		}
		this.addSpecies(specList);

		//		Element envelopeElement =(Element)doc.getElementsByTagName(Tags.envelopeCustomization).item(0);
		//		if(envelopeElement!=null){
		//			NodeList customizedSpeciesNodes=envelopeElement.getElementsByTagName(Tags.customizationSet);		
		//			for(int i=0;i<customizedSpeciesNodes.getLength();i++){
		//				Element customizationSetEl=(Element)customizedSpeciesNodes.item(i);
		//				String specId= customizationSetEl.getAttribute(SpeciesOccursumFields.SpeciesID+"");
		//				// look for weights
		//				NodeList weights=customizationSetEl.getElementsByTagName("Weights");
		//				if((weights!=null)&&(weights.getLength()>0)){
		//					NodeList settedWeights=((Element)weights.item(0)).getElementsByTagName("Field");
		//					Map<EnvelopeFields,Field> toPutList=new HashMap<EnvelopeFields, Field>();
		//					for(int j=0;j<settedWeights.getLength();j++){
		//						Field f=new Field((Element) settedWeights.item(j));
		//						toPutList.put(EnvelopeFields.valueOf(f.getName()),f);
		//					}
		////					this.getEnvelopeWeights().put(specId, toPutList);
		//				}
		//				//look for customizations
		//				NodeList customizations=customizationSetEl.getElementsByTagName(Tags.customization);
		//				if((customizations!=null)&&(customizations.getLength()>0)){
		//					Map<String,Perturbation> toPutMap=new HashMap<String, Perturbation>();
		//					for(int j=0;j<customizations.getLength();j++){
		//						Element customization=(Element) customizations.item(j);
		//						Element fieldName=(Element) customization.getElementsByTagName("FieldName").item(0);
		//						Element type=(Element) customization.getElementsByTagName("Type").item(0);
		//						Element value=(Element) customization.getElementsByTagName("Value").item(0);
		//						Perturbation pert=new Perturbation(PerturbationType.valueOf(XMLUtils.getTextContent(type)),
		//								XMLUtils.getTextContent(value));
		//						toPutMap.put(XMLUtils.getTextContent(fieldName), pert);
		//					}
		////					this.getEnvelopeCustomization().put(specId, toPutMap);
		//				}
		//			}
		//		}

	}
	public void setGis(Boolean gis) {
		this.gis = gis;
	}
	public Boolean getGis() {
		return gis;
	}

	public AquaMapsObject(org.gcube.application.aquamaps.stubs.AquaMap toLoad){
		this.setAuthor(toLoad.getAuthor());
		this.getBoundingBox().parse(toLoad.getBoundingBox());
		this.setDate(toLoad.getDate());
		this.getRelatedResources().addAll(File.load(toLoad.getRelatedResources()));
		this.getSelectedSpecies().addAll(Species.load(toLoad.getSelectedSpecies()));
		this.setGis(toLoad.isGis());
		this.setId(toLoad.getId());
		this.setName(toLoad.getName());
		this.setStatus(SubmittedStatus.valueOf(toLoad.getStatus()));
		this.setThreshold(toLoad.getThreshold());
		this.setType(ObjectType.valueOf(toLoad.getType()));	
		this.setLayerId(toLoad.getLayerId());
	}
	public static List<AquaMapsObject> load(org.gcube.application.aquamaps.stubs.AquaMapArray toLoad){
		List<AquaMapsObject> toReturn= new ArrayList<AquaMapsObject>();
		if((toLoad!=null)&&(toLoad.getAquaMapList()!=null))
			for(org.gcube.application.aquamaps.stubs.AquaMap a: toLoad.getAquaMapList())
				toReturn.add(new AquaMapsObject(a));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.AquaMapArray toStubsVersion(List<AquaMapsObject> toConvert){
		List<org.gcube.application.aquamaps.stubs.AquaMap> list=new ArrayList<org.gcube.application.aquamaps.stubs.AquaMap>();
		if(toConvert!=null)
			for(AquaMapsObject obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.AquaMapArray(list.toArray(new org.gcube.application.aquamaps.stubs.AquaMap[list.size()]));
	}

	public org.gcube.application.aquamaps.stubs.AquaMap toStubsVersion(){
		org.gcube.application.aquamaps.stubs.AquaMap toReturn= new org.gcube.application.aquamaps.stubs.AquaMap();
		toReturn.setAuthor(this.author);
		toReturn.setBoundingBox(this.boundingBox.toString());
		toReturn.setDate(this.date);
		toReturn.setGis(this.gis);
		toReturn.setId(this.id);
		toReturn.setName(this.name);
		toReturn.setRelatedResources(File.toStubsVersion(this.relatedResources));
		toReturn.setSelectedSpecies(Species.toStubsVersion(this.selectedSpecies));
		toReturn.setStatus(this.status+"");
		toReturn.setThreshold(this.threshold);
		toReturn.setType(this.type.toString());
		toReturn.setLayerId(this.layerId);
		return toReturn;
	}

	public AquaMapsObject(String name,int id, ObjectType type){
		this.setName(name);
		this.setId(id);
		this.setType(type);
	}
	public AquaMapsObject(){}
	
	
	
	public void setLayerId(String layerId) {
		this.layerId = layerId;
	}
	public String getLayerId() {
		return layerId;
	}
}

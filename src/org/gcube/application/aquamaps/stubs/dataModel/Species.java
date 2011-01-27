package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.stubs.dataModel.util.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;


@XStreamAlias("Species")
public class Species {

	@XStreamAlias("SpeciesID")
	@XStreamAsAttribute
	private String id;
	
	@XStreamOmitField
	public List<Field> attributesList=new ArrayList<Field>();
	public List<Field> getAttributesList() {
		return attributesList;
	}
	public void setAttributesList(List<Field> attributesList) {
		this.attributesList = attributesList;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

	public Field getFieldbyName(String fieldName){
		for(Field field:attributesList){
			if(field.getName().equals(fieldName)) return field;
		}
		return null;		
	}

	public void addField(Field toAddField){
		attributesList.add(toAddField);
	}

	public Envelope extractEnvelope(){
		Envelope toReturn=new Envelope();
		for(EnvelopeFields envelopeField:EnvelopeFields.values()){
			for(HspenFields paramName:toReturn.getValueNames(envelopeField)){
				toReturn.setValue(envelopeField, paramName,Double.parseDouble(this.getFieldbyName(paramName.toString()).getValue()));
			}
		}
		
		String eString=this.getFieldbyName(HspenFields.EMostLong+"").getValue();
		if((eString!=null)&&(!eString.equalsIgnoreCase("null")))
			toReturn.getBoundingBox().setE(Float.valueOf(eString));
		
		String nString=this.getFieldbyName(HspenFields.NMostLat+"").getValue();
		if((nString!=null)&&(!nString.equalsIgnoreCase("null")))
			toReturn.getBoundingBox().setN(Float.valueOf(nString));
		
		String wString=this.getFieldbyName(HspenFields.WMostLong+"").getValue();
		if((wString!=null)&&(!wString.equalsIgnoreCase("null")))
			toReturn.getBoundingBox().setW(Float.valueOf(wString));
		
		String sString=this.getFieldbyName(HspenFields.SMostLat+"").getValue();
		if((sString!=null)&&(!sString.equalsIgnoreCase("null")))
			toReturn.getBoundingBox().setS(Float.valueOf(sString));
		
		toReturn.setFaoAreas(this.getFieldbyName(HspenFields.FAOAreas+"").getValue());
		toReturn.setPelagic(Boolean.parseBoolean(this.getFieldbyName(HspenFields.Pelagic+"").getValue()));
		toReturn.setUseBottomSeaTempAndSalinity(this.getFieldbyName(HspenFields.Layer+"").getValue().equalsIgnoreCase("b"));
		return toReturn;
	}


	//	public String toJSON(){
	//		StringBuilder toReturn=new StringBuilder();
	//		toReturn.append("{\""+Tags.ID+"\":\""+id+"\"");
	//		for(Field field:attributesList){
	//			toReturn.append(" ,\""+field.getName()+"\":\""+field.getValue()+"\"");
	//		}
	//		toReturn.append("}");
	//		return toReturn.toString();
	//	}
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Species>");
		toReturn.append("<"+SpeciesOccursumFields.SpeciesID+">"+id+"</"+SpeciesOccursumFields.SpeciesID+">");
		toReturn.append("<Attributes>");
		for(Field field:attributesList){
			toReturn.append(field.toXML());
		}
		toReturn.append("</Attributes>");
		toReturn.append("</Species>");
		return toReturn.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Species))
			return false;
		Species other = (Species) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Species (Element el){
		Element idEl=(Element) el.getElementsByTagName(SpeciesOccursumFields.SpeciesID.toString()).item(0);
		this.setId(XMLUtils.getTextContent(idEl));
		NodeList fieldNodes=el.getElementsByTagName("Field");
		for(int i=0;i<fieldNodes.getLength();i++){
			this.addField(new Field((Element)fieldNodes.item(i)));
		}
	}

	public Species(org.gcube.application.aquamaps.stubs.Specie toLoad){
		super();
		this.setId(toLoad.getId());
		this.getAttributesList().addAll(Field.load(toLoad.getAdditionalField()));
	}

	public static List<Species> load(org.gcube.application.aquamaps.stubs.SpeciesArray toLoad){
		ArrayList<Species> toReturn = new ArrayList<Species>();
		if((toLoad!=null)&&(toLoad.getSpeciesList()!=null))
			for(org.gcube.application.aquamaps.stubs.Specie s:toLoad.getSpeciesList())
				toReturn.add(new Species(s));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.stubs.SpeciesArray toStubsVersion(Set<Species> toConvert){
		List<org.gcube.application.aquamaps.stubs.Specie> list=new ArrayList<org.gcube.application.aquamaps.stubs.Specie>();
		if(toConvert!=null)
			for(Species obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.stubs.SpeciesArray(list.toArray(new org.gcube.application.aquamaps.stubs.Specie[list.size()]));
	}

	public org.gcube.application.aquamaps.stubs.Specie toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Specie toReturn=new org.gcube.application.aquamaps.stubs.Specie();
		toReturn.setAdditionalField(Field.toStubsVersion(this.getAttributesList()));
		toReturn.setId(this.id);
		return toReturn;
	}

	public Species(String speciesId){
		this.id=speciesId;
	}
}

package org.gcube.application.aquamaps.stubs.dataModel.util;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Parsers {
	public static AquaMapsObject parseProfile(String profile) throws ParserConfigurationException{
		AquaMapsObject toReturn=new AquaMapsObject();
		Document doc=XMLUtils.getDocumentGivenXML(profile);
		
		Element nameElement=(Element) doc.getElementsByTagName("Name").item(0);
		toReturn.setName(XMLUtils.getTextContent(nameElement));
		Element idElement=(Element) doc.getElementsByTagName("ID").item(0);
		toReturn.setId(XMLUtils.getTextContent(idElement));
		Element dateElement=(Element) doc.getElementsByTagName("Date").item(0);
		toReturn.setDate(XMLUtils.getTextContent(dateElement));
		Element authorElement=(Element) doc.getElementsByTagName("Author").item(0);
		toReturn.setAuthor(XMLUtils.getTextContent(authorElement));
		Element typeElement=(Element) doc.getElementsByTagName("Type").item(0);
		toReturn.setType(AquaMapsObject.Type.valueOf(XMLUtils.getTextContent(typeElement)));
		Element statusElement=(Element) doc.getElementsByTagName("Status").item(0);
		toReturn.setStatus(XMLUtils.getTextContent(statusElement));
		Element bbElement=(Element) doc.getElementsByTagName("BoundingBox").item(0);
		toReturn.getBoundingBox().parse(XMLUtils.getTextContent(bbElement));
		Element thresholdElement=(Element) doc.getElementsByTagName("Threshold").item(0);
		toReturn.setThreshold(Float.parseFloat(XMLUtils.getTextContent(thresholdElement)));
		NodeList speciesNodes=doc.getElementsByTagName("Species");
		ArrayList<Species> specList=new ArrayList<Species>(); 
		for(int i=0;i<speciesNodes.getLength();i++){
		 Element speciesEl=(Element) speciesNodes.item(i);
		 Species toAdd=parseSpecies(speciesEl);
		 specList.add(toAdd);
		}
		toReturn.addSpecies(specList);
		return toReturn;
	}	
	
	public static Species parseSpecies(Element el){
		Species toReturn=new Species();
		Element idEl=(Element) el.getElementsByTagName(Species.Tags.ID).item(0);
		toReturn.setId(XMLUtils.getTextContent(idEl));
		NodeList fieldNodes=el.getElementsByTagName("Field");
		for(int i=0;i<fieldNodes.getLength();i++){
			toReturn.addField(parseField((Element)fieldNodes.item(i)));
		}
		return toReturn;
	}
	
	public static Area parseArea(Element el){
		Area toReturn=new Area();
		return toReturn;
	}
	
	public static Cell parseCell(Element el){
		Cell toReturn=new Cell();
		return toReturn;
	}
	public static Field parseField(Element el){
		Field toReturn=new Field();
		Element typeElement=(Element) el.getElementsByTagName("Type").item(0);
		toReturn.setType(Field.Type.valueOf(XMLUtils.getTextContent(typeElement)));
		Element nameElement=(Element) el.getElementsByTagName("Name").item(0);
		toReturn.setName(XMLUtils.getTextContent(nameElement));
		Element valueElement=(Element) el.getElementsByTagName("Value").item(0);
		toReturn.setValue(XMLUtils.getTextContent(valueElement));
		return toReturn;
	}
	
	public static Perturbation parsePerturbation(Element el){
		Perturbation toReturn=new Perturbation();
		return toReturn;
	}
	
}

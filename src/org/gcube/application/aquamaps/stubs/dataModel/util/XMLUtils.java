package org.gcube.application.aquamaps.stubs.dataModel.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLUtils {
	public static Document getDocumentGivenXML(String result) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
//		Using factory get an instance of document builder
		DocumentBuilder db;
		Document document = null;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(new ByteArrayInputStream(result.getBytes()));	
		} catch (ParserConfigurationException e1) {			
			e1.printStackTrace();
		} catch (SAXException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}

		return document;

	}
	
	public static String getTextContent(Element parent) { 
		StringBuffer result = new StringBuffer(); 
		if(parent == null) 
			return result.toString(); 
		Node current = parent.getFirstChild(); 
		while(current != null) { 
			if(current.getNodeType() == Node.TEXT_NODE) { 
				result.append(current.getNodeValue()); 
			} 
			current = current.getNextSibling(); 
		} 


		return result.toString(); 
	} 
}

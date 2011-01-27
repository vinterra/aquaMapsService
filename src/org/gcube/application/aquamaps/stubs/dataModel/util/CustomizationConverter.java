package org.gcube.application.aquamaps.stubs.dataModel.util;

import java.util.Map;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class CustomizationConverter implements Converter {

	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
		Map<String,Map<String,Perturbation>> toMarshal=(Map<String, Map<String, Perturbation>>) obj;
		for(Entry<String,Map<String,Perturbation>> entry:toMarshal.entrySet()){
			writer.startNode("Customization");
			writer.addAttribute(SpeciesOccursumFields.SpeciesID+"", entry.getKey());
			
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Deprecated
	public boolean canConvert(Class arg0) {
		return true;
	}

}

package org.gcube.application.aquamaps.stubs.dataModel.util;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Area;
import org.gcube.application.aquamaps.stubs.Cell;
import org.gcube.application.aquamaps.stubs.EnvelopeWeights;
import org.gcube.application.aquamaps.stubs.Field;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.File;
import org.gcube.application.aquamaps.stubs.FileArray;
import org.gcube.application.aquamaps.stubs.Filter;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.Resource;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.application.aquamaps.stubs.Weight;
import org.gcube.application.aquamaps.stubs.dataModel.Envelope;
import org.gcube.application.aquamaps.stubs.dataModel.Species;


public class StubsToModel {
	
	
	
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Field.Type SQLtoFieldType(String sqlType){		
		if((sqlType.equalsIgnoreCase("float")) ||(sqlType.equalsIgnoreCase("double"))) return org.gcube.application.aquamaps.stubs.dataModel.Field.Type.DOUBLE;
		else if((sqlType.equalsIgnoreCase("tinyint"))|| (sqlType.equalsIgnoreCase("int"))) return org.gcube.application.aquamaps.stubs.dataModel.Field.Type.INTEGER;
		else if((sqlType.equalsIgnoreCase("byte"))|| (sqlType.equalsIgnoreCase("boolean"))) return org.gcube.application.aquamaps.stubs.dataModel.Field.Type.BOOLEAN;
		else return org.gcube.application.aquamaps.stubs.dataModel.Field.Type.STRING;
	}
	
	
	
	public static String resourceTyperesolver(org.gcube.application.aquamaps.stubs.dataModel.Resource.Type type){
		switch(type){
		case HCAF: return ModelCostants.HCAF;
		case HSPEC: return ModelCostants.HSPEC;
		case HSPEN:return ModelCostants.HSPEN;
		case JOB:return ModelCostants.JOB;
		default: return null;
		}
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Field translateToClient(Field toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Field toReturn= new org.gcube.application.aquamaps.stubs.dataModel.Field();
		if(toTranslate.getName()!=null)toReturn.setName(toTranslate.getName());
		else toReturn.setName(ModelCostants.VOID);
		if(toTranslate.getValue()!=null)toReturn.setValue(toTranslate.getValue());
		else toReturn.setValue(ModelCostants.VOID);		
			toReturn.setType(SQLtoFieldType(toTranslate.getType()));
		return toReturn;	
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Area translateToClient(Area toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Area toReturn=new org.gcube.application.aquamaps.stubs.dataModel.Area();
		if(toTranslate.getType().equals(ModelCostants.FAO))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Area.Type.FAO);
		else if(toTranslate.getType().equals(ModelCostants.LME))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Area.Type.LME);
		else //default Value
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Area.Type.EEZ);
		if(toTranslate.getName()!=null)toReturn.setName(toTranslate.getName());
		else toReturn.setName(ModelCostants.VOID);
		if(toTranslate.getCode()!=null)toReturn.setCode(toTranslate.getCode());
		else toReturn.setCode(ModelCostants.VOID);		
		for(Field field:toTranslate.getAdditionalField().getFields()){
			toReturn.attributes.put(field.getName(), translateToClient(field));
		}
		return toReturn;		
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Cell translateToClient(Cell toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Cell toReturn=new org.gcube.application.aquamaps.stubs.dataModel.Cell();
		toReturn.setCode(toTranslate.getCode());
		for(Field field:toTranslate.getAdditionalField().getFields()){
			toReturn.attributes.put(field.getName(), translateToClient(field));
		}
		return toReturn;
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Filter translateToClient(Filter toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Filter toReturn=new org.gcube.application.aquamaps.stubs.dataModel.Filter();
		if(toTranslate.getType().equals(ModelCostants.is))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Filter.Type.is);
		else if(toTranslate.getType().equals(ModelCostants.contains))
		toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Filter.Type.contains);
		else if(toTranslate.getType().equals(ModelCostants.begins))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Filter.Type.begins);
		else //default Value
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Filter.Type.ends);
		org.gcube.application.aquamaps.stubs.dataModel.Field field=new org.gcube.application.aquamaps.stubs.dataModel.Field();
		if(toTranslate.getName()!=null)field.setName(toTranslate.getName());
		else field.setName(ModelCostants.VOID);
		if(toTranslate.getValue()!=null)field.setValue(toTranslate.getValue());
		else field.setValue(ModelCostants.VOID);		
		field.setType(org.gcube.application.aquamaps.stubs.dataModel.Field.Type.STRING);
		toReturn.setField(field);
		return toReturn;
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Resource translateToClient(Resource toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Resource toReturn=new org.gcube.application.aquamaps.stubs.dataModel.Resource();
		if(toTranslate.getType().equals(ModelCostants.HCAF))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Resource.Type.HCAF);
		else if(toTranslate.getType().equals(ModelCostants.HSPEC))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Resource.Type.HSPEC);
		else if(toTranslate.getType().equals(ModelCostants.HSPEN))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Resource.Type.HSPEN);
		else if(toTranslate.getType().equals(ModelCostants.JOB))
			toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Resource.Type.JOB);
		org.gcube.application.aquamaps.stubs.dataModel.Field idField=new org.gcube.application.aquamaps.stubs.dataModel.Field();
		if(toTranslate.getId()!=null)idField.setValue(toTranslate.getId());
		else idField.setValue(ModelCostants.VOID);
		idField.setType(org.gcube.application.aquamaps.stubs.dataModel.Field.Type.STRING);
		idField.setName(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.RESID);
		toReturn.attributes.put(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.RESID, idField);
		org.gcube.application.aquamaps.stubs.dataModel.Field nameField=new org.gcube.application.aquamaps.stubs.dataModel.Field();
		if(toTranslate.getName()!=null)nameField.setValue(toTranslate.getName());
		else nameField.setValue(ModelCostants.VOID);
		nameField.setType(org.gcube.application.aquamaps.stubs.dataModel.Field.Type.STRING);
		nameField.setName(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.NAME);
		toReturn.attributes.put(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.NAME, idField);
		for(Field field : toTranslate.getAdditionalField().getFields()){
			if(field==null){
				System.out.println("field null for resource "+ nameField.getValue());
			}else{
			toReturn.attributes.put(field.getName(), translateToClient(field));}
		}
		return toReturn;
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Species translateToClient(Specie toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Species toReturn= new org.gcube.application.aquamaps.stubs.dataModel.Species();
		if(toTranslate.getId()!=null)toReturn.setId(toTranslate.getId());
		else toReturn.setId(ModelCostants.VOID);
		for(Field field : toTranslate.getAdditionalField().getFields()){
			toReturn.attributesList.add(translateToClient(field));
		}
		return toReturn;
	}
	

	public static Envelope FieldsToEnvelope(FieldArray fields)throws Exception{
		Envelope toReturn=new Envelope();
		String north="90";
		String south="-90";
		String east="180";
		String west="-180";
		if(fields.getFields()==null) throw new Exception("Envelope definition not found in the selected source table");
		for(Field field:fields.getFields()){
			if(field.getName().equalsIgnoreCase(Species.Tags.FAOAreaM))toReturn.setFaoAreas(field.getValue());
			else if(field.getName().equalsIgnoreCase(Species.Tags.EMostLong))east=(field.getValue()!=null)?field.getValue():east;
			else if(field.getName().equalsIgnoreCase(Species.Tags.WMostLong))west=(field.getValue()!=null)?field.getValue():west;
			else if(field.getName().equalsIgnoreCase(Species.Tags.SMostLat))south=(field.getValue()!=null)?field.getValue():south;
			else if(field.getName().equalsIgnoreCase(Species.Tags.NMostLat))north=(field.getValue()!=null)?field.getValue():north;			
			else if(field.getName().equalsIgnoreCase(Species.Tags.Pelagic)) toReturn.setPelagic(Boolean.parseBoolean(field.getValue()));
			else
				for(Envelope.Fields paramName:toReturn.getParameterNames())
					for(String valueName: toReturn.getValueNames(paramName))
						try{
							if(field.getName().equalsIgnoreCase(valueName)) toReturn.setValue(paramName, valueName, Float.parseFloat(field.getValue()));					 	
						}catch(NullPointerException e){
							toReturn.setValue(paramName, valueName, 0);
						}

		}
		toReturn.getBoundingBox().parse(north+","+south+","+east+","+west);
		return toReturn;
	}
	
/*	public static org.gcube.application.aquamaps.dataModel.AquaMapsObject translateToClient(AquaMap obj){
		 org.gcube.application.aquamaps.dataModel.AquaMapsObject toReturn=new  org.gcube.application.aquamaps.dataModel.AquaMapsObject();
		 toReturn.setAuthor(obj.getAuthor());
		 toReturn.getBoundingBox().parse(obj.getBoundingBox());
		 toReturn.setCreator(obj.getCreator());
		 toReturn.setDate(obj.getDate());
		 if((obj.getEnvelopCustomization()!=null)&&(obj.getEnvelopCustomization().getPerturbationList()!=null)){
			 for(Perturbation p : obj.getEnvelopCustomization().getPerturbationList()){
				 if(!toReturn.getEnvelopeCustomization().containsKey(p.getToPerturbId()))
					  toReturn.getEnvelopeCustomization().put(p.getToPerturbId(), new HashMap<String, org.gcube.application.aquamaps.dataModel.Perturbation>());				
				 toReturn.getEnvelopeCustomization().get(p.getToPerturbId()).put(p.getField(), translateToClient(p));
			 }
		 }
		 if((obj.getEnvironmentCustomization()!=null)&&(obj.getEnvironmentCustomization().getPerturbationList()!=null)){
			 for(Perturbation p : obj.getEnvironmentCustomization().getPerturbationList()){
				 if(!toReturn.getEnvironmentCustomization().containsKey(p.getToPerturbId()))
					  toReturn.getEnvironmentCustomization().put(p.getToPerturbId(), new HashMap<String, org.gcube.application.aquamaps.dataModel.Perturbation>());				
				 toReturn.getEnvironmentCustomization().get(p.getToPerturbId()).put(p.getField(), translateToClient(p));
			 }
		 }
		 if((obj.getWeights()!=null)&&(obj.getWeights().getEnvelopeWeightList()!=null)){
			 for(EnvelopeWeights w : obj.getWeights().getEnvelopeWeightList()){
				 List<org.gcube.application.aquamaps.dataModel.Field> list = new ArrayList<org.gcube.application.aquamaps.dataModel.Field>();
				 if((w.getWeights()!=null)&&(w.getWeights().getWeightList()!=null))
					 for(Weight weight : w.getWeights().getWeightList())
						 list.add(translateToClient(weight));
				 toReturn.getEnvelopeWeights().put(w.getSpeciesId(),list);
			 }
		 }
		 if((obj.getExcludedCells()!=null)&&(obj.getExcludedCells().getCellList()!=null)){
			 for(Cell c:obj.getExcludedCells().getCellList()){
				 toReturn.getExcludedCells().add(translateToClient(c));
			 }
		 }
		 toReturn.setId(obj.getId());
		 toReturn.setName(obj.getName());
		 //TODO Change stubs
		 FileArray files=obj.getRelatedResources()
		 if((files!=null)&&(files.getFileList()!=null)){
				for(File f:files.getFileList()){
					if(f.getType().equalsIgnoreCase("xml")) toUpdate.setProfileUrl(f.getUrl());
					else fileMap.put(f.getName(), f.getUrl());
				}
			}
		 toReturn.setProfileUrl(obj.get)
		 toReturn.setPublisher(obj.getPublisher());
		 toReturn.setRelatedResources(relatedResources)
		 if()toReturn.setSelectedAreas(selectedAreas)
	}*/
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Perturbation translateToClient(Perturbation obj){
		 org.gcube.application.aquamaps.stubs.dataModel.Perturbation.Type type=org.gcube.application.aquamaps.stubs.dataModel.Perturbation.Type.valueOf(obj.getType());
		org.gcube.application.aquamaps.stubs.dataModel.Perturbation toReturn=new org.gcube.application.aquamaps.stubs.dataModel.Perturbation(type,obj.getValue());
		return toReturn;
	}
	
	public static org.gcube.application.aquamaps.stubs.dataModel.Field translateToClient(Weight toTranslate){
		org.gcube.application.aquamaps.stubs.dataModel.Field toReturn= new org.gcube.application.aquamaps.stubs.dataModel.Field();
		if(toTranslate.getParameterName()!=null)toReturn.setName(toTranslate.getParameterName());
		else toReturn.setName(ModelCostants.VOID);
		toReturn.setValue(String.valueOf(toTranslate.isChosenWeight()));				
		toReturn.setType(org.gcube.application.aquamaps.stubs.dataModel.Field.Type.BOOLEAN);
		return toReturn;	
	}
}

package org.gcube.application.aquamaps.stubs.dataModel.util;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.AquaMapArray;
import org.gcube.application.aquamaps.stubs.Area;
import org.gcube.application.aquamaps.stubs.AreasArray;
import org.gcube.application.aquamaps.stubs.Cell;
import org.gcube.application.aquamaps.stubs.CellArray;
import org.gcube.application.aquamaps.stubs.EnvelopeWeightArray;
import org.gcube.application.aquamaps.stubs.EnvelopeWeights;
import org.gcube.application.aquamaps.stubs.Field;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.Filter;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.PerturbationArray;
import org.gcube.application.aquamaps.stubs.Resource;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.application.aquamaps.stubs.SpeciesArray;
import org.gcube.application.aquamaps.stubs.StringArray;
import org.gcube.application.aquamaps.stubs.Weight;
import org.gcube.application.aquamaps.stubs.WeightArray;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;

public class ModelToStubs {
	public static Cell translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Cell toTranslate){
		Cell toReturn=new Cell();
		toReturn.setCode(toTranslate.getCode());
		List<Field> appList=new ArrayList<Field>();
		for(String key:toTranslate.attributes.keySet()){			
				Field appField=translateToServer(toTranslate.attributes.get(key));
				appList.add(appField);			
		}
		toReturn.setAdditionalField(new FieldArray(appList.toArray(new Field[appList.size()])));
		return toReturn;
	}
	
	
	public static Field translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Field toTranslate){
		Field toReturn=new Field();
		toReturn.setName(toTranslate.getName());
		toReturn.setType(toTranslate.getType().toString());			
		toReturn.setValue(toTranslate.getValue());
		return toReturn;
	}
	
	public static Area translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Area toTranslate){
	 Area toReturn=new Area();
	 toReturn.setCode(toTranslate.getCode());
	 toReturn.setName(toTranslate.getName());
	 switch(toTranslate.getType()){
	 case FAO: toReturn.setType(ModelCostants.FAO);
	 				break;
	 case EEZ: toReturn.setType(ModelCostants.EEZ);
	 			break;
	 case LME: toReturn.setType(ModelCostants.LME);
	 			break;
	 }
	 List<Field> appList=new ArrayList<Field>();
		for(String key:toTranslate.attributes.keySet()){			
				Field appField=translateToServer(toTranslate.attributes.get(key));
				appList.add(appField);			
		}
		toReturn.setAdditionalField(new FieldArray(appList.toArray(new Field[appList.size()])));
		return toReturn;
	}
	
	public static Filter translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Filter toTranslate){
		Filter toReturn=new Filter();
		toReturn.setName(toTranslate.getField().getName());
		toReturn.setValue(toTranslate.getField().getValue());
		toReturn.setType(toTranslate.getType().toString());
		return toReturn;
	}
	public static Resource translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Resource toTranslate){
		Resource toReturn=new Resource();
		toReturn.setName(toTranslate.attributes.get(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.NAME).getValue());
		
		toReturn.setId(toTranslate.attributes.get(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.RESID).getValue());
		
		List<Field> appList=new ArrayList<Field>();
		for(String key:toTranslate.attributes.keySet()){
			if((!key.equals(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.NAME))&&
					(!key.equals(org.gcube.application.aquamaps.stubs.dataModel.Resource.Tags.NAME))){
				Field appField=translateToServer(toTranslate.attributes.get(key));
				appList.add(appField);
			}
		}
		toReturn.setAdditionalField(new FieldArray(appList.toArray(new Field[appList.size()])));
		return toReturn;
	}
	
	public static Specie translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Species toTranslate){
		Specie toReturn=new Specie();
		toReturn.setId(toTranslate.getId());
		List<Field> appList=new ArrayList<Field>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Field field:toTranslate.attributesList){
			appList.add(translateToServer(field));			
		}
		toReturn.setAdditionalField(new FieldArray(appList.toArray(new Field[appList.size()])));
		return toReturn;
	}
	public static Weight fieldToWeight(org.gcube.application.aquamaps.stubs.dataModel.Field toTranslate){
		Weight toReturn=new Weight();
		toReturn.setParameterName(toTranslate.getValue());		
		toReturn.setChosenWeight(Boolean.parseBoolean(toTranslate.getValue()));
		return toReturn;
	}
	
	public static Perturbation translateToServer(String toPerturbId,String fieldName, org.gcube.application.aquamaps.stubs.dataModel.Perturbation toTranslate){	
		Perturbation toReturn=new Perturbation();		
		toReturn.setField(fieldName);
		toReturn.setToPerturbId(toPerturbId);
		toReturn.setType(toTranslate.getType().toString());
		toReturn.setValue(toTranslate.getPerturbationValue());		
		return toReturn;
	}
	public static AquaMap translateToServer(AquaMapsObject toTranslate){
		AquaMap toReturn=new AquaMap();
		toReturn.setAuthor(toTranslate.getAuthor());
		toReturn.setCreator(toTranslate.getCreator());
		toReturn.setDate(toTranslate.getDate());
		toReturn.setId(toTranslate.getId());
		toReturn.setName(toTranslate.getName());
		toReturn.setPublisher(toTranslate.getPublisher());
		toReturn.setSource(toTranslate.getSource());
		toReturn.setThreshold((float) toTranslate.getThreshold());
		toReturn.setType(toTranslate.getType().toString());
		toReturn.setBoundingBox(toTranslate.getBoundingBox().toString());
		toReturn.setGis(toTranslate.getGis());
		ArrayList<Perturbation> envelopCustomization=new ArrayList<Perturbation>();
		for(String specId:toTranslate.getEnvelopeCustomization().keySet())
			for(String fieldName:toTranslate.getEnvelopeCustomization().get(specId).keySet())
				envelopCustomization.add(translateToServer(specId,fieldName,toTranslate.getEnvelopeCustomization().get(specId).get(fieldName)));
		toReturn.setEnvelopCustomization(new PerturbationArray(envelopCustomization.toArray(new Perturbation[envelopCustomization.size()])));
		
		ArrayList<Perturbation> environmentCustomization=new ArrayList<Perturbation>();
		for(String cellId:toTranslate.getEnvironmentCustomization().keySet())
			for(String fieldName:toTranslate.getEnvironmentCustomization().get(cellId).keySet())
				environmentCustomization.add(translateToServer(cellId,fieldName,toTranslate.getEnvironmentCustomization().get(cellId).get(fieldName)));
		toReturn.setEnvironmentCustomization(new PerturbationArray(environmentCustomization.toArray(new Perturbation[environmentCustomization.size()])));
		
		ArrayList<Cell> excludedCells=new ArrayList<Cell>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Cell cell:toTranslate.getExcludedCells())
			excludedCells.add(translateToServer(cell));
		toReturn.setExcludedCells(new CellArray(excludedCells.toArray(new Cell[excludedCells.size()])));
		
		//TODO Modify stubs.aquamaps and re align translation
		toReturn.setRelatedResources(new StringArray(toTranslate.getRelatedResources().keySet().toArray(new String[toTranslate.getRelatedResources().keySet().size()])));
		
		ArrayList<Area> selectedAreas=new ArrayList<Area>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Area area:toTranslate.getSelectedAreas())
			selectedAreas.add(translateToServer(area));
		toReturn.setSelectedAreas(new AreasArray(selectedAreas.toArray(new Area[selectedAreas.size()])));
		
		ArrayList<Specie> selectedSpecies=new ArrayList<Specie>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Species area:toTranslate.getSelectedSpecies())
			selectedSpecies.add(translateToServer(area));
		toReturn.setSelectedSpecies(new SpeciesArray(selectedSpecies.toArray(new Specie[selectedSpecies.size()])));
		
		ArrayList<EnvelopeWeights> envelopeWeights = new ArrayList<EnvelopeWeights>();
		for(String specId : toTranslate.getEnvelopeWeights().keySet()){			
			ArrayList<Weight> weights=new ArrayList<Weight>();
			for(org.gcube.application.aquamaps.stubs.dataModel.Field field: toTranslate.getEnvelopeWeights().get(specId))
				weights.add(fieldToWeight(field));
			EnvelopeWeights env=new EnvelopeWeights();
			env.setSpeciesId(specId);
			env.setWeights(new WeightArray(weights.toArray(new Weight[weights.size()])));
		}
		toReturn.setWeights(new EnvelopeWeightArray(envelopeWeights.toArray(new EnvelopeWeights[envelopeWeights.size()])));
		
		toReturn.setStatus(toTranslate.getStatus());
		return toReturn;
	}
	
	public static Job translateToServer(org.gcube.application.aquamaps.stubs.dataModel.Job toTranslate){
		Job toReturn=new Job();
		toReturn.setAuthor(toTranslate.getAuthor());		
		toReturn.setDate(toTranslate.getDate());
		toReturn.setId(toTranslate.getId());
		toReturn.setName(toTranslate.getName());		
		toReturn.setHcaf(translateToServer(toTranslate.getSourceHCAF()));
		toReturn.setHspen(translateToServer(toTranslate.getSourceHSPEN()));
		toReturn.setHspec(translateToServer(toTranslate.getSourceHSPEC()));
		toReturn.setStatus(toTranslate.getStatus());
		
		ArrayList<AquaMap> aquaMapsList=new ArrayList<AquaMap>();
		for(AquaMapsObject obj:toTranslate.getAquaMapsObjectList())
			aquaMapsList.add(translateToServer(obj));
		toReturn.setAquaMapList(new AquaMapArray (aquaMapsList.toArray(new AquaMap[aquaMapsList.size()])));
		
		ArrayList<Perturbation> envelopCustomization=new ArrayList<Perturbation>();
		for(String specId:toTranslate.getEnvelopeCustomization().keySet())
			for(String fieldName:toTranslate.getEnvelopeCustomization().get(specId).keySet())
				envelopCustomization.add(translateToServer(specId,fieldName,toTranslate.getEnvelopeCustomization().get(specId).get(fieldName)));
		toReturn.setEnvelopCustomization(new PerturbationArray(envelopCustomization.toArray(new Perturbation[envelopCustomization.size()])));
		
		ArrayList<Perturbation> environmentCustomization=new ArrayList<Perturbation>();
		for(String cellId:toTranslate.getEnvironmentCustomization().keySet())
			for(String fieldName:toTranslate.getEnvironmentCustomization().get(cellId).keySet())
				environmentCustomization.add(translateToServer(cellId,fieldName,toTranslate.getEnvironmentCustomization().get(cellId).get(fieldName)));
		toReturn.setEnvironmentCustomization(new PerturbationArray(environmentCustomization.toArray(new Perturbation[environmentCustomization.size()])));
		
		ArrayList<Cell> excludedCells=new ArrayList<Cell>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Cell cell:toTranslate.getCellExclusion())
			excludedCells.add(translateToServer(cell));
		toReturn.setExcludedCells(new CellArray(excludedCells.toArray(new Cell[excludedCells.size()])));
		
		toReturn.setRelatedResources(new StringArray(toTranslate.getRelated().toArray(new String[toTranslate.getRelated().size()])));
		
		ArrayList<Area> selectedAreas=new ArrayList<Area>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Area area:toTranslate.getSelectedAreas())
			selectedAreas.add(translateToServer(area));
		toReturn.setSelectedAreas(new AreasArray(selectedAreas.toArray(new Area[selectedAreas.size()])));
		
		ArrayList<Specie> selectedSpecies=new ArrayList<Specie>();
		for(org.gcube.application.aquamaps.stubs.dataModel.Species area:toTranslate.getSelectedSpecies())
			selectedSpecies.add(translateToServer(area));
		toReturn.setSelectedSpecies(new SpeciesArray(selectedSpecies.toArray(new Specie[selectedSpecies.size()])));
		
		ArrayList<EnvelopeWeights> envelopeWeights = new ArrayList<EnvelopeWeights>();
		for(String specId : toTranslate.getEnvelopeWeights().keySet()){			
			ArrayList<Weight> weights=new ArrayList<Weight>();
			for(org.gcube.application.aquamaps.stubs.dataModel.Field field: toTranslate.getEnvelopeWeights().get(specId))
				weights.add(fieldToWeight(field));
			EnvelopeWeights env=new EnvelopeWeights();
			env.setSpeciesId(specId);
			env.setWeights(new WeightArray(weights.toArray(new Weight[weights.size()])));
		}
		toReturn.setWeights(new EnvelopeWeightArray(envelopeWeights.toArray(new EnvelopeWeights[envelopeWeights.size()])));
		
		return toReturn;
	}
	
}

package testClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.PerturbationType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;

public class DummyObjects {
	public static List<Area> getAreaSelection(){
		List<Area> areas=new ArrayList<Area>();
		areas.add(new Area(AreaType.FAO,"18"));
		areas.add(new Area(AreaType.FAO,"21"));
		areas.add(new Area(AreaType.FAO,"27"));
		return areas;
	}
	
	public static Set<Species> getSpeciesBasket(){
		Set<Species> basket= new HashSet<Species>();
		basket.add(new Species("Fis-22726"));
		basket.add(new Species("Fis-22850"));
		return basket;
	}
	
	public static Job createDummyJob(boolean areaSelection,boolean customization,boolean weights,boolean GIS){
		Job toReturn= new Job();
		toReturn.setName("Dummy Testing Job");
		toReturn.setAuthor("Tester");
		toReturn.setDate("2/10/2010");
		toReturn.setIsGis(false);
		Set<Species> basket=getSpeciesBasket();
		toReturn.addSpecies(basket);
		
//		Map<String,Perturbation> pert=new HashMap<String, Perturbation>();
//		pert.put(HspenFields.TempMin+"", new Perturbation(PerturbationType.ASSIGN, "21"));
//		toReturn.getEnvelopeCustomization().put(basket.iterator().next().getId(), pert);
////		
		
		if(areaSelection){
		
		toReturn.addAreas(getAreaSelection());
		}
		
		if(customization){
			toReturn.setCustomization(basket.iterator().next(),
					new Field(HspenFields.depthmax+"","15"),
					new Perturbation(PerturbationType.ASSIGN, "25"));
		}
		if(weights){
			List<Field> settedWeights= new ArrayList<Field>();
			for(EnvelopeFields f: EnvelopeFields.values())
				settedWeights.add(new Field(f+"","true",FieldType.BOOLEAN));
			toReturn.setWeights(basket.iterator().next(), settedWeights);
		}
		
		toReturn.setSourceHCAF(new Resource(ResourceType.HCAF, 1));
		toReturn.setSourceHSPEC(new Resource(ResourceType.HSPEC, 2));
		toReturn.setSourceHSPEN(new Resource(ResourceType.HSPEN, 3));
		
		toReturn.getAquaMapsObjectList().addAll(getObject(ObjectType.Biodiversity,GIS,getSpeciesBasket()));
		
		
		
		toReturn.getAquaMapsObjectList().addAll(getObject(ObjectType.SpeciesDistribution,GIS,getSpeciesBasket()));
		
		return toReturn;
		
	}
	
	public static Resource getResource(ResourceType type){
		switch(type){
		case HCAF: return new Resource (type,1);
		case HSPEC: return new Resource (type,2);
		case HSPEN: return new Resource (type,3);
		}
		return null;
	}
	
	
	public static List<AquaMapsObject> getObject(ObjectType type,boolean GIS,Set<Species> selection){
		List<AquaMapsObject> toReturn = new ArrayList<AquaMapsObject>();
		switch(type){
		case Biodiversity:{
			AquaMapsObject bio=new AquaMapsObject();
			bio.setType(ObjectType.Biodiversity);
			bio.setGis(false);
			bio.setName("BioTest");
			bio.setSelectedSpecies(selection);
			bio.setGis(GIS);
			toReturn.add(bio);
		}
		case SpeciesDistribution: {
			for(Species s : selection){
			AquaMapsObject specDistr=new AquaMapsObject();
			specDistr.setType(ObjectType.SpeciesDistribution);
			specDistr.setGis(false);
			specDistr.setName("DistrTest");
			specDistr.getSelectedSpecies().add(s);
			specDistr.setGis(GIS);
			toReturn.add(specDistr);
			}
		}
		}
		return toReturn;
	}
	
}

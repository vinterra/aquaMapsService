package testClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.PerturbationType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;

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
		basket.add(new Species("Alg-106"));
		basket.add(new Species("AFD-Pul-494"));
		return basket;
	}
	
	public static Job createDummyJob(boolean areaSelection,boolean customization,boolean weights,boolean GIS){
		Job toReturn= new Job();
		toReturn.setName("Dummy Testing Job");
		toReturn.setAuthor("fabio.sinibaldi");
		toReturn.setDate(System.currentTimeMillis());
		toReturn.setIsGis(false);
		Set<Species> basket=getSpeciesBasket();
		toReturn.addSpecies(basket);
		
//		Map<String,Perturbation> pert=new HashMap<String, Perturbation>();
//		pert.put(HspenFields.TempMin+"", new Perturbation(PerturbationType.ASSIGN, "21"));
//		toReturn.getEnvelopeCustomization().put(basket.iterator().next().getId(), pert);
		
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
		
		toReturn.setSourceHCAF(new Resource(ResourceType.HCAF, 121));
		toReturn.setSourceHSPEC(new Resource(ResourceType.HSPEC, 136));
		toReturn.setSourceHSPEN(new Resource(ResourceType.HSPEN, 125));
		
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
			break;
		}
		case SpeciesDistribution: {
			for(Species s : selection){
			AquaMapsObject specDistr=new AquaMapsObject();
			specDistr.setType(ObjectType.SpeciesDistribution);
			specDistr.setGis(false);
			specDistr.setName("Distr"+s.getId());
			specDistr.getSelectedSpecies().add(s);
			specDistr.setGis(GIS);
			toReturn.add(specDistr);
			}
			break;
		}
		}
		return toReturn;
	}
	
}

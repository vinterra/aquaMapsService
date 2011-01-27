package testClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Envelope;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Resource;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.PerturbationType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.stubs.wrapper.AquaMapsServiceWrapper;
import org.gcube.application.aquamaps.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;


public class AquaMapsServiceTester {

//	private static final String SERVICE_URI="http://localhost:9000/wsrf/services/gcube/application/aquamaps/AquaMaps";
	
	private static final String SERVICE_URI="http://wn06.research-infrastructures.eu:9001/wsrf/services/gcube/application/aquamaps/AquaMaps";
	
	public static void main(String[] args){
		try{
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/gcube/devsec");
		AquaMapsServiceWrapper wrapper=new AquaMapsServiceWrapper(session, SERVICE_URI);
		
		Species spec=new Species("Fis-29501");
//		System.out.println("Occurrence cells for ");
//		System.out.println(wrapper.getJSONOccurrenceCells(spec.getId(), new PagedRequestSettings(10, 0, Cell.ID, "ASC")));
//		System.out.println("Getting envelope");
//		System.out.println(wrapper.loadEnvelope(spec.getId()));
//		System.out.println("Getting resources HCAF");
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(10, 0, Resource.DATE	, "ASC"), ResourceType.HCAF));
//		ArrayList<Field> chars=new ArrayList<Field>();
//		chars.add(new Field(SpeciesOccursumFields.Family+"","Gadidae",FieldType.STRING));
//		System.out.println("Getting species (gadidae)");
//		System.out.println(wrapper.getJSONSpecies(1, chars, new ArrayList<Filter>(), new ArrayList<Filter>(), new PagedRequestSettings(10, 0, SpeciesOccursumFields.SpeciesID+"", "ASC")));
		
//		System.out.println("Getting hcaf list json");
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(10, 0, Resource.SEARCHID,"ASC"), ResourceType.HCAF));
//		System.out.println("Getting default hcaf");
//		System.out.println(wrapper.loadResource(1, ResourceType.HCAF));
		
//		System.out.println("Submitting Job");
//		wrapper.submitJob(createDummyJob());
		
//		System.out.println("Loading obj");
//		wrapper.loadObject(285);
	
//		System.out.println("Loading Envelope");
//		
//		Envelope env= wrapper.loadEnvelope(spec.getId(), 1);
//		
		
//		Resource r= wrapper.loadResource(1, ResourceType.HCAF);
//		r=wrapper.loadResource(1, ResourceType.HSPEC);
//		r=wrapper.loadResource(1, ResourceType.HSPEN);
//		
		wrapper.submitJob(createDummyJob(true,true,true));
		
		
		System.out.println("Done");
		}catch(Exception e){
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		
		
		
	}

	public static Job createDummyJob(boolean areaSelection,boolean customization,boolean weights){
		Job toReturn= new Job();
		toReturn.setName("Dummy Testing Job");
		toReturn.setAuthor("Tester");
		toReturn.setDate("2/10/2010");
		
		Set<Species> basket= new HashSet<Species>();
		basket.add(new Species("Fis-22726"));
		basket.add(new Species("Fis-22850"));
		toReturn.addSpecies(basket);
		
//		Map<String,Perturbation> pert=new HashMap<String, Perturbation>();
//		pert.put(HspenFields.TempMin+"", new Perturbation(PerturbationType.ASSIGN, "21"));
//		toReturn.getEnvelopeCustomization().put(basket.iterator().next().getId(), pert);
////		
		
		if(areaSelection){
		
		List<Area> areas=new ArrayList<Area>();
		areas.add(new Area(AreaType.FAO,"18"));
		areas.add(new Area(AreaType.FAO,"21"));
		areas.add(new Area(AreaType.FAO,"27"));
		toReturn.addAreas(areas);
		}
		
		if(customization){
			toReturn.setCustomization(basket.iterator().next(), new Field(HspenFields.DepthMax+"","15"), new Perturbation(PerturbationType.ASSIGN, "25"));
		}
		if(weights){
			List<Field> settedWeights= new ArrayList<Field>();
			for(EnvelopeFields f: EnvelopeFields.values())
				settedWeights.add(new Field(f+"","true",FieldType.BOOLEAN));
			toReturn.setWeights(basket.iterator().next(), settedWeights);
		}
		
		toReturn.setSourceHCAF(new Resource(ResourceType.HCAF, 1));
		toReturn.setSourceHSPEC(new Resource(ResourceType.HSPEC, 1));
		toReturn.setSourceHSPEN(new Resource(ResourceType.HSPEN, 1));
		
		
		AquaMapsObject bio=new AquaMapsObject();
		bio.setType(ObjectType.Biodiversity);
		bio.setGis(false);
		bio.setName("BioTest");
		bio.setSelectedSpecies(basket);
		toReturn.getAquaMapsObjectList().add(bio);
		
		AquaMapsObject specDistr=new AquaMapsObject();
		specDistr.setType(ObjectType.SpeciesDistribution);
		specDistr.setGis(false);
		specDistr.setName("DistrTest");
		specDistr.getSelectedSpecies().add(basket.iterator().next());
		toReturn.getAquaMapsObjectList().add(specDistr);
		
		return toReturn;
		
	}

	
}

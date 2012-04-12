package testClient;

import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceInterface;
import org.gcube.common.core.scope.GCUBEScope;


public class AquaMapsServiceTester {

//	private static final String SERVICE_URI="http://localhost:9000/wsrf/services/gcube/application/aquamaps/AquaMaps";
	
//	static final String AQ_SERVICE_URI="http://aquamaps.ifm-geomar.de:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/AquaMapsService";
	static final String AQ_SERVICE_URI="http://dbtest.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/AquaMapsService";
	static final String DM_SERVICE_URI="http://dbtest.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement";
	
	
//	static final String AQ_SERVICE_URI="http://node49.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/AquaMapsService";
//	static final String DM_SERVICE_URI="http://node49.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement";
	static final String RS_SERVICE_URI="http://dbtest.research-infrastructures.eu:8080/wsrf/services/gcube/common/searchservice/ResultSet";
	
	
	public static void main(String[] args){
		try{
//		AquaMapsServiceWrapper wrapper=new AquaMapsServiceWrapper(GCUBEScope.getScope("/gcube/devsec"), SERVICE_URI);
		AquaMapsServiceInterface wrapper= AquaMapsServiceCall.getCall(GCUBEScope.getScope("/gcube/devsec"),AQ_SERVICE_URI,false);
//		Species spec=new Species("Fis-29501");
//		System.out.println("Occurrence cells for ");
////		System.out.println(wrapper.getJSONOccurrenceCells(spec.getId(), new PagedRequestSettings(10, 0, Cell.ID, "ASC")));
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
		
		Job job= DummyObjects.createDummyJob(false,false,false,true);
		Job translated=new Job(job.toStubsVersion());
		AquaMapsObject obj =translated.getAquaMapsObjectList().get(0);
		System.out.println(obj.getAuthor());
		
		System.out.println("ORIGINAL");
		for(Species s: job.getSelectedSpecies())
			if(job.getEnvelopeCustomization().containsKey(s.getId()))
			for(Entry<String,Perturbation> pert:job.getEnvelopeCustomization().get(s.getId()).entrySet())
				System.out.println(AquaMapsXStream.getXMLInstance().toXML(pert));
			else System.out.println(s.getId()+" not customized");
		
		
		
		System.out.println("TRANSLATED");
		for(Species s: translated.getSelectedSpecies())
		if(translated.getEnvelopeCustomization().containsKey(s.getId()))
		for(Entry<String,Perturbation> pert:translated.getEnvelopeCustomization().get(s.getId()).entrySet())
			System.out.println(AquaMapsXStream.getXMLInstance().toXML(pert));
		else System.out.println(s.getId()+" not customized");
		
		
		System.out.println("Going to submit : "+AquaMapsXStream.getXMLInstance().toXML(translated));
		
		wrapper.submitJob(job);
		System.out.println("Done");
		}catch(Exception e){
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		
		
		
	}

	

	
}

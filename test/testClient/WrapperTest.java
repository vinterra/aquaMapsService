package testClient;

import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.stubs.wrapper.AquaMapsServiceWrapper;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;


public class WrapperTest {

	static String specId="Fis-22836";
	
	
	public static void main(String[] args) throws Exception{
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/gcube/devsec");
		AquaMapsServiceWrapper wrapper=new AquaMapsServiceWrapper(session, AquaMapsServiceTester.SERVICE_URI);
		
		
		
//		System.out.println(wrapper.getJSONSpecies(1, new ArrayList<Field>(), new ArrayList<Filter>(), new ArrayList<Filter>(), new PagedRequestSettings(3, 0, SpeciesOccursumFields.speciesid+"", "ASC")));
////		System.out.println(wrapper.getJSONPhilogeny());
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", "ASC"), ResourceType.HCAF));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", "ASC"), ResourceType.HSPEN));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", "ASC"), ResourceType.HSPEC));
//		System.out.println(wrapper.getJSONSubmitted(true, null, null, null, null, new PagedRequestSettings(3, 0, SubmittedFields.searchid+"", "ASC")));
		
		Species s=new Species(specId);
		System.out.println("loading Envelope for Species ");
		
		
		
		System.out.println(AquaMapsXStream.getInstance().toXML((wrapper.loadEnvelope(s.getId(), 1))));
		
		
		
//		System.out.println(wrapper.loadResource(1, ResourceType.HCAF));
//		System.out.println(wrapper.loadResource(1, ResourceType.HSPEN));
//		System.out.println(wrapper.loadResource(1, ResourceType.HSPEC));
		
		System.out.println("Completed");
	}

	
	
}
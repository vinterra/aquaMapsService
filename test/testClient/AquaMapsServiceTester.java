package testClient;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;

import org.gcube.application.aquamaps.stubs.*;
import org.gcube.application.aquamaps.stubs.service.AquaMapsServiceAddressingLocator;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
//import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;

public class AquaMapsServiceTester {

	private static final String SERVICE_URI="http://wn02.research-infrastructures.eu:9001/wsrf/services/gcube/application/aquamaps/AquaMaps";
	
	public static AquaMapsPortType getPortType(ASLSession session) throws Exception{
		AquaMapsServiceAddressingLocator asal= new AquaMapsServiceAddressingLocator();
		EndpointReferenceType epr= new EndpointReferenceType();
		epr.setAddress(new AttributedURI(SERVICE_URI));		
		AquaMapsPortType aquamapsPT=asal.getAquaMapsPortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, session.getScope());	
	}
	
	public static void main(String[] args) {
		Job job=new Job();
		AquaMap obj=new AquaMap();
		obj.setAuthor("Tester");
		obj.setCreator("Tester");
		obj.setEnvelopCustomization(new PerturbationArray(new Perturbation[0]));
		obj.setEnvironmentCustomization(new PerturbationArray(new Perturbation[0]));
		obj.setExcludedCells(new CellArray(new Cell[0]));
		obj.setName("Dummy_Obj");
		obj.setPublisher("Tester Publisher");
		obj.setRelatedResources(new StringArray(new String[0]));
		obj.setSelectedAreas(new AreasArray(new Area[0]));
		Specie spec=new Specie();
		spec.setId("FIS-1086");
		obj.setSelectedSpecies(new SpeciesArray(new Specie[]{spec}));
		obj.setThreshold((float) 0.5);
		obj.setType("Biodiversity");
		Weight w=new Weight();		
		//obj.setWeights(new WeightArray(new Weight[]{w,w,w,w}));
		job.setAquaMapList(new AquaMapArray(new AquaMap[]{obj}));
		job.setAuthor(obj.getAuthor());
		job.setEnvelopCustomization(obj.getEnvelopCustomization());
		job.setEnvironmentCustomization(obj.getEnvironmentCustomization());
		job.setExcludedCells(obj.getExcludedCells());
		Resource r=new Resource();
		r.setId("1");		
		job.setHcaf(r);
		job.setHspec(r);
		job.setHspen(r);
		job.setName("Dummy_JOB");
		job.setRelatedResources(obj.getRelatedResources());
		job.setSelectedAreas(obj.getSelectedAreas());
		job.setSelectedSpecies(obj.getSelectedSpecies());
		job.setWeights(obj.getWeights());
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/d4science.research-infrastructures.eu/FARM/AquaMaps");
		AquaMapsPortType pt;
		try {
			pt = getPortType(session);
			System.out.println(pt.submitJob(job));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	
}

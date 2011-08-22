package testClient;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.PredictionLayerGenerationRequest;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;

public class XStreamTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Job dummy=DummyObjects.createDummyJob(true, true, true, true);
		AquaMapsObject obj=dummy.getAquaMapsObjectList().get(1);
		LayerGenerationRequest theRequest=null;
		
		
		
		if(obj.getType().equals(ObjectType.Biodiversity)){
			Set<String> speciesCoverage=new HashSet<String>();
			for(Species s:obj.getSelectedSpecies()) speciesCoverage.add(s.getId());
			theRequest=new PredictionLayerGenerationRequest(obj.getId(), speciesCoverage,
					dummy.getSourceHCAF(), dummy.getSourceHSPEN(), dummy.getEnvelopeCustomization(), dummy.getEnvelopeWeights(),
					dummy.getSelectedAreas(), obj.getBoundingBox(), obj.getThreshold(), "bla.csv", obj.getName(), 1, 5);
		}else{
			Species selectedSpecies=obj.getSelectedSpecies().iterator().next();
			theRequest=new PredictionLayerGenerationRequest(obj.getId(),obj.getName(),selectedSpecies,
					dummy.getSourceHCAF(),dummy.getSourceHSPEN(),dummy.getEnvelopeCustomization().get(selectedSpecies.getId()),
					dummy.getEnvelopeWeights().get(selectedSpecies.getId()),dummy.getSelectedAreas(),obj.getBoundingBox(),".csv",true);
		}
		String xml=AquaMapsXStream.getXMLInstance().toXML(theRequest);
		System.out.println(AquaMapsXStream.getXMLInstance().toXML(theRequest));
		System.out.println("********** MD5************");
		System.out.println(new String(MessageDigest.getInstance("MD5").digest(xml.getBytes())));
	}

}

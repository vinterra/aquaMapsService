package testClient;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceWrapper;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Envelope;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Filter;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.scope.GCUBEScope;


public class WrapperTest {

	static String specId="Fis-22836";
	
	
	public static void main(String[] args) throws Exception{
		
		AquaMapsServiceWrapper wrapper=new AquaMapsServiceWrapper(GCUBEScope.getScope("/gcube/devsec"), AquaMapsServiceTester.SERVICE_URI);
		
		
		
		System.out.println(wrapper.getJSONSpecies(1, new ArrayList<Field>(), new ArrayList<Filter>(), new ArrayList<Filter>(), new PagedRequestSettings(3, 0, SpeciesOccursumFields.speciesid+"", "ASC")));
//		System.out.println(wrapper.getJSONPhilogeny());
		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", "ASC"), ResourceType.HCAF));
		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", "ASC"), ResourceType.HSPEN));
		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", "ASC"), ResourceType.HSPEC));
//		System.out.println(wrapper.getJSONSubmitted(true, null, null, null, null, new PagedRequestSettings(3, 0, SubmittedFields.searchid+"", "ASC")));
		
		Species s=new Species(specId);
		System.out.println("loading Envelope for Species ");
		
		
		
		System.out.println(AquaMapsXStream.getXMLInstance().toXML((wrapper.loadEnvelope(s.getId(), 3))));
		
		System.out.println(AquaMapsXStream.getXMLInstance().toXML(wrapper.loadResource(1, ResourceType.HCAF)));
		
//		System.out.println(wrapper.loadResource(1, ResourceType.HCAF));
//		System.out.println(wrapper.loadResource(1, ResourceType.HSPEN));
//		System.out.println(wrapper.loadResource(1, ResourceType.HSPEC));
		
		
		Envelope env=new Envelope();
		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),true, true, true);
		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),true, true, false);
		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),false, true, true);
		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),true, false, true);
		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
//		calculateEnvelopeFromCellSelection(List<String> cellIds,String speciesId);

//		public int deleteSubmitted(List<Integer> ids)throws Exception;

//		wrapper.getJSONSubmitted(String userName,boolean showObjects,String date,Integer jobId,SubmittedStatus status,ObjectType objType, PagedRequestSettings settings)throws Exception;

		System.out.println(wrapper.getJSONOccurrenceCells(DummyObjects.getSpeciesBasket().iterator().next().getId(), new PagedRequestSettings(1,0,HCAF_SFields.csquarecode+"","ASC")));

//		public AquaMapsObject loadObject(int objectId)throws Exception;

//		public Resource loadResource(int resId,ResourceType type)throws Exception;

//		public String getJSONResources(PagedRequestSettings settings, ResourceType type)throws Exception;

//		public String getJSONSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception;

//		public Species loadEnvelope(String speciesId, int hspenId)throws Exception;

//		public void markSaved(List<Integer> submittedIds)throws Exception;

//		public void submitJob(Job toSubmit) throws Exception;

		System.out.println(AquaMapsXStream.getXMLInstance().toXML(wrapper.loadObject(12634)));
		
		
		
		System.out.println("Completed");
	}

	
	
}

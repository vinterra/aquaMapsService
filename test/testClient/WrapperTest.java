package testClient;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Envelope;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Filter;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.scope.GCUBEScope;


public class WrapperTest {

	static String specId="Fis-22836";
	static String DEVSEC="/gcube/devsec";
	static String ECOSYSTEM="/d4science.research-infrastructures.eu/Ecosystem";
	
	
	
	public static void main(String[] args) throws Exception{
		
		AquaMapsServiceInterface wrapper= AquaMapsServiceCall.getCall(GCUBEScope.getScope(DEVSEC),AquaMapsServiceTester.AQ_SERVICE_URI);
//		DataManagementInterface dmInterface=DataManagementCall.getCall(GCUBEScope.getScope("/gcube/devsec"), AquaMapsServiceTester.DM_SERVICE_URI);
		
		
//		AquaMapsServiceInterface wrapper= AquaMapsServiceCall.getCall(
//				GCUBEScope.getScope("/d4science.research-infrastructures.eu/Ecosystem/TryIt"),
//				"http://node49.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement");	
//		System.out.println(wrapper.getJSONSpecies(1, new ArrayList<Field>(), new ArrayList<Filter>(), new ArrayList<Filter>(), new PagedRequestSettings(3, 0, SpeciesOccursumFields.speciesid+"", OrderDirection.ASC)));
//		System.out.println(wrapper.getJSONPhilogeny());
		
		ArrayList<Field> filter=new ArrayList<Field>();
		filter.add(new Field(MetaSourceFields.type+"",ResourceType.HSPEC+"",FieldType.STRING));
		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", OrderDirection.ASC), filter));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", OrderDirection.ASC), ResourceType.HSPEN));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, MetaSourceFields.searchid+"", OrderDirection.ASC), ResourceType.HSPEC));
//		System.out.println(wrapper.getJSONSubmitted(true, null, null, null, null, new PagedRequestSettings(3, 0, SubmittedFields.searchid+"", OrderDirection.ASC)));
		
//		Species s=new Species(specId);
//		System.out.println("loading Envelope for Species ");
//		
//		
//		
//		System.out.println(AquaMapsXStream.getXMLInstance().toXML((wrapper.loadEnvelope(s.getId(), 3))));
//		
//		System.out.println(AquaMapsXStream.getXMLInstance().toXML(wrapper.loadResource(1, ResourceType.HCAF)));
		
		
//		System.out.println("Checking default sources");
//		for(Field f:dmInterface.getDefaultSources()){
//			try{
//				ResourceType type=ResourceType.valueOf(f.getName());
//				int id=f.getValueAsInteger();
//				System.out.println(wrapper.loadResource(id, type));
//			}catch(Exception e){
//				System.err.println("Skipping "+f.getName()+" : "+f.getValue());
//				e.printStackTrace();
//			}
//		}
			
		System.out.println(wrapper.loadResource(141, ResourceType.OCCURRENCECELLS));
		
		
//		Envelope env=new Envelope();
//		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),true, true, true);
//		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
//		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),true, true, false);
//		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
//		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),false, true, true);
//		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
//		env=wrapper.calculateEnvelope(new BoundingBox(),DummyObjects.getAreaSelection(),DummyObjects.getSpeciesBasket().iterator().next().getId(),true, false, true);
//		System.out.println("Calculate env "+ AquaMapsXStream.getXMLInstance().toXML(env));
//		calculateEnvelopeFromCellSelection(List<String> cellIds,String speciesId);

//		public int deleteSubmitted(List<Integer> ids)throws Exception;

//		wrapper.getJSONSubmitted(String userName,boolean showObjects,String date,Integer jobId,SubmittedStatus status,ObjectType objType, PagedRequestSettings settings)throws Exception;

//		System.out.println(wrapper.getJSONOccurrenceCells(DummyObjects.getSpeciesBasket().iterator().next().getId(), new PagedRequestSettings(1,0,HCAF_SFields.csquarecode+"",OrderDirection.ASC)));

//		public AquaMapsObject loadObject(int objectId)throws Exception;

//		public Resource loadResource(int resId,ResourceType type)throws Exception;

//		public String getJSONResources(PagedRequestSettings settings, ResourceType type)throws Exception;

//		public String getJSONSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception;

//		public Species loadEnvelope(String speciesId, int hspenId)throws Exception;

//		public void markSaved(List<Integer> submittedIds)throws Exception;

//		public void submitJob(Job toSubmit) throws Exception;

//		System.out.println(AquaMapsXStream.getXMLInstance().toXML(wrapper.loadObject(346282)));
		
		
		
		System.out.println("Completed");
	}

	
	
}

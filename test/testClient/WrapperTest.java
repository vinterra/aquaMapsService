package testClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PublisherInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PublisherServiceCall;
import org.gcube.common.core.scope.GCUBEScope;


public class WrapperTest {

	static String specId="Fis-22836";
	static String DEVSEC="/gcube/devsec";
	static String DEVVRE="/gcube/devsec/devVRE";
	static String ECOSYSTEM="/d4science.research-infrastructures.eu/Ecosystem";
	static String FARM="/d4science.research-infrastructures.eu/FARM";
	static String gCubeApps="/d4science.research-infrastructures.eu/gCubeApps";
	
	
	static AquaMapsServiceInterface getAM()throws Exception{
		return AquaMapsServiceCall.getCall(GCUBEScope.getScope(DEVSEC),AquaMapsServiceTester.AQ_SERVICE_URI,false);
	}
	static DataManagementInterface getDM()throws Exception{
		return DataManagementCall.getCall(GCUBEScope.getScope(DEVSEC), AquaMapsServiceTester.DM_SERVICE_URI,false);
	}
	static PublisherInterface getPUB()throws Exception{
		return PublisherServiceCall.getCall(GCUBEScope.getScope(DEVSEC), AquaMapsServiceTester.PUB_SERVICE_URI,false);
	}
	public static void main(String[] args) throws Exception{
		
		AquaMapsServiceInterface wrapper= getAM();
		DataManagementInterface dmInterface=getDM();
		PublisherInterface pubInterface=getPUB();
		
//		AquaMapsServiceInterface wrapper= AquaMapsServiceCall.getCall(
//				GCUBEScope.getScope("/d4science.research-infrastructures.eu/Ecosystem/TryIt"),
//				"http://node49.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement");	
//		System.out.println(wrapper.getJSONSpecies(1, new ArrayList<Field>(), new ArrayList<Filter>(), new ArrayList<Filter>(), new PagedRequestSettings(3, 0, SpeciesOccursumFields.speciesid+"", OrderDirection.ASC)));
//		System.out.println(wrapper.getJSONPhilogeny());
		
//		ArrayList<Field> filter=new ArrayList<Field>();
//		filter.add(new Field(MetaSourceFields.type+"",ResourceType.HSPEC+"",FieldType.STRING));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, OrderDirection.ASC, MetaSourceFields.searchid+""), filter));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, OrderDirection.ASC, MetaSourceFields.searchid+""), ResourceType.HSPEN));
//		System.out.println(wrapper.getJSONResources(new PagedRequestSettings(3, 0, OrderDirection.ASC, MetaSourceFields.searchid+""), ResourceType.HSPEC));
//		System.out.println(wrapper.getJSONSubmitted(true, null, null, null, null, new PagedRequestSettings(3, 0, OrderDirection.ASC, SubmittedFields.searchid+"")));
//		
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
//				int id=f.getValueAsInteger();
//				System.out.println(wrapper.loadResource(id));
//			}catch(Exception e){
//				System.err.println("Skipping "+f.getName()+" : "+f.getValue());
//				e.printStackTrace();
//			}
//		}
			
//		System.out.println(wrapper.loadResource(141, ResourceType.OCCURRENCECELLS));
		
		
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

//		public String getJSONSpecies(int hspen.getSearchId(), List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception;

//		public Species loadEnvelope(String speciesId, int hspen.getSearchId())throws Exception;

//		public void markSaved(List<Integer> submittedIds)throws Exception;

//		wrapper.submitJob(DummyObjects.createDummyJob(false, false, false, true));
		
//		System.out.println(AquaMapsXStream.getXMLInstance().toXML(wrapper.loadObject(416596)));
//		
//		System.out.println("Completed");
		
		
		
		//********************** CHeck species filter
		
//		Resource hspen=null;
//		System.out.println("Checking defaults..");
//		for(Field f:dmInterface.getDefaultSources())
//			if(f.getName().equals(ResourceType.HSPEN+""))hspen=wrapper.loadResource(f.getValueAsInteger());
//		
//		if(hspen!=null) System.out.println("HSPEN IS "+hspen);
//		else throw new Exception("No Default HSPEN");
//		
//		PagedRequestSettings settings=new PagedRequestSettings(1, 0, OrderDirection.ASC, SpeciesOccursumFields.speciesid+"");
//		ArrayList<Filter> advancedFilter=new ArrayList<Filter>();
//		ArrayList<Filter> genericFilter=new ArrayList<Filter>();
//		System.out.println("String");
//		advancedFilter.add(new Filter(FilterType.begins, new Field(SpeciesOccursumFields.species+"", "a", FieldType.STRING)));
//		System.out.println("BEGINS : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		advancedFilter.add(new Filter(FilterType.ends, new Field(SpeciesOccursumFields.species+"", "a", FieldType.STRING)));
//		System.out.println("ENDS : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		advancedFilter.add(new Filter(FilterType.contains, new Field(SpeciesOccursumFields.species+"", "b", FieldType.STRING)));
//		System.out.println("Contains : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		advancedFilter.add(new Filter(FilterType.is, new Field(SpeciesOccursumFields.kingdom+"", "Animalia", FieldType.STRING)));
//		System.out.println("IS : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		
//		System.out.println("INTEGER");
//		
//		genericFilter.add(new Filter(FilterType.is, new Field(HspenFields.depthmax+"", 100+"", FieldType.DOUBLE)));
//		System.out.println("IS : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		
//		genericFilter.add(new Filter(FilterType.greater_then, new Field(HspenFields.depthmax+"", 100+"", FieldType.DOUBLE)));
//		System.out.println("GT : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		
//		genericFilter.add(new Filter(FilterType.smaller_then, new Field(HspenFields.depthmax+"", 100+"", FieldType.DOUBLE)));
//		System.out.println("ST : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		
//		System.out.println("BOOLEAN");
//		genericFilter.add(new Filter(FilterType.is, new Field(HspenFields.pelagic+"", true+"", FieldType.BOOLEAN)));
//		System.out.println("GT : "+wrapper.getJSONSpecies(hspen.getSearchId(), genericFilter, advancedFilter, settings));
//		
//		
//		System.out.println("NULLS");
//		System.out.println(wrapper.getJSONSpecies(hspen.getSearchId(), null, null, settings));
		
		
		
		//**************** Check Publisher Interface
		
		
		List<AquaMap> result=(pubInterface.getMapsBySpecies(new String[]{"Lycenchelys_ratmanovi"}, true, false, null));
		System.out.println("SPecies ONLY Loaded No Custom"+result.size());
		for(AquaMap a:result) System.out.println("Map : "+result);
		
//		result=(pubInterface.getMapsBySpecies(new String[]{species}, true, true, null));
//		System.out.println("SPecies ONLY Loaded + Custom"+result.size());
//		for(AquaMap a:result) System.out.println("Map : "+result);
//		
//		result=(pubInterface.getMapsBySpecies(new String[]{species}, true, true, new ArrayList<Resource>(Arrays.asList(new Resource[]{hspec}))));
//		System.out.println("Selected Hspec Loaded + "+result.size());
//		for(AquaMap a:result) System.out.println("Map : "+result);
	}

	
	
}

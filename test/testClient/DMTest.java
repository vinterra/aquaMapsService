package testClient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.Constant;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;

public class DMTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedScopeExpressionException 
	 */
	
	
	private static DataManagementInterface dmInterface=null;
	private static AquaMapsServiceInterface amInterface=null;
	
	
	public static void main(String[] args) throws MalformedScopeExpressionException, Exception {
		
		
		amInterface =WrapperTest.getAM();
		dmInterface = WrapperTest.getDM();
		//PROD
//		dmInterface=DataManagementCall.getCall(
//				GCUBEScope.getScope("/d4science.research-infrastructures.eu/Ecosystem/TryIt"),
//				"http://node49.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement");
		
//
//		System.out.println("Query Resource :");
//		System.out.println(dmInterface.queryResource("Select * from hspen where pelagic = 1", new PagedRequestSettings(2, 0, null, OrderDirection.ASC)));
//		
		
//		System.out.println("Export Resource :");
//
//		File csv=dmInterface.exportResource(109);
//		
//		csv.renameTo(new File("exported_mini.csv"));
//		
//		System.out.println("Exported to exported_mini.csv"); 
		
		
//		System.out.println("Import Resource :");
//
//		System.out.println("ID IS "+dmInterface.importResource(new File("/home/fabio/Desktop/MiniOccurrence.csv"), "fabio.sinibaldi", ResourceType.OCCURRENCECELLS,
//				"UTF-8",new boolean[]{true,true,true,true,true,true,true,true,true,true},true,'\t'));
//		
		
		
		
//		System.out.println("Delete Resource : ");
//		dmInterface.deleteResource(95);
//		System.out.println("DONE");

		
//		System.out.println("GENERATE MAPS");
//		ArrayList<Field> filter= new ArrayList<Field>();
//		filter.add(new Field(SpeciesOccursumFields.classcolumn+"","Bivalvia",FieldType.STRING)); // ~ 300 species
//		filter.add(new Field(SpeciesOccursumFields.classcolumn+"","Holothuroidea",FieldType.STRING)); // 21 species
//		filter.add(new Field(SpeciesOccursumFields.classcolumn+"","Mammalia",FieldType.STRING)); // 53 species (NB  pelagic hspen)
//		filter.add(new Field(SpeciesOccursumFields.kingdom+"","Animalia",FieldType.STRING)); // ~ 11500 species
//		System.out.println("JOB ID IS "+dmInterface.generateMaps("fabio.sinibaldi", true, 135, filter));

		
		
		
		
		
//		System.out.println("ID IS "+fromRequestToGeneralEnvironment());
	
		
//		EnvironmentalExecutionReportItem report=dmInterface.getReport(0);
//		System.out.println("");
//		
		
//		
		for(Field f:dmInterface.getDefaultSources())
			System.out.println(f.toJSONObject());
		
//		System.out.println("Done");
		
		
		//***************** INTERPOLATE HCAFs
		
//		SourceGenerationRequest request=new SourceGenerationRequest();
//		request.getAlgorithms().add(AlgorithmType.PARABOLIC);
//		request.setAuthor("fabio.sinibaldi");
//		request.setGenerationname("HCAF_Interpolation");
//		request.setDescription("Testing");
//		Resource firstHCAF=amInterface.loadResource(121, ResourceType.HCAF);
//		Resource secondHCAF=amInterface.loadResource(127, ResourceType.HCAF);
//		request.addSource(firstHCAF);
//		request.addSource(secondHCAF);
//		request.getParameters().add(new Field(SourceGenerationRequest.FIRST_HCAF_ID,firstHCAF.getSearchId()+"",FieldType.INTEGER));
//		request.getParameters().add(new Field(SourceGenerationRequest.FIRST_HCAF_TIME,2010+"",FieldType.INTEGER));
//		request.getParameters().add(new Field(SourceGenerationRequest.SECOND_HCAF_ID,secondHCAF.getSearchId()+"",FieldType.INTEGER));
//		request.getParameters().add(new Field(SourceGenerationRequest.SECOND_HCAF_TIME,2050+"",FieldType.INTEGER));
//		request.getParameters().add(new Field(SourceGenerationRequest.NUM_INTERPOLATIONS,4+"",FieldType.INTEGER));
//		
//		request.setExecutionEnvironment(Constant.AQUAMAPSSERVICE_PT_NAME);
//		request.setBackendURL("");
//		request.setEnvironmentConfiguration(new HashMap<String, String>());
//		request.setLogic(LogicType.HCAF);
//		request.setNumPartitions(16);
//		
//		request.setEnableimagegeneration(true);
//		request.setEnablelayergeneration(true);
//		
//		System.out.println(dmInterface.submitRequest(request));
//		
		
		
		
		//************************* ANALYZE TABLES
		
		Analysis toSend=new Analysis();
		toSend.setAuthor("fabio.sinibaldi");
		toSend.setDescription("Just a simple execution");
		toSend.setSources(Arrays.asList(new Integer[]{
			121,173,174,127
		}));
		toSend.setTitle("Ordered HCAF analysis");
		toSend.setType(AnalysisType.HCAF);
		System.out.println(dmInterface.analyzeTables(toSend));
		
		
		
		
		
//		System.out.println(dmInterface.getJSONSPECGroupGenreationRequests(new PagedRequestSettings(5, 0, GroupGenerationRequestFields.submissiontime+"", "ASC")));
//		EnvironmentalExecutionReportItem report= dmInterface.getReport(0);
//		System.out.println("**** REPORT *****");
//		System.out.println("resources Map : "+report.getResourcesMap());
//		System.out.println("resources Load : "+report.getResourceLoad());
//		System.out.println("percent : "+report.getPercent());
//		System.out.println("Species : "+report.getElaboratedSpecies());
	}

	
	private static String fromRequestToGeneralEnvironment(List<Resource> selection)throws Exception{
		SourceGenerationRequest request=new SourceGenerationRequest();
		request.setAuthor("fabio.sinibaldi");
		request.setGenerationname("Initial execution");
		request.setDescription("First execution for suitable default hspec data");
		for(Resource r:selection)request.addSource(r);
		request.setSubmissionBackend(Constant.SERVICE_NAME);
//		request.setSubmissionBackend("RainyCloud");
		
		return dmInterface.submitRequest(request);
	}
	
	
	
}

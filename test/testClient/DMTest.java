package testClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.Constant;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;

public class DMTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedScopeExpressionException 
	 */
	
	
	private static DataManagementInterface dmInterface=null;
	
	public static void main(String[] args) throws MalformedScopeExpressionException, Exception {
		//DEV
		dmInterface=DataManagementCall.getCall(GCUBEScope.getScope(WrapperTest.ECOSYSTEM), AquaMapsServiceTester.DM_SERVICE_URI);
		
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
//		System.out.println("ID IS "+dmInterface.importResource(new File("exported.csv"), "fabio.sinibaldi", ResourceType.OCCURRENCECELLS));
		
		
		
		
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
			System.out.println(f);
		
//		System.out.println("Done");
		
		
		
		
		
		
		
		
		
		
		
		
//		System.out.println(dmInterface.getJSONSPECGroupGenreationRequests(new PagedRequestSettings(5, 0, GroupGenerationRequestFields.submissiontime+"", "ASC")));
//		EnvironmentalExecutionReportItem report= dmInterface.getReport(0);
//		System.out.println("**** REPORT *****");
//		System.out.println("resources Map : "+report.getResourcesMap());
//		System.out.println("resources Load : "+report.getResourceLoad());
//		System.out.println("percent : "+report.getPercent());
//		System.out.println("Species : "+report.getElaboratedSpecies());
	}

	
	private static String fromRequestToGeneralEnvironment()throws Exception{
		SourceGenerationRequest request=new SourceGenerationRequest();
		request.setAuthor("fabio.sinibaldi");
		request.setGenerationname("Initial execution");
		request.setDescription("First execution for suitable default hspec data");
		request.setHcafId(1);
		request.setHspenId(3);
		request.setOccurrenceCellId(93);
		request.setSubmissionBackend(Constant.SERVICE_NAME);
//		request.setSubmissionBackend("RainyCloud");
		request.setExecutionEnvironment("AquaMaps VRE");
		request.setBackendURL("http://node16.d.d4science.research-infrastructures.eu:9000/RainyCloud-web-0.00.01");
		request.setEnvironmentConfiguration(new HashMap<String, String>());
		request.setLogic(LogicType.HSPEN);
		request.setNumPartitions(16);
		request.getAlgorithms().addAll(Arrays.asList(new String[] {AlgorithmType.HSPENRegeneration+""}));
		request.setEnableimagegeneration(true);
		request.setEnablelayergeneration(true);
		return dmInterface.submitRequest(request);
	}
	
}

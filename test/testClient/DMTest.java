package testClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.environments.HSPECGroupGenerationRequest;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;

public class DMTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedScopeExpressionException 
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, Exception {
		DataManagementInterface dmInterface=DataManagementCall.getCall(GCUBEScope.getScope("/gcube/devsec"), AquaMapsServiceTester.DM_SERVICE_URI);
		
//		dmInterface.generateMaps("fabio.sinibaldi", true, 36, new ArrayList<Field>());
		System.out.println("ID IS "+fromRequestToGeneralEnvironment());
		
		System.out.println("Done");
		
		
		
		
		
		
		
		
		
		
		
		
//		System.out.println(dmInterface.getJSONSPECGroupGenreationRequests(new PagedRequestSettings(5, 0, GroupGenerationRequestFields.submissiontime+"", "ASC")));
//		EnvironmentalExecutionReportItem report= dmInterface.getReport(0);
//		System.out.println("**** REPORT *****");
//		System.out.println("resources Map : "+report.getResourcesMap());
//		System.out.println("resources Load : "+report.getResourceLoad());
//		System.out.println("percent : "+report.getPercent());
//		System.out.println("Species : "+report.getElaboratedSpecies());
	}

	
	private static String fromRequestToGeneralEnvironment()throws Exception{
		HSPECGroupGenerationRequest request=new HSPECGroupGenerationRequest();
		request.setAuthor("genericTester");
		request.setGenerationname("Test execution");
		request.setDescription("Just a simple execution");
		request.setHcafsearchid(1);
		request.setHspensearchid(3);
		request.setSubmissionBackend("AquaMapsVRE");
//		request.setSubmissionBackend("RainyCloud");
		request.setExecutionEnvironment("Private Cloud");
		request.setBackendURL("http://node16.d.d4science.research-infrastructures.eu:9000/RainyCloud-web-0.00.01");
		request.setEnvironmentConfiguration(new HashMap<String, String>());
		request.setLogic(LogicType.HSPEC);
		request.setNumPartitions(4);
		request.getAlgorithms().addAll(Arrays.asList(new String[] {AlgorithmType.NativeRange+""}));
		request.setEnableimagegeneration(true);
		request.setEnablelayergeneration(true);
		DataManagementInterface dmInterface=DataManagementCall.getCall(GCUBEScope.getScope("/gcube/devsec"), AquaMapsServiceTester.DM_SERVICE_URI);
		return dmInterface.submitRequest(request);
	}
	
}

package testClient;

import org.gcube.application.aquamaps.aquamapsservice.stubs.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementCall;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.dataModel.enhanced.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.fields.GroupGenerationRequestFields;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;

public class DMTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedScopeExpressionException 
	 */
	public static void main(String[] args) throws MalformedScopeExpressionException, Exception {
		DataManagementInterface dmInterface=DataManagementCall.getCall(GCUBEScope.getScope("/gcube/devsec"), AquaMapsServiceTester.SERVICE_URI);
		
		System.out.println(dmInterface.getJSONSPECGroupGenreationRequests(new PagedRequestSettings(5, 0, GroupGenerationRequestFields.submissiontime+"", "ASC")));
		EnvironmentalExecutionReportItem report= dmInterface.getReport(0);
		System.out.println("**** REPORT *****");
		System.out.println("resources Map : "+report.getResourcesMap());
		System.out.println("resources Load : "+report.getResourceLoad());
		System.out.println("percent : "+report.getPercent());
		System.out.println("Species : "+report.getElaboratedSpecies());
	}

}

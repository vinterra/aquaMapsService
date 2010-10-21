package testClient;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.SourceGeneratorRequest;
import org.gcube.application.aquamaps.stubs.AquaMapsPortType;
import org.gcube.application.aquamaps.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.stubs.GenerateHCAFRequestType;
import org.gcube.application.aquamaps.stubs.service.AquaMapsServiceAddressingLocator;
import org.gcube.application.aquamaps.stubs.service.DataManagementServiceAddressingLocator;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.types.StringArray;

public class HCAFGenerationTest {

	private static final String SERVICE_URI="http://wn06.research-infrastructures.eu:9001/wsrf/services/gcube/application/aquamaps/AquaMaps";
	
	public static DataManagementPortType getPortType(ASLSession session)throws Exception{
		DataManagementServiceAddressingLocator asal= new DataManagementServiceAddressingLocator();
		EndpointReferenceType epr= new EndpointReferenceType();
		epr.setAddress(new AttributedURI(SERVICE_URI));		
		DataManagementPortType aquamapsPT=asal.getDataManagementPortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, session.getScope());
	}
	
	
	public static void main(String[] args) throws Exception{
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/gcube/devsec");
		DataManagementPortType pt=getPortType(session);
		GenerateHCAFRequestType request=new GenerateHCAFRequestType();
		request.setResultingHCAFName("Testing HCAF");
		request.setSourceHCAFId("1");
		request.setUrls(new StringArray(new String[]{
				"http://maps.terradue.com/catalogue/gpod/MER_RR__2P/MER_RR__2PNPDE20070601_004647_000026132058_00346_27454_0694.N1/xml"
		}));
		request.setUserId("Tester");
		pt.generateHCAF(request);
	}
	
}

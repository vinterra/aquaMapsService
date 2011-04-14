package testClient;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateHCAFRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.DataManagementServiceAddressingLocator;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.StringArray;

public class HCAFGenerationTest {

//	private static final String SERVICE_URI="http://wn02.research-infrastructures.eu:9001/wsrf/services/gcube/application/aquamaps/DataManagement";
	
	private static final String SERVICE_URI="http://wn06.research-infrastructures.eu:9001/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement";
	
	public static DataManagementPortType getPortType(GCUBEScope scope)throws Exception{
		DataManagementServiceAddressingLocator asal= new DataManagementServiceAddressingLocator();
		EndpointReferenceType epr= new EndpointReferenceType();
		epr.setAddress(new AttributedURI(SERVICE_URI));		
		DataManagementPortType aquamapsPT=asal.getDataManagementPortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, scope);
	}
	
	
	public static void main(String[] args) throws Exception{
		
		DataManagementPortType pt=getPortType(GCUBEScope.getScope("/gcube/devsec"));
		GenerateHCAFRequestType request=new GenerateHCAFRequestType();
		request.setResultingHCAFName("Testing HCAF");
		request.setSourceHCAFId("1");
		request.setUrls(new StringArray(new String[]{
				"http://www.genesi-dec.eu/catalogue/genesi/MERIS_L3_CHL1/L3_ENV_MER_CHL1_m__20020601_GLOB_SI_ACR_9277x9277_-90%2b90%2b-180%2b180_0000.tar.gz/rdf"
					
		}));
		request.setUserId("Tester");
		pt.generateHCAF(request);
	}
	
}

package testClient;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.AquaMapsPublisherPortType;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.service.AquaMapsPublisherServiceAddressingLocator;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.types.VOID;
import org.gcube.common.core.utils.logging.GCUBELog;

public class PublisherTesting {

	
protected static ISClient isClient;
	

private static final GCUBELog logger=new GCUBELog(PublisherTesting.class);
	
	static{
		try {
			isClient = GHNContext.getImplementation(ISClient.class);
		} catch (Exception e) {
			logger.error("Unable to get ISImplementation : "+e);
		}
	}
	
	private static final String SERVICE_URI="http://pc-biagini.isti.cnr.it:9002/wsrf/services/gcube/application/aquamapspublisher/AquaMapsPublisher";
	
	public static void main(String[] args)throws Exception{
		
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/gcube/devsec");
		AquaMapsPublisherPortType pt= getPortType(session, SERVICE_URI);
		System.out.println(pt.getWMSContextTemplate(new VOID()));
		
		
	}
	
	
	private static AquaMapsPublisherPortType getPortType(ASLSession session,String defaultURI) throws Exception{
		AquaMapsPublisherServiceAddressingLocator asal= new AquaMapsPublisherServiceAddressingLocator();
		EndpointReferenceType epr;
			GCUBERIQuery query = isClient.getQuery(GCUBERIQuery.class);		
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceClass","Application"));
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceName","AquaMapsPublisher"));
//			List<GCUBERunningInstance> toReturn= isClient.execute(query, session.getScope());
			List<GCUBERunningInstance> toReturn=new ArrayList<GCUBERunningInstance>();
			if(toReturn.isEmpty()) {				
				System.out.println("No runnning instance found, using default service @ : "+defaultURI);
				epr=new EndpointReferenceType();
				epr.setAddress(new AttributedURI(defaultURI));
			}else{
				epr= toReturn.get(0).getAccessPoint().getEndpoint("gcube/application/aquamapspublisher/AquaMapsPublisher");
				System.out.println("Found RI @ : "+epr.getAddress().getHost());
			}
		AquaMapsPublisherPortType aquamapsPT=asal.getAquaMapsPublisherPortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, session.getScope());	
	}
	
}

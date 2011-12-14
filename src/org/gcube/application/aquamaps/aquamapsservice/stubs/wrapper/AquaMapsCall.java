package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.calls.RICall;
import org.gcube.common.core.utils.logging.GCUBELog;

public abstract class AquaMapsCall extends RICall{

	protected static GCUBELog logger= new GCUBELog(AquaMapsCall.class);
	
	protected static ISClient isClient;

	protected GCUBEScope scope=null;

	static{
		try {
			isClient = GHNContext.getImplementation(ISClient.class);
		} catch (Exception e) {
			logger.error("Unable to get ISImplementation : "+e);
		}
	}
	
	
	protected EndpointReferenceType epr= new EndpointReferenceType();
	
	
	protected AquaMapsCall(GCUBEScope scope,
			GCUBESecurityManager[] securityManager,String defaultURI, boolean queryIS) throws Exception {
		super(scope, securityManager);
		// looking for service epr
		this.scope=scope;
		List<GCUBERunningInstance> list=new ArrayList<GCUBERunningInstance>();
		if(queryIS){
			GCUBERIQuery query = isClient.getQuery(GCUBERIQuery.class);		
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceClass",getServiceClass()));
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceName",getServiceName()));
			list= isClient.execute(query, scope);
		}
		if(list.isEmpty()) {				
			System.out.println("Using default service @ : "+defaultURI);
			epr=new EndpointReferenceType();
			epr.setAddress(new AttributedURI(defaultURI));
		}else{
			epr= list.get(0).getAccessPoint().getEndpoint(getPortTypeName());
			System.out.println("Found RI @ : "+epr.getAddress().getHost());
		}
	}

		
	@Override
	protected String getServiceClass() {
		return Constant.SERVICE_CLASS;
	}

	@Override
	protected String getServiceName() {
		return Constant.SERVICE_NAME;
	}
}

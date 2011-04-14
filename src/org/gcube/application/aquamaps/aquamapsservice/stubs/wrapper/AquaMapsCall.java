package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.calls.RICall;
import org.apache.axis.message.addressing.EndpointReferenceType;

public abstract class AquaMapsCall extends RICall{

	final protected EndpointReferenceType serviceEpr= new EndpointReferenceType();
	
	
	public AquaMapsCall(GCUBEScope scope,
			GCUBESecurityManager[] securityManager) throws Exception {
		super(scope, securityManager);
		// TODO Auto-generated constructor stub
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

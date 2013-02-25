package org.gcube.application.aquamaps.aquamapsservice.impl.contexts;

import org.gcube.application.aquamaps.aquamapsservice.impl.Constants;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

public abstract class PortTypeContext extends GCUBEStatefulPortTypeContext {

	
	/** {@inheritDoc}*/
	public String getNamespace() {return Constants.NS;}

	/**{@inheritDoc}*/
	@Override public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}

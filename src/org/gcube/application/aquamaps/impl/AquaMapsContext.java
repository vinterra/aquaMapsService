package org.gcube.application.aquamaps.impl;

import org.gcube.common.core.contexts.GCUBEPortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;


public class AquaMapsContext extends GCUBEPortTypeContext {

	
	/** Single context instance, created eagerly */
	private static AquaMapsContext cache = new AquaMapsContext();
	
	private AquaMapsContext(){}
	
	/** Returns cached instance */
	public static AquaMapsContext getContext() {return cache;}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {return "gcube/application/aquamaps/AquaMaps";}

	/** {@inheritDoc}*/
	public String getNamespace() {return "http://gcube-system.org/namespaces/application/aquamaps";}

	/** {@inheritDoc}*/
	public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}

}

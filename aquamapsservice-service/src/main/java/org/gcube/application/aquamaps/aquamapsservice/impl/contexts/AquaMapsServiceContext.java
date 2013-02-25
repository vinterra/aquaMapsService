package org.gcube.application.aquamaps.aquamapsservice.impl.contexts;

import org.gcube.application.aquamaps.aquamapsservice.impl.Constants;

public class AquaMapsServiceContext extends PortTypeContext{

	/** Singleton instance. */
	protected static AquaMapsServiceContext singleton = new AquaMapsServiceContext();

	/** Creates an instance . */
	private AquaMapsServiceContext(){}
	
	/** Returns a context instance.
	 * @return the context
	 * */
	public static AquaMapsServiceContext getContext() {
		return singleton;
	}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {
		return Constants.JNDI_NAME;
	}
	
}

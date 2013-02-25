package org.gcube.application.aquamaps.aquamapsservice.impl.contexts;

import org.gcube.application.aquamaps.aquamapsservice.impl.Constants;

public class PublisherContext extends PortTypeContext{

	/** Singleton instance. */
	protected static PublisherContext singleton = new PublisherContext();

	/** Creates an instance . */
	private PublisherContext(){}
	
	/** Returns a context instance.
	 * @return the context
	 * */
	public static PublisherContext getContext() {
		return singleton;
	}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {
		return Constants.JNDI_NAME;
	}
	
}

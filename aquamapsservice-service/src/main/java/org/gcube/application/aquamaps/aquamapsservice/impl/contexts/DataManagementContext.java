package org.gcube.application.aquamaps.aquamapsservice.impl.contexts;

import org.gcube.application.aquamaps.aquamapsservice.impl.Constants;

public class DataManagementContext extends PortTypeContext{

	/** Singleton instance. */
	protected static DataManagementContext singleton = new DataManagementContext();

	/** Creates an instance . */
	private DataManagementContext(){}
	
	/** Returns a context instance.
	 * @return the context
	 * */
	public static DataManagementContext getContext() {
		return singleton;
	}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {
		return Constants.JNDI_NAME;
	}
}

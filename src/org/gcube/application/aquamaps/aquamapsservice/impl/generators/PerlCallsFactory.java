package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.apache.commons.pool.BasePoolableObjectFactory;



public class PerlCallsFactory extends BasePoolableObjectFactory {

	@Override
	public Object makeObject() throws Exception {
		return new ImageGenerator();
	}
	@Override
	public void activateObject(Object obj) throws Exception {
		
//		super.activateObject(obj);
		((ImageGenerator)obj).setRequest(null);
	}
	@Override
	public void destroyObject(Object obj) throws Exception {		
		super.destroyObject(obj);
	}
	@Override
	public void passivateObject(Object obj) throws Exception {

//		super.passivateObject(obj);
		((ImageGenerator)obj).setRequest(null);
	}
}

package org.gcube.application.aquamaps.aquamapsservice.impl.engine.image;

import org.apache.commons.pool.BasePoolableObjectFactory;



public class PerlCallsFactory extends BasePoolableObjectFactory {

	@Override
	public Object makeObject() throws Exception {
		return new PerlImageGenerator();
	}
	@Override
	public void activateObject(Object obj) throws Exception {
		super.activateObject(obj);
		((PerlImageGenerator)obj).setRequest(null);
	}
	@Override
	public void destroyObject(Object obj) throws Exception {		
		super.destroyObject(obj);
	}
	@Override
	public void passivateObject(Object obj) throws Exception {
		super.passivateObject(obj);
		((PerlImageGenerator)obj).setRequest(null);
	}
	
	@Override
	public boolean validateObject(Object obj) {
		return (obj!=null) && (obj instanceof PerlImageGenerator)
			&&((((PerlImageGenerator)obj).getRequest()==null));
	}
	
	
}

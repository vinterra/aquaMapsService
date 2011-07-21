package org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class GISGeneratorFactory extends BasePoolableObjectFactory {

	@Override
	public Object makeObject() throws Exception {
		return new GISGenerator();
	}
	@Override
	public void activateObject(Object obj) throws Exception {
		super.activateObject(obj);
		((GISGenerator)obj).setRequest(null);
	}
	@Override
	public void destroyObject(Object obj) throws Exception {		
		super.destroyObject(obj);
	}
	@Override
	public void passivateObject(Object obj) throws Exception {
		super.passivateObject(obj);
			((GISGenerator)obj).setRequest(null);
	}
	@Override
	public boolean validateObject(Object obj) {
		return (obj!=null) && (obj instanceof GISGenerator)
		&&((((GISGenerator)obj).getRequest()==null));
	}
}

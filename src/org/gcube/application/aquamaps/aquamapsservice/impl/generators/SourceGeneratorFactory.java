package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.apache.commons.pool.BasePoolableObjectFactory;

public class SourceGeneratorFactory extends BasePoolableObjectFactory{
	@Override
	public Object makeObject() throws Exception {
		return new SourceGenerator();
	}
	@Override
	public void activateObject(Object obj) throws Exception {
		
//		super.activateObject(obj);
//		((SourceGenerator)obj).setRequest(null);
	}
	@Override
	public void destroyObject(Object obj) throws Exception {		
		super.destroyObject(obj);
	}
	@Override
	public void passivateObject(Object obj) throws Exception {

//		super.passivateObject(obj);
//		((SourceGenerator)obj).setRequest(null);
	}

}

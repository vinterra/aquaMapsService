package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISGenerator;

public class GISGeneratorFactory extends BasePoolableObjectFactory {

	@Override
	public Object makeObject() throws Exception {
		return new GISGenerator();
	}
	@Override
	public void activateObject(Object obj) throws Exception {
		
//		super.activateObject(obj);
//		((GISGenerator)obj).setRequest(null);
	}
	@Override
	public void destroyObject(Object obj) throws Exception {		
		super.destroyObject(obj);
	}
	@Override
	public void passivateObject(Object obj) throws Exception {

//		super.passivateObject(obj);
//		((GIImageGenerator)obj).setRequest(null);
	}

}

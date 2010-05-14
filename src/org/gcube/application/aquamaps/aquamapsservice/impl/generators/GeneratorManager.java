package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.apache.commons.pool.impl.GenericObjectPool;

public class GeneratorManager {

	private static GenericObjectPool pool=new GenericObjectPool(new PerlCallsFactory());
	
	static{
		pool.setLifo(false);
		pool.setMaxActive(3);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try{
			for(int i =0;i<pool.getMaxActive();i++){
				pool.addObject();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static int requestGeneration(ImageGeneratorRequest request) throws Exception{
		
		PerlImageGenerator obj=(PerlImageGenerator) pool.borrowObject();
		obj.setRequest(request);
		int toReturn= obj.generate();
		pool.returnObject(obj);
		return toReturn;
	}
		
}

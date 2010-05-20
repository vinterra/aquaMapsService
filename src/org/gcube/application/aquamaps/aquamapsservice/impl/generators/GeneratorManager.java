package org.gcube.application.aquamaps.aquamapsservice.impl.generators;



import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISGenerationRequest;
import org.gcube.common.core.utils.logging.GCUBELog;

public class GeneratorManager {
	static GCUBELog logger= new GCUBELog(GeneratorManager.class);
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
	
	
	private static int requestPerlGeneration(ImageGeneratorRequest request) throws Exception{
		
		PerlImageGenerator obj=(PerlImageGenerator) pool.borrowObject();
		obj.setRequest(request);
		int toReturn= obj.generate();
		pool.returnObject(obj);
		return toReturn;
	}
		
	private static int requestGISGeneration(GISGenerationRequest req)throws Exception{
		//TODO implement
		throw new Exception("Not yest implemented");
	}
	
	public static int requestGeneration(GenerationRequest request)throws Exception{
		if(request instanceof ImageGeneratorRequest) return requestPerlGeneration((ImageGeneratorRequest) request);
		else if(request instanceof GISGenerationRequest) return requestGISGeneration((GISGenerationRequest) request);
		else throw new BadRequestException();
	}
	
}

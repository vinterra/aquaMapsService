package org.gcube.application.aquamaps.aquamapsservice.impl.engine;



import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.GISGeneratorFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.GISRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.image.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.image.PerlCallsFactory;
import org.gcube.common.core.utils.logging.GCUBELog;

public class GeneratorManager {
	static GCUBELog logger= new GCUBELog(GeneratorManager.class);
	private static GenericObjectPool perlPool=new GenericObjectPool(new PerlCallsFactory());
	private static GenericObjectPool gisPool=new GenericObjectPool(new GISGeneratorFactory());
	private static GenericObjectPool sourceGeneratorPool=new GenericObjectPool(new SourceGeneratorFactory());

	
	static{
		logger.debug("Initializing pools...");
		perlPool.setLifo(false);
		perlPool.setMaxActive(3);
		perlPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		perlPool.setTestOnBorrow(false);
		perlPool.setTestOnReturn(false);
		perlPool.setTestWhileIdle(false);
		try{
			for(int i =0;i<perlPool.getMaxActive();i++){
				perlPool.addObject();
			}
			logger.debug("Added "+perlPool.getMaxActive()+" objects to perl pool");
		}catch(Exception e){
			logger.error("Unable to init perl pool",e);
		}


		gisPool.setLifo(false);
		gisPool.setMaxActive(10);
		gisPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		gisPool.setTestOnBorrow(false);
		gisPool.setTestOnReturn(false);
		gisPool.setTestWhileIdle(false);
		try{
			for(int i =0;i<gisPool.getMaxActive();i++){
				gisPool.addObject();
			}
			logger.debug("Added "+gisPool.getMaxActive()+" objects to gis pool");
		}catch(Exception e){
			logger.error("Unable to init gis pool",e);
		}

		sourceGeneratorPool.setLifo(false);
		sourceGeneratorPool.setMaxActive(1);
		sourceGeneratorPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		sourceGeneratorPool.setTestOnBorrow(false);
		sourceGeneratorPool.setTestOnReturn(false);
		sourceGeneratorPool.setTestWhileIdle(false);
		try{
			for(int i =0;i<sourceGeneratorPool.getMaxActive();i++){
				sourceGeneratorPool.addObject();
			}
			logger.debug("Added "+sourceGeneratorPool.getMaxActive()+" objects to source generator pool");
		}catch(Exception e){
			logger.error("Unable to init source generator pool",e);
		}

		
	
		
		
	}



	public static boolean requestGeneration(GenerationRequest request)throws Exception{
		
		GenericObjectPool thePool;
		if(request instanceof ImageGeneratorRequest)
			thePool=perlPool;
		else if(request instanceof GISRequest)
			thePool=gisPool;
		else if(request instanceof SourceGeneratorRequest)
			thePool=sourceGeneratorPool;
		else throw new BadRequestException();
		
		Generator obj = null;
		try{
			obj=(Generator) thePool.borrowObject();
			obj.setRequest(request);
			return obj.getResponse();
		}catch(Exception e){
			throw e;
		}finally{
			thePool.returnObject(obj);
		}
	}

}

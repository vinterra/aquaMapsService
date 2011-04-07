package org.gcube.application.aquamaps.aquamapsservice.impl.generators;



import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISRequest;
import org.gcube.common.core.utils.logging.GCUBELog;

public class GeneratorManager {
	static GCUBELog logger= new GCUBELog(GeneratorManager.class);
	private static GenericObjectPool perlPool=new GenericObjectPool(new PerlCallsFactory());
	private static GenericObjectPool gisPool=new GenericObjectPool(new GISGeneratorFactory());
	private static GenericObjectPool sourceGeneratorPool=new GenericObjectPool(new SourceGeneratorFactory());
	static{
		perlPool.setLifo(false);
		perlPool.setMaxActive(3);
		perlPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try{
			for(int i =0;i<perlPool.getMaxActive();i++){
				perlPool.addObject();
			}
		}catch(Exception e){
			e.printStackTrace();
		}


		gisPool.setLifo(false);
		gisPool.setMaxActive(10);
		gisPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try{
			for(int i =0;i<gisPool.getMaxActive();i++){
				gisPool.addObject();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		sourceGeneratorPool.setLifo(false);
		sourceGeneratorPool.setMaxActive(1);
		sourceGeneratorPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try{
			for(int i =0;i<sourceGeneratorPool.getMaxActive();i++){
				sourceGeneratorPool.addObject();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}


//	private static boolean requestPerlGeneration(ImageGeneratorRequest request) throws Exception{
//		boolean toReturn=false;
//		PerlImageGenerator obj = null;
//		try{
//			obj=(PerlImageGenerator) perlPool.borrowObject();
//			obj.setRequest(request);
//			toReturn= obj.generate();
//		}catch(Exception e){
//			throw e;
//		}finally{
//			perlPool.returnObject(obj);
//		}
//
//		return toReturn;
//	}

//	private static boolean requestSourceGeneration(SourceGeneratorRequest request) throws Exception{
//		boolean toReturn=false;
//		SourceGenerator obj = null;
//		try{
//			obj=(SourceGenerator) sourceGeneratorPool.borrowObject();
//			obj.setRequest(request);
//			toReturn= obj.generate();
//		}catch(Exception e){
//			throw e;
//		}finally{
//			sourceGeneratorPool.returnObject(obj);
//		}
//
//		return toReturn;
//	}
	
	
//	private static boolean requestGISGeneration(GISGenerationRequest req)throws Exception{
//		GISGenerator obj=null;
//		boolean toReturn=false;
//		try{
//			obj=(GISGenerator) gisPool.borrowObject();
//			if(req instanceof GroupGenerationRequest) toReturn=obj.createGroup((GroupGenerationRequest) req);
//			else if(req instanceof LayerGenerationRequest) toReturn=obj.createLayer((LayerGenerationRequest) req);
//			else if(req instanceof StyleGenerationRequest) toReturn=obj.generateStyle((StyleGenerationRequest) req);
//			else throw new BadRequestException();
//		}catch(Exception e){
//			throw e;
//		}finally{
//			gisPool.returnObject(obj);
//		}
//		return toReturn;		
//	}

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
		
		
//		if(request instanceof ImageGeneratorRequest) return requestPerlGeneration((ImageGeneratorRequest) request);
//		else if(request instanceof GISGenerationRequest) return requestGISGeneration((GISGenerationRequest) request);
//		else if(request instanceof SourceGeneratorRequest) return requestSourceGeneration((SourceGeneratorRequest)request);
//		else throw new BadRequestException();
	}

}

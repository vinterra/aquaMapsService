package org.gcube.application.aquamaps.aquamapsservice.impl.generators;



import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GroupGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.StyleGenerationRequest;
import org.gcube.common.core.utils.logging.GCUBELog;

public class GeneratorManager {
	static GCUBELog logger= new GCUBELog(GeneratorManager.class);
	private static GenericObjectPool perlPool=new GenericObjectPool(new PerlCallsFactory());
	private static GenericObjectPool gisPool=new GenericObjectPool(new GISGeneratorFactory());

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

	}


	private static boolean requestPerlGeneration(ImageGeneratorRequest request) throws Exception{
		boolean toReturn=false;
		PerlImageGenerator obj = null;
		try{
			obj=(PerlImageGenerator) perlPool.borrowObject();
			obj.setRequest(request);
			toReturn= obj.generate();
		}catch(Exception e){
			throw e;
		}finally{
			perlPool.returnObject(obj);
		}

		return toReturn;
	}

	private static boolean requestGISGeneration(GISGenerationRequest req)throws Exception{
		GISGenerator obj=null;
		boolean toReturn=false;
		try{
			obj=(GISGenerator) gisPool.borrowObject();
			if(req instanceof GroupGenerationRequest) toReturn=obj.createGroup((GroupGenerationRequest) req);
			else if(req instanceof LayerGenerationRequest) toReturn=obj.createLayer((LayerGenerationRequest) req);
			else if(req instanceof StyleGenerationRequest) toReturn=obj.generateStyle((StyleGenerationRequest) req);
			else throw new BadRequestException();
		}catch(Exception e){
			throw e;
		}finally{
			gisPool.returnObject(obj);
		}
		return toReturn;		
	}

	public static boolean requestGeneration(GenerationRequest request)throws Exception{
		if(request instanceof ImageGeneratorRequest) return requestPerlGeneration((ImageGeneratorRequest) request);
		else if(request instanceof GISGenerationRequest) return requestGISGeneration((GISGenerationRequest) request);
		else throw new BadRequestException();
	}

}

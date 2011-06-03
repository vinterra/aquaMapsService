package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.common.core.utils.logging.GCUBELog;


public class EnvironmentalLogicManager {

	
	static GCUBELog logger= new GCUBELog(EnvironmentalLogicManager.class);
	private static GenericObjectPool batchPool=new GenericObjectPool(new BatchGeneratorObjectFactory());
	
	
	
	
	
	
	static{
		batchPool.setLifo(false);
		batchPool.setMaxActive(ServiceContext.getContext().getBATCH_POOL_SIZE());
		batchPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try{
			for(int i =0;i<batchPool.getMaxActive();i++){
				batchPool.addObject();
			}
			logger.debug("Added "+batchPool.getMaxActive()+" objects to source generator pool");
		}catch(Exception e){
			logger.error("Unable to init batch pool",e);
		}
		
	}
	public static BatchGeneratorI getBatch() throws Exception{
		return (BatchGeneratorI) batchPool.borrowObject();
	}
	public static void leaveBatch(BatchGeneratorI theBatch)throws Exception{
		batchPool.returnObject(theBatch);
	}
	
}

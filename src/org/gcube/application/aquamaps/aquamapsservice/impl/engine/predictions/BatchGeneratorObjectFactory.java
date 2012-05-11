package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.HashMap;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BatchGeneratorObjectFactory extends BasePoolableObjectFactory{

	static GCUBELog logger= new GCUBELog(BatchGeneratorObjectFactory.class);	
	
	public static enum BatchPoolType{
		LOCAL,REMOTE
	}
	
	private static HashMap<Integer,BatchGenerator> generatorMap=new HashMap<Integer, BatchGenerator>();
	
	
	public static EnvironmentalExecutionReportItem getReport(int batchId,boolean getResourceInfo)throws Exception{
		BatchGenerator batch=generatorMap.get(batchId);
		if(batch==null) return null;
		else return batch.getReport(getResourceInfo);
	}
	
	
	//******************* INSTANCE 
	
	private final BatchPoolType type;
	public BatchGeneratorObjectFactory(BatchPoolType type) {
		super();
		this.type=type;
	}
	
	
	
	
	@Override
	public Object makeObject() throws Exception {		
		BatchGenerator batch=new BatchGenerator(type);
		generatorMap.put(batch.getReportId(),batch);
		return batch;
	}
	@Override
	public void activateObject(Object obj) throws Exception {
		
		super.activateObject(obj);
//		((BatchGenerator)obj).setRequest(null);
	}
	@Override
	public void destroyObject(Object obj) throws Exception {		
		super.destroyObject(obj);
	}
	@Override
	public void passivateObject(Object obj) throws Exception {

		super.passivateObject(obj);
//		((SourceGenerator)obj).setRequest(null);
	}
	
	
	
}

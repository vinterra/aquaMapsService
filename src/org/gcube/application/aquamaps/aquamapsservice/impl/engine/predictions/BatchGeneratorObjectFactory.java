package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.ArrayList;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BatchGeneratorObjectFactory extends BasePoolableObjectFactory{

	static GCUBELog logger= new GCUBELog(BatchGeneratorObjectFactory.class);	
	
	
	private static ArrayList<BatchGenerator> batchGenerators=new ArrayList<BatchGenerator>();
	
	
	@Override
	public Object makeObject() throws Exception {
		int id = batchGenerators.size();
		BatchGenerator batch=new BatchGenerator(id);
		batchGenerators.add(batch);
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
	
	
	public static EnvironmentalExecutionReportItem getReport(int batchId,boolean getResourceInfo)throws Exception{
		try{
			return batchGenerators.get(batchId).getReport(getResourceInfo);
		}catch (IndexOutOfBoundsException e){
			return null;
		}
	}
	
	
}

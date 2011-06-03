package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import java.util.ArrayList;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BatchGeneratorObjectFactory extends BasePoolableObjectFactory{

	static GCUBELog logger= new GCUBELog(BatchGeneratorObjectFactory.class);	
	
	
	private ArrayList<BatchGenerator> batchGenerators=new ArrayList<BatchGenerator>();
	
	
	@Override
	public Object makeObject() throws Exception {
		int id = batchGenerators.size()+1;
		BatchGenerator batch=new BatchGenerator(id);
		batchGenerators.add(id, batch);
		return batch;
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
	
	
	public Report getReport(int batchId)throws Exception{
		try{
			return batchGenerators.get(batchId).getReport();
		}catch (IndexOutOfBoundsException e){
			throw new Exception("Requested wrong batch Id "+batchId);
		}
	}
	
	
}

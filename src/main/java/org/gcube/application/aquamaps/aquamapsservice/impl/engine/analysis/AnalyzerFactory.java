package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.util.ArrayList;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AnalyzerFactory extends BasePoolableObjectFactory{
	static GCUBELog logger= new GCUBELog(AnalyzerFactory.class);	
	
	private static ArrayList<Analyzer> batchGenerators=new ArrayList<Analyzer>();
	
	@Override
	public Object makeObject() throws Exception {
		int id = batchGenerators.size();
		Analyzer batch=new Analyzer(id);
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
			if(batchId<0||batchId>batchGenerators.size()-1) return null;
			return batchGenerators.get(batchId).getReport(getResourceInfo);
		}catch (IndexOutOfBoundsException e){
			return null;
		}
	}
	
}

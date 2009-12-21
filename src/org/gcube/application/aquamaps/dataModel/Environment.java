package org.gcube.application.aquamaps.dataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Environment {

	public static enum Fields{
	Depth,
	Temperature,
	Salinity,
	PrimaryProduction,
	IceConcentration}
	
	/**
	 * Map 
	 * 		String parameterName -> (String valueName-->Float value);
	 * 		eg:
	 * 		Depth -> min-> 100
	 */
	
	private Map<Fields,Map<String,Float>> parameters=new HashMap<Fields, Map<String,Float>> ();
	
	public Environment(){
		Map<String,Float> depth=new HashMap<String, Float>();
		depth.put(Cell.Tags.DepthMin, new Float(0));
		depth.put(Cell.Tags.DepthMax, new Float(0));
		depth.put(Cell.Tags.DepthMean, new Float(0));
		depth.put(Cell.Tags.DepthSD, new Float(0));
		parameters.put(Fields.Depth, depth);
		Map<String,Float> sst=new HashMap<String, Float>();
		sst.put(Cell.Tags.SSTMNMin, new Float(0));
		sst.put(Cell.Tags.SSTMNMax, new Float(0));
		sst.put(Cell.Tags.SSTMNRange, new Float(0));
		sst.put(Cell.Tags.SSTANSD, new Float(0));
		sst.put(Cell.Tags.SSTANMean, new Float(0));
		sst.put(Cell.Tags.SBTANMean, new Float(0));
		parameters.put(Fields.Temperature, sst);
		Map<String,Float> salinity=new HashMap<String, Float>();
		salinity.put(Cell.Tags.SalinityMin, new Float(0));
		salinity.put(Cell.Tags.SalinityMean, new Float(0));
		salinity.put(Cell.Tags.SalinityMax, new Float(0));
		salinity.put(Cell.Tags.SalinitySD, new Float(0));
		salinity.put(Cell.Tags.SalinityBMean, new Float(0));
		parameters.put(Fields.Salinity, salinity);
		Map<String,Float> primProd=new HashMap<String, Float>();
		primProd.put(Cell.Tags.PrimProdMean,new Float(0));
		parameters.put(Fields.PrimaryProduction, primProd);
		Map<String,Float> iceCon=new HashMap<String, Float>();
		iceCon.put(Cell.Tags.IceAnn, new Float(0));
		iceCon.put(Cell.Tags.IceWin, new Float(0));
		iceCon.put(Cell.Tags.IceFall, new Float(0));
		iceCon.put(Cell.Tags.IceSpring, new Float(0));
		iceCon.put(Cell.Tags.IceSum, new Float(0));
		parameters.put(Fields.IceConcentration,iceCon);
	}
	public Set<Fields> getParameterNames(){
		return parameters.keySet();
	}
	public Set<String> getValueNames(Fields parameterName){
		return parameters.get(parameterName).keySet();
	}
	public float getValue(Fields parameterName,String valueName){
		return parameters.get(parameterName).get(valueName).floatValue();
	}
	public void setValue(Fields parameterName,String valueName,float value){
		parameters.get(parameterName).put(valueName,new Float(value));
	}
	
}

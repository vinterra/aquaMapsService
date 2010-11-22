package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Envelope {

	public static enum Fields{
		Salinity,
		PrimProd,
		Depth,
		Temp,
		IceCon,
		LandDist}
		
	
	private boolean useFaoAreas=true;
	private boolean useBoundingBox=true;
	private boolean useBottomSeaTempAndSalinity=true;
	
	
	/**
	 * @return the useFaoAreas
	 */
	public boolean isUseFaoAreas() {
		return useFaoAreas;
	}
	/**
	 * @param useFaoAreas the useFaoAreas to set
	 */
	public void setUseFaoAreas(boolean useFaoAreas) {
		this.useFaoAreas = useFaoAreas;
	}
	/**
	 * @return the useBoundingBox
	 */
	public boolean isUseBoundingBox() {
		return useBoundingBox;
	}
	/**
	 * @param useBoundingBox the useBoundingBox to set
	 */
	public void setUseBoundingBox(boolean useBoundingBox) {
		this.useBoundingBox = useBoundingBox;
	}
	/**
	 * @return the useBottomSeaTempAndSalinity
	 */
	public boolean isUseBottomSeaTempAndSalinity() {
		return useBottomSeaTempAndSalinity;
	}
	/**
	 * @param useBottomSeaTempAndSalinity the useBottomSeaTempAndSalinity to set
	 */
	public void setUseBottomSeaTempAndSalinity(boolean useBottomSeaTempAndSalinity) {
		this.useBottomSeaTempAndSalinity = useBottomSeaTempAndSalinity;
	}

	private String FaoAreas="";
	/**
	 * @return the faoAreas
	 */
	public String getFaoAreas() {
		return FaoAreas;
	}
	/**
	 * @param faoAreas the faoAreas to set
	 */
	public void setFaoAreas(String faoAreas) {
		FaoAreas = faoAreas;
	}
	/**
	 * @return the boundingBox
	 */
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	/**
	 * @param boundingBox the boundingBox to set
	 */
	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	/**
	 * @return the pelagic
	 */
	public boolean isPelagic() {
		return pelagic;
	}
	/**
	 * @param pelagic the pelagic to set
	 */
	public void setPelagic(boolean pelagic) {
		this.pelagic = pelagic;
	}

	private BoundingBox boundingBox=new BoundingBox();
	private boolean pelagic;
	private boolean useMeanDepth;
	
	
		/**
		 * Map 
		 * 		String parameterName -> (String valueName-->Float value);
		 * 		eg:
		 * 		Depth -> min-> 100
		 */
		
		private Map<Fields,Map<String,Float>> parameters=new HashMap<Fields, Map<String,Float>> ();
		
		public Envelope(){
			Map<String,Float> depth=new HashMap<String, Float>();
			depth.put(Species.Tags.DepthMin, new Float(0));
			depth.put(Species.Tags.DepthMax, new Float(0));
			depth.put(Species.Tags.DepthPrefMax, new Float(0));
			depth.put(Species.Tags.DepthPrefMin, new Float(0));
			parameters.put(Fields.Depth, depth);
			Map<String,Float> temp=new HashMap<String, Float>();
			temp.put(Species.Tags.TempMin, new Float(0));
			temp.put(Species.Tags.TempMax, new Float(0));
			temp.put(Species.Tags.TempPrefMax, new Float(0));
			temp.put(Species.Tags.TempPrefMin, new Float(0));
			parameters.put(Fields.Temp, temp);
			Map<String,Float> salinity=new HashMap<String, Float>();
			salinity.put(Species.Tags.SalinityMin, new Float(0));
			salinity.put(Species.Tags.SalinityMax, new Float(0));
			salinity.put(Species.Tags.SalinityPrefMax, new Float(0));
			salinity.put(Species.Tags.SalinityPrefMin, new Float(0));			
			parameters.put(Fields.Salinity, salinity);
			Map<String,Float> primProd=new HashMap<String, Float>();
			primProd.put(Species.Tags.PrimProdPrefMin,new Float(0));
			primProd.put(Species.Tags.PrimProdPrefMax,new Float(0));
			primProd.put(Species.Tags.PrimProdMin,new Float(0));
			primProd.put(Species.Tags.PrimProdMax,new Float(0));
			parameters.put(Fields.PrimProd, primProd);
			Map<String,Float> iceCon=new HashMap<String, Float>();
			iceCon.put(Species.Tags.IceConMin, new Float(0));
			iceCon.put(Species.Tags.IceConMax, new Float(0));
			iceCon.put(Species.Tags.IceConPrefMin, new Float(0));
			iceCon.put(Species.Tags.IceConPrefMax, new Float(0));			
			parameters.put(Fields.IceCon,iceCon);
			Map<String,Float> land=new HashMap<String, Float>();
			land.put(Species.Tags.LandDistMin, new Float(0));
			land.put(Species.Tags.LandDistMax, new Float(0));
			land.put(Species.Tags.LandDistPrefMin, new Float(0));
			land.put(Species.Tags.LandDistPrefMax, new Float(0));			
			parameters.put(Fields.LandDist,land);
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
		
		public float getMinValue(Fields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,Species.Tags.DepthMin);
			case IceCon : return getValue(parameter,Species.Tags.IceConMin);
			case LandDist: return getValue(parameter,Species.Tags.LandDistMin);
			case PrimProd: return getValue(parameter,Species.Tags.PrimProdMin);
			case Salinity : return getValue(parameter,Species.Tags.SalinityMin);
			case Temp: return getValue(parameter,Species.Tags.TempMin);
			default : return 0;
			}
		}
		
		public static String getMinName(Fields parameter){
			switch(parameter){
			case Depth : return Species.Tags.DepthMin;
			case IceCon : return Species.Tags.IceConMin;
			case LandDist: return Species.Tags.LandDistMin;
			case PrimProd: return Species.Tags.PrimProdMin;
			case Salinity : return Species.Tags.SalinityMin;
			case Temp: return Species.Tags.TempMin;
			default : return "";
			}
		}
		
		public float getMaxValue(Fields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,Species.Tags.DepthMax);
			case IceCon : return getValue(parameter,Species.Tags.IceConMax);
			case LandDist: return getValue(parameter,Species.Tags.LandDistMax);
			case PrimProd: return getValue(parameter,Species.Tags.PrimProdMax);
			case Salinity : return getValue(parameter,Species.Tags.SalinityMax);
			case Temp: return getValue(parameter,Species.Tags.TempMax);
			default : return 0;
			}
		}
		
		public static String getMaxName(Fields parameter){
			switch(parameter){
			case Depth : return Species.Tags.DepthMax;
			case IceCon : return Species.Tags.IceConMax;
			case LandDist: return Species.Tags.LandDistMax;
			case PrimProd: return Species.Tags.PrimProdMax;
			case Salinity : return Species.Tags.SalinityMax;
			case Temp: return Species.Tags.TempMax;
			default : return "";
			}
		}
		
		
		public float getPrefMinValue(Fields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,Species.Tags.DepthPrefMin);
			case IceCon : return getValue(parameter,Species.Tags.IceConPrefMin);
			case LandDist: return getValue(parameter,Species.Tags.LandDistPrefMin);
			case PrimProd: return getValue(parameter,Species.Tags.PrimProdPrefMin);
			case Salinity : return getValue(parameter,Species.Tags.SalinityPrefMin);
			case Temp: return getValue(parameter,Species.Tags.TempPrefMin);
			default : return 0;
			}
		}
		
		public static String getPrefMinName(Fields parameter){
			switch(parameter){
			case Depth : return Species.Tags.DepthPrefMin;
			case IceCon : return Species.Tags.IceConPrefMin;
			case LandDist: return Species.Tags.LandDistPrefMin;
			case PrimProd: return Species.Tags.PrimProdPrefMin;
			case Salinity : return Species.Tags.SalinityPrefMin;
			case Temp: return Species.Tags.TempPrefMin;
			default : return "";
			}
		}
		
		
		public float getPrefMaxValue(Fields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,Species.Tags.DepthPrefMax);
			case IceCon : return getValue(parameter,Species.Tags.IceConPrefMax);
			case LandDist: return getValue(parameter,Species.Tags.LandDistPrefMax);
			case PrimProd: return getValue(parameter,Species.Tags.PrimProdPrefMax);
			case Salinity : return getValue(parameter,Species.Tags.SalinityPrefMax);
			case Temp: return getValue(parameter,Species.Tags.TempPrefMax);
			default : return 0;
			}
		}
		
		public static String getPrefMaxName(Fields parameter){
			switch(parameter){
			case Depth : return Species.Tags.DepthPrefMax;
			case IceCon : return Species.Tags.IceConPrefMax;
			case LandDist: return Species.Tags.LandDistPrefMax;
			case PrimProd: return Species.Tags.PrimProdPrefMax;
			case Salinity : return Species.Tags.SalinityPrefMax;
			case Temp: return Species.Tags.TempPrefMax;
			default : return "";
			}
		}
		public void setUseMeanDepth(boolean useMeanDepth) {
			this.useMeanDepth = useMeanDepth;
		}
		public boolean isUseMeanDepth() {
			return useMeanDepth;
		}
		
		
		
}

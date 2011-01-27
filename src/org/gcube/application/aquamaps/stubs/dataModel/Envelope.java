package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;

import com.thoughtworks.xstream.annotations.XStreamAlias;


public class Envelope {

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
		
		private Map<EnvelopeFields,Map<HspenFields,Double>> parameters=new HashMap<EnvelopeFields, Map<HspenFields,Double>> ();
		
		public Envelope(){
			Map<HspenFields,Double> depth=new HashMap<HspenFields, Double>();
			depth.put(HspenFields.DepthMin, new Double(0));
			depth.put(HspenFields.DepthMax, new Double(0));
			depth.put(HspenFields.DepthPrefMax, new Double(0));
			depth.put(HspenFields.DepthPrefMin, new Double(0));
			parameters.put(EnvelopeFields.Depth, depth);
			Map<HspenFields,Double> temp=new HashMap<HspenFields, Double>();
			temp.put(HspenFields.TempMin, new Double(0));
			temp.put(HspenFields.TempMax, new Double(0));
			temp.put(HspenFields.TempPrefMax, new Double(0));
			temp.put(HspenFields.TempPrefMin, new Double(0));
			parameters.put(EnvelopeFields.Temperature, temp);
			Map<HspenFields,Double> salinity=new HashMap<HspenFields, Double>();
			salinity.put(HspenFields.SalinityMin, new Double(0));
			salinity.put(HspenFields.SalinityMax, new Double(0));
			salinity.put(HspenFields.SalinityPrefMax, new Double(0));
			salinity.put(HspenFields.SalinityPrefMin, new Double(0));			
			parameters.put(EnvelopeFields.Salinity, salinity);
			Map<HspenFields,Double> primProd=new HashMap<HspenFields, Double>();
			primProd.put(HspenFields.PrimProdPrefMin,new Double(0));
			primProd.put(HspenFields.PrimProdPrefMax,new Double(0));
			primProd.put(HspenFields.PrimProdMin,new Double(0));
			primProd.put(HspenFields.PrimProdMax,new Double(0));
			parameters.put(EnvelopeFields.PrimaryProduction, primProd);
			Map<HspenFields,Double> iceCon=new HashMap<HspenFields, Double>();
			iceCon.put(HspenFields.IceConMin, new Double(0));
			iceCon.put(HspenFields.IceConMax, new Double(0));
			iceCon.put(HspenFields.IceConPrefMin, new Double(0));
			iceCon.put(HspenFields.IceConPrefMax, new Double(0));			
			parameters.put(EnvelopeFields.IceConcentration,iceCon);
			Map<HspenFields,Double> land=new HashMap<HspenFields, Double>();
			land.put(HspenFields.LandDistMin, new Double(0));
			land.put(HspenFields.LandDistMax, new Double(0));
			land.put(HspenFields.LandDistPrefMin, new Double(0));
			land.put(HspenFields.LandDistPrefMax, new Double(0));			
			parameters.put(EnvelopeFields.LandDistance,land);
		}
		
		public Set<HspenFields> getValueNames(EnvelopeFields parameterName){
			return parameters.get(parameterName).keySet();
		}
		public float getValue(EnvelopeFields parameterName,HspenFields depthprefmax){
			return parameters.get(parameterName).get(depthprefmax).floatValue();
		}
		public void setValue(EnvelopeFields parameterName,HspenFields valueName,double d){
			parameters.get(parameterName).put(valueName,new Double(d));
		}
		
		public float getMinValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.DepthMin);
			case IceConcentration : return getValue(parameter,HspenFields.IceConMin);
			case LandDistance: return getValue(parameter,HspenFields.LandDistMin);
			case PrimaryProduction: return getValue(parameter,HspenFields.PrimProdMin);
			case Salinity : return getValue(parameter,HspenFields.SalinityMin);
			case Temperature: return getValue(parameter,HspenFields.TempMin);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getMinName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.DepthMin;
			case IceConcentration : return HspenFields.IceConMin;
			case LandDistance: return HspenFields.LandDistMin;
			case PrimaryProduction: return HspenFields.PrimProdMin;
			case Salinity : return HspenFields.SalinityMin;
			case Temperature: return HspenFields.TempMin;
			default : throw new IllegalArgumentException();
			}
		}
		
		public float getMaxValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.DepthMax);
			case IceConcentration : return getValue(parameter,HspenFields.IceConMax);
			case LandDistance: return getValue(parameter,HspenFields.LandDistMax);
			case PrimaryProduction: return getValue(parameter,HspenFields.PrimProdMax);
			case Salinity : return getValue(parameter,HspenFields.SalinityMax);
			case Temperature: return getValue(parameter,HspenFields.TempMax);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getMaxName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.DepthMax;
			case IceConcentration : return HspenFields.IceConMax;
			case LandDistance: return HspenFields.LandDistMax;
			case PrimaryProduction: return HspenFields.PrimProdMax;
			case Salinity : return HspenFields.SalinityMax;
			case Temperature: return HspenFields.TempMax;
			default : throw new IllegalArgumentException();
			}
		}
		
		
		public float getPrefMinValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.DepthPrefMin);
			case IceConcentration : return getValue(parameter,HspenFields.IceConPrefMin);
			case LandDistance: return getValue(parameter,HspenFields.LandDistPrefMin);
			case PrimaryProduction: return getValue(parameter,HspenFields.PrimProdPrefMin);
			case Salinity : return getValue(parameter,HspenFields.SalinityPrefMin);
			case Temperature: return getValue(parameter,HspenFields.TempPrefMin);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getPrefMinName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.DepthPrefMin;
			case IceConcentration : return HspenFields.IceConPrefMin;
			case LandDistance: return HspenFields.LandDistPrefMin;
			case PrimaryProduction: return HspenFields.PrimProdPrefMin;
			case Salinity : return HspenFields.SalinityPrefMin;
			case Temperature: return HspenFields.TempPrefMin;
			default : throw new IllegalArgumentException();
			}
		}
		
		
		public float getPrefMaxValue(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return getValue(parameter,HspenFields.DepthPrefMax);
			case IceConcentration : return getValue(parameter,HspenFields.IceConPrefMax);
			case LandDistance: return getValue(parameter,HspenFields.LandDistPrefMax);
			case PrimaryProduction: return getValue(parameter,HspenFields.PrimProdPrefMax);
			case Salinity : return getValue(parameter,HspenFields.SalinityPrefMax);
			case Temperature: return getValue(parameter,HspenFields.TempPrefMax);
			default : throw new IllegalArgumentException();
			}
		}
		
		public static HspenFields getPrefMaxName(EnvelopeFields parameter){
			switch(parameter){
			case Depth : return HspenFields.DepthPrefMax;
			case IceConcentration : return HspenFields.IceConPrefMax;
			case LandDistance: return HspenFields.LandDistPrefMax;
			case PrimaryProduction: return HspenFields.PrimProdPrefMax;
			case Salinity : return HspenFields.SalinityPrefMax;
			case Temperature: return HspenFields.TempPrefMax;
			default : throw new IllegalArgumentException();
			}
		}
		public void setUseMeanDepth(boolean useMeanDepth) {
			this.useMeanDepth = useMeanDepth;
		}
		public boolean isUseMeanDepth() {
			return useMeanDepth;
		}
		
		public FieldArray toFieldArray(){
			List<org.gcube.application.aquamaps.stubs.Field> fields=new ArrayList<org.gcube.application.aquamaps.stubs.Field>();
			for(EnvelopeFields envF:EnvelopeFields.values())
				for(HspenFields paramName:this.getValueNames(envF)){
					org.gcube.application.aquamaps.stubs.Field f= new org.gcube.application.aquamaps.stubs.Field();
					f.setName(paramName.toString());
					f.setType(FieldType.DOUBLE.toString());
					f.setValue(String.valueOf(this.getValue(envF, paramName)));
					fields.add(f);
				}
			return new FieldArray(fields.toArray(new org.gcube.application.aquamaps.stubs.Field[fields.size()]));
		}
		
		
		
}

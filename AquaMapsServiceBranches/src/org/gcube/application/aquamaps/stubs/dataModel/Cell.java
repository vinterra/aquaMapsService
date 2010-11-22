package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.HashMap;
import java.util.Map;

public class Cell {
	
	public static class Tags{
		public static final String ID="CSquareCode";
		public static final String LOICZID="LOICZID";
		public static final String NLimit="NLimit";
		public static final String SLimit="SLimit";
		public static final String ELimit="ELimit";
		public static final String WLimit="WLimit";
		public static final String CenterLat="CenterLat";
		public static final String CenterLong="CenterLong";
		public static final String CellArea="CellArea";
		public static final String OceanArea="OceanArea";
		public static final String CellType="CellType";
		public static final String PWater="PWater";
		public static final String FAOAreaM="FAOAreaM";
		public static final String FAOAreaIn="FaoAreaIn";
		public static final String CountryMain="CountryMain";
		public static final String CountrySecond="CountrySecond";
		public static final String CountryThird="CountryThird";
		public static final String EEZFirst="EEZFirst";
		public static final String EEZSecond="EEZSecond";
		public static final String EEZThird="EEZThird";
		public static final String EEZAll="EEZAll";
		public static final String EEZRemark="EEZRemark";
		public static final String LME="LME";
		public static final String OceanBasis="OCEANBasis";
		public static final String LongHurst="Longhurst";
		public static final String IslandsNo="IslandNo";
		public static final String Area0="Area0_20";
		public static final String Area20="Area20_40";
		public static final String Area40="Area40_60";
		public static final String Area60="Area60_80";
		public static final String Area80="Area80_100";
		public static final String AreaBelow="AreaBelow100";
		public static final String ElevationMin="ElevationMin";
		public static final String ElevationMax="ElevationMax";
		public static final String ElevationMean="ElevationMean";
		public static final String ElevationSD="ElevationSD";
		public static final String WaveH="WaveHeight";
		public static final String TidalRange="TidalRange";
		public static final String LandDist="LandDist";
		public static final String Shelf="Shelf";
		public static final String Slope="Slope";
		public static final String Abyssal="Abyssal";
		public static final String Coral="Coral";
		public static final String Estuary="Estuary";
		public static final String SeaGrass="SeaGrass";
		public static final String SeaMount="SeaMount";
		//Environment
		
	 	public static final String DepthMin="DepthMin";
	 	public static final String DepthMax="DepthMax";
	 	public static final String DepthSD="DepthSD";
	 	public static final String DepthMean="DepthMean";
	 	
	 	public static final String SSTMNMin="SSTMnMin";
	 	public static final String SSTMNMax="SSTMnMax";
	 	public static final String SSTMNRange="SSTMnRange";	 	
	 	public static final String SSTANSD="SSTAnSD";
	 	public static final String SSTANMean="SSTAnMean";
	 	public static final String SBTANMean="SBTAnMean";
	 	
	 	public static final String SalinityMin="SalinityMin";
	 	public static final String SalinityMax="SalinityMax";
	 	public static final String SalinitySD="SalinitySD";
	 	public static final String SalinityMean="SalinityMean";
	 	public static final String SalinityBMean="SalinityBMean";
	 	
	 	public static final String PrimProdMean="PrimProdMean";
	 	
	 	public static final String IceAnn="IceConAnn";
	 	public static final String IceWin="IceConWin";
	 	public static final String IceFall="IceConFall";
	 	public static final String IceSpring="IceConSpr";
	 	public static final String IceSum="IceConSum";
	 	
	 	public static final String GoodCell="GoodCell";
	 	public static final String InFaoArea="InFAOArea";
	 	public static final String InBoundBox="InBoundBox";
	 	
	}
	
	
	
	public Map<String,Field> attributes= new HashMap<String, Field>();
	private String code;
	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Cell>");
		toReturn.append("<"+Tags.ID+">"+code+"</"+Tags.ID+">");
		toReturn.append("<Attributes>");
		for(Field field:attributes.values()) toReturn.append(field.toXML());
		toReturn.append("</Attributes>");
		toReturn.append("</Cell>");
		return toReturn.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Cell))
			return false;
		Cell other = (Cell) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}
	
	
}

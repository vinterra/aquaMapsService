package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;


public class Cell {
	
//		public static final String ID="CSquareCode";
//		public static final String LOICZID="LOICZID";
//		public static final String NLIMIT="NLimit";
//		public static final String SLIMIT="SLimit";
//		public static final String ELIMIT="ELimit";
//		public static final String WLIMIT="WLimit";
//		public static final String CENTERLAT="CenterLat";
//		public static final String CENTERLONG="CenterLong";
//		public static final String CELLAREA="CellArea";
//		public static final String OCEANAREA="OceanArea";
//		public static final String CELLTYPE="CellType";
//		public static final String PWATER="PWater";
//		public static final String FAOAREAM="FAOAreaM";
//		public static final String FAOAREAIN="FaoAreaIn";
//		public static final String COUNTRYMAIN="CountryMain";
//		public static final String COUNTRYSECOND="CountrySecond";
//		public static final String COUNTRYTHIRD="CountryThird";
//		public static final String EEZFIRST="EEZFirst";
//		public static final String EEZSECOND="EEZSecond";
//		public static final String EEZTHIRD="EEZThird";
//		public static final String EEZALL="EEZAll";
//		public static final String EEZREMARK="EEZRemark";
//		public static final String LME="LME";
//		public static final String OCEANBASIS="OCEANBasis";
//		public static final String LONGHURST="Longhurst";
//		public static final String ISLANDNO="IslandNo";
//		public static final String AREA0="Area0_20";
//		public static final String AREA20="Area20_40";
//		public static final String AREA40="Area40_60";
//		public static final String AREA60="Area60_80";
//		public static final String AREA80="Area80_100";
//		public static final String AREABELOW="AreaBelow100";
//		public static final String ELEVATIONMIN="ElevationMin";
//		public static final String ELEVATIONMAX="ElevationMax";
//		public static final String ELEVATIONMEAN="ElevationMean";
//		public static final String ELEVATIONSD="ElevationSD";
//		public static final String WAVEHEIGHT="WaveHeight";
//		public static final String TIDALRANGE="TidalRange";
//		public static final String LANDDIST="LandDist";
//		public static final String SHELF="Shelf";
//		public static final String SLOPE="Slope";
//		public static final String ABYSSAL="Abyssal";
//		public static final String CORAL="Coral";
//		public static final String ESTUARY="Estuary";
//		public static final String SEAGRASS="SeaGrass";
//		public static final String SEAMOUNT="SeaMount";
//		//Environment
//		
//		public static final String DEPTHMIN="DepthMin";
//	 	public static final String DEPTHMAX="DepthMax";
//	 	public static final String DEPTHSD="DepthSD";
//	 	public static final String DEPTHMEAN="DepthMean";
//	 	
//	 	public static final String SSTMNMIN="SSTMnMin";
//	 	public static final String SSTMNMAX="SSTMnMax";
//	 	public static final String SSTMNRANGE="SSTMnRange";	 	
//	 	public static final String SSTANSD="SSTAnSD";
//	 	public static final String SSTANMEAN="SSTAnMean";
//	 	public static final String SBTANMEAN="SBTAnMean";
//	 	
//	 	public static final String SALINITYMIN="SalinityMin";
//	 	public static final String SALINITYMAX="SalinityMax";
//	 	public static final String SALINITYSD="SalinitySD";
//	 	public static final String SALINITYMEAN="SalinityMean";
//	 	public static final String SALINITYBMEAN="SalinityBMean";
//	 	
//	 	public static final String PRIMPRODMEAN="PrimProdMean";
//	 	
//	 	public static final String ICEANN="IceConAnn";
//	 	public static final String ICEWIN="IceConWin";
//	 	public static final String ICEFALL="IceConFall";
//	 	public static final String ICESPRING="IceConSpr";
//	 	public static final String ICESUM="IceConSum";
//	 	
//	 	
//	 	// goodCell- occurrence point
//	 	public static final String GOODCELL="GoodCell";
//	 	public static final String INFAOAREA="InFAOArea";
//	 	public static final String INBOUNDBOX="InBoundBox";
//		
//	
	public Cell(String code){this.cSquareCode=code;}
		
 	private String cSquareCode;
 	
 	public void setCode(String code){this.cSquareCode=code;}
 	public String getCode(){return cSquareCode;}
 	
 	public List<Field> attributesList=new ArrayList<Field>();
	public List<Field> getAttributesList() {
		return attributesList;
	}
	public void setAttributesList(List<Field> attributesList) {
		this.attributesList = attributesList;
	}
	public Field getFieldbyName(String fieldName){
		for(Field field:attributesList){
			if(field.getName().equals(fieldName)) return field;
		}
		return null;		
	}
	
	public void addField(Field toAddField){
		attributesList.add(toAddField);
	}
	
	public Cell(List<Field> initFields){
		this("DUMMYCODE");
		attributesList.addAll(initFields);
		this.setCode(this.getFieldbyName(HCAF_SFields.CSquareCode+"").getValue());
	}
	
	
	
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Cell>");
		toReturn.append("<"+HCAF_SFields.CSquareCode+">"+cSquareCode+"</"+HCAF_SFields.CSquareCode+">");
		toReturn.append("<Attributes>");
		for(Field field:attributesList) toReturn.append(field.toXML());
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
		result = prime * result + ((cSquareCode == null) ? 0 : cSquareCode.hashCode());
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
		if (cSquareCode == null) {
			if (other.cSquareCode != null)
				return false;
		} else if (!cSquareCode.equals(other.cSquareCode))
			return false;
		return true;
	}
	
	
}

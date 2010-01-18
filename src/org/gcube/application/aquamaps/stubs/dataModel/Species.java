package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

public class Species {

	public static class Tags{
		public static final String ID="SpeciesID";
		public static final String GENUS="Genus";
		public static final String FBNAME="FBNAME";
		public static final String SpecCode="SpecCode";
		public static final String Kingdom="Kingdom";
		public static final String Phylum="Phylum";
		public static final String Class="Class";
		public static final String order="Order";
		public static final String Family="Family";
		public static final String deepwater="deepwater";
		public static final String mammals="m_mammals";
		public static final String angling="angling";
		public static final String diving="diving";
		public static final String dangerous="dangerous";
		public static final String invertebrate="m_invertebrates";
		public static final String algae="algae";
		public static final String seaBirds="seabirds";
		public static final String freshwater="freshwater";
		public static final String ScientificName="Scientific_Name";
		public static final String FrenchName="French_Name";
		public static final String EnglishName="English_Name";
		public static final String SpanishName="Spanish_Name";
		public static final String Pelagic="pelagic";
		
		public static final String Common=FBNAME;
		public static final String Species="Species";
		public static final String OccurRecs="OccurRecs";
	 	public static final String OccurCells="OccurCells";
	 	public static final String MapBefore="map_beforeafter";
	 	public static final String MapSeasonal="map_seasonal";
	 	public static final String with_gte5="with_gte_5";
	 	public static final String with_gte6="with_gte_6";
	 	public static final String with_gte66="with_gte_66";
	 	public static final String NoCells3="no_of_cells_3";
	 	public static final String NoCells5="no_of_cells_5";
	 	public static final String NoCells0="no_of_cells_0";
	 	public static final String DBID="database_id";
	 	public static final String picName="picname";
	 	public static final String auth="authname";
	 	public static final String entered="entered";
	 	public static final String totalNative="total_native_csc_cnt";
	 	public static final String Timestamp="timestamp";
	 	public static final String PicUrl="pic_source_url"; 	
	 	
	 	public static final String DepthMin="DepthMin";
	 	public static final String DepthMax="DepthMax";
	 	public static final String DepthPrefMin="DepthPrefMin";
	 	public static final String DepthPrefMax="DepthPrefMax";
	 	
	 	public static final String TempMin="TempMin";
	 	public static final String TempMax="TempMax";
	 	public static final String TempPrefMin="TempPrefMin";
	 	public static final String TempPrefMax="TempPrefMax";
	 	
	 	public static final String SalinityMin="SalinityMin";
	 	public static final String SalinityMax="SalinityMax";
	 	public static final String SalinityPrefMin="SalinityPrefMin";
	 	public static final String SalinityPrefMax="SalinityPrefMax";
	 	
	 	public static final String PrimProdMin="PrimProdMin";
	 	public static final String PrimProdMax="PrimProdMax";
	 	public static final String PrimProdPrefMin="PrimProdPrefMin";
	 	public static final String PrimProdPrefMax="PrimProdPrefMax";
	 	
	 	public static final String IceConMin="IceConMin";
	 	public static final String IceConMax="IceConMax";
	 	public static final String IceConPrefMin="IceConPrefMin";
	 	public static final String IceConPrefMax="IceConPrefMax";
	 	
	 	public static final String LandDistMin="LandDistMin";
	 	public static final String LandDistMax="LandDistMax";
	 	public static final String LandDistPrefMin="LandDistPrefMin";
	 	public static final String LandDistPrefMax="LandDistPrefMax";
	 	
	 	public static final String Depth="Depth";
		public static final String Temp="Temperature";
		public static final String Salinity="Salinity";
		public static final String IceCon="IceConcentration";
		public static final String LandDist="LandDistance";
		public static final String PrimProd="PrimaryProduction";
		
		public static final String FAOAreaM="FAOAreas";
		public static final String NMostLat="NMostLat";
		public static final String SMostLat="SMostLat";
		public static final String EMostLong="EMostLong";
		public static final String WMostLong="WMostLong";
		public static final String Layer="Layer";
	}
	
	private String id;
	public List<Field> attributesList=new ArrayList<Field>();
	public List<Field> getAttributesList() {
		return attributesList;
	}
	public void setAttributesList(List<Field> attributesList) {
		this.attributesList = attributesList;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
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
	
	public String toJSON(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("{\""+Tags.ID+"\":\""+id+"\"");
		for(Field field:attributesList){
			toReturn.append(" ,\""+field.getName()+"\":\""+field.getValue()+"\"");
		}
		toReturn.append("}");
		return toReturn.toString();
	}
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Species>");
		toReturn.append("<"+Tags.ID+">"+id+"</"+Tags.ID+">");
		toReturn.append("<Attributes>");
			for(Field field:attributesList){
				toReturn.append(field.toXML());
			}
		toReturn.append("</Attributes>");
		toReturn.append("</Species>");
		return toReturn.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (!(obj instanceof Species))
			return false;
		Species other = (Species) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	/*public boolean equals(Object obj) {
		if(!(obj instanceof Species)) return false;
		Species toCheck=(Species) obj;
		return(toCheck.getId().equals(this.getId()));
	
	}*/
	
	
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import org.gcube.application.aquamaps.stubs.Area;

public class DBCostants {

	public static final String HCAF_S="HCAF_S";
	public static final String JOB_Table="JOBS";
	public static final String HCAF_D="HCAF_D";
	public static final String HSPEN="hspen";
	public static final String HSPEC="hcaf_species_native";
	
	
	
	public static final String FAOType="FAO";
	public static final String EEZType="EEZ";
	public static final String LMEType="LME";
	
	public static final String cSquareCode="cSquareCode";
	public static final String probability="probability"; 
	public static final String SpeciesID="SpeciesID";
	public static final String cell_FAO="FAOAreaM";
	public static final String cell_EEZ="EEZAll";
	public static final String cell_LME="LME";
	public static final String UNASSIGNED="unassigned";
	public static final String areaCode="code";
	
	public static String clusteringBiodiversityQuery(String hspecName, String tmpTable){
		return "Select "+cSquareCode+", count("+hspecName+"."+SpeciesID+") AS MaxSpeciesCountInACell FROM "+hspecName+
				" INNER JOIN "+tmpTable+" ON "+hspecName+"."+SpeciesID+" = "+tmpTable+"."+SpeciesID+" where probability > ? GROUP BY "+cSquareCode+" ORDER BY MaxSpeciesCountInACell DESC";
	}
	public static String clusteringDistributionQuery(String hspecName){
		return "Select "+cSquareCode+", "+probability+"  FROM "+hspecName+" where "+
				hspecName+"."+SpeciesID+"=? AND "+probability+" > 0.5 ORDER BY "+probability+" DESC";
	}
	
	public static String filterCellByAreaQuery(String newName,String sourceTable,String tempName){
		return "Insert into "+newName+" (Select * from "+sourceTable+	
				 " where "+DBCostants.cSquareCode+" in "+
				 	" (Select "+DBCostants.cSquareCode+" from "+DBCostants.HCAF_S+ ", "+tempName+
								" where "+HCAF_S+"."+cell_FAO+" = "+tempName+"."+areaCode+
								" OR "+HCAF_S+"."+cell_EEZ+" = "+tempName+"."+areaCode+
								" OR "+HCAF_S+"."+cell_LME+" = "+tempName+"."+areaCode+"))";
	}
	public static final String JobStatusUpdating="UPDATE JOBS SET status=? WHERE searchID=?";
	

	public static final String AquaMapStatusUpdating="UPDATE AquaMap SET status=? WHERE searchID=?";
	
	public static final String fileInsertion="INSERT INTO Files (published, nameHuman , Path, Type, owner) VALUE(?, ?, ?, ?, ?)";
	
	public static final String speciesEnvelop="Select * from "+HSPEN+" where "+SpeciesID+" = ?";
	
	public static final String cellEnvironment="Select * from "+HCAF_D+" where "+cSquareCode+" = ?";
	
	public static final String cellFilter(String areaType){
		return ((areaType.equals(LMEType))?cell_LME+" = ?":(areaType.equals(FAOType))?cell_FAO+" = ?":
				"FIND_IN_SET(?,"+cell_EEZ+")");
	}
	
	/**
	 * 
	 * @param selection
	 * @param source
	 * @return [0] query for filter cells against an Area selection
	 * 			[1] query modified for returning the total count of filtered cells 
	 */
	
	public static final String[] cellFiltering(Area[] selection,String source){
		StringBuilder toPerformQuery= new StringBuilder("Select * from "+DBCostants.HCAF_S);
		if((selection!=null)&&(selection.length>0)){
			toPerformQuery.append(" where ");
			for(int i=0;i<selection.length;i++){
				toPerformQuery.append(DBCostants.cellFilter(selection[i].getType())+" ");
				if(i<selection.length-1) toPerformQuery.append(" OR ");
			}
		}
		return new String[]{toPerformQuery.toString(),
				toPerformQuery.toString().replace("*", "count("+cSquareCode+")")};
	}
	public static final String mySQLServerUri="jdbc:mysql://localhost:3306/aquamaps_DB?user=root&password=mybohemian";
	public static final String JDBCClassName="com.mysql.jdbc.Driver";
}

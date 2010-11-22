package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DataTranslation;
import org.gcube.application.aquamaps.stubs.Area;
import org.gcube.application.aquamaps.stubs.Field;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.Filter;
import org.gcube.application.aquamaps.stubs.FilterArray;
import org.gcube.application.aquamaps.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DBCostants {

	private static GCUBELog logger= new GCUBELog(DBCostants.class);
	public static final String toDropTables="tempTables";
	public static final String tempFolders="tempFolders";
	public static final String selectedSpecies="selectedSpecies";
	public static final String HCAF_S="HCAF_S";
	public static final String JOB_Table="submitted";
//	public static final String HCAF_D="HCAF_D";
//	public static final String HSPEN="hspen";
//	public static final String HSPEC="HSPEC";
	public static final String speciesOccurSum="speciesoccursum";
	public static final String GOOD_CELLS="occurrenceCells";
	
	public static final String FAOType="FAO";
	public static final String EEZType="EEZ";
	public static final String LMEType="LME";
	public static final String FaoAreaM="FaoAreaM";
	
	public static final String cSquareCode="cSquareCode";
	public static final String probability="probability"; 
	public static final String SpeciesID="SpeciesID";
	public static final String cell_FAO="FAOAreaM";
	public static final String cell_EEZ="EEZAll";
	public static final String cell_LME="LME";
	public static final String UNASSIGNED="unassigned";
	public static final String areaCode="code";
	public static final String jobId="jobId";
	
	public static String clusteringBiodiversityQuery(String hspecName, String tmpTable){
		String query= "Select "+cSquareCode+", count("+hspecName+"."+SpeciesID+") AS MaxSpeciesCountInACell FROM "+hspecName+
				" INNER JOIN "+tmpTable+" ON "+hspecName+"."+SpeciesID+" = "+tmpTable+"."+SpeciesID+" where probability > ? GROUP BY "+cSquareCode+" ORDER BY MaxSpeciesCountInACell DESC";
		logger.trace("clusteringBiodiversityQuery: "+query);
		return query;
	}
	public static String clusteringDistributionQuery(String hspecName){
		String query= "Select "+cSquareCode+", "+probability+"  FROM "+hspecName+" where "+
				hspecName+"."+SpeciesID+"=?  ORDER BY "+probability+" DESC";
		logger.trace("clusteringDistributionQuery: "+query);
		return query;
	}
	
	
	public static String filterCellByFaoAreas(String newName,String sourceTable){
	return "INSERT IGNORE INTO "+newName+" ( Select "+sourceTable+".* from "+sourceTable+
		" where "+sourceTable+"."+cell_FAO+" = ? ) ";
	}	
	
	public static String filterCellByLMEAreas(String newName,String sourceTable){
		return "INSERT IGNORE INTO "+newName+" ( Select "+sourceTable+".* from "+sourceTable+
			" where "+sourceTable+"."+cell_LME+" = ? ) ";
		}
	public static String filterCellByEEZAreas(String newName,String sourceTable){
		return "INSERT IGNORE INTO "+newName+" ( Select "+sourceTable+".* from "+sourceTable+
			" where find_in_set( ? , "+sourceTable+"."+cell_EEZ+")) ";
		}
	
	public static String filterCellByAreaQuery(String newName,String sourceTable,String tempName){
		String query= "Insert IGNORE into "+newName+" (Select "+sourceTable+".* from "+sourceTable+" , "+tempName+	
		" where (("+tempName+".type ='"+FAOType+"')AND("+sourceTable+"."+cell_FAO+" = "+tempName+"."+areaCode+
		")) OR (("+tempName+".type ='"+EEZType+"')AND( find_in_set("+tempName+"."+areaCode+" , "+sourceTable+"."+cell_EEZ+")) )"+
		" OR (("+tempName+".type ='"+LMEType+"')AND( "+sourceTable+"."+cell_LME+" = "+tempName+"."+areaCode+")) )";
		logger.trace("filterCellByAreaQuery: "+query);
		return query;
	}
	
	public static String getCompleteName(String hspenName,String fieldName)throws Exception{
		if(DataTranslation.speciesoccurSumFields.contains(fieldName))
			return speciesOccurSum+"."+fieldName;
		else if(DataTranslation.hspenFields.contains(fieldName))
			return hspenName+"."+fieldName;
		else throw new Exception("invalid field name");
	}
	
	public static String getFieldOperator(String fieldName){
		if(fieldName.contains("min")) return ">=";
		else if(fieldName.contains("max")) return "<=";
		else return "=";
	}
	
	public static String calculateGoodCells(boolean useFaoRestriction, boolean useBoundingBoxRestriction, String FaoRestriction,
			float n,float s, float w, float e){
		StringBuilder toReturn=new StringBuilder("Select * from "+GOOD_CELLS+" where "+SpeciesID+"= ? ");
		
		if(useFaoRestriction)
			toReturn.append(" AND (find_in_set("+FaoAreaM+" , '"+FaoRestriction.replace(" ", "")+"'))");
		if(useBoundingBoxRestriction){
			toReturn.append("AND (CenterLat<"+n+") AND (CenterLat>"+s+") AND (CenterLong<"+w+") AND (CenterLong>"+e+")");
		}
		return toReturn.toString();
	}
	
	public static final String completeSpeciesById(String HSPEN){
		return "Select * from "+speciesOccurSum+" INNER JOIN "+HSPEN+" ON "+speciesOccurSum+"."+SpeciesID+"="+HSPEN+"."+SpeciesID+" where "+speciesOccurSum+"."+SpeciesID+" = ? ";
	}
	
	public static final String completeCellById(String HCAF_D){
		return "Select * from "+HCAF_S+" INNER JOIN "+HCAF_D+" ON "+HCAF_S+"."+cSquareCode+"="+HCAF_D+"."+cSquareCode+" where "+HCAF_S+"."+cSquareCode+" = ? ";
	}
	
	public static final String profileUpdate="Update Files set Path=? where owner=? and type ='XML'";
	
	
	/**
	 * Creates a query string to filter species against characteristic OR names OR codes filtering
	 * 
	 * @param req
	 * @return
	 * 			[0] query to retrieve species information
	 * 			[1] query to count retrieved species
	 * 
	 * @throws Exception
	 */
	
	public static String[] filterSpecies(GetSpeciesByFiltersRequestType req)throws Exception{
		String selHspen=req.getHspen();
		
		//FIXME request is never complete with HSPEN id
		selHspen=SourceManager.getSourceName(SourceType.HSPEN, SourceManager.getDefaultId(SourceType.HSPEN));
		FieldArray characteristics=req.getCharacteristicFilters();
		FilterArray names=req.getNameFilters();
		FilterArray codes=req.getCodeFilters();
		StringBuilder characteristicsFilter=new StringBuilder();
		StringBuilder namesFilter=new StringBuilder();
		StringBuilder codesFilter=new StringBuilder();
		
		if((characteristics!=null)&&(characteristics.getFields()!=null)&&(characteristics.getFields().length>0)){
			for(int i=0;i<characteristics.getFields().length;i++){				
				Field field=characteristics.getFields(i);
				String fieldName=field.getName().toLowerCase();
				characteristicsFilter.append(getCompleteName(selHspen, fieldName));
				String value=(field.getType().toLowerCase().equals("string"))?"'"+field.getValue()+"'":field.getValue();
				characteristicsFilter.append(" "+getFieldOperator(fieldName)+" "+value);
				if(i<characteristics.getFields().length-1) characteristicsFilter.append(" AND ");
			}
		}
		
		if((names!=null)&&(names.getFilterList()!=null)&&(names.getFilterList().length>0)){
			for(int i=0;i<names.getFilterList().length;i++){				
				Filter filter=names.getFilterList(i);
				namesFilter.append(DataTranslation.filterToString(filter));
				if(i<names.getFilterList().length-1) namesFilter.append(" AND ");
			}
		}
		
		if((codes!=null)&&(codes.getFilterList()!=null)&&(codes.getFilterList().length>0)){
			for(int i=0;i<codes.getFilterList().length;i++){				
				Filter filter=codes.getFilterList(i);
				codesFilter.append(DataTranslation.filterToString(filter));
				if(i<codes.getFilterList().length-1) codesFilter.append(" AND ");
			}
		}
		
		StringBuilder filter=new StringBuilder((characteristicsFilter.length()>0)?"( "+characteristicsFilter.toString()+" )":"");
		if(namesFilter.length()>0) {
			filter.append((filter.length()>0)?" AND ":"");
			filter.append("( "+namesFilter.toString()+")");			
		}
		if(codesFilter.length()>0) {
			filter.append((filter.length()>0)?" AND ":"");
			filter.append("( "+codesFilter.toString()+")");			
		}
		
		String fromString = " from "+speciesOccurSum +((filter.indexOf(selHspen)>-1)?" INNER JOIN "+selHspen+" ON "+speciesOccurSum+"."+SpeciesID+" = "+selHspen+"."+SpeciesID:"");
		String query= "Select "+speciesOccurSum+".* "+fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		String count= "Select count("+speciesOccurSum+"."+SpeciesID+") "+fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		logger.trace("filterSpecies: "+query);
		logger.trace("filterSpecies: "+count);
		return new String[] {query,count};		
	}
	
	
	public static final String submittedStatusUpdating="UPDATE "+JOB_Table+" SET status=? WHERE searchID=?";
	
	public static final String profileRetrieval="Select Path from Files where owner=? and type='xml'";
	
	
	

	public static final String AquaMapsListPerAuthor="select * from "+JOB_Table+" where author = ? AND isAquaMap= ?";
	
	public static final String AquaMapsListPerJob="SELECT * from submitted where jobId=? AND isAquaMap= true";
	
	public static final String fileInsertion="INSERT INTO Files (published, nameHuman , Path, Type, owner) VALUE(?, ?, ?, ?, ?)";
	
	public static final String speciesEnvelop(String HSPEN){
		return "Select * from "+HSPEN+" where "+SpeciesID+" = ?";
	}
	
	public static final String cellEnvironment(String HCAF_D){
		return "Select * from "+HCAF_D+" where "+cSquareCode+" = ?";
	}
	
	public static final String submittedRetrieval="Select * from "+JOB_Table+" where searchId = ?";
	
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
	
	
	public static final String fileRetrievalByOwner="Select * from Files where owner = ?";
	public static final String fileDeletingByOwner="Delete from Files where owner = ?";
	public static final String deleteSubmittedById="Delete from "+JOB_Table+" where searchId= ?";
	
	public static String perturbationUpdate(String hspenTable,List<Perturbation> toSetList, String speciesId) throws Exception{
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("UPDATE "+hspenTable+" SET ");
		for(int i=0;i<toSetList.size();i++){
			Perturbation pert=toSetList.get(i);			
			toReturn.append(getCompleteName(hspenTable, pert.getField().toLowerCase())+" = "+
					pert.getValue());	
			if(i<toSetList.size()-1) toReturn.append(" , ");
		}
		toReturn.append(" WHERE "+SpeciesID+"= '"+speciesId+"'");
		return toReturn.toString();
	}
	
	public static final String markSaved="Update "+JOB_Table+" set saved = true where searchId=?";

	public static String getMetaTable(SourceType type) throws Exception{
		switch(type){
		case HCAF: return "Meta_HCaf";
		case HSPEC: return "Meta_HSPEC";
		case HSPEN: return "Meta_HSpen";
		}
		throw new Exception("Source type not valid "+type.toString());
	}
}

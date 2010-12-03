package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DataTranslation;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DBCostants {

	private static GCUBELog logger= new GCUBELog(DBCostants.class);
	
	public static final String HCAF_S="HCAF_S";
//	public static final String JOB_Table="submitted";
//	public static final String HCAF_D="HCAF_D";
//	public static final String HSPEN="hspen";
//	public static final String HSPEC="HSPEC";
	
	
//	public static final String FAOType="FAO";
//	public static final String EEZType="EEZ";
//	public static final String LMEType="LME";
//	public static final String FaoAreaM="FaoAreaM";
//	
//	public static final String cSquareCode="cSquareCode";
//	public static final String probability="probability"; 
//	public static final String cell_FAO="FAOAreaM";
//	public static final String cell_EEZ="EEZAll";
//	public static final String cell_LME="LME";
//	public static final String UNASSIGNED="unassigned";
//	public static final String areaCode="code";
//	public static final String jobId="jobId";
//	

	

//	public static String filterCellByAreaQuery(String newName,String sourceTable,String tempName){
//		String query= "Insert IGNORE into "+newName+" (Select "+sourceTable+".* from "+sourceTable+" , "+tempName+	
//		" where (("+tempName+".type ='"+FAOType+"')AND("+sourceTable+"."+cell_FAO+" = "+tempName+"."+areaCode+
//		")) OR (("+tempName+".type ='"+EEZType+"')AND( find_in_set("+tempName+"."+areaCode+" , "+sourceTable+"."+cell_EEZ+")) )"+
//		" OR (("+tempName+".type ='"+LMEType+"')AND( "+sourceTable+"."+cell_LME+" = "+tempName+"."+areaCode+")) )";
//		logger.trace("filterCellByAreaQuery: "+query);
//		return query;
//	}
	
	
	
	
	
//	public static String calculateGoodCells(boolean useFaoRestriction, boolean useBoundingBoxRestriction, String FaoRestriction,
//			float n,float s, float w, float e){
//		StringBuilder toReturn=new StringBuilder("Select * from "+GOOD_CELLS+" where "+SpeciesID+"= ? ");
//		
//		if(useFaoRestriction)
//			toReturn.append(" AND (find_in_set("+FaoAreaM+" , '"+FaoRestriction.replace(" ", "")+"'))");
//		if(useBoundingBoxRestriction){
//			toReturn.append("AND (CenterLat<"+n+") AND (CenterLat>"+s+") AND (CenterLong<"+w+") AND (CenterLong>"+e+")");
//		}
//		return toReturn.toString();
//	}
	
//	public static final String completeSpeciesById(String HSPEN){
//		return "Select * from "+speciesOccurSum+" INNER JOIN "+HSPEN+" ON "+speciesOccurSum+"."+SpeciesID+"="+HSPEN+"."+SpeciesID+" where "+speciesOccurSum+"."+SpeciesID+" = ? ";
//	}
//	
//	public static final String completeCellById(String HCAF_D){
//		return "Select * from "+HCAF_S+" INNER JOIN "+HCAF_D+" ON "+HCAF_S+"."+cSquareCode+"="+HCAF_D+"."+cSquareCode+" where "+HCAF_S+"."+cSquareCode+" = ? ";
//	}
//	
//	
//	
//	
//	
//	
//	public static final String submittedStatusUpdating="UPDATE "+JOB_Table+" SET status=? WHERE searchID=?";
//	
//	public static final String profileRetrieval="Select Path from Files where owner=? and type='xml'";
//	
//	
//	
//
//	public static final String AquaMapsListPerAuthor="select * from "+JOB_Table+" where author = ? AND isAquaMap= ?";
//	
//	public static final String AquaMapsListPerJob="SELECT * from submitted where jobId=? AND isAquaMap= true";
//	
//	
//	public static final String speciesEnvelop(String HSPEN){
//		return "Select * from "+HSPEN+" where "+SpeciesID+" = ?";
//	}
//	
//	public static final String cellEnvironment(String HCAF_D){
//		return "Select * from "+HCAF_D+" where "+cSquareCode+" = ?";
//	}
//	
//	public static final String submittedRetrieval="Select * from "+JOB_Table+" where searchId = ?";
//	
//	public static final String cellFilter(AreaType areaType){
//		switch(areaType){
//		case EEZ: return "FIND_IN_SET(?,"+cell_EEZ+")";
//		case FAO: return cell_FAO+" = ?";
//		case LME: return cell_LME+" = ?";
//		}
//		return null;		
//	}
//	
//	/**
//	 * 
//	 * @param selection
//	 * @param source
//	 * @return [0] query for filter cells against an Area selection
//	 * 			[1] query modified for returning the total count of filtered cells 
//	 */
//	
//	public static final String[] cellFiltering(Set<Area> selection,String source){
//		StringBuilder toPerformQuery= new StringBuilder("Select * from "+DBCostants.HCAF_S);
//		if(selection.size()>0){
//			toPerformQuery.append(" where ");
//			for(Area a:selection){
//				toPerformQuery.append(DBCostants.cellFilter(a.getType())+" OR ");
//			}
//			toPerformQuery.delete(toPerformQuery.lastIndexOf("OR"), toPerformQuery.lastIndexOf("OR")+1);
//		}
//		return new String[]{toPerformQuery.toString(),
//				toPerformQuery.toString().replace("*", "count("+cSquareCode+")")};
//	}
//	public static final String mySQLServerUri="jdbc:mysql://localhost:3306/aquamaps_DB?user=root&password=mybohemian";
//	public static final String JDBCClassName="com.mysql.jdbc.Driver";
//	
//	
//	public static final String fileRetrievalByOwner="Select * from Files where owner = ?";
//	public static final String fileDeletingByOwner="Delete from Files where owner = ?";
//	public static final String deleteSubmittedById="Delete from "+JOB_Table+" where searchId= ?";
//	
//	
//	
//	public static final String markSaved="Update "+JOB_Table+" set saved = true where searchId=?";
//
//	public static String getMetaTable(ResourceType type) throws Exception{
//		switch(type){
//		case HCAF: return "Meta_HCaf";
//		case HSPEC: return "Meta_HSPEC";
//		case HSPEN: return "Meta_HSpen";
//		}
//		throw new Exception("Source type not valid "+type.toString());
//	}
}

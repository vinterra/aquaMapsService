package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.Field;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.Filter;
import org.gcube.application.aquamaps.stubs.Perturbation;
import org.gcube.application.aquamaps.stubs.Resource;

public class DataTranslation {

	public static HashMap<String,String> completeFieldNamesMap = new HashMap<String, String>();
	static HashMap<String,String> completePhylogenyQuery = new HashMap<String, String>();
	public static HashMap<String,String> completeResourceListQuery = new HashMap<String, String>();

	public static Set<String> speciesoccurSumFields=new HashSet<String>();
	public static Set<String> hspenFields=new HashSet<String>();
	
	static{
		speciesoccurSumFields.add("genus");
		speciesoccurSumFields.add("species");
		speciesoccurSumFields.add("fbname");
		speciesoccurSumFields.add("speccode");
		speciesoccurSumFields.add("speciesid");
		speciesoccurSumFields.add("kingdom");
		speciesoccurSumFields.add("phylum");
		speciesoccurSumFields.add("class");
		speciesoccurSumFields.add("order");
		speciesoccurSumFields.add("family");
		speciesoccurSumFields.add("deepwater");
		speciesoccurSumFields.add("angling");
		speciesoccurSumFields.add("diving");
		speciesoccurSumFields.add("dangerous");
		speciesoccurSumFields.add("algae");
		speciesoccurSumFields.add("seabirds");
		speciesoccurSumFields.add("freshwater");
		speciesoccurSumFields.add("scientific_name");
		speciesoccurSumFields.add("english_name");
		speciesoccurSumFields.add("french_name");
		speciesoccurSumFields.add("spanish_name");
		
		hspenFields.add("speccode");
		hspenFields.add("speciesid");
		hspenFields.add("depthmin");
		hspenFields.add("depthmax");
		hspenFields.add("pelagic");
		hspenFields.add("tempmin");
		hspenFields.add("tempmax");
		hspenFields.add("salinitymin");
		hspenFields.add("salinitymax");
		hspenFields.add("landdistmin");
		hspenFields.add("landdistmax");
		hspenFields.add("primprodmin");
		hspenFields.add("primprodmax");
		hspenFields.add("depthmean");
		
		completeFieldNamesMap.put("DepthMin", "hspen.DepthMin>=");
		completeFieldNamesMap.put("DepthMax", "hspen.DepthMax<=");
		
		completeFieldNamesMap.put("TempMin", "hspen.TempMin>=");
		completeFieldNamesMap.put("TempMax", "hspen.TempMax<=");
		
		completeFieldNamesMap.put("SalinityMin", "hspen.SalinityMin>=");
		completeFieldNamesMap.put("SalinityMax", "hspen.SalinityMax<=");
		
		completeFieldNamesMap.put("LandDistMin", "hspen.LandDistMin>=");
		completeFieldNamesMap.put("LandDistMax", "hspen.LandDistMax<=");
		
		completeFieldNamesMap.put("IceConMin", "hspen.IceConMin>=");
		completeFieldNamesMap.put("IceConMax", "hspen.IceConMax<=");
		
		completeFieldNamesMap.put("Pelagic", "hspen.Pelagic=");
		
		completeFieldNamesMap.put("deepwater", "speciesoccursum.deepwater=");
		completeFieldNamesMap.put("angling", "speciesoccursum.angling=");
		completeFieldNamesMap.put("diving", "speciesoccursum.diving=");
		completeFieldNamesMap.put("dangerous", "speciesoccursum.dangerous=");
		
		completeFieldNamesMap.put("Kingdom", "speciesoccursum.Kingdom=");
		completeFieldNamesMap.put("Phylum", "speciesoccursum.Phylum=");
		completeFieldNamesMap.put("Class", "speciesoccursum.Class=");
		completeFieldNamesMap.put("Order", "speciesoccursum.Order=");
		completeFieldNamesMap.put("Family", "speciesoccursum.Family=");
		
		completePhylogenyQuery.put("Kingdom", "Select distinct speciesoccursum.Kingdom from speciesoccursum ORDER BY speciesoccursum.Kingdom");
		completePhylogenyQuery.put("Phylum", "Select distinct speciesoccursum.Kingdom, speciesoccursum.Phylum from speciesoccursum ORDER BY speciesoccursum.Phylum");
		completePhylogenyQuery.put("Class", "Select distinct speciesoccursum.Kingdom, speciesoccursum.Phylum, speciesoccursum.Class from speciesoccursum ORDER BY speciesoccursum.Class");
		completePhylogenyQuery.put("Order", "Select distinct speciesoccursum.Kingdom, speciesoccursum.Phylum, speciesoccursum.Class, speciesoccursum.Order from speciesoccursum ORDER BY speciesoccursum.Order");
		completePhylogenyQuery.put("Family", "Select distinct speciesoccursum.Kingdom, speciesoccursum.Phylum, speciesoccursum.Class, speciesoccursum.Order, speciesoccursum.Family from speciesoccursum ORDER BY speciesoccursum.Family ");
		completePhylogenyQuery.put(DBCostants.SpeciesID, "Select speciesoccursum.Kingdom, speciesoccursum.Phylum, speciesoccursum.Class, speciesoccursum.Order, speciesoccursum.Family, speciesoccursum.SPECIESID  from speciesoccursum ORDER BY speciesoccursum.SPECIESID ");
	
		completeResourceListQuery.put("JOBS", "select JOBS.*, Meta_HSPEC.title As sourceName from JOBS, Meta_HSPEC where Meta_HSPEC.searchId = JOBS.sourceId");
		completeResourceListQuery.put("Meta_HCaf", "select * from Meta_HCaf");
		completeResourceListQuery.put("Meta_HSPEC", "select Meta_HSPEC.*, Meta_HCaf.title AS sourceHCafName, Meta_HSpen.title AS sourceHSpenName from Meta_HSPEC, Meta_HSpen, Meta_HCaf where Meta_HSPEC.sourceHCafId = Meta_HCaf.searchId AND Meta_HSPEC.sourceHSpenId = Meta_HSpen.searchId");
		completeResourceListQuery.put("Meta_HSpen", "select * from Meta_HSpen");
	}
	
	public static final int ResourceIdIndex=8;
	public static final int ResourceNameIndex=1;
	
	public static final Resource getResourceFromResultSet(ResultSet rs,ResultSetMetaData metaData, String type) throws SQLException{		 
		int numberOfColumns = metaData.getColumnCount();
		Resource toReturn=new Resource();
		toReturn.setType(type);
		Field[] fields;
		if(type.equalsIgnoreCase("Jobs"))fields =new Field[numberOfColumns+1];
		else fields=new Field[numberOfColumns];
		for(int i=0;i<numberOfColumns;i++){
			Field app=new Field();
			app.setType(metaData.getColumnTypeName(i+1));
			app.setName(metaData.getColumnName(i+1));
			app.setValue(rs.getString(i+1));
			fields[i]=app;
		}
		toReturn.setAdditionalField(new FieldArray(fields));
		toReturn.setId(rs.getString(ResourceIdIndex));
		toReturn.setName(rs.getString(ResourceNameIndex));
		return toReturn;
	}
	
	public static String filterToString(Filter filter)throws Exception{
		StringBuilder toReturn=new StringBuilder();		
		toReturn.append(DBCostants.getCompleteName(DBCostants.HSPEN, filter.getName().toLowerCase()));
		if(filter.getType().equalsIgnoreCase("is")){
	    	toReturn.append(" = '"+filter.getValue()+"'");	  
	    }else
	    if(filter.getType().equalsIgnoreCase("contains")){
	    	toReturn.append(" like '%"+filter.getValue()+"%'");	    	
	    }else
	    if(filter.getType().equalsIgnoreCase("begins")){
	    	toReturn.append(" like '"+filter.getValue()+"%'");	    	
	    }else
	    if(filter.getType().equalsIgnoreCase("ends")){
	    	toReturn.append(" like '%"+filter.getValue()+"'");	    	
	    }else throw new Exception("invalid filter Condition : "+filter.getType());
		return toReturn.toString();
	}
	
	public static ArrayList<Field> resultSetToFields(ResultSet rs, ResultSetMetaData meta)throws SQLException{
		ArrayList<Field> toReturn=new ArrayList<Field>();
		for(int i=0;i<meta.getColumnCount();i++){
			Field app=new Field();
			app.setName(meta.getColumnName(i+1));
			app.setValue(rs.getString(i+1));
			app.setType(meta.getColumnTypeName(i+1));
			toReturn.add(app);
		}
		return toReturn;
	}

	
	
	
	
}

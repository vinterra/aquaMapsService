package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Perturbation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.datamodel.PagedRequestSettings;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesManager {

	private static GCUBELog logger= new GCUBELog(SpeciesManager.class);
	public static final String speciesOccurSum="speciesoccursum";
	
	
	
	public static Species getSpeciesById(boolean fetchStatic,boolean fetchEnvelope, String id, int hspenId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(SpeciesOccursumFields.speciesid+"", id, FieldType.STRING));
			Species toReturn=new Species(id);
			if(fetchStatic){
				List<Field> row=Field.loadResultSet(session.executeFilteredQuery(filters, speciesOccurSum,null,null)).get(0);
				if(row!=null) toReturn.getAttributesList().addAll(row);
			}
			if(fetchEnvelope){
				String hspenTable=SourceManager.getSourceName(hspenId);
				List<Field> row=Field.loadResultSet(session.executeFilteredQuery(filters, hspenTable,null,null)).get(0);
				if(row!=null) toReturn.getAttributesList().addAll(row);
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static Set<Species> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return loadRS(session.executeFilteredQuery(filters, speciesOccurSum,null,null));
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static Set<Species> getList(List<Field> filters,Resource hspen)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			String app=ServiceUtils.generateId("spec", "");
			String query="CREATE TABLE "+app+" AS (SELECT * FROM "+speciesOccurSum+" WHERE "+SpeciesOccursumFields.speciesid+" IN (SELECT "+SpeciesOccursumFields.speciesid+" FROM "+hspen.getTableName()+"))";
			logger.debug("QUERY IS : "+query);
			session.executeUpdate(query);
			Set<Species> toReturn= loadRS(session.executeFilteredQuery(filters, app,null,null));
			session.dropTable(app);
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static String getJsonList(String orderBy, String orderDir, int limit, int offset, List<Field> characteristics, List<Filter> names, List<Filter> codes, int HSPENId)throws Exception{
		String[] queries;
		String selHspen=SourceManager.getSourceName(HSPENId);
		queries=formfilterQueries(characteristics, names, codes, selHspen);
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			PreparedStatement psCount=session.preparedStatement(queries[1]);
			PreparedStatement psSelection=session.preparedStatement(queries[0]+
					((orderBy!=null)?" order by "+getCompleteName(selHspen, orderBy)+" "+orderDir:"")+" LIMIT "+
					limit+" OFFSET "+offset);
			if(characteristics.size()>0){
				psCount=session.fillParameters(characteristics, 0, psCount);
				psSelection=session.fillParameters(characteristics, 0, psSelection);
			}
			
			ResultSet rs=psCount.executeQuery();
			rs.next();
			Long totalCount=rs.getLong(1);
			return DBUtils.toJSon(psSelection.executeQuery(), totalCount);
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static File getCSVList(List<Field> characteristics, List<Filter> names, List<Filter> codes, int HSPENId)throws Exception{
		String[] queries;
		String selHspen=SourceManager.getSourceName(HSPENId);
		queries=formfilterQueries(characteristics, names, codes, selHspen);
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			PreparedStatement psSelection=session.preparedStatement(queries[0]);
			if(characteristics.size()>0){				
				psSelection=session.fillParameters(characteristics, 0, psSelection);
			}
			File out=File.createTempFile("speciesSelection", ".csv");
			
			CSVUtils.resultSetToCSVFile(psSelection.executeQuery(),out.getAbsolutePath(),true);
			return out;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	
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
	
	private static String[] formfilterQueries(List<Field> characteristics, List<Filter> names, List<Filter> codes, String selHspen)throws Exception{
		
		
		StringBuilder characteristicsFilter=new StringBuilder();
		StringBuilder namesFilter=new StringBuilder();
		StringBuilder codesFilter=new StringBuilder();
		
		if(characteristics.size()>0){
			for(Field field:characteristics){				
				String fieldName=field.getName();
				characteristicsFilter.append(getCompleteName(selHspen, fieldName));
				String value="?";
//				(field.getType().equals(FieldType.STRING))?"'"+field.getValue()+"'":field.getValue();
				characteristicsFilter.append(" "+field.getOperator()+" "+value+" AND ");
			}
//			logger.debug("characteristics filter string : "+characteristicsFilter);
			int index=characteristicsFilter.lastIndexOf("AND");
			characteristicsFilter.delete(index, index+3);
		}
		
		if((names.size()>0)){
			for(Filter filter:names)				
				namesFilter.append(getCompleteName(selHspen, filter.getField().getName())+filter.toSQLString()+" AND ");
			int index=namesFilter.lastIndexOf("AND");
			namesFilter.delete(index, index+3);
		}
		
		if((codes.size()>0)){
			for(Filter filter:codes)				
				codesFilter.append(getCompleteName(selHspen, filter.getField().getName())+filter.toSQLString()+" AND ");
			int index=codesFilter.lastIndexOf("AND");
			codesFilter.delete(index, index+3);
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
		
		
		String fromString = " from "+speciesOccurSum +((true)?" INNER JOIN "+selHspen+" ON "+speciesOccurSum+"."+SpeciesOccursumFields.speciesid+" = "+selHspen+"."+SpeciesOccursumFields.speciesid:"");
		
		String query= "Select "+speciesOccurSum+".* , "+
				selHspen+"."+HspenFields.pelagic+				
				fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		String count= "Select count("+speciesOccurSum+"."+SpeciesOccursumFields.speciesid+") "+fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		logger.trace("filterSpecies: "+query);
		logger.trace("filterSpecies: "+count);
		return new String[] {query,count};		
	}
	
	
	public static String getFilteredHSPEN(String sourceHSPEN, Set<Species> toInsert)throws Exception{
		DBSession session=null;
		String tmpHspen=null;
		logger.trace("Filtering "+sourceHSPEN);
		try{
			session=DBSession.getInternalDBSession();
			tmpHspen=ServiceUtils.generateId("filteredhspen", "");
			session.createLikeTable(tmpHspen,sourceHSPEN);
			logger.trace("going to fill table "+tmpHspen);
			List<Field> condition=new ArrayList<Field>();
			condition.add(new Field(SpeciesOccursumFields.speciesid+"","",FieldType.STRING));
			PreparedStatement ps=session.getPreparedStatementForInsertFromSelect(condition, tmpHspen, sourceHSPEN);
			int count=0;
			for(Species s: toInsert){
				ps.setString(1, s.getId());
				int inserted=ps.executeUpdate();
				if(inserted==0)logger.warn("Species ID : "+s.getId()+" hasn't been inserted");
				else count+=inserted;
			}
			logger.trace("Inserted "+count+"/"+toInsert.size()+" species");
			return tmpHspen;
		}catch(Exception e){
			logger.error("Unable to filter against species selection");
			if(tmpHspen!=null) session.dropTable(tmpHspen);
			throw e;
		}finally{
			if(session!=null) session.close();			
		}
	}
	
	
	public static String getCompleteName(String hspenName,String fieldName)throws Exception{
		try{
			return speciesOccurSum+"."+SpeciesOccursumFields.valueOf(fieldName);
		}catch(IllegalArgumentException e){
			//not a speciesoccursum field
		}
		return hspenName+"."+HspenFields.valueOf(fieldName);		
	}
	
	public static String perturbationUpdate(String hspenTable,Map<String,Perturbation> toSetPerturbations, String speciesId) throws Exception{
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("UPDATE "+hspenTable+" SET ");
		for(Entry<String, Perturbation> settings:toSetPerturbations.entrySet()){
			toReturn.append(settings.getKey()+" = "+settings.getValue().getPerturbationValue()+" , ");
		}
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		toReturn.append(" WHERE "+SpeciesOccursumFields.speciesid+"= '"+speciesId+"'");
		
		return toReturn.toString();
	}
	
	
	
	private static Set<Species> loadRS(ResultSet rs) throws SQLException{
		HashSet<Species> toReturn=new HashSet<Species>();
		List<List<Field>> rows=Field.loadResultSet(rs);
		for(List<Field> row:rows){
			Species toAdd=new Species("***");
			toAdd.getAttributesList().addAll(row);
			toAdd.setId(toAdd.getFieldbyName(SpeciesOccursumFields.speciesid+"").getValue());
			toReturn.add(toAdd);
		}
		return toReturn;
	}
	
	
	public static String getJSONTaxonomy(Field toSelect, List<Field> filters, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			return DBUtils.toJSon(session.getDistinct(toSelect, filters, speciesOccurSum, 
					settings.getOrderField(), settings.getOrderDirection()), settings.getOffset(), settings.getLimit()+settings.getOffset());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static String getCommonTaxonomy(Set<Species> species)throws Exception{
		logger.info("Chcking common taxonomy, to analyze species count : "+species.size());
		logger.info("loading species static info..");		
		long start=System.currentTimeMillis();		
		Set<Species> enrichedSpecies=new HashSet<Species>();
		for(Species s: species)enrichedSpecies.add(getSpeciesById(true, false, s.getId(), 0));
		logger.info("Loaded in "+(System.currentTimeMillis()-start)+" ms");		
		HashMap<SpeciesOccursumFields,String> commonLevels=new HashMap<SpeciesOccursumFields, String>();
		SpeciesOccursumFields[] toCheckValues=new SpeciesOccursumFields[]{
			SpeciesOccursumFields.kingdom,
			SpeciesOccursumFields.phylum,
			SpeciesOccursumFields.classcolumn,
			SpeciesOccursumFields.ordercolumn,
			SpeciesOccursumFields.familycolumn
		};
		boolean continueCheck=true;
		for(SpeciesOccursumFields toCheck:toCheckValues){
			if(continueCheck)for(Species s: enrichedSpecies){
				if(!commonLevels.containsKey(toCheck))commonLevels.put(toCheck, s.getFieldbyName(toCheck+"").getValue());
				else if(!s.getFieldbyName(toCheck+"").getValue().equalsIgnoreCase(commonLevels.get(toCheck))){
					continueCheck=false;
					commonLevels.remove(toCheck);
				}
			}
		}
		StringBuilder toReturn=new StringBuilder();
		for(SpeciesOccursumFields level:toCheckValues)
			if(commonLevels.containsKey(level))toReturn.append(commonLevels.get(level)+File.separator);
		if(toReturn.length()>0)toReturn.deleteCharAt(toReturn.lastIndexOf(File.separator));
		logger.info("Found common taxonomy in "+(System.currentTimeMillis()-start)+" ms");
		return toReturn.toString();
	}
}

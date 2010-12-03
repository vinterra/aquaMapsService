package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SpeciesManager {

	private static GCUBELog logger= new GCUBELog(SpeciesManager.class);
	public static final String speciesOccurSum="speciesoccursum";
	public static final String GOOD_CELLS="occurrenceCells";
	
	
	public static Species getSpeciesById(boolean fetchStatic,boolean fetchEnvelope, String id, int hspenId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(SpeciesOccursumFields.SpeciesID+"", id, FieldType.STRING));
			Species toReturn=new Species(id);
			if(fetchStatic){
				List<Field> row=DBUtils.toFields(session.executeFilteredQuery(filters, speciesOccurSum,null,null)).get(0);
				if(row!=null) toReturn.attributesList.addAll(row);
			}
			if(fetchEnvelope){
				String hspenTable=SourceManager.getSourceName(ResourceType.HSPEN, hspenId);
				List<Field> row=DBUtils.toFields(session.executeFilteredQuery(filters, hspenTable,null,null)).get(0);
				if(row!=null) toReturn.attributesList.addAll(row);
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	public static Set<Species> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			return loadRS(session.executeFilteredQuery(filters, speciesOccurSum,null,null));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	public static String getJsonList(String orderBy, String orderDir, int limit, int offset, List<Field> characteristics, List<Filter> names, List<Filter> codes, int HSPENId)throws Exception{
		String[] queries;
		String selHspen=SourceManager.getSourceName(ResourceType.HSPEN, HSPENId);
		queries=formfilterQueries(characteristics, names, codes, selHspen);
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			ResultSet rs=session.executeQuery(queries[1]);
			rs.next();
			int totalCount=rs.getInt(1);
			return DBUtils.toJSon(session.executeQuery(queries[0]+
					((orderBy!=null)?" order by "+getCompleteName(selHspen, orderBy)+" "+orderDir:"")+" LIMIT "+
					limit+" OFFSET "+offset), totalCount);
		}catch(Exception e){throw e;}
		finally{session.close();}
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
				String value=(field.getType().equals(FieldType.STRING))?"'"+field.getValue()+"'":field.getValue();
				characteristicsFilter.append(" "+field.getOperator()+" "+value+" AND");
			}
//			logger.debug("characteristics filter string : "+characteristicsFilter);
			int index=characteristicsFilter.lastIndexOf("AND");
			characteristicsFilter.delete(index, index+3);
		}
		
		if((names.size()>0)){
			for(Filter filter:names)				
				namesFilter.append(getCompleteName(selHspen, filter.getField().getName())+filter.toSQLString());
			int index=namesFilter.lastIndexOf("AND");
			namesFilter.delete(index, index+3);
		}
		
		if((codes.size()>0)){
			for(Filter filter:codes)				
				codesFilter.append(getCompleteName(selHspen, filter.getField().getName())+filter.toSQLString());
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
		
		String fromString = " from "+speciesOccurSum +((filter.indexOf(selHspen)>-1)?" INNER JOIN "+selHspen+" ON "+speciesOccurSum+"."+SpeciesOccursumFields.SpeciesID+" = "+selHspen+"."+SpeciesOccursumFields.SpeciesID:"");
		String query= "Select "+speciesOccurSum+".* "+fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		String count= "Select count("+speciesOccurSum+"."+SpeciesOccursumFields.SpeciesID+") "+fromString+" "+((filter.length()>0)?" where ":"")+filter.toString();
		logger.trace("filterSpecies: "+query);
		logger.trace("filterSpecies: "+count);
		return new String[] {query,count};		
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
			toReturn.append(getCompleteName(hspenTable,settings.getKey())+" = "+settings.getValue().getPerturbationValue()+" , ");
		}
		toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		toReturn.append(" WHERE "+SpeciesOccursumFields.SpeciesID+"= '"+speciesId+"'");
		
		return toReturn.toString();
	}
	
	private static Set<Species> loadRS(ResultSet rs) throws SQLException{
		HashSet<Species> toReturn=new HashSet<Species>();
		List<List<Field>> rows=DBUtils.toFields(rs);
		for(List<Field> row:rows){
			Species toAdd=new Species("***");
			toAdd.attributesList.addAll(row);
			toAdd.setId(toAdd.getFieldbyName(SpeciesOccursumFields.SpeciesID+"").getValue());
			toReturn.add(toAdd);
		}
		return toReturn;
	}
}

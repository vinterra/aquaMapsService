package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;

public class SourceGenerationManager {

//	private static final GCUBELog logger=new GCUBELog(SourceGenerationManager.class);

	private static final String searchId="searchid";
	private static final String author="author";
	private static final String generationTable="hcafgeneration";
	private static final String HCAFsourceId="hcafsourceid";
	private static final String submittedDate="submitteddate";
	private static final String status="status";
	private static final String sources="sources";
	private static final String generatedHCAFName="generatedhcafname";
	private static final String generatedHCAFId="generatedhcafid";


	private static Object getValue(String fieldName,int generationId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(searchId,generationId+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, generationTable, searchId, OrderDirection.ASC);
			if(rs.next())
				return rs.getObject(fieldName);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	private static void setValue(String fieldName,Object value,FieldType type,int generationId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(searchId,generationId+"",FieldType.INTEGER));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(fieldName,value+"",type));
			values.add(valueList);
			session.updateOperation(generationTable, keys, values);
			
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	public static int insertHCAFRequest(String authorValue,int HCAFSourceIdValue, String toGenerateNameValue,String[] sourcesValue)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> row= new ArrayList<Field>();
			row.add(new Field(author,authorValue,FieldType.STRING));
			row.add(new Field(HCAFsourceId,HCAFSourceIdValue+"",FieldType.INTEGER));
			row.add(new Field(generatedHCAFName,toGenerateNameValue,FieldType.STRING));
			
			StringBuilder sourceString=new StringBuilder();
			for(String source:sourcesValue){
				sourceString.append(source+";");
			}
			sourceString.deleteCharAt(sourceString.lastIndexOf(";"));
			row.add(new Field(sources,sourceString.toString(),FieldType.STRING));
			row.add(new Field(submittedDate,ServiceUtils.getDate(),FieldType.STRING));
			row.add(new Field(status,SourceGenerationStatus.Pending+"",FieldType.STRING));
			values.add(row);
			List<List<Field>> ids=session.insertOperation(generationTable, values);
			return Integer.parseInt(ids.get(0).get(0).getValue());
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
		
	}
	
	
	
	public static String getReport(PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(new ArrayList<Field>(), generationTable, settings.getOrderColumn(), settings.getOrderDirection()),
					session.getCount(generationTable, new ArrayList<Field>()));
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	//*********************** SETTERS
	
	
	
	public static void setStatus(int requestId, SourceGenerationStatus toSet)throws Exception{
		setValue(status, toSet.toString(),FieldType.STRING, requestId);
	}
	
	public static void setGeneratedHCAFId(int requestId, int generatedId)throws Exception{
		setValue(generatedHCAFId,generatedId,FieldType.INTEGER,requestId);
	}

	//************************ GETTERS
	
	public static int getSourceId(int requestId)throws Exception{
		return (Integer) getValue(HCAFsourceId, requestId);
	}
	
	
	public static String getSources(int requestId)throws Exception{
		return (String) getValue(sources,requestId);
	}
	
	public static String getGeneratedHCAFName(int requestId)throws Exception{
		return (String) getValue(generatedHCAFName,requestId);
	}
	
	public static String getAuthor(int requestId)throws Exception{
		return (String) getValue(author,requestId);
	}
}

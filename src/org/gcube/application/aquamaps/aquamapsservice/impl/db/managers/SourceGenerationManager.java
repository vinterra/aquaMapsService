package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;

public class SourceGenerationManager {

//	private static final GCUBELog logger=new GCUBELog(SourceGenerationManager.class);

	private static final String searchId="searchId";
	private static final String author="author";
	private static final String generationTable="HCAFGeneration";
	private static final String HCAFsourceId="HCAFSourceId";
	private static final String submittedDate="submittedDate";
	private static final String status="status";
	private static final String sources="sources";
	private static final String generatedHCAFName="generatedHCAFName";
	private static final String generatedHCAFId="generatedHCAFId";


	private static Object getValue(String fieldName,int generationId)throws Exception{
		DBSession session=null;
		try{			
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from "+generationTable+" where "+searchId+" = ?");
			ps.setInt(1, generationId);
			ResultSet rs= ps.executeQuery();
			if(rs.next())
				return rs.getObject(fieldName);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	private static void setValue(String fieldName,Object value,int generationId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Update "+generationTable+" SET "+fieldName+"= ? where "+searchId+"= ?");
			ps.setObject(1, value);
			ps.setInt(2, generationId);
			ps.executeUpdate();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static int insertHCAFRequest(String authorValue,int HCAFSourceIdValue, String toGenerateNameValue,String[] sourcesValue)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.getConnection().prepareStatement("INSERT into "+generationTable+" ("+author+","+
					HCAFsourceId+","+generatedHCAFName+","+sources+","+submittedDate+","+status+") values(?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			ps.setString(1,authorValue);
			ps.setInt(2,HCAFSourceIdValue);
			ps.setString(3,toGenerateNameValue);
			StringBuilder sourceString=new StringBuilder();
			for(String source:sourcesValue){
				sourceString.append(source+";");
			}
			sourceString.deleteCharAt(sourceString.lastIndexOf(";"));
			
			ps.setString(4, sourceString.toString());
			ps.setDate(5, new Date(System.currentTimeMillis()));
			ps.setString(6,SourceGenerationStatus.Pending.toString());
			return ps.executeUpdate();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
		
	}
	
	
	
	public static String getReport(String orderColumn, String orderMode, int offset, int limit)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			ResultSet rs=session.executeQuery("Select count(*) from "+generationTable);
			rs.next();
			int totalCount=rs.getInt(1);
			rs= session.executeQuery("Select * from "+generationTable+" ORDER BY "+orderColumn+" "+orderMode);
			return DBUtils.toJSon(rs, totalCount);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	//*********************** SETTERS
	
	
	
	public static void setStatus(int requestId, SourceGenerationStatus toSet)throws Exception{
		setValue(status, toSet.toString(), requestId);
	}
	
	public static void setGeneratedHCAFId(int requestId, int generatedId)throws Exception{
		setValue(generatedHCAFId,generatedId,requestId);
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

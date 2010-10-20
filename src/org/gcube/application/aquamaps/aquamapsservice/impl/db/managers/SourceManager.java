package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceManager {

	private static final GCUBELog logger=new GCUBELog(SourceManager.class);	
	
	private static final String searchId="searchId";
	private static final String title="title";
	private static final String tableName="tableName";
	private static final String description="description";
	private static final String author="author";
	private static final String disclaimer="disclaimer";
	private static final String provenience="provenience";
	private static final String data="data";
	private static final String sourceId="sourceId";
	private static final String parameters="parameters";
	private static final String status="status";
	private static final String sourceName="sourceName";
	
	
	public static int getDefaultId(SourceType type){
		switch(type){
		case HCAF: return 1;
		case HSPEC: return 1;
		case HSPEN: return 1;
		}
		return 0;
	}
	
	public static int registerSource(SourceType type,String toSetTableName,String toSetDescription,String toSetAuthor, Integer toSetSourceId,SourceType sourceType)throws Exception{
		DBSession session=null;
		logger.trace("registering source "+toSetTableName+" ("+type.toString()+")");
		String toSetSourceName=null;
		try{
		toSetSourceName=getSourceName(sourceType, toSetSourceId);
		}catch(Exception e){
			logger.trace("source not found, skipping..");			
		}
		try{
			String metaTable=DBCostants.getMetaTable(type);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.getConnection().prepareStatement("INSERT into "+metaTable+" ("+tableName+","+description+","+author+","+sourceId+","+sourceName+") values(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);		
			ps.setString(1, toSetTableName);
			ps.setString(2,toSetDescription);
			ps.setString(3, toSetAuthor);
			ps.setInt(4, toSetSourceId);
			ps.setString(5, toSetSourceName);
		if(ps.executeUpdate()>0){			
			ResultSet rs=ps.getGeneratedKeys();
			rs.next();
			int id=rs.getInt(1);
			logger.trace("registered source with id : "+id);
			return id;
		}else throw new Exception ("Nothing generated");		
		}catch(Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	public static void deleteSource(SourceType type,int id) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			String metaTable=DBCostants.getMetaTable(type);
			PreparedStatement ps=session.preparedStatement("DELETE from "+metaTable+" where "+searchId+"=?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}catch(Exception e){
			throw e;			
		}finally{
			session.close();
		}
	}
	
	public static String getSourceName(SourceType type, int id)throws Exception{
		return (String) getField(type, id, tableName);
	}
	
	public static int getSourceId(SourceType type,int id)throws Exception{
		return (Integer) getField(type, id, sourceId);
	}
	
	private static Object getField(SourceType type, int id, String field)throws Exception{
		DBSession session=null;
		try{
			String metaTable=DBCostants.getMetaTable(type);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from "+metaTable+" where "+searchId+" = ?");
			ps.setInt(1, id);
			ResultSet rs= ps.executeQuery();
			if(rs.next())
				return rs.getObject(field);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	private static void updateField(SourceType type, int id, String field, Object value)throws Exception{
		DBSession session=null;
		try{
			String metaTable=DBCostants.getMetaTable(type);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("UPDATE "+metaTable+" SET "+field+" = ? where "+searchId+" = ?");
			ps.setObject(1,value);
			ps.setInt(2, id);
			ps.executeUpdate();			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	
	public static void setTableTitle(SourceType type,int id, String tableTitle)throws Exception{
		updateField(type, id, title, tableTitle);
	}
	
	
}



package org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.AquaMapsManagerI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.JobManagerI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.SubmittedManagerI;

public class ManagersUtils {

	static void setString(String table,String field,int submittedId,String value,String idField)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update "+table+" set "+field+" = ? where "+idField+" = ?");
			ps.setString(1, value);
			ps.setInt(2,submittedId);
			ps.execute();			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	static String getString(String table,String field, int submittedId,String idField)throws Exception{
		DBSession session = null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select "+field+" from "+table+" where "+idField+" = ?");
			ps.setInt(1, submittedId);
			ResultSet rs =ps.executeQuery();
			rs.first();						
			return rs.getString(field);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	static Object getObject(String table,String field, int submittedId,String idField) throws Exception{
		DBSession session = null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select "+field+" from "+table+" where "+idField+" = ?");
			ps.setInt(1, submittedId);
			ResultSet rs =ps.executeQuery();
			rs.first();						
			return rs.getObject(field);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	static void setObject(String table,String field,int submittedId,String value,String idField) throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update "+table+" set "+field+" = ? where "+idField+" = ?");
			ps.setObject(1, value);
			ps.setInt(2,submittedId);
			ps.execute();			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	
	public static AquaMapsManagerI getAquaMapsManagerInstance(){return AquaMapsManager.get();}
	public static JobManagerI getJobManagerInstance(){return JobManager.get();}
	public static SubmittedManagerI getSubmittedManagerInstance(){return SubmittedManager.get();}
}

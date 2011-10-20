package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.QueryConstructurThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.QueryConstructurThread.Operation;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.common.core.utils.logging.GCUBELog;

public class CustomQueryManager {

	public static final String userQueryTable="userqueries";
	public static final String userQueryUser="userid";
	public static final String userQueryResultTable="resulttable";
	public static final String userQueryCreationTime="creationtime";
	public static final String userQueryCount="count";
	public static final String userQuerySQL="query";
	public static final String userQueryReady="isready";
	
	private static final GCUBELog logger=new GCUBELog(CustomQueryManager.class);
	
	
	public static void setUserCustomQuery(String user,String queryString)throws Exception{
		DBSession session=null;
		try{
			deleteUserQuery(user);
			session=DBSession.getInternalDBSession();
			String tableName=ServiceUtils.generateId("CUSTOM", "");
			ArrayList<Field> row=new ArrayList<Field>();
			
			logger.trace("Inserting reference on table..");
			row.add(new Field(userQueryResultTable,tableName,FieldType.STRING));
			row.add(new Field(userQueryCreationTime,System.currentTimeMillis()+"",FieldType.INTEGER));
			row.add(new Field(userQuerySQL,queryString,FieldType.STRING));
			row.add(new Field(userQueryUser,user,FieldType.STRING));
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(row);
			session.insertOperation(userQueryTable, rows);
			
			QueryConstructurThread t=new QueryConstructurThread(user, Operation.CREATE, queryString, tableName);
			t.start();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static String getPagedResult(String user, PagedRequestSettings settings) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(userQueryUser,user,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, userQueryTable, userQueryUser, OrderDirection.ASC);
			if(rs.next()) {
				String tableName=rs.getString(userQueryResultTable);
				Long count=rs.getLong(userQueryCount);
				String query= "SELECT * from "+tableName+" LIMIT "+settings.getLimit()+" OFFSET "+settings.getOffset();
				return DBUtils.toJSon(session.executeQuery(query),count);
			}else return null;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static int deleteUserQuery(String user)throws Exception{
		DBSession session=null;
		try{
			ArrayList<Field> field=new ArrayList<Field>();
			session=DBSession.getInternalDBSession();
			field.add(new Field(userQueryUser,user,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, userQueryTable, userQueryUser, OrderDirection.ASC);
			if(rs.next()){
				String toDropTable=rs.getString(userQueryResultTable);
				String query=rs.getString(userQuerySQL);				
				QueryConstructurThread t=new QueryConstructurThread(user, Operation.DELETE, query, toDropTable);
				t.start();
			}
			logger.trace("Deleting "+user+"'s custom query reference");
			return session.deleteOperation(userQueryTable, field);
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static String getUsersTableName(String user)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(userQueryUser,user,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, userQueryTable, userQueryUser, OrderDirection.ASC);
			if(rs.next()) return rs.getString(userQueryResultTable);
			else return null;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static List<Field> isCustomQueryReady(String userid) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(userQueryUser,userid,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, userQueryTable, userQueryUser, OrderDirection.ASC);
			if(rs.next()) 
				if(rs.getInt(userQueryReady)==1){
					//query ready
					String tableName=rs.getString(userQueryResultTable);
					ResultSet rsColumns=session.executeQuery("SELECT * FROM "+tableName+" LIMIT 1 OFFSET 0");
					ResultSetMetaData meta=rsColumns.getMetaData();
					ArrayList<Field> toReturn=new ArrayList<Field>();
					for(int i=1;i<=meta.getColumnCount();i++)
						toReturn.add(new Field(meta.getColumnName(i),"",FieldType.STRING));
					return toReturn;
				} else return null;
			else {
				logger.trace("No query found for user "+userid);
				return null;
			}
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
}

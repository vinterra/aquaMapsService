package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.common.core.utils.logging.GCUBELog;

public class QueryConstructurThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(QueryConstructurThread.class);

	public enum Operation {
		DELETE,CREATE
	}
	
	
	private String userId;
	private Operation op;
	private String table;
	private String queryString;
	
	
	
	
	public QueryConstructurThread(String userId, Operation op, String query, String tableName) {
		super(op+"_"+userId);
		this.userId=userId;
		this.op=op;
		this.table=tableName;
		this.queryString=query;
	}
	
	@Override
	public void run() {
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(CustomQueryManager.userQueryUser,userId,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, CustomQueryManager.userQueryTable, CustomQueryManager.userQueryUser, OrderDirection.ASC);
			if(rs.next()){				
				switch(op){
					case CREATE : 
						logger.trace("Creating view [ "+table+" ]for "+userId+"'s query [ "+queryString+" ]");
						session.executeUpdate("CREATE TABLE "+table+" AS ( "+queryString+" )");
						long count=session.getTableCount(table);
						ArrayList<Field> row=new ArrayList<Field>();
						row.add(new Field(CustomQueryManager.userQueryCount,count+"",FieldType.INTEGER));
						row.add(new Field(CustomQueryManager.userQueryReady,true+"",FieldType.BOOLEAN));
						ArrayList<List<Field>>  rows=new ArrayList<List<Field>>();
						rows.add(row);
						ArrayList<Field> key=new ArrayList<Field>();
						key.add(new Field(CustomQueryManager.userQueryUser,userId,FieldType.STRING));			
						ArrayList<List<Field>> keys=new ArrayList<List<Field>>();
						keys.add(key);
						session.updateOperation(CustomQueryManager.userQueryTable, keys, rows);
						break;
					case DELETE : 
						logger.trace("Dropping "+userId+"'s custom query view "+table+", query was [ "+queryString+" ]");
						session.dropTable(table);
						break;
						default : throw new Exception ("Operation not defined");
				}
				logger.trace("DONE");
			}
			else throw new Exception("User entry not found");
		}catch(Exception e){
			logger.error("Unable to "+op+" table",e);
		}
		finally{if(session!=null)
			try {
				session.close();
			} catch (Exception e) {
				logger.fatal("Unable to close session",e);
			}}
	}
	
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
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
			switch(op){
				case CREATE :
					ArrayList<Field> field=new ArrayList<Field>();
					field.add(new Field(CustomQueryManager.userQueryUser,userId,FieldType.STRING));
					ResultSet rs=session.executeFilteredQuery(field, CustomQueryManager.userQueryTable, CustomQueryManager.userQueryUser, OrderDirection.ASC);
					if(rs.next()){				
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
					}
					else throw new Exception("User entry not found");
					break;
				case DELETE : 
					logger.trace("Dropping "+userId+"'s custom query view "+table+", query was [ "+queryString+" ]");
					session.dropTable(table);
					break;
				default : throw new Exception ("Operation not defined");
			}
			logger.trace("DONE");
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

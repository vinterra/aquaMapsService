package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.gcube.application.aquamaps.aquamapsservice.client.model.enhanced.CustomQueryDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.client.model.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.types.FieldType;

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
			switch(op){
			case CREATE :
				CustomQueryDescriptor desc=CustomQueryManager.getDescriptor(userId);
				desc.setStatus(ExportStatus.ONGOING);
				CustomQueryManager.updateDescriptor(desc);
				try{
					session=DBSession.getInternalDBSession();
					
					logger.trace("Creating view [ "+table+" ]for "+userId+"'s query [ "+queryString+" ]");
					session.executeUpdate("CREATE TABLE "+table+" AS ( "+queryString+" )");
					logger.trace("Getting meta for custom table "+table);
					
					desc.setRows(session.getTableCount(table));
					ResultSet rsColumns=session.executeQuery("SELECT * FROM "+table+" LIMIT 1 OFFSET 0");
					ResultSetMetaData meta=rsColumns.getMetaData();
					
					for(int i=1;i<=meta.getColumnCount();i++)
						desc.getFields().add(new Field(meta.getColumnName(i),"",FieldType.STRING));
					
					desc.setStatus(ExportStatus.COMPLETED);
					CustomQueryManager.updateDescriptor(desc);
				}catch(Exception e){
					desc.setStatus(ExportStatus.ERROR);
					desc.setErrorMessage(e.getMessage());
					CustomQueryManager.updateDescriptor(desc);
				}
				break;
			case DELETE : 
				logger.trace("Dropping "+userId+"'s custom query view "+table+", query was [ "+queryString+" ]");
				session=DBSession.getInternalDBSession();
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

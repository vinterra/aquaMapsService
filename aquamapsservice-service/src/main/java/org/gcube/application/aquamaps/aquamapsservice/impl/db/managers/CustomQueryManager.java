package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.gcube.application.aquamaps.aquamapsservice.client.model.enhanced.CustomQueryDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.client.model.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.client.model.fields.CustomQueryDescriptorFields;
import org.gcube.application.aquamaps.aquamapsservice.impl.contexts.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.QueryConstructurThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.QueryConstructurThread.Operation;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.Filter;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;



public class CustomQueryManager {

	public static final String userQueryTable="userqueries";

	private static final GCUBELog logger=new GCUBELog(CustomQueryManager.class);


	public static String setUserCustomQuery(String user,String queryString)throws Exception{
		DBSession session=null;
		try{
			deleteUserQuery(user);
			session=DBSession.getInternalDBSession();
			String tableName=ServiceUtils.generateId("CUSTOM", "").toLowerCase();

			logger.trace("Inserting reference on table..");
			CustomQueryDescriptor toInsert= new CustomQueryDescriptor();
			toInsert.setActualTableName(tableName);
			toInsert.setCreationTime(System.currentTimeMillis());
			toInsert.setQuery(queryString);
			toInsert.setUser(user);
			toInsert.setLastAccess(System.currentTimeMillis());
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(toInsert.toRow());
			session.insertOperation(userQueryTable, rows);

			QueryConstructurThread t=new QueryConstructurThread(user, Operation.CREATE, queryString, tableName);
			t.start();
			return user;
		}finally{if(session!=null) session.close();}
	}

	public static String getPagedResult(String user, PagedRequestSettings settings) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			updateLastAccessTime(user);

			CustomQueryDescriptor desc=getDescriptor(user);

			String query="SELECT * from "+desc.getActualTableName()+" ORDER BY "+settings.getOrderField()+" "+settings.getOrderDirection()+" LIMIT "+settings.getLimit()+" OFFSET "+settings.getOffset();
			return DBUtils.toJSon(session.executeQuery(query),desc.getRows());

		}finally{if(session!=null) session.close();}
	}




	private static int deleteUserQuery(String user)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			CustomQueryDescriptor desc=getDescriptor(user);

			QueryConstructurThread t=new QueryConstructurThread(user, Operation.DELETE, desc.getQuery(), desc.getActualTableName());
			t.start();

			logger.trace("Deleting "+user+"'s custom query reference");
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(desc.getField(CustomQueryDescriptorFields.userid));
			return session.deleteOperation(userQueryTable, field);
		}catch(Exception e){
			// custom query non existent
			return 0;
		}finally{if(session!=null) session.close();}
	}




	private static void updateLastAccessTime(String userId) throws Exception{
		DBSession session=null;
		try{
			List<List<Field>> keys=new ArrayList<List<Field>>();
			keys.add(new ArrayList<Field>(Arrays.asList(new Field[]{
					new Field(CustomQueryDescriptorFields.userid+"",userId,FieldType.STRING)
			})));
			List<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(new ArrayList<Field>(Arrays.asList(new Field[]{
					new Field(CustomQueryDescriptorFields.lastaccess+"",System.currentTimeMillis()+"",FieldType.LONG)
			})));
			session=DBSession.getInternalDBSession();
			if(session.updateOperation(userQueryTable, keys, rows)==0)throw new Exception ("No custom query reference found for user "+userId);
		}finally{if(session!=null) session.close();}
	}

	public static int clean()throws Exception{
		DBSession session=null;
		try{
			int keepAliveMinutes=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.CUSTOM_QUERY_KEEP_ALIVE_MINUTES);
			Filter timeFilter=new Filter(FilterType.smaller_then, new Field(CustomQueryDescriptorFields.lastaccess+"",(System.currentTimeMillis()-(keepAliveMinutes*1000*60))+"",FieldType.LONG));
			String queryString="SELECT * FROM "+userQueryTable+" WHERE "+CustomQueryDescriptorFields.lastaccess+" "+timeFilter.toSQLString();
			//			logger.trace("Query String is "+queryString);
			session=DBSession.getInternalDBSession();
			ResultSet rs=session.executeQuery(queryString);
			int count=0;
			while(rs.next()){
				String user=rs.getString(CustomQueryDescriptorFields.userid+"");
				try{
					count+=deleteUserQuery(user);
				}catch(Exception e){
					logger.warn("Unable to delete custom query for user "+user, e);
				}
			}
			return count;
		}finally{if(session!=null) session.close();}
	}

	public static CustomQueryDescriptor getDescriptor(String userid) throws Exception{
		DBSession session=null;
		try{
			updateLastAccessTime(userid);
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(CustomQueryDescriptorFields.userid+"",userid,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, userQueryTable, CustomQueryDescriptorFields.userid+"", OrderDirection.ASC);
			if(rs.next()){
				return new CustomQueryDescriptor(rs);
			}else throw new Exception("Custom Query Not Found for user "+userid);			
		}finally{if(session!=null) session.close();}
	}
	
	public static int updateDescriptor(CustomQueryDescriptor desc)throws Exception{
		DBSession session=null;
		try{
			desc.setLastAccess(System.currentTimeMillis());
			session=DBSession.getInternalDBSession();
			ArrayList<List<Field>> keys=new ArrayList<List<Field>>();
			ArrayList<Field> key=new ArrayList<Field>();
			key.add(desc.getField(CustomQueryDescriptorFields.userid));
			keys.add(key);

			
			ArrayList<Field> row=new ArrayList<Field>();
			for(CustomQueryDescriptorFields f: CustomQueryDescriptorFields.values()){
				if(!f.equals(CustomQueryDescriptorFields.userid))
					row.add(desc.getField(f));
			}
			ArrayList<List<Field>>  rows=new ArrayList<List<Field>>();
			rows.add(row);
			return session.updateOperation(CustomQueryManager.userQueryTable, keys, rows);
		}finally{if(session!=null) session.close();}
	}
}

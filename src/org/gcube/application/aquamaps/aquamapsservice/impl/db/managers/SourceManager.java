package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Resource;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceManager {

	private static final GCUBELog logger=new GCUBELog(SourceManager.class);	
	
	private static String getMetaTable(ResourceType type) throws Exception{
		switch(type){
		case HCAF: return "Meta_HCaf";
		case HSPEC: return "Meta_HSPEC";
		case HSPEN: return "Meta_HSpen";
		}
		throw new Exception("Source type not valid "+type.toString());
	}
	
	public static int getDefaultId(ResourceType type){
		switch(type){
		case HCAF: return 1;
		case HSPEC: return 1;
		case HSPEN: return 1;
		}		
		return 0;
	}
	
	public static int registerSource(ResourceType type,String toSetTableName,String toSetDescription,String toSetAuthor, Integer toSetSourceId,ResourceType sourceType)throws Exception{
		DBSession session=null;
		logger.trace("registering source "+toSetTableName+" ("+type.toString()+")");
		String toSetSourceName=null;
		try{
		toSetSourceName=getSourceName(sourceType, toSetSourceId);
		}catch(Exception e){
			logger.trace("source not found, skipping..");			
		}
		try{
			String metaTable=getMetaTable(type);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.getConnection().prepareStatement("INSERT into "+metaTable+" ("+Resource.TABLENAME+","+Resource.DESCRIPTION+","+Resource.AUTHOR+","+Resource.SOURCEID+","+Resource.SOURCENAME+") values(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);		
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
	
	public static void deleteSource(ResourceType type,int id) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			String metaTable=getMetaTable(type);
			PreparedStatement ps=session.preparedStatement("DELETE from "+metaTable+" where "+Resource.SEARCHID+"=?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}catch(Exception e){
			throw e;			
		}finally{
			session.close();
		}
	}
	
	public static String getSourceName(ResourceType type, int id)throws Exception{
		return (String) getField(type, id, Resource.TABLENAME);
	}
	
	public static String getSourceTitle(ResourceType type, int id)throws Exception{
		return (String) getField(type,id,Resource.TITLE);
	}
	
	public static int getSourceId(ResourceType type,int id)throws Exception{
		return (Integer) getField(type, id, Resource.SOURCEID);
	}
	
	private static Object getField(ResourceType type, int id, String field)throws Exception{
		DBSession session=null;
		try{
			String metaTable=getMetaTable(type);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from "+metaTable+" where "+Resource.SEARCHID+" = ?");
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
	private static void updateField(ResourceType type, int id, String field, Object value)throws Exception{
		DBSession session=null;
		try{
			String metaTable=getMetaTable(type);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("UPDATE "+metaTable+" SET "+field+" = ? where "+Resource.SEARCHID+" = ?");
			ps.setObject(1,value);
			ps.setInt(2, id);
			ps.executeUpdate();			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	
	public static void setTableTitle(ResourceType type,int id, String tableTitle)throws Exception{
		updateField(type, id, Resource.TITLE, tableTitle);
	}
	
	public static Set<Resource> getList(ResourceType type)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			return loadRS((session.executeFilteredQuery(new ArrayList<Field>(), getMetaTable(type), Resource.SEARCHID, "ASC")));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	public static String getJsonList(ResourceType type, String orderBy, String orderDir, int limit, int offset)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			return DBUtils.toJSon(session.executeFilteredQuery(new ArrayList<Field>(), getMetaTable(type), orderBy, orderDir), offset, limit+offset);
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	private static Set<Resource> loadRS(ResultSet rs) throws SQLException{
		HashSet<Resource> toReturn=new HashSet<Resource>();
		List<List<Field>> rows=DBUtils.toFields(rs);
		for(List<Field> row:rows){
			Resource toAdd=new Resource(ResourceType.HCAF, 0);
			for(Field f : row){
				if(f.getName().equals(Resource.AUTHOR))toAdd.setAuthor(f.getValue());
				else if(f.getName().equals(Resource.DATE))toAdd.setDate(f.getValue());
				else if(f.getName().equals(Resource.DESCRIPTION))toAdd.setDescription(f.getValue());
				else if(f.getName().equals(Resource.DISCLAIMER))toAdd.setDisclaimer(f.getValue());
				else if(f.getName().equals(Resource.PARAMETERS))toAdd.setParameters(f.getValue());
				else if(f.getName().equals(Resource.PROVENANCE))toAdd.setProvenance(f.getValue());
				else if(f.getName().equals(Resource.SEARCHID))toAdd.setSearchId(Integer.valueOf(f.getValue()));
				else if(f.getName().equals(Resource.SOURCEID))toAdd.setSourceId(Integer.valueOf(f.getValue()));
				else if(f.getName().equals(Resource.SOURCENAME))toAdd.setSourceName((f.getValue()));
				else if(f.getName().equals(Resource.STATUS))toAdd.setStatus(f.getValue());
				else if(f.getName().equals(Resource.TABLENAME))toAdd.setTableName(f.getValue());
				else if(f.getName().equals(Resource.TITLE))toAdd.setTableName(f.getValue());
			}
			toReturn.add(toAdd);
		}
		return toReturn;
	}
	
}



package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.MetaSourceFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceManager {

	private static final GCUBELog logger=new GCUBELog(SourceManager.class);	
	
	private static String getMetaTable(ResourceType type) throws Exception{
		
		return "meta_sources";
//		switch(type){
//		case HCAF: return "meta_hcaf";
//		case HSPEC: return "meta_hspec";
//		case HSPEN: return "meta_hspen";
//		}
//		throw new Exception("Source type not valid "+type.toString());
	}
	
	public static int getDefaultId(ResourceType type)throws Exception{
		switch(type){
		case HCAF: return ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.DEFAULT_HCAF_ID);
		case HSPEC: return ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.DEFAULT_HSPEC_ID);
		case HSPEN: return ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.DEFAULT_HSPEN_ID);
		}		
		return 0;
		
//		return 1;
	}
	
	public static Resource registerSource(Resource toRegister)throws Exception{
		DBSession session=null;
		logger.trace("registering source "+toRegister.getTableName()+" ("+toRegister.getType()+")");
//		String toSetSourceName=null;
//		try{
//			toRegister.setSgetSourceName(sourceType, toSetSourceId);
//		}catch(Exception e){
//			logger.trace("source not found, skipping..");			
//		}
		try{
			String metaTable=getMetaTable(toRegister.getType());
			session=DBSession.getInternalDBSession();
			List<List<Field>> rows= new ArrayList<List<Field>>();
			ArrayList<Field> row= new ArrayList<Field>();
			row.add(toRegister.getField(MetaSourceFields.algorithm));
			row.add(toRegister.getField(MetaSourceFields.author));
			row.add(toRegister.getField(MetaSourceFields.generationtime));
			row.add(toRegister.getField(MetaSourceFields.description));
			row.add(toRegister.getField(MetaSourceFields.disclaimer));
			row.add(toRegister.getField(MetaSourceFields.parameters));
			row.add(toRegister.getField(MetaSourceFields.provenience));
			row.add(toRegister.getField(MetaSourceFields.sourcehcaf));
			row.add(toRegister.getField(MetaSourceFields.sourcehcaftable));
			row.add(toRegister.getField(MetaSourceFields.sourcehspec));
			row.add(toRegister.getField(MetaSourceFields.sourcehspectable));
			row.add(toRegister.getField(MetaSourceFields.sourcehspen));
			row.add(toRegister.getField(MetaSourceFields.sourcehspentable));
			row.add(toRegister.getField(MetaSourceFields.status));
			row.add(toRegister.getField(MetaSourceFields.tablename));
			row.add(toRegister.getField(MetaSourceFields.title));
			row.add(toRegister.getField(MetaSourceFields.type));
			rows.add(row);
			List<List<Field>> ids = session.insertOperation(metaTable, rows);
			for(Field f: ids.get(0)) 
				if(f.getName().equals(MetaSourceFields.searchid+"")) toRegister.setSearchId(f.getValueAsInteger());
			logger.trace("registered source with id : "+toRegister.getSearchId());
			return toRegister;
		}catch(Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	public static void deleteSource(ResourceType type,int id) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			String metaTable=getMetaTable(type);
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			session.deleteOperation(metaTable, filter);
		}catch(Exception e){
			throw e;			
		}finally{
			session.close();
		}
	}
	
	public static String getSourceName(ResourceType type, int id)throws Exception{
		return (String) getField(type, id, MetaSourceFields.tablename);
	}
	
	public static String getSourceTitle(ResourceType type, int id)throws Exception{
		return (String) getField(type,id,MetaSourceFields.title);
	}
	
	public static int getSourceId(ResourceType type,int id)throws Exception{
		switch(type){
		case HCAF : return (Integer) getField(type, id, MetaSourceFields.sourcehcaf);
		case HSPEC: return (Integer) getField(type, id, MetaSourceFields.sourcehspec);
		case HSPEN : return (Integer) getField(type, id, MetaSourceFields.sourcehspen);
		default : throw new Exception("INVALID TYPE");
		}
		
	}
	
	private static Object getField(ResourceType type, int id, MetaSourceFields field)throws Exception{
		DBSession session=null;
		try{
			String metaTable=getMetaTable(type);
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, metaTable, MetaSourceFields.searchid+"", OrderDirection.ASC);
			if(rs.next())
				return rs.getObject(field+"");
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	private static void updateField(ResourceType type, int id, MetaSourceFields field, FieldType objectType,Object value)throws Exception{
		DBSession session=null;
		try{
			String metaTable=getMetaTable(type);
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",objectType));
			values.add(valueList);
			session.updateOperation(metaTable, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	
	public static void setTableTitle(ResourceType type,int id, String tableTitle)throws Exception{
		updateField(type, id, MetaSourceFields.title, FieldType.STRING,tableTitle);
	}
	
	public static Set<Resource> getList(ResourceType type)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return loadRS((session.executeFilteredQuery(new ArrayList<Field>(), getMetaTable(type), MetaSourceFields.searchid+"", OrderDirection.ASC)));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	public static String getJsonList(ResourceType type, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.type+"",type+"",FieldType.STRING));
			return DBUtils.toJSon(session.executeFilteredQuery(filter, getMetaTable(type), settings.getOrderColumn(), settings.getOrderDirection()), settings.getOffset(), settings.getLimit());
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	private static Set<Resource> loadRS(ResultSet rs) throws Exception{
		HashSet<Resource> toReturn=new HashSet<Resource>();
		while(rs.next()){
			toReturn.add(new Resource(rs));
		}
		return toReturn;
	}
	
	
	
	public static Resource getById(ResourceType type, int id)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			String table = getMetaTable(type);
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			return loadRS(session.executeFilteredQuery(filters, table, MetaSourceFields.searchid+"", OrderDirection.ASC)).iterator().next();
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
}



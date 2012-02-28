package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads.SourceImporter;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ImportResourceRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceManager {

	private static final GCUBELog logger=new GCUBELog(SourceManager.class);	
	
	private static final String sourcesTable="meta_sources";
	
	
	public static int getDefaultId(ResourceType type)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> filter=new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.type+"",type+"",FieldType.STRING));
			filter.add(new Field(MetaSourceFields.defaultsource+"",true+"",FieldType.BOOLEAN));
			Set<Resource> found=loadRS(session.executeFilteredQuery(filter, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC));
			if(found.isEmpty()) throw new Exception("No Default Found for type "+type);
			else return found.iterator().next().getSearchId();
		}catch(Exception e){throw e;}
		finally{if(session!=null)if(session!=null) session.close();}
	}
	
	
	
	public static Resource registerSource(Resource toRegister)throws Exception{
		DBSession session=null;
		logger.trace("registering source "+toRegister);
		try{
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
			row.add(toRegister.getField(MetaSourceFields.sourcehcafids));
			row.add(toRegister.getField(MetaSourceFields.sourcehcaftables));
			row.add(toRegister.getField(MetaSourceFields.sourcehspecids));
			row.add(toRegister.getField(MetaSourceFields.sourcehspectables));
			row.add(toRegister.getField(MetaSourceFields.sourcehspenids));
			row.add(toRegister.getField(MetaSourceFields.sourcehspentables));
			row.add(toRegister.getField(MetaSourceFields.sourceoccurrencecellsids));
			row.add(toRegister.getField(MetaSourceFields.sourceoccurrencecellstables));
			row.add(toRegister.getField(MetaSourceFields.status));
			row.add(toRegister.getField(MetaSourceFields.tablename));
			row.add(toRegister.getField(MetaSourceFields.title));
			row.add(toRegister.getField(MetaSourceFields.type));
			row.add(toRegister.getField(MetaSourceFields.rowcount));
			rows.add(row);
			List<List<Field>> ids = session.insertOperation(sourcesTable, rows);
			for(Field f: ids.get(0)) 
				if(f.getName().equals(MetaSourceFields.searchid+"")) toRegister.setSearchId(f.getValueAsInteger());
			logger.trace("registered source with id : "+toRegister.getSearchId());
			return toRegister;
		}catch(Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	
	public static void deleteSource(int id,boolean deleteTable) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			Resource toDelete=getById(id);
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			session.deleteOperation(sourcesTable, filter);
			
			if(deleteTable) session.dropTable(toDelete.getTableName());
		}catch(Exception e){
			throw e;			
		}finally{
			if(session!=null) session.close();
		}
	}
	
	public static String getSourceName(int id)throws Exception{
		return (String) getField(id, MetaSourceFields.tablename);
	}
	
	public static String getSourceTitle(int id)throws Exception{
		return (String) getField(id,MetaSourceFields.title);
	}
	
	public static int getSourceId(ResourceType type,int id)throws Exception{
		switch(type){
		case HCAF : return (Integer) getField(id, MetaSourceFields.sourcehcafids);
		case HSPEC: return (Integer) getField(id, MetaSourceFields.sourcehspecids);
		case HSPEN : return (Integer) getField(id, MetaSourceFields.sourcehspenids);
		default : throw new Exception("INVALID TYPE");
		}
		
	}
	
	private static Object getField(int id, MetaSourceFields field)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC);
			if(rs.next())
				return rs.getObject(field+"");
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	private static void updateField(int id, MetaSourceFields field, FieldType objectType,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",objectType));
			values.add(valueList);
			session.updateOperation(sourcesTable, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	
	
	public static void setTableTitle(int id, String tableTitle)throws Exception{
		updateField( id, MetaSourceFields.title, FieldType.STRING,tableTitle);
	}
	public static void setCountRow(int id, Long count)throws Exception{
		updateField( id, MetaSourceFields.rowcount, FieldType.INTEGER,count);
	}
	public static Set<Resource> getList(List<Field> filter)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return loadRS((session.executeFilteredQuery(filter, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC)));
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	public static String getJsonList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filter, sourcesTable, settings.getOrderColumn(), settings.getOrderDirection()), settings.getOffset(), settings.getLimit());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	private static Set<Resource> loadRS(ResultSet rs) throws Exception{
		HashSet<Resource> toReturn=new HashSet<Resource>();
		while(rs.next()){
			toReturn.add(new Resource(rs));
		}
		return toReturn;
	}
	
	
	
	public static Resource getById(int id)throws Exception{
		if(id==0) return null;
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(MetaSourceFields.searchid+"",id+"",FieldType.INTEGER));
			return loadRS(session.executeFilteredQuery(filters, sourcesTable, MetaSourceFields.searchid+"", OrderDirection.ASC)).iterator().next();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	public static int update(Resource toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();
			if(toUpdate.getDefaultSource()){
				List<List<Field>> values=new ArrayList<List<Field>>();
				List<Field> toSet=new ArrayList<Field>();
				toSet.add(new Field(MetaSourceFields.defaultsource+"",false+"",FieldType.BOOLEAN));
				values.add(toSet);
				List<List<Field>> keys=new ArrayList<List<Field>>();
				List<Field> key=new ArrayList<Field>();
				key.add(toUpdate.getField(MetaSourceFields.type));
				keys.add(key);
				session.updateOperation(sourcesTable, keys, values);
			}
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> value=new ArrayList<Field>();
			value.add(toUpdate.getField(MetaSourceFields.title));
			value.add(toUpdate.getField(MetaSourceFields.description));
			value.add(toUpdate.getField(MetaSourceFields.disclaimer));
			value.add(toUpdate.getField(MetaSourceFields.defaultsource));
			value.add(toUpdate.getField(MetaSourceFields.status));
			value.add(toUpdate.getField(MetaSourceFields.rowcount));
			value.add(toUpdate.getField(MetaSourceFields.provenience));
			values.add(value);
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key=new ArrayList<Field>();
			key.add(toUpdate.getField(MetaSourceFields.searchid));
			keys.add(key);
			int rows=session.updateOperation(sourcesTable, keys, values);
			session.commit();
			return rows;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static Integer importFromCSVFile(final String csvFile,ImportResourceRequestType request)throws Exception{
		DBSession session=null;
		try{
			ResourceType type=ResourceType.valueOf(request.getResourceType());
			session=DBSession.getInternalDBSession();
			final String tableName=ServiceUtils.generateId(type+"", "").toLowerCase();
			logger.debug("Importing "+csvFile+" to TABLE "+tableName+" [ "+type+" ]");
			session.createLikeTable(tableName, getById(getDefaultId(type)).getTableName());
			
			Resource toRegister=new Resource(type, 0);
			toRegister.setAuthor(request.getUser());
			toRegister.setDefaultSource(false);
			toRegister.setGenerationTime(System.currentTimeMillis());
			toRegister.setDescription("Imported csv file ");
			toRegister.setTableName(tableName);
			toRegister.setTitle("Import_"+request.getUser());
			toRegister.setStatus(ResourceStatus.Importing);
			toRegister.setRowCount(0l);
			toRegister=registerSource(toRegister);
			SourceImporter t=new SourceImporter(csvFile, toRegister,getDefaultId(type),request.getDelimiter().charAt(0),request.getFieldsMask(),request.isHasHeader(),request.getEncoding());
			t.start();
			return toRegister.getSearchId();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static void checkTables()throws Exception{
		DBSession session =null;
		Set<Resource> list=getList(new ArrayList<Field>());
		try{
			session=DBSession.getInternalDBSession();
		for(Resource r:list){
			try{
				//check table Existance
				logger.trace("Checking "+r);
				boolean existing=true;
				
				try{
					session.executeQuery("SELECT * FROM "+r.getTableName()+" LIMIT 1 OFFSET 0");
				}catch(Exception e){
					logger.trace("Unable to detect table "+r.getTableName()+", going to delete resource");
					deleteSource(r.getSearchId(), false);
					existing=false;
				}
				
				if(existing){
					if(r.getRowCount()==0){
						logger.trace("Updateing row count");
						r.setRowCount(session.getCount(r.getTableName(), new ArrayList<Field>()));
					}
					for(Integer id:r.getSourceHCAFIds()){
						Resource HCAF=getById(id);
						if(HCAF!=null) r.addSource(HCAF);
						else logger.trace("Unable to find source , id was "+id);
					}
					for(Integer id:r.getSourceHSPENIds()){
						Resource HCAF=getById(id);
						if(HCAF!=null) r.addSource(HCAF);
						else logger.trace("Unable to find source , id was "+id);
					}
					for(Integer id:r.getSourceHSPECIds()){
						Resource HCAF=getById(id);
						if(HCAF!=null) r.addSource(HCAF);
						else logger.trace("Unable to find source , id was "+id);
					}
					for(Integer id:r.getSourceOccurrenceCellsIds()){
						Resource HCAF=getById(id);
						if(HCAF!=null) r.addSource(HCAF);
						else logger.trace("Unable to find source , id was "+id);
					}					
					update(r);
				}
			}catch (Exception e){
				logger.warn("Unable to check resource "+r.getSearchId());
			}
		}
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
	
	public static final String getToUseTableStore()throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();			
			ResultSet rs=session.executeFilteredQuery(new ArrayList<Field>(), sourcesTable, MetaSourceFields.searchid+"", OrderDirection.DESC);
			int lastId=0;
			if(rs.next()){
				lastId=rs.getInt(MetaSourceFields.searchid+"");
				rs.close();
			}
			int numTableSpaces=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.INTERNAL_DB_NUM_TABLESTORE);
			int toUseTableSpace=((lastId+1) % numTableSpaces)+1;
			String toReturn=ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_TABLESTORE_PATTERN)+toUseTableSpace;
			logger.debug("TableSpace to use : "+toReturn);
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
}



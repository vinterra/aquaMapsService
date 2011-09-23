package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceStatus;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.MetaSourceFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceManager {

	private static final GCUBELog logger=new GCUBELog(SourceManager.class);	
	
	private static final String sourcesTable="meta_sources";
	
	
	
//	public static int getDefaultId(ResourceType type)throws Exception{
//		switch(type){
//		case HCAF: return ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.DEFAULT_HCAF_ID);
//		case HSPEC: return ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.DEFAULT_HSPEC_ID);
//		case HSPEN: return ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.DEFAULT_HSPEN_ID);
//		}		
//		return 0;
//		
////		return 1;
//	}
	
	
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
//		String toSetSourceName=null;
//		try{
//			toRegister.setSgetSourceName(sourceType, toSetSourceId);
//		}catch(Exception e){
//			logger.trace("source not found, skipping..");			
//		}
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
			row.add(toRegister.getField(MetaSourceFields.sourcehcaf));
			row.add(toRegister.getField(MetaSourceFields.sourcehcaftable));
			row.add(toRegister.getField(MetaSourceFields.sourcehspec));
			row.add(toRegister.getField(MetaSourceFields.sourcehspectable));
			row.add(toRegister.getField(MetaSourceFields.sourcehspen));
			row.add(toRegister.getField(MetaSourceFields.sourcehspentable));
			row.add(toRegister.getField(MetaSourceFields.sourceoccurrencecells));
			row.add(toRegister.getField(MetaSourceFields.sourceoccurrencecellstable));
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
		case HCAF : return (Integer) getField(id, MetaSourceFields.sourcehcaf);
		case HSPEC: return (Integer) getField(id, MetaSourceFields.sourcehspec);
		case HSPEN : return (Integer) getField(id, MetaSourceFields.sourcehspen);
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
	
	public static Integer importFromCSVFile(final File csvFile,final String author,final ResourceType type)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			final String tableName=ServiceUtils.generateId(type+"", "").toLowerCase();
			logger.debug("Importing "+csvFile.getAbsolutePath()+" to TABLE "+tableName+" [ "+type+" ]");
			session.createLikeTable(tableName, getById(getDefaultId(type)).getTableName());
			
			Resource toRegister=new Resource(type, 0);
			toRegister.setAuthor(author);
			toRegister.setDefaultSource(false);
			toRegister.setGenerationTime(System.currentTimeMillis());
			toRegister.setDescription("Imported csv file ");
			toRegister.setTableName(tableName);
			toRegister.setTitle("Import_"+author);
			toRegister.setStatus(ResourceStatus.Importing);
			toRegister.setRowCount(0l);
			toRegister=registerSource(toRegister);
			SourceImporter t=new SourceImporter(csvFile, toRegister.getSearchId());
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
					logger.trace("Updateing row count");
					r.setRowCount(session.getCount(r.getTableName(), new ArrayList<Field>()));					
				}catch(Exception e){
					logger.warn("Unable to detect row count, going to delete resource");
					deleteSource(r.getSearchId(), false);
					existing=false;
				}
				if(existing){
					if(r.getSourceHCAFId()!=0){
						Resource HCAF=getById(r.getSourceHCAFId());
						if(HCAF!=null) r.setSourceHCAFTable(HCAF.getTableName());
						else{
							logger.trace("Unable to find source HCAF, id was "+r.getSourceHCAFId());
						}
					}else r.setSourceHCAFTable("");
					if(r.getSourceHSPENId()!=0){
						Resource HSPEN=getById(r.getSourceHSPENId());
						if(HSPEN!=null) r.setSourceHSPENTable(HSPEN.getTableName());
						else{
							logger.trace("Unable to find source HSPEN, id was "+r.getSourceHSPENId());
						}
					}else r.setSourceHSPENTable("");
					if(r.getSourceHSPECId()!=0){
						Resource HSPEC=getById(r.getSourceHSPECId());
						if(HSPEC!=null) r.setSourceHSPECTable(HSPEC.getTableName());
						else{
							logger.trace("Unable to find source HSPEC, id was "+r.getSourceHSPECId());
						}
					}else r.setSourceHSPECTable("");
					if(r.getSourceOccurrenceCellsId()!=0){
						Resource OCCURRENCE=getById(r.getSourceOccurrenceCellsId());
						if(OCCURRENCE!=null) r.setSourceOccurrenceCellsTable(OCCURRENCE.getTableName());
						else{
							logger.trace("Unable to find source OCCURRENCE CELLS, id was "+r.getSourceOccurrenceCellsId());
						}
					}else r.setSourceOccurrenceCellsTable("");
					update(r);
				}
			}catch (Exception e){
				logger.warn("Unable to check resource "+r.getSearchId());
			}
		}
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
	}
}



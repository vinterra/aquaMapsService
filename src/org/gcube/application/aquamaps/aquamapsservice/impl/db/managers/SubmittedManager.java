package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;
import org.gcube.common.gis.dataModel.types.LayersType;

public class SubmittedManager {

	protected static GCUBELog logger= new GCUBELog(SubmittedManager.class);

	protected static final String submittedTable="submitted";

	protected static Object getField(int id, SubmittedFields field)throws Exception{
		DBSession session=null;
		try{			
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",id+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, submittedTable, SubmittedFields.searchid+"", OrderDirection.ASC);
			if(rs.next())
				return rs.getObject(field.toString());
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	protected static int updateField(int id,SubmittedFields field,FieldType type,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",id+"",FieldType.INTEGER));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",type));
			values.add(valueList);
			return session.updateOperation(submittedTable, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}


	protected static int deleteFromTables(int submittedId)throws Exception{


		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			return session.deleteOperation(submittedTable, filter);			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}


	public static int delete(int submittedId)throws Exception{
		if(isAquaMap(submittedId)) return AquaMapsManager.deleteObject(submittedId);
		else return JobManager.deleteJob(submittedId);
	}


	// **************************************** getters *********************************

	public static int getHCAFTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourcehcaf);		
	}
	public static int getHSPENTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourcehspen);		
	}
	public static int getHSPECTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourcehspec);		
	}
	public static Boolean isGIS(int submittedId) throws Exception{
		return ((Integer) getField(submittedId,SubmittedFields.gisenabled)==1);
	}
	public static List<String> getGisReference(int submittedId)throws Exception{
		return CSVUtils.CSVToList((String) getField(submittedId,SubmittedFields.geoserverreference));
	}
	public static List<String> getGisId(int submittedId)throws Exception{
		return CSVUtils.CSVToList((String) getField(submittedId,SubmittedFields.gispublishedid));
	}

	public static SubmittedStatus getStatus(int submittedId)throws Exception{
		return SubmittedStatus.valueOf((String) getField(submittedId,SubmittedFields.status));
	}
	public static String getAuthor(int submittedId)throws Exception{
		return (String) getField(submittedId,SubmittedFields.author);
	}

	public static Boolean isAquaMap(int submittedId)throws Exception{
		return ((Integer) getField(submittedId,SubmittedFields.isaquamap)==1);
	}

	public static Boolean isPostponePublishing(int submittedId)throws Exception{
		return ((Integer) getField(submittedId,SubmittedFields.postponepublishing)==1);
	} 

	
	
	
	//**************************************** setters **********************************
	//
	//	public static int setHCAFTable(int HCAFId,int jobId)throws Exception{
	//		return updateField(jobId,SubmittedFields.sourceHCAF,HCAFId);
	//	}
	//	public static int setHSPENTable(int HCAFId,int jobId)throws Exception{
	//		return updateField(jobId,SubmittedFields.sourceHSPEN,HCAFId);
	//	}
	//	public static int setHSPECTable(int HCAFId,int jobId)throws Exception{
	//		return updateField(jobId,SubmittedFields.sourceHCAF,HCAFId);
	//	}

	public static int updateGISData(int submittedId,Boolean gisEnabled)throws Exception{
		return updateField(submittedId,SubmittedFields.gisenabled,FieldType.BOOLEAN,gisEnabled+"");
	}

	public static int markSaved(int submittedId)throws Exception{
		return updateField(submittedId,SubmittedFields.saved,FieldType.BOOLEAN,true);
	}

	public static int setGisPublishedId(int submittedId,List<String> gisId)throws Exception{
		String value=CSVUtils.listToCSV(gisId);
		logger.debug("Setting gis publishd id : "+value+" for Submitted id : "+submittedId);
		return updateField(submittedId,SubmittedFields.gispublishedid,FieldType.STRING,value);
	}
	public static int setGisReference(int submittedId,List<String> gisreference)throws Exception{
		String value=CSVUtils.listToCSV(gisreference);
		logger.debug("Setting geo server references id : "+value+" for Submitted id : "+submittedId);
		return updateField(submittedId,SubmittedFields.geoserverreference,FieldType.STRING,value);
	}

	
	public static int setSerializedPath(int submittedId,String path)throws Exception{
		return updateField(submittedId,SubmittedFields.serializedpath,FieldType.STRING,path);
	}

	//******** Logic

	/**
	 * Updates internal Status, in case of Error status updates published element as side effect
	 */

	public static void updateStatus(int toUpdateId,SubmittedStatus statusValue)throws SQLException, IOException, Exception{
		updateField(toUpdateId,SubmittedFields.status,FieldType.STRING,statusValue.toString());
		Boolean postponePublishing=isPostponePublishing(toUpdateId);
		if(statusValue.equals(SubmittedStatus.Error)||statusValue.equals(SubmittedStatus.Completed)){
			updateField(toUpdateId,SubmittedFields.endtime,FieldType.INTEGER,System.currentTimeMillis()+"");
			Publisher pub=ServiceContext.getContext().getPublisher();

			if(isAquaMap(toUpdateId)){
				if(!postponePublishing){				
					logger.trace("Object status was "+statusValue+", updateing Publisher..");
					AquaMapsObject obj = pub.getAquaMapsObjectById(toUpdateId);
					obj.setStatus(statusValue);
					pub.publishAquaMapsObject(obj);
				}
			}else{
				Job job=null;
				if(!postponePublishing){
					job=pub.getJobById(toUpdateId);
					job.setStatus(statusValue);
					pub.publishJob(job);
				}else{
					HashMap<Integer,Map<String,String>> toPublishMaps=new HashMap<Integer, Map<String,String>>();
					logger.trace("Job was skip Publisher enabled, loading generated data links..");
					job=(Job) AquaMapsXStream.deSerialize((String)getField(toUpdateId, SubmittedFields.serializedpath));
					for(Submitted submittedObj:JobManager.getObjects(toUpdateId)){
						for(AquaMapsObject obj:job.getAquaMapsObjectList())
							if(obj.getId()==submittedObj.getSearchId()){	
								try{
									obj.setStatus(submittedObj.getStatus());
									if(submittedObj.getGisEnabled())
										for(String layerId:submittedObj.getGisPublishedId()){
											LayerInfo layer=pub.getLayerByIdAndType(layerId,(
													submittedObj.getType().equals(ObjectType.Biodiversity)?LayersType.Biodiversity:
														LayersType.NativeRange));
											if(layer!=null)
												obj.getLayers().add(layer);
											else logger.warn("Unable to load published layer ID "+layerId);
										}
									toPublishMaps.put(obj.getId(), (Map<String, String>) AquaMapsXStream.deSerialize(submittedObj.getSerializedPath()));
								}catch(Exception e){
									logger.error("Unable to load layers and images for obj "+submittedObj.getSearchId(),e);
								}
							}
					}
					
					job.setStatus(statusValue);
					logger.debug("Publishing job...");
					pub.publishJob(job);
					logger.debug("Publishing images for "+toPublishMaps.size()+" objects..");
					for(Entry<Integer,Map<String,String>> entry:toPublishMaps.entrySet())
						pub.publishImages(entry.getKey(), entry.getValue());
					logger.trace("Update Complete");
				}
				
							
			}
		}
		logger.trace("done submitted[ID : "+toUpdateId+"] status updateing status : "+statusValue.toString());
	}


	public static Submitted insertInTable(Submitted toInsert)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			for(Field f: toInsert.toRow())
				if(!f.getName().equals(SubmittedFields.searchid+"")) row.add(f);
			rows.add(row);
			List<List<Field>> inserted=session.insertOperation(submittedTable,rows);
			return new Submitted(inserted.get(0));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}





	public static List<Submitted> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return Submitted.loadResultSet(session.executeFilteredQuery(filters, submittedTable,null,null));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

	public static String getJsonList(List<Field> filters,PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, submittedTable,settings.getOrderColumn(),settings.getOrderDirection()),settings.getOffset(), settings.getLimit());
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

	public static Submitted getSubmittedById(int objId) throws Exception{
		List<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SubmittedFields.searchid+"",objId+"",FieldType.INTEGER));
		List<Submitted> found= getList(filter,new PagedRequestSettings(1, 0, SubmittedFields.searchid+"", OrderDirection.ASC));
		return found.get(0);
	}


	public static void update(Submitted toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> id=new ArrayList<Field>();
			id.add(toUpdate.getField(SubmittedFields.searchid));
			ArrayList<Field> values=new ArrayList<Field>();
			for(Field f: toUpdate.toRow())
				if(!f.getName().equals(SubmittedFields.searchid+"")) values.add(f);
			PreparedStatement psUpdate=session.getPreparedStatementForUpdate(values, id, submittedTable);
			psUpdate=session.fillParameters(values, 0, psUpdate);
			psUpdate=session.fillParameters(id, values.size(), psUpdate);
			psUpdate.executeUpdate();
		}catch(Exception e){throw e;}
		finally{session.close();}
	}


	public static List<Submitted> getList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Submitted> toReturn=new ArrayList<Submitted>();
			ResultSet rs=session.executeFilteredQuery(filter, submittedTable, settings.getOrderColumn(), settings.getOrderDirection());
			int rowIndex=0;
			while(rs.next()&&toReturn.size()<settings.getPageSize()){
				if(rowIndex>=settings.getOffset()) toReturn.add(new Submitted(rs));
				rowIndex++;				
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

	public static int getCount(List<Field> filter)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return session.getCount(submittedTable, filter);
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

	public static void setStartTime(int submittedId)throws Exception{
		updateField(submittedId,SubmittedFields.starttime,FieldType.INTEGER,System.currentTimeMillis());
	}
}

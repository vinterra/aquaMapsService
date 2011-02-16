package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Submitted;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SubmittedManager {

	protected static GCUBELog logger= new GCUBELog(SubmittedManager.class);

	protected static final String submittedTable="submitted";

	protected static Object getField(int id, SubmittedFields field)throws Exception{
		DBSession session=null;
		try{			
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",id+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, submittedTable, SubmittedFields.searchid+"", "ASC");
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
	public static String getGIS(int submittedId) throws Exception{
		return (String) getField(submittedId,SubmittedFields.gis);
	}
	public static SubmittedStatus getStatus(int submittedId)throws Exception{
		return SubmittedStatus.valueOf((String) getField(submittedId,SubmittedFields.status));
	}
	public static String getAuthor(int submittedId)throws Exception{
		return (String) getField(submittedId,SubmittedFields.author);
	}

	public static Boolean isAquaMap(int submittedId)throws Exception{
		return (Boolean) getField(submittedId,SubmittedFields.isaquamap);
	}

	public static int getMapId(int submittedId)throws Exception{
		return (Integer) getField(submittedId,SubmittedFields.mapid);
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

	public static int updateGISData(int submittedId,String GeoId)throws Exception{
		return updateField(submittedId,SubmittedFields.gis,FieldType.STRING,GeoId);
	}

	public static int markSaved(int submittedId)throws Exception{
		return updateField(submittedId,SubmittedFields.saved,FieldType.BOOLEAN,true);
	}

	//******** Logic

	/**
	 * Updates internal Status, in case of Error status updates published element as side effect
	 */
	
	public static void updateStatus(int jobId,SubmittedStatus statusValue)throws SQLException, IOException, Exception{
		updateField(jobId,SubmittedFields.status,FieldType.STRING,statusValue.toString());
		if(statusValue.equals(SubmittedStatus.Error)){
			Publisher pub=PublisherImpl.getPublisher();
			if(isAquaMap(jobId)){
				AquaMapsObject obj = pub.getAquaMapsObjectById(jobId);
				obj.setStatus(statusValue);
				pub.publishAquaMapsObject(obj);
			}else{
				Job job=pub.getJobById(jobId);
				job.setStatus(statusValue);
				pub.publishJob(job);
			}
		}
		logger.trace("done Job status updateing status : "+statusValue.toString());
	}






	public static List<Submitted> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return Submitted.loadResultSet(session.executeFilteredQuery(filters, submittedTable,null,null));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

	public static String getJsonList(List<Field> filters,String orderBy, String orderDir, int limit, int offset)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, submittedTable,null,null),offset, offset+limit);
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

}

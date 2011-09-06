package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.SourceGenerationPhase;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceGenerationRequestsManager {

	private static final GCUBELog logger=new GCUBELog(SourceGenerationRequestsManager.class);

	public final static String requestsTable="hspec_group_requests";

	public String getJSONRequests(List<Field> filters, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, requestsTable, settings.getOrderColumn(), settings.getOrderDirection()),settings.getOffset(),settings.getOffset()+settings.getLimit());
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}


	public static String insertRequest(SourceGenerationRequest toInsert)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			toInsert.setId(ServiceUtils.generateId("HGGR", ""));
			toInsert.setPhase(SourceGenerationPhase.pending);
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> toInsertRow=new ArrayList<Field>();
			logger.debug("Inserting request, fields are :");
			for(Field f:toInsert.toRow())
				if(!f.getValue().equalsIgnoreCase("null")){
					toInsertRow.add(f);
					logger.debug(f.toXML());
				}
			rows.add(toInsertRow);
			session.insertOperation(requestsTable, rows);
			return toInsert.getId();
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}

	private static void updateField(String id, List<Field> values)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
			keys.add(key);
			List<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(values);
			session.updateOperation(requestsTable, keys, rows);
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}

//	public static SourceGenerationRequest getFirst()throws Exception{
//		ArrayList<Field> filter= new ArrayList<Field>();
//		filter.add(new Field(SourceGenerationRequestFields.phase+"",SourceGenerationPhase.pending+"",FieldType.STRING));
//		ArrayList<SourceGenerationRequest> requests=getList(filter);
//		return (requests.size()>0)?requests.get(0):null;
//	}


	public static ArrayList<SourceGenerationRequest> getList(ArrayList<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return SourceGenerationRequest.loadResultSet(
					session.executeFilteredQuery(filter, requestsTable, SourceGenerationRequestFields.submissiontime+"", OrderDirection.ASC));
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}

	
	public static List<SourceGenerationRequest> getList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<SourceGenerationRequest> toReturn=new ArrayList<SourceGenerationRequest>();
			ResultSet rs=session.executeFilteredQuery(filter,requestsTable, settings.getOrderColumn(), settings.getOrderDirection());
			int rowIndex=0;
			while(rs.next()&&toReturn.size()<settings.getPageSize()){
				if(rowIndex>=settings.getOffset()) toReturn.add(new SourceGenerationRequest(rs));
				rowIndex++;				
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	
	
	

	private static Field getField(String id, String field)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
			ResultSet rs= session.executeFilteredQuery(key, requestsTable, field, OrderDirection.ASC);
			if(rs.next())
				for(Field f:Field.loadRow(rs)){
					if(f.getName().equals(field)) return f;
				}
			throw new Exception("Field not found "+field);
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}

	public static void setPhase(SourceGenerationPhase phase, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.phase+"",phase+"",FieldType.STRING));
		if(phase.equals(SourceGenerationPhase.completed)){
			fields.add(new Field(SourceGenerationRequestFields.endtime+"",System.currentTimeMillis()+"",FieldType.INTEGER));
			fields.add(new Field(SourceGenerationRequestFields.currentphasepercent+"",100+"",FieldType.DOUBLE));
		}
		updateField(id, fields);
	}
	public static void setReportId(int reportId, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.reportid+"",reportId+"",FieldType.INTEGER));
		updateField(id, fields);
	}
	public static void setPhasePercent(double percent,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.currentphasepercent+"",percent+"",FieldType.DOUBLE));
		updateField(id, fields);
	}
	public static void addGeneratedResource(int hspecId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToList(getField(id, SourceGenerationRequestFields.generatedsourcesid+"").getValue());
		current.add(hspecId+"");
		fields.add(new Field(SourceGenerationRequestFields.generatedsourcesid+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}
	public static void addJobIds(int jobId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToList(getField(id, SourceGenerationRequestFields.jobids+"").getValue());
		current.add(jobId+"");
		fields.add(new Field(SourceGenerationRequestFields.jobids+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}

	public static void setStartTime(String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.starttime+"",System.currentTimeMillis()+"",FieldType.INTEGER));
		updateField(id,fields);
	}

	public static String getJSONList(List<Field> filters, PagedRequestSettings settings) throws Exception{
		if(filters==null) filters=new ArrayList<Field>();
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, requestsTable, settings.getOrderColumn(), settings.getOrderDirection()),
					settings.getOffset(),settings.getOffset()+settings.getOffset()+settings.getLimit());
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static int delete(ArrayList<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			PreparedStatement ps= session.getPreparedStatementForDelete(filter, requestsTable);
			return session.fillParameters(filter,0, ps).executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}

	
	public static int delete (String id)throws Exception{
		ArrayList<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
		return delete(filter);
	}
	
	public static int getCount(List<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return session.getCount(requestsTable, filter);
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}
	
}
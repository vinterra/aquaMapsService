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
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.environments.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.GroupGenerationRequestFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.common.core.utils.logging.GCUBELog;

public class HSPECGroupGenerationRequestsManager {

	private static final GCUBELog logger=new GCUBELog(HSPECGroupGenerationRequestsManager.class);

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


	public static String insertRequest(HSPECGroupGenerationRequest toInsert)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			toInsert.setId(ServiceUtils.generateId("HGGR", ""));
			toInsert.setPhase(HSPECGroupGenerationPhase.pending);
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
			key.add(new Field(GroupGenerationRequestFields.id+"",id,FieldType.STRING));
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

//	public static HSPECGroupGenerationRequest getFirst()throws Exception{
//		ArrayList<Field> filter= new ArrayList<Field>();
//		filter.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.pending+"",FieldType.STRING));
//		ArrayList<HSPECGroupGenerationRequest> requests=getList(filter);
//		return (requests.size()>0)?requests.get(0):null;
//	}


	public static ArrayList<HSPECGroupGenerationRequest> getList(ArrayList<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return HSPECGroupGenerationRequest.loadResultSet(
					session.executeFilteredQuery(filter, requestsTable, GroupGenerationRequestFields.submissiontime+"", OrderDirection.ASC));
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}

	
	public static List<HSPECGroupGenerationRequest> getList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<HSPECGroupGenerationRequest> toReturn=new ArrayList<HSPECGroupGenerationRequest>();
			ResultSet rs=session.executeFilteredQuery(filter,requestsTable, settings.getOrderColumn(), settings.getOrderDirection());
			int rowIndex=0;
			while(rs.next()&&toReturn.size()<settings.getPageSize()){
				if(rowIndex>=settings.getOffset()) toReturn.add(new HSPECGroupGenerationRequest(rs));
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
			key.add(new Field(GroupGenerationRequestFields.id+"",id,FieldType.STRING));
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

	public static void setPhase(HSPECGroupGenerationPhase phase, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(GroupGenerationRequestFields.phase+"",phase+"",FieldType.STRING));
		if(phase.equals(HSPECGroupGenerationPhase.completed)){
			fields.add(new Field(GroupGenerationRequestFields.endtime+"",System.currentTimeMillis()+"",FieldType.INTEGER));
			fields.add(new Field(GroupGenerationRequestFields.currentphasepercent+"",100+"",FieldType.DOUBLE));
		}
		updateField(id, fields);
	}
	public static void setReportId(int reportId, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(GroupGenerationRequestFields.reportid+"",reportId+"",FieldType.INTEGER));
		updateField(id, fields);
	}
	public static void setPhasePercent(double percent,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(GroupGenerationRequestFields.currentphasepercent+"",percent+"",FieldType.DOUBLE));
		updateField(id, fields);
	}
	public static void addGeneratedHSPEC(int hspecId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToList(getField(id, GroupGenerationRequestFields.generatedhspecids+"").getValue());
		current.add(hspecId+"");
		fields.add(new Field(GroupGenerationRequestFields.generatedhspecids+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}
	public static void addJobIds(int jobId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToList(getField(id, GroupGenerationRequestFields.jobids+"").getValue());
		current.add(jobId+"");
		fields.add(new Field(GroupGenerationRequestFields.jobids+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}

	public static void setStartTime(String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(GroupGenerationRequestFields.starttime+"",System.currentTimeMillis()+"",FieldType.INTEGER));
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

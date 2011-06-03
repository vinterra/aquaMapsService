package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.GroupGenerationRequestFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;

public class HSPECGroupGenerationRequestsManager {

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
	
	
	public static String insertRequest(String author, String generationName, String algorithms, 
			boolean enableLayer,boolean enableImage, String description, Resource hcaf, Resource hspen,boolean isCloud)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> toInsertRow=new ArrayList<Field>();
			String id=ServiceUtils.generateId("HGGR", "");
			toInsertRow.add(new Field(GroupGenerationRequestFields.author+"",author,FieldType.STRING));
			toInsertRow.add(new Field(GroupGenerationRequestFields.generationname+"",generationName,FieldType.STRING));
			toInsertRow.add(new Field(GroupGenerationRequestFields.id+"",id,FieldType.STRING));
			toInsertRow.add(new Field(GroupGenerationRequestFields.submissiontime+"",System.currentTimeMillis()+"",FieldType.INTEGER));
			toInsertRow.add(new Field(GroupGenerationRequestFields.algorithms+"",algorithms,FieldType.STRING));
			toInsertRow.add(new Field(GroupGenerationRequestFields.enableimagegeneration+"",enableImage+"",FieldType.BOOLEAN));
			toInsertRow.add(new Field(GroupGenerationRequestFields.enablelayergeneration+"",enableLayer+"",FieldType.BOOLEAN));
			toInsertRow.add(new Field(GroupGenerationRequestFields.description+"",description,FieldType.STRING));
			toInsertRow.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.Pending+"",FieldType.STRING));
			toInsertRow.add(new Field(GroupGenerationRequestFields.hcafsearchid+"",hcaf.getSearchId()+"",FieldType.INTEGER));
			toInsertRow.add(new Field(GroupGenerationRequestFields.hspensearchid+"",hspen.getSearchId()+"",FieldType.INTEGER));
			toInsertRow.add(new Field(GroupGenerationRequestFields.iscloud+"",isCloud+"",FieldType.BOOLEAN));
			List<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(toInsertRow);
			session.insertOperation(requestsTable, rows);
			return id;
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
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(GroupGenerationRequestFields.id+"",id,FieldType.STRING));
			session.getPreparedStatementForUpdate(key, values, requestsTable).executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}
	
	public static HSPECGroupGenerationRequest getFirst()throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> filter= new ArrayList<Field>();
			filter.add(new Field(GroupGenerationRequestFields.phase+"",HSPECGroupGenerationPhase.Pending+"",FieldType.STRING));
			ArrayList<HSPECGroupGenerationRequest> requests=HSPECGroupGenerationRequest.loadResultSet(
					session.executeFilteredQuery(filter, requestsTable, GroupGenerationRequestFields.submissiontime+"", "ASC"));
			return (requests.size()>0)?requests.get(0):null;
		}catch(Exception e){
			throw e;
		}finally{
			session.close();
		}
	}
	
	
	private static Field getField(String id, String field)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(GroupGenerationRequestFields.id+"",id,FieldType.STRING));
			List<Field> row=Field.loadRow(session.executeFilteredQuery(key, requestsTable, field, "ASC"));
			for(Field f:row){
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
		updateField(id, fields);
	}
	public static void setReportId(int reportId, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(GroupGenerationRequestFields.reportid+"",reportId+"",FieldType.STRING));
		updateField(id, fields);
	}
	public static void setPhasePercent(double percent,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(GroupGenerationRequestFields.currentphasepercent+"",percent+"",FieldType.STRING));
		updateField(id, fields);
	}
	public static void addGeneratedHSPEC(int hspecId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToList(getField(id, GroupGenerationRequestFields.generatedhspecids+"").getValue());
		current.add(hspecId+"");
		fields.add(new Field(GroupGenerationRequestFields.generatedhspecids+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}
}

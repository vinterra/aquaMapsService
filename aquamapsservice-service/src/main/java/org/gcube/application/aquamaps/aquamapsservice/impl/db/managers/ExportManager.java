package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportTableStatusType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils.RSWrapper;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;

public class ExportManager extends Thread{

	private static final GCUBELog logger=new GCUBELog(CustomQueryManager.class);
	
	private static final String EXPORT_REFERENCE_TABLE="exports";
	private static final String EXPORT_ID="id";
	private static final String EXPORT_TABLE="toexporttable";
	private static final String EXPORT_SETTINGS="settings";
	private static final String EXPORT_STATUS="status";
	private static final String EXPORT_ERROR_MSG="errors";
	private static final String EXPORT_LOCAL_PATH="localpath";
	private static final String EXPORT_TIME="time";
	private static final String EXPORT_LOCATOR="locator";
	private static final String EXPORT_SCOPE="scope";
	
	public static String submitExportOperation(String tableName, ExportCSVSettings settings) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			String referenceId=ServiceUtils.generateId("EXPORT", "").toLowerCase();
			ArrayList<Field> row= new ArrayList<Field>();
			
			row.add(new Field(EXPORT_ID,referenceId,FieldType.STRING));
			row.add(new Field(EXPORT_TABLE,tableName,FieldType.STRING));
			row.add(new Field(EXPORT_SETTINGS,AquaMapsXStream.getXMLInstance().toXML(settings),FieldType.STRING));
			row.add(new Field(EXPORT_STATUS,ExportStatus._PENDING,FieldType.STRING));
			row.add(new Field(EXPORT_SCOPE,ServiceContext.getContext().getScope()+"",FieldType.STRING));
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(row);
			session.insertOperation(EXPORT_REFERENCE_TABLE, rows);
			ExportManager thread=new ExportManager(referenceId);
			thread.start();
			return referenceId;
		}finally{if(session!=null) session.close();}
	}

	public static ExportTableStatusType getStatus(String requestId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(EXPORT_ID,requestId,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, EXPORT_REFERENCE_TABLE, EXPORT_ID, OrderDirection.ASC);
			if(rs.next()){
				ExportTableStatusType status=new ExportTableStatusType();
				status.setCsvSettings((ExportCSVSettings) AquaMapsXStream.getXMLInstance().fromXML(rs.getString(EXPORT_SETTINGS)));				
				status.setStatus(ExportStatus.fromValue(rs.getString(EXPORT_STATUS)));
				status.setRsLocator(rs.getString(EXPORT_LOCATOR));
				status.setTableName(rs.getString(EXPORT_TABLE));
				return status;
			}else throw new Exception("Reference "+requestId+" not found");
		}finally{if(session!=null) session.close();}		
	}
	
	private static int updateField(String id, String field, FieldType objectType,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(EXPORT_ID,id,FieldType.STRING));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",objectType));
			values.add(valueList);
			return session.updateOperation(EXPORT_REFERENCE_TABLE, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	
	//******************** INSTANCE
	
	private String referenceId;
	
	private ExportManager(String referenceId){		
		this.referenceId=referenceId;
		this.setName("EXPORTER_"+getId());
	}
	
	@Override
	public void run() {
		DBSession session=null;
		boolean found=false;
		try{
			updateField(referenceId, EXPORT_STATUS, FieldType.STRING, ExportStatus._ONGOING);
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(EXPORT_ID,referenceId,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, EXPORT_REFERENCE_TABLE, EXPORT_ID, OrderDirection.ASC);			
			if(rs.next()){
				found=true;				
				ExportCSVSettings settings=(ExportCSVSettings) AquaMapsXStream.getXMLInstance().fromXML(rs.getString(EXPORT_SETTINGS));
				String fileName=session.exportTableToCSV(rs.getString(EXPORT_TABLE), settings.isHasHeader(),settings.getDelimiter().charAt(0));				
				updateField(referenceId, EXPORT_LOCAL_PATH, FieldType.STRING, fileName);

				GCUBEScope scope=GCUBEScope.getScope(rs.getString(EXPORT_SCOPE));
				RSWrapper wrapper=new RSWrapper(scope);
				wrapper.add(new File(fileName));
				String locator = wrapper.getLocator().toString();
				logger.trace("Added file to locator "+locator);
				
				updateField(referenceId, EXPORT_LOCATOR, FieldType.STRING, locator);
				updateField(referenceId, EXPORT_TIME, FieldType.LONG, System.currentTimeMillis());
				updateField(referenceId, EXPORT_STATUS, FieldType.STRING, ExportStatus.COMPLETED);
			}else throw new Exception("Reference "+referenceId+" not found");
		}catch(Exception e){
			if(found){
				try{
					updateField(referenceId, EXPORT_ERROR_MSG, FieldType.STRING, e.getMessage());
					updateField(referenceId, EXPORT_STATUS, FieldType.STRING, ExportStatus._ERROR);
				}catch(Exception e1){
					logger.fatal("Unable to update export reference id  "+referenceId,e);
				}
			}
		}finally{if(session!=null)
			try {
				session.close();
			} catch (Exception e) {
				
			}}		
	}
}

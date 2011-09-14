package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.dataModel.Types.ResourceStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.common.core.utils.logging.GCUBELog;

import net.sf.csv4j.CSVLineProcessor;

public class StatefullCSVLineProcessor implements CSVLineProcessor {
	
	private static final GCUBELog logger=new GCUBELog(StatefullCSVLineProcessor.class);	
	boolean continueProcess=true;
	PreparedStatement ps=null;
	DBSession session=null;
	Long count=0l;
	private ResourceStatus status=ResourceStatus.Completed;
	List<Field> model;
	int[] modelCSVFieldsMapping;
	String tableName;
	Long totalCount;
	Integer metaId;
	
	public ResourceStatus getStatus() {
		return status;
	}
	
	
	public StatefullCSVLineProcessor(List<Field> model,String tablename,Long totalCount,Integer metaId) {
		 this.model=model;
		 modelCSVFieldsMapping= new int[model.size()];
		 this.totalCount=totalCount;
		 this.metaId=metaId;
		 this.tableName=tablename;
	}
	
	
	@Override
	public boolean continueProcessing() {return continueProcess;}
	
	@Override
	public void processDataLine(int arg0, List<String> arg1) {
		List<Field> line= new ArrayList<Field>();
		try{
		for(int i=0;i<model.size();i++){
			Field modelField=model.get(i);
			line.add(new Field(modelField.getName(),arg1.get(modelCSVFieldsMapping[i]),modelField.getType()));
		}
		count+=(session.fillParameters(line, 0, ps)).executeUpdate();
		if(count % 100==0) {
			logger.debug("Updateing "+count+" / "+totalCount);
			SourceManager.setCountRow(metaId, count);
		}
		}catch(Exception e){
			logger.error("Unable to insert row",e);
			try{
			ParameterMetaData meta=ps.getParameterMetaData();
			logger.error("Parameters :");
			for(int i=0;i<meta.getParameterCount();i++){
				Field f=line.get(i);
				logger.error(f.getName()+" FIELD TYPE : "+f.getType()+" SQL TYPE : "+meta.getParameterType(i+1)+" VALUE :"+f.getValue());
			}
			}catch(Exception e1){
				logger.error("Unable to read parameter metadata ",e1);
			}
			status=ResourceStatus.Error;
		}
	}
	
	@Override
	public void processHeaderLine(int arg0, List<String> arg1) {
		try{
			logger.trace("Processing Header..");
			continueProcess=arg1.size()==model.size();
			if(continueProcess){
				for(int i=0;i<arg1.size();i++)
					for(int j=0;j<model.size();j++)
						if(arg1.get(i).equalsIgnoreCase(model.get(j).getName()))
							modelCSVFieldsMapping[j]=i;
				logger.trace("Matched "+arg1.size()+" fields : ");
				for(int i=0;i<model.size();i++){
					Field modelField=model.get(i);
					logger.debug(modelField.getName()+" , " +arg1.get(modelCSVFieldsMapping[i])+" , "+modelField.getType());
				}
				session=DBSession.getInternalDBSession();
				ps=session.getPreparedStatementForInsert(model, tableName);
			}
		}catch(Exception e){
			logger.error("Unable to initialize reading",e);
			continueProcess=false;
			status=ResourceStatus.Error;
		}
	}
	
	public void close(){
		if (session!=null){
			try{
				session.close();
			}catch(Exception e){
				logger.warn("Unable to close session", e);
			}
		}
	}
}

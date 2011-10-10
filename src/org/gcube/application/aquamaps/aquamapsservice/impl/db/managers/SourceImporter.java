package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.util.List;

import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.dataModel.Types.ResourceStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceImporter extends Thread {
	private static final GCUBELog logger=new GCUBELog(SourceImporter.class);	
	
	private String csv;
	private Resource importing;
	private Integer metaId;

	
	
	public SourceImporter(String csv, Integer metaId) {
		super();
		this.csv = csv;
		this.metaId = metaId;
	}




	public void run() {
		DBSession session=null;
		try{
			importing=SourceManager.getById(metaId);
			logger.debug("Started importing operation from "+csv+" TO "+importing.getTableName());
			Resource defaultResource=SourceManager.getById(SourceManager.getDefaultId(importing.getType()));
			session=DBSession.getInternalDBSession();
			
			logger.debug("Going to evaluate default table "+defaultResource.getTableName()+" fields");
			ResultSet templateRs=session.executeQuery("SELECT * FROM "+defaultResource.getTableName()+" LIMIT 1 OFFSET 0");
			templateRs.next();
			final List<Field> model=Field.loadRow(templateRs); 
			
			
			
			CSVReaderProcessor processor=new CSVReaderProcessor();
			processor.setDelimiter(',');					
			Reader reader= new InputStreamReader(new FileInputStream(csv), Charset.defaultCharset());
			
			StatefullCSVLineProcessor lineProcessor=new StatefullCSVLineProcessor(model, importing.getTableName(),CSVUtils.countCSVRows(csv, ',', true),metaId);
			logger.debug("Starting file processing");
			processor.processStream(reader , lineProcessor);
			logger.debug("Complete processing");
			importing.setStatus(lineProcessor.getStatus());
			
			SourceManager.update(importing);
			logger.debug("Completed import of source ID "+importing.getSearchId());
		}catch(Exception e){
			logger.error("Unexpected Exception ", e);
			importing.setStatus(ResourceStatus.Error);
			try{
				SourceManager.update(importing);				
			}catch (Exception e1){
				logger.error("Unable to update resource "+metaId, e1);
			}
		}finally{
			if(session!=null)
				try{
					session.close();
				}catch(Exception e){
					logger.fatal("Unable to close session ",e);
				}
		}
	};
}

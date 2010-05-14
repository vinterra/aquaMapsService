package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.common.core.utils.logging.GCUBELog;




public class GroupGenerator {
	private static GCUBELog logger= new GCUBELog(GroupGenerator.class);
	public static char delimiter=',';
	public static boolean hasHeader=false;
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private static String[] columnsAndConstraintDefinition = new String[]{
		"CsquareCode varchar(10)",
		"feature float(3,2)"
	};
	
	long count=0;
	long lines=0;
	
	
	
	public String createLayerData(final String fileName)throws Exception{		
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(delimiter);
		processor.setHasHeader(hasHeader);
//		final ArrayList<Object[]> data = new ArrayList<Object[]>();
		logger.trace("Reading from file "+fileName); 
		String tableName="S"+(uuidGen.nextUUID()).replaceAll("-", "_");
		DBSession session = DBSession.openSession(PoolManager.DBType.postGIS);
		session.createTable(tableName, columnsAndConstraintDefinition, DBSession.ENGINE.InnoDB);
		
		final PreparedStatement ps = session.getPreparedStatementForInsert(columnsAndConstraintDefinition.length, tableName);
		count=0;
		lines=0;
		Reader reader= new InputStreamReader(new FileInputStream(fileName), Charset.defaultCharset());
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				lines++;
				try{
					ps.setString(1, arg1.get(0));				
					ps.setString(2, arg1.get(1));
					if(ps.execute()) count++;
				}catch(Exception e){
					logger.warn("Unable to complete insertion from file "+fileName, e);
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {}});
		if(lines>0){
			if(count>0)
				logger.trace("Inserted "+count+" entries of "+lines+" lines");			 
		}else logger.warn("No records found"); 
		return tableName;
	}
	
	
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sf.csv4j.CSVWriter;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.gcube.common.core.utils.logging.GCUBELog;

public class GeneratorManager {
	private static GCUBELog logger= new GCUBELog(GeneratorManager.class);
	private static GenericObjectPool pool=new GenericObjectPool(new PerlCallsFactory());
	
	static{
		pool.setLifo(false);
		pool.setMaxActive(3);
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		try{
			for(int i =0;i<pool.getMaxActive();i++){
				pool.addObject();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static int requestGeneration(ImageGeneratorRequest request) throws Exception{
		
		PerlImageGenerator obj=(PerlImageGenerator) pool.borrowObject();
		obj.setRequest(request);
		int toReturn= obj.generate();
		pool.returnObject(obj);
		return toReturn;
	}
		
	
	public static long ResultSetToCSVFile(ResultSet rs, String outFile)throws IOException,SQLException{
		final FileWriter fileWriter = new FileWriter(outFile);
		final CSVWriter csvWriter = new CSVWriter( fileWriter );	
		//csvWriter.writeLine( new String[] { "column1", "column2", "column3" } );
		ResultSetMetaData meta=rs.getMetaData();	
		logger.trace("Writing record values ...");
		long count = 0;
		while(rs.next()){
			String[] record= new String[meta.getColumnCount()];
			for(int column=0;column<record.length;column++){
				String value=rs.getString(column+1);
				record[column]=(value!=null)?value:"null";
				//record[column]=value;
			}
			csvWriter.writeLine(record);
			count++;
		}
		fileWriter.close();
		if((new File(outFile)).exists())
			logger.trace("Wrote "+count+" records");
		else logger.error("File "+outFile+" not created");
		return count;
	}
	
}

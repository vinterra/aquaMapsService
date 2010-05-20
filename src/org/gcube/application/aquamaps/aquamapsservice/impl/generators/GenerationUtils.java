package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import net.sf.csv4j.CSVWriter;

public class GenerationUtils {

	public static long ResultSetToCSVFile(ResultSet rs, String outFile)throws IOException,SQLException{
		final FileWriter fileWriter = new FileWriter(outFile);
		final CSVWriter csvWriter = new CSVWriter( fileWriter );	
		//csvWriter.writeLine( new String[] { "column1", "column2", "column3" } );
		ResultSetMetaData meta=rs.getMetaData();	
		GeneratorManager.logger.trace("Writing record values ...");
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
			GeneratorManager.logger.trace("Wrote "+count+" records");
		else GeneratorManager.logger.error("File "+outFile+" not created");
		return count;
	}

}

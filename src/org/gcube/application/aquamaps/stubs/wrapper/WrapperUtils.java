package org.gcube.application.aquamaps.stubs.wrapper;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;

public class WrapperUtils {

	public static List<List<Field>> loadCSV(String path,char delimiter)throws Exception{
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(delimiter);
		processor.setHasHeader(true);
		final List<List<Field>> toReturn=new ArrayList<List<Field>>();
		final List<String> headers=new ArrayList<String>();
		Reader reader= new InputStreamReader(new FileInputStream(path), Charset.defaultCharset());
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				List<Field> line= new ArrayList<Field>();
				for(int i=0;i<headers.size();i++)
					line.add(new Field(headers.get(i),arg1.get(i),FieldType.STRING));
				toReturn.add(line);
			}
			public void processHeaderLine(int arg0, List<String> arg1) {
				headers.addAll(arg1);
			}});
	
		return toReturn;
	}

	/**
	 * Returns a list of String from a csvString
	 * 
	 * @return
	 */
	public static List<String> CSVToList(String theString){
		List<String> toReturn= new ArrayList<String>();
		if(theString!=null)
			for(String s:theString.split("'"))
				toReturn.add(s.trim());
		return toReturn;
	}

	public static String listToCSV(List<String> values){
		StringBuilder toReturn=new StringBuilder();
		if(values!=null){
			for(String v:values)
				toReturn.append(v.trim()+",");
			toReturn.deleteCharAt(toReturn.lastIndexOf(","));
		}
		return toReturn.toString();
	}

}

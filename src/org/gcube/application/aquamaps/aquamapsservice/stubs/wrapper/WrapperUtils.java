package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;

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
	
}

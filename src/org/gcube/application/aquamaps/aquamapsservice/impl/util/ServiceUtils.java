package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;

public class ServiceUtils {

	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private static DateFormat dateFormatter= new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
	private static DateFormat shortDateFormatter=new SimpleDateFormat("yyyy_MM_dd");
	
	
	public static String fileToString(String path) throws IOException {
		
		
		BufferedReader filebuf = null;
		String nextStr = null;
		String ret = new String();
		
			filebuf = new BufferedReader(new FileReader(path));
			nextStr = filebuf.readLine(); // legge una riga dal file
			while (nextStr != null) {
				ret += nextStr ;
				nextStr = filebuf.readLine(); // legge la prossima riga 
			}
			filebuf.close(); // chiude il file 
		
		return ret;
	}
	
	public static String URLtoString(String path) throws IOException{
		URL yahoo = new URL(path);
		BufferedReader in = new BufferedReader(
					new InputStreamReader(
					yahoo.openStream()));

		String inputLine;
		StringBuilder toReturn=new StringBuilder(); 
		
		
		while ((inputLine = in.readLine()) != null)
		    toReturn.append(inputLine);

		in.close();
		return toReturn.toString();
	}
	
	
	public static void deleteFile(String path) throws IOException {
		File f=new File(path);
		File dir = f.getParentFile();
		f.delete();
		if(dir.list().length==0)dir.delete(); 
	}
	
	public static String generateId(String prefix,String suffix){
//		return prefix+(uuidGen.nextUUID()).replaceAll("-", "_")+suffix;
		return prefix+getTimeStamp().replaceAll("-", "_")+suffix;
	}
	public static String getTimeStamp(){
		return dateFormatter.format(System.currentTimeMillis());
	}
	public static String getDate(){
		return shortDateFormatter.format(System.currentTimeMillis());
	}
	
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
	
}

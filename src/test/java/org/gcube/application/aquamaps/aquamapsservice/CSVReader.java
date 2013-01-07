package testClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;

import com.thoughtworks.xstream.XStream;

public class CSVReader {

	/**
	 * @param args
	 * @throws ProcessingException 
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException, IOException, ProcessingException {
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter('|');
		processor.setHasHeader(true);
		final List<String> headers=new ArrayList<String>();
		Reader reader= new InputStreamReader(new FileInputStream("/home/fabio/Desktop/layers.csv"), Charset.defaultCharset());
		final PrintWriter out = new PrintWriter("filename.txt");
		processor.processStream(reader , new CSVLineProcessor(){
			int lineCount=2;
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				if(arg1.size()>2){
					try{
						LayerInfo l=(LayerInfo) new XStream().fromXML(arg1.get(2));
						System.out.println(l.getName());
						lineCount--;
						out.println(l.getName());
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {
				headers.addAll(arg1);
			}});	
		out.flush();
		out.close();
	}

}

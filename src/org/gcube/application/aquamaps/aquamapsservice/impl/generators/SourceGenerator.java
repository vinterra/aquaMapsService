package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceGenerator {

private static GCUBELog logger= new GCUBELog(SourceGenerator.class);
	

	private SourceGeneratorRequest request;

	public SourceGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public SourceGenerator(SourceGeneratorRequest req) {		
		request=req;
	}
	
	public boolean generate() throws IOException{
		
	return (generateCSV(request.getInputFile(), request.getOutputFile())==0);	
//		return (dummyProcess(request.getRequestId())==0);
	}
	
	
//	private static int dummyProcess(int requestId){
//		try {
//			logger.debug("Executing.."+requestId);
//			Thread.sleep(5*1000);
//		} catch (InterruptedException e) {
//			logger.debug("Done"+requestId);
//		}
//		return 0;
//	}
	
	
	private static int  generateCSV(String inputFile,String outputFile) throws IOException{


		Runtime rt  = Runtime.getRuntime();
		String cmdLine[] = { System.getenv("GLOBUS_LOCATION")+File.separator+"ML3"+
				File.separator+"bin"+File.separator+"runML3.sh",inputFile,outputFile};
		logger.trace("starting csv generation from "+inputFile);
		long startTime=System.currentTimeMillis();
		Process p = rt.exec(cmdLine);

		BufferedReader  input = new BufferedReader (new InputStreamReader (p.getInputStream()));
		String line = null;
		while ((line = input.readLine())!=null){
			logger.debug(line);
		}

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			logger.trace("wrote "+outputFile+" in "+(System.currentTimeMillis()-startTime)+" ms ");
		}
		p.destroy();		
		
		return p.exitValue();		
	}

	/**
	 * @return the request
	 */
	public SourceGeneratorRequest getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(SourceGeneratorRequest request) {
		this.request = request;
	}
	
	
}

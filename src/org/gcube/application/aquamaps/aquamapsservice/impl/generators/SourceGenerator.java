package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SourceGenerator implements Generator{

private static GCUBELog logger= new GCUBELog(SourceGenerator.class);
	
	private static final String shPath=System.getenv("GLOBUS_LOCATION")+File.separator+"ML3_stub"+
	File.separator+"bin"+File.separator+"runML3.sh";


	private SourceGeneratorRequest request;

	public SourceGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public SourceGenerator(SourceGeneratorRequest req) {		
		request=req;
	}
	
	
	
	
	private static int  generateCSV(String inputFile,String outputFile) throws IOException{


		Runtime rt  = Runtime.getRuntime();
		logger.trace("TERRADUE APPLICATION SHOULD BE : "+shPath);
		String cmdLine[] = { "/bin/sh", shPath,inputFile,outputFile};
		logger.trace("starting csv generation from "+inputFile);
		long startTime=System.currentTimeMillis();
		Process p = rt.exec(cmdLine);

		BufferedReader  input = new BufferedReader (new InputStreamReader (p.getInputStream()));
		String line = null;
		while ((line = input.readLine())!=null){
			if(ServiceContext.getContext().isEnableScriptLogging())
			logger.trace(line);
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

	
	public boolean getResponse() throws Exception {
		if(request==null) throw new Exception("No request setted");
		return (generateCSV(request.getInputFile(), request.getOutputFile())==0);
	}

	public void setRequest(GenerationRequest theRequest)
			throws BadRequestException {
		if (theRequest instanceof SourceGeneratorRequest)
			this.request=(SourceGeneratorRequest) theRequest;
		else throw new BadRequestException();
		
	}
	
	
}

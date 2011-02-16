package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.gcube.common.core.utils.logging.GCUBELog;


public class PerlImageGenerator implements Generator{
	private static GCUBELog logger= new GCUBELog(PerlImageGenerator.class);
	



	private ImageGeneratorRequest request;

	public PerlImageGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public PerlImageGenerator(ImageGeneratorRequest req) {		
		request=req;
	}
	
	
	private static int  generateImages(String file) throws IOException{


		Runtime rt  = Runtime.getRuntime();
		String cmdLine[] = { "/usr/bin/perl", "-w",  System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid"+
				File.separator+"bin"+File.separator+"cs_mapMod.pl",file};
		Process p = rt.exec(cmdLine);

		BufferedReader  input = new BufferedReader (new InputStreamReader (p.getInputStream()));
		String line = null;
		while ((line = input.readLine())!=null){
//			logger.debug(line);
		}

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			logger.trace("Perl process exited");
		}
		p.destroy();
		File f= new File(file);
		if(!f.delete())logger.warn("Unable to delete clustering file "+file);
		return p.exitValue(); 
	}

	/**
	 * @return the request
	 */
	public ImageGeneratorRequest getRequest() {
		return request;
	}

	

	public boolean getResponse() throws Exception {
		if(request==null) throw new Exception("No request setted");
		int result=generateImages(request.getClusterFile());
		return (result==0);
	}

	public void setRequest(GenerationRequest theRequest)
			throws BadRequestException {
		if(theRequest instanceof ImageGeneratorRequest)
			this.request=(ImageGeneratorRequest) theRequest;
		else throw new BadRequestException();
	}
}

package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.gcube.common.core.utils.logging.GCUBELog;


public class PerlImageGenerator {
	private static GCUBELog logger= new GCUBELog(PerlImageGenerator.class);
	



	private ImageGeneratorRequest request;

	public PerlImageGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public PerlImageGenerator(ImageGeneratorRequest req) {		
		request=req;
	}
	
	public boolean generate() throws IOException{
		int result=generateImages(request.getClusterFile());
		return (result==0);
		
//		return dummyProcess(request.getFile());
	}
	
	
	private static int dummyProcess(String file){
		try {
			logger.debug("Executing.."+file);
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			logger.debug("Done"+file);
		}
		return 0;
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

	/**
	 * @param request the request to set
	 */
	public void setRequest(ImageGeneratorRequest request) {
		this.request = request;
	}
}

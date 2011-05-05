package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.common.core.utils.logging.GCUBELog;


public class PerlImageGenerator implements Generator{
	private static GCUBELog logger= new GCUBELog(PerlImageGenerator.class);
	


	private static final String CLUSTERING_dirPath=ServiceContext.getContext().getPersistenceRoot()+File.separator+"clusters";
	private static final String GENERATED_IMAGES=System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/";

	private ImageGeneratorRequest request;

	public PerlImageGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public PerlImageGenerator(ImageGeneratorRequest req) {		
		request=req;
	}
	
	
	private static int  generateImages(String file) throws Exception{

		
		logger.trace("Checking perl...");
		String perlFileLocation=System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid"+
		File.separator+"bin"+File.separator+"cs_mapMod.pl";
		File perlFile=new File(perlFileLocation);
		if(perlFile.exists()){
			logger.trace("Checking file existance... : "+file);
			File clusteringFile=new File(file);
			if(clusteringFile.exists()&&clusteringFile.canRead()){
				Runtime rt  = Runtime.getRuntime();
				String cmdLine[] = { "/usr/bin/perl", "-w", perlFileLocation ,file};
				Process p = rt.exec(cmdLine);

				BufferedReader  input = new BufferedReader (new InputStreamReader (p.getInputStream()));
				String line = null;
				while ((line = input.readLine())!=null){
					if(ServiceContext.getContext().isEnableScriptLogging())
					logger.debug(line);
				}

				try {
					p.waitFor();
				} catch (InterruptedException e) {
					logger.trace("Perl process exited");
				}
				int value=p.exitValue();
				logger.trace("Exit value is "+value);
				p.destroy();
				return value;
			}else throw new Exception("No access to clustering file "+file);
		}else throw new Exception("Perl File "+perlFileLocation+" NOT FOUND, unable to proceed");
		

		 
	}

	/**
	 * @return the request
	 */
	public ImageGeneratorRequest getRequest() {
		return request;
	}

	

	public boolean getResponse() throws Exception {
		if(request==null) throw new Exception("No request setted");
		
		Submitted obj=SubmittedManager.getSubmittedById(request.getObjId());
		String header=obj.getJobId()+"_"+obj.getSearchId()+"_"+obj.getTitle();
		header=header.replaceAll(" ", "_");
		
		
		
		String clusterFile=createClusteringFile(obj.getSearchId(),obj.getJobId(),request.getCsq_str(),header);
		
		int result=generateImages(clusterFile);
		
		if(result==0){
			request.setGeneratedImagesNameAndPath(getToPublishList(header));
			JobManager.addToDeleteTempFolder(obj.getJobId(), GENERATED_IMAGES+header+File.separator);
			return request.getGeneratedImagesNameAndPath().size()>0;
		}else return false;
	}

	public void setRequest(GenerationRequest theRequest)
			throws BadRequestException {
		if(theRequest instanceof ImageGeneratorRequest)
			this.request=(ImageGeneratorRequest) theRequest;
		else throw new BadRequestException();
	}
	
	/**
	 * 
	 * @param objectName
	 * @param csq_str
	 * @param header
	 * @param header_map
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	private static String createClusteringFile(int objId, int jobId, StringBuilder[]csq_str,String header) throws Exception{

		String to_out = "color=FFFF84 fill=Y color2=FFDE6B fill2=Y color3=FFAD6B fill3=Y color4=FF6B6B fill4=Y color5=DE4242 fill5=Y "+
		((csq_str[0].toString().compareTo("")!=0)?" csq="+csq_str[0].toString():" csq=0000:000:0")+
		((csq_str[1].toString().compareTo("")!=0)?" csq2="+csq_str[1].toString():"")+
		((csq_str[2].toString().compareTo("")!=0)?" csq3="+csq_str[2].toString():"")+
		((csq_str[3].toString().compareTo("")!=0)?" csq4="+csq_str[3].toString():"")+
		((csq_str[4].toString().compareTo("")!=0)?" csq5="+csq_str[4].toString():"")+
		" header="+header+" enlarge=7200 title="+header+
		" dilate=N cSub popup=Y landmask=1 filedesc=map_pic legend=  mapsize=large";
		
		String fileName=objId+"_clustering";
		try {
			File d=new File(CLUSTERING_dirPath+File.separator+jobId);
			JobManager.addToDeleteTempFolder(jobId, d.getAbsolutePath());
			d.mkdirs();
			File f=new File(d,fileName);
			f.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(to_out);
			out.close();
			String toReturn=f.getAbsolutePath();
			logger.debug("Clustering string saved into "+toReturn);
			return toReturn;
		} catch(Exception e){
				logger.error("Unable to write clutering file ",e);
				throw  e;
		}
	}
	
	private static final Map<String,String> imageFileAndName= new HashMap<String, String>();

	static {
		imageFileAndName.put("_map_pic.jpg", "Earth");
		imageFileAndName.put("_afr.jpg", "Continent View : Africa");
		imageFileAndName.put("_asia.jpg", "Continent View : Asia");
		imageFileAndName.put("_aus.jpg", "Continent View : Australia");
		imageFileAndName.put("_eur.jpg", "Continent View : Europa");
		imageFileAndName.put("_nAm.jpg", "Continent View : North America");
		imageFileAndName.put("_sAm.jpg", "Continent View : South America");
		imageFileAndName.put("_xmapAtlan.jpg", "Ocean View : Atlantic");
		imageFileAndName.put("_xmapI.jpg", "Ocean View : Indian");
		imageFileAndName.put("_xmapN.jpg", "Pole View : Artic");
		imageFileAndName.put("_xmapNAtlan.jpg", "Ocean View : North Atlantic");
		imageFileAndName.put("_xmapP.jpg", "Ocean View : Pacific");
		imageFileAndName.put("_xmapS.jpg", "Pole View : Antarctic");
		imageFileAndName.put("_xmapSAtlan.jpg", "Ocean View : South Atlantic");		
	}

	
	private static Map<String,String> getToPublishList(String header){
		Map<String,String> toReturn=new HashMap<String, String>();
		String basePath=GENERATED_IMAGES+header+File.separator;
		logger.trace("Checking generated images...");
		logger.trace("base path : "+basePath);
		logger.trace("header is "+header);
		File f1 = new File(GENERATED_IMAGES+"csq_map127.0.0.1_"+header+"_pic.jpg");
		logger.trace("Checking file "+f1.getAbsolutePath());
		if (f1.exists())
			toReturn.put("Earth",f1.getAbsolutePath());			

		for(String suffix:imageFileAndName.keySet()){
			File f2 = new File(basePath+header+suffix);
			logger.trace("Checking file "+f2.getAbsolutePath());
			if (f2.exists())
				toReturn.put(imageFileAndName.get(suffix), f2.getAbsolutePath());
		}

		for(Entry<String,String> entry:toReturn.entrySet())
			logger.trace("Found "+entry.getKey()+" @ "+entry.getValue());
		return toReturn;
	}
	
}

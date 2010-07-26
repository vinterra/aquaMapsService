package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.MapRegistration;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DistributionThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(DistributionThread.class);
	private static final int waitTime=10*1000;

	private int aquamapsId;
	private String aquamapsName;	
	//private String HSPECName;
	private String[] speciesId;
	private DBSession session;
	private int jobId;	
	private boolean gisEnabled=false;

	private GCUBEScope actualScope;

	public DistributionThread(ThreadGroup group,int jobId,int aquamapsId,String aquamapsName,GCUBEScope scope) {
		super(group,"SAD_AquaMapObj:"+aquamapsName);
		this.aquamapsId=aquamapsId;
		this.aquamapsName=aquamapsName;
		this.jobId=jobId;
		logger.trace("Passed scope : "+scope.toString());
		//		ServiceContext.getContext().setScope(this, scope);
		//		logger.trace("Setted scope : "+ServiceContext.getContext().getScope());
		this.actualScope=scope;
	}
	public void setRelatedSpeciesId(String[] species){
		speciesId=species;
	}

	public void setGis(boolean gis){
		gisEnabled=gis;
	}

	public void run() {
		logger.trace(this.getName()+" started");
		try {
			while(!JobGenerationDetails.isSpeciesListReady(jobId, speciesId)){
				try{
					Thread.sleep(waitTime);
				}catch(InterruptedException e){}
				logger.trace("waiting for "+speciesId[0]+" to Be ready");
			}

			boolean needToGenerate=false;
			List<String> publishedMaps=null;
			boolean hasCustomizations=JobGenerationDetails.isSpeciesSetCustomized(jobId,speciesId);
			if(hasCustomizations) {
				needToGenerate=true;
				logger.trace(this.getName()+" has Customizations, going to generate..");
			}
			else{
				logger.trace(this.getName()+" hasn't Customizations, looking for default maps..");
				publishedMaps=Publisher.getPublisher().getPublishedMaps(speciesId,JobGenerationDetails.getHSPENTable(jobId),JobGenerationDetails.getHCAFTable(jobId),this.actualScope);
				logger.trace(this.getName()+" found "+publishedMaps.size()+" default images");
				if(publishedMaps.size()==0) needToGenerate=true;
			}

			if(needToGenerate){
				//*********************************** GENERATION
				
				logger.trace(this.getName()+" entering image generation phase");
				
								
				ResultSet rs=queryForProbabilities();
				
				String csvFile=null;
				if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
							csvFile=generateCsvFile(rs);	
				}

				String header=jobId+"_"+aquamapsName;
				String header_map = header+"_maps";
				StringBuilder[] csq_str;
				csq_str=JobUtils.clusterize(rs, 2, 1, 2,false);
				rs.close();
				session.close();

				if(csq_str==null) logger.trace(this.getName()+"Empty selection, nothing to render");
				else {

					///************************ PERL IMAGES GENERATION AND PUBBLICATION

					String clusterFile=JobUtils.createClusteringFile(aquamapsName, csq_str, header, header_map, jobId+File.separator+aquamapsName+"_clustering");
					JobGenerationDetails.addToDeleteTempFolder(jobId, System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/"+header);
					logger.trace(this.getName()+"Clustering completed, gonna call perl with file " +clusterFile);
					JobUtils.updateAquaMapStatus(aquamapsId,Status.Publishing);
					boolean result=GeneratorManager.requestGeneration(new ImageGeneratorRequest(clusterFile));
					//				JobUtils.generateImages(clusterFile);
					logger.trace(this.getName()+" Perl execution exit message :"+result);
					if(!result) logger.warn("No images were generated");
					else {
						Map<String,String> app=JobUtils.getToPublishList(System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/",header);


						logger.trace(this.getName()+" found "+app.size()+" files to publish");
						if(app.size()>0){
							//						String basePath=JobUtils.publish(HSPECName, String.valueOf(jobId), app.values());
							String basePath=Publisher.getPublisher().publishImages(this.jobId, speciesId, app.values(),actualScope,hasCustomizations);
							logger.trace(this.getName()+" files moved to public access location, inserting information in DB");
							logger.trace(this.getName()+" "+JobUtils.linkImagesInDB(app, basePath,aquamapsId)+" file information inserted in DB");
						}
					}

					/// *************************** GIS GENERATION

					if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
						generateLayerFromCsv(csvFile);
					}

				}	
			}else{
				// ********************************** Using already published Images
				logger.trace(this.getName()+" using already published Images");				
				Map<String,String> imagesNameAndLink=JobUtils.parsePublished(publishedMaps);
				String firstUrl=publishedMaps.get(0);
				String basePath=firstUrl.substring(0, firstUrl.lastIndexOf("/")+1);
				logger.trace(this.getName()+" "+JobUtils.linkImagesInDB(imagesNameAndLink, basePath,aquamapsId)+" file information inserted in DB");
				if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
					String layer= Publisher.getPublisher().getPublishedLayer(speciesId,JobGenerationDetails.getHSPENTable(jobId),JobGenerationDetails.getHCAFTable(jobId),this.actualScope);
					
					if(layer!=null){
						logger.trace("Going to associate to pre-generated layer "+layer);
						JobGenerationDetails.updateGISData(aquamapsId, layer);
					}else{
						ResultSet rs=queryForProbabilities();
						String csvFile=generateCsvFile(rs);
						session.close();
						generateLayerFromCsv(csvFile);
						layer= JobGenerationDetails.getGIS(aquamapsId);
						Publisher.getPublisher().registerLayer(speciesId, JobGenerationDetails.getHSPENTable(jobId),JobGenerationDetails.getHCAFTable(jobId),layer);
					}					
						
				}
			}


			JobUtils.updateAquaMapStatus(aquamapsId,Status.Completed);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			try {
				JobUtils.updateAquaMapStatus(aquamapsId, Status.Error);
			} catch (Exception e1) {
				logger.error("Unable to handle previous error! : "+e1.getMessage());
			}
		}finally{
			try{
			session.close();
			}catch(Exception e){
				logger.error("Unexpected Error, unable to close session");
			}
		}
	}
	

	private void generateLayerFromCsv(String csvFile) throws Exception{
		logger.trace("Going to generate layer");
		LayerGenerationRequest request= new LayerGenerationRequest();
		request.setCsvFile(csvFile);
		request.setFeatureLabel("Probability");
		request.setFeatureDefinition("real");
		request.setLayerName(speciesId[0]);
		request.setDefaultStyle(ServiceContext.getContext().getDistributionDefaultStyle());
		request.setSubmittedId(aquamapsId);
		logger.trace("submitting Gis layer generation for obj Id :"+aquamapsId);
		if(GeneratorManager.requestGeneration(request))
			logger.trace("generated layer for obj Id : "+aquamapsId);
		else logger.trace("unable to generate layer for obj Id : "+aquamapsId);
	}	
	
	private String generateCsvFile(ResultSet rs)throws Exception{
		String csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+jobId+File.separator+aquamapsName+".csv";
		FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
		GenerationUtils.ResultSetToCSVFile(rs, csvFile);
		return csvFile;
	}
	
	
	private ResultSet queryForProbabilities()throws Exception{
		String HSPECName=JobGenerationDetails.getHSPECTable(jobId);
		JobUtils.updateAquaMapStatus(aquamapsId,Status.Simulating);
		session=DBSession.openSession(PoolManager.DBType.mySql);

		String clusteringQuery=DBCostants.clusteringDistributionQuery(HSPECName);
		logger.trace("Gonna use query "+clusteringQuery);
		PreparedStatement ps= session.preparedStatement(clusteringQuery);
		ps.setString(1,speciesId[0]);
		//		ps.setFloat(2,toPerform.getThreshold());

		return ps.executeQuery();
	}
	
}

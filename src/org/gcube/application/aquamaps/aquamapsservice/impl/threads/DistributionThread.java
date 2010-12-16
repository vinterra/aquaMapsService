package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.FileManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HSPECFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DistributionThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(DistributionThread.class);
	private static final int waitTime=10*1000;

	private int aquamapsId;
	private String aquamapsName;	
	
	
//	private String HSPEN;
//	private String HCAF;
	
	private Set<String> speciesId=new HashSet<String>();
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
	public void setRelatedSpeciesId(Set<Species> species){
		speciesId.add(species.iterator().next().getId());
	}

	public void setGis(boolean gis){
		gisEnabled=gis;
	}

	public void run() {
		logger.trace(this.getName()+" started");
		try {
			while(!JobManager.isSpeciesListReady(jobId, speciesId)){
				try{
					Thread.sleep(waitTime);
				}catch(InterruptedException e){}
				logger.trace("waiting for species to Be ready");
			}
			
			String sourceHSPEN=SourceManager.getSourceName(ResourceType.HSPEN, JobManager.getHSPENTableId(jobId));
			String sourceHCAF=SourceManager.getSourceName(ResourceType.HCAF, JobManager.getHCAFTableId(jobId));
			
			boolean needToGenerate=false;
			List<String> publishedMaps=null;
			boolean hasCustomizations=JobManager.isSpeciesSetCustomized(jobId,speciesId);
			if(hasCustomizations) {
				needToGenerate=true;
				logger.trace(this.getName()+" has Customizations, going to generate..");
			}
			else{
				logger.trace(this.getName()+" hasn't Customizations, looking for default maps..");
				
				publishedMaps=Publisher.getPublisher().getPublishedMaps(speciesId,sourceHSPEN,sourceHCAF,this.actualScope);
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
					JobManager.addToDeleteTempFolder(jobId, System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/"+header);
					logger.trace(this.getName()+"Clustering completed, gonna call perl with file " +clusterFile);
					SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Publishing);
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
							logger.trace(this.getName()+" "+FileManager.linkImagesInDB(app, basePath,aquamapsId)+" file information inserted in DB");
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
				logger.trace(this.getName()+" "+FileManager.linkImagesInDB(imagesNameAndLink, basePath,aquamapsId)+" file information inserted in DB");
				if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
					String layer= Publisher.getPublisher().getPublishedLayer(speciesId,sourceHSPEN,sourceHCAF,this.actualScope);
					
					if(layer!=null){
						logger.trace("Going to associate to pre-generated layer "+layer);
						JobManager.updateGISData(aquamapsId, layer);
					}else{
						ResultSet rs=queryForProbabilities();
						String csvFile=generateCsvFile(rs);
						session.close();
						generateLayerFromCsv(csvFile);
						layer= JobManager.getGIS(aquamapsId);
						Publisher.getPublisher().registerLayer(speciesId, sourceHSPEN,sourceHCAF,layer);
					}					
						
				}
			}


			SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Completed);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			try {
				SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Error);
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
		request.setLayerName(speciesId.iterator().next());
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
		String HSPECName=JobManager.getWorkingHSPEC(jobId);
		SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Simulating);
		session=DBSession.openSession(PoolManager.DBType.mySql);

		String clusteringQuery=clusteringDistributionQuery(HSPECName);
		logger.trace("Gonna use query "+clusteringQuery);
		PreparedStatement ps= session.preparedStatement(clusteringQuery);
		ps.setString(1,speciesId.iterator().next());


		return ps.executeQuery();
	}

	
	public static String clusteringDistributionQuery(String hspecName){
		String query= "Select "+HCAF_SFields.CSquareCode+", "+HSPECFields.Probability+"  FROM "+hspecName+" where "+
		hspecName+"."+SpeciesOccursumFields.SpeciesID+"=?  ORDER BY "+HSPECFields.Probability+" DESC";
		logger.trace("clusteringDistributionQuery: "+query);
		return query;
	}

	
}

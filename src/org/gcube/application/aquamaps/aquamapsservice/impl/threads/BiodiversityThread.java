package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.awt.Color;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.StyleGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.Status;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BiodiversityThread extends Thread {	
	private static final GCUBELog logger=new GCUBELog(BiodiversityThread.class);
	private static final int waitTime=10*1000;
//	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	private int aquamapsId;
	private int jobId;
	private String aquamapsName;
	private float threshold;
	private String HSPECName;	
	private String[] species;
	private DBSession session;
	private boolean gisEnabled=false;
	private GCUBEScope actualScope;
	
	
	public BiodiversityThread(ThreadGroup group,int jobId,int aquamapsId,String aquamapsName,float threshold,GCUBEScope scope) {
		super(group,"BioD_AquaMapObj:"+aquamapsName);	
		this.threshold=threshold;
		this.aquamapsId=aquamapsId;
		this.aquamapsName=aquamapsName;
		this.jobId=jobId;	
		logger.trace("Passed scope : "+scope.toString());
//		ServiceContext.getContext().setScope(this, scope);
//		logger.trace("Setted scope : "+ServiceContext.getContext().getScope());
		this.actualScope=scope;
	}

	public void setRelatedSpeciesList(String[] ids){
		species=ids;
	}
	public void setGis(boolean gis){
		gisEnabled=gis;
	}

	public void run() {
		logger.trace(this.getName()+" started");
		try {
			//Waiting for needed simulation data
			while(!JobGenerationDetails.isSpeciesListReady(jobId,species)){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}			
				logger.trace("waiting for selected species to be ready");
			}

			boolean needToGenerate=true;
			List<String> publishedMaps=null;
			boolean hasCustomizations=JobGenerationDetails.isSpeciesSetCustomized(jobId,species);
//			if(hasCustomizations) {
//				needToGenerate=true;
//				logger.trace(this.getName()+" has Customizations, going to generate..");
//			}
//			else{
//				logger.trace(this.getName()+" hasn't Customizations, looking for default maps..");
//				publishedMaps=Publisher.getPublisher().getPublishedMaps(species,JobGenerationDetails.getHSPENTable(jobId),JobGenerationDetails.getHCAFTable(jobId),this.actualScope);
//				logger.trace(this.getName()+" found "+publishedMaps.size()+" default images");
//				if(publishedMaps.size()==0) needToGenerate=true;
//			}
			if(needToGenerate){
				//*********************************** GENERATION
				
				logger.trace(this.getName()+" entering image generation phase");

			session=DBSession.openSession(PoolManager.DBType.mySql);
			JobUtils.updateAquaMapStatus(aquamapsId, Status.Generating);
			String tableName=ServiceUtils.generateId("S", "");
			PreparedStatement prep=null;
			String creationSQL="CREATE TABLE "+tableName+" ("+DBCostants.SpeciesID+" varchar(50) PRIMARY KEY )";
			logger.trace("Going to execute query : "+creationSQL);

			session.executeUpdate(creationSQL);

			JobGenerationDetails.addToDropTableList(jobId, tableName);	
			for(String specId: species){
				session.executeUpdate("INSERT INTO "+tableName+" VALUES('"+specId+"')");
				logger.trace("INSERT INTO "+tableName+" VALUES('"+specId+"')");
				;}
			logger.trace(this.getName()+" species temp table filled, gonna select relevant HSPEC records");
			HSPECName=JobGenerationDetails.getHSPECTable(jobId);
			JobUtils.updateAquaMapStatus(aquamapsId, Status.Simulating);
			prep=session.preparedStatement(DBCostants.clusteringBiodiversityQuery(HSPECName,tableName));				
			prep.setFloat(1,threshold);
			ResultSet rs=prep.executeQuery();
			
			
			String header=jobId+"_"+aquamapsName;
			String header_map = header+"_maps";
			StringBuilder[] csq_str;
			csq_str=JobUtils.clusterize(rs, 2, 1, 2,true);
			rs.first();
			int maxValue=rs.getInt(2);
			rs.last();
			int minValue=rs.getInt(2);
			String attributeName=rs.getMetaData().getColumnLabel(2);
			logger.trace(this.getName()+" Found minValue : "+minValue+"; maxValue : "+maxValue+" for AttributeName :"+attributeName);
			String csvFile=null;
			if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
				csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+jobId+File.separator+aquamapsName+".csv";
				FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
				GenerationUtils.ResultSetToCSVFile(rs, csvFile);				
			}
			
			rs.close();
			session.close();
			
			
			
			if(csq_str==null) logger.trace(this.getName()+"Empty selection, nothing to render");
			else {
				String clusterFile=JobUtils.createClusteringFile(aquamapsName, csq_str, header, header_map, jobId+File.separator+aquamapsName+"_clustering");
				JobGenerationDetails.addToDeleteTempFolder(jobId, System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/"+header);
				logger.trace(this.getName()+"Clustering completed, gonna call perl with file " +clusterFile);
				JobUtils.updateAquaMapStatus(aquamapsId, Status.Publishing);
				boolean result=GeneratorManager.requestGeneration(new ImageGeneratorRequest(clusterFile));
//				JobUtils.generateImages(clusterFile);
				logger.trace(this.getName()+" Perl execution exit message :"+result);		
				if(!result) logger.warn("No images were generated");
				else {
					Map<String,String> app=JobUtils.getToPublishList(System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/",header);


					logger.trace(this.getName()+" found "+app.size()+" files to publish");
					if(app.size()>0){
						String basePath=Publisher.getPublisher().publishImages(this.jobId, species, app.values(),actualScope,hasCustomizations);
						logger.trace(this.getName()+" files moved to public access location, inserting information in DB");
						logger.trace(this.getName()+" "+JobUtils.linkImagesInDB(app, basePath,aquamapsId)+" file information inserted in DB");
						
					}
				}
				
/// *************************** GIS GENERATION
				
				if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
					logger.trace(this.getName()+"is gisEnabled");
					StyleGenerationRequest styleReq=new StyleGenerationRequest();
					styleReq.setAttributeName(attributeName);
					styleReq.setC1(Color.YELLOW);
					styleReq.setC2(Color.RED);
					styleReq.setMax(String.valueOf(maxValue));
					styleReq.setMin(String.valueOf(minValue));					
					styleReq.setNameStyle(ServiceUtils.generateId(aquamapsName, "style"));					
					int Nclasses=((maxValue-minValue)>4)?5:maxValue-minValue;
					logger.debug("Found "+Nclasses+" classes for style");
					styleReq.setNClasses(Nclasses);
					styleReq.setTypeValue(Integer.class);
					

					if(GeneratorManager.requestGeneration(styleReq)){
					
					LayerGenerationRequest request= new LayerGenerationRequest();
					request.setCsvFile(csvFile);
					request.setFeatureLabel(attributeName);
					request.setFeatureDefinition("integer");
					request.setLayerName(aquamapsName);
					request.setDefaultStyle(styleReq.getNameStyle());
					request.setSubmittedId(aquamapsId);
					logger.trace("submitting Gis layer generation for obj Id :"+aquamapsId);
					if(GeneratorManager.requestGeneration(request))
						logger.trace("generated layer for obj Id : "+aquamapsId);
					else throw new Exception("unable to generate layer for obj Id : "+aquamapsId);
					}else throw new Exception("Unable to generate/submit style for "+aquamapsId);
				}
				
			}	
			}else{
				// ********************************** Using already published Images
				logger.trace(this.getName()+" using already published Images");
				
				Map<String,String> imagesNameAndLink=JobUtils.parsePublished(publishedMaps);
				String firstUrl=publishedMaps.get(0);
				String basePath=firstUrl.substring(0, firstUrl.lastIndexOf("/")+1);
				logger.trace(this.getName()+" "+JobUtils.linkImagesInDB(imagesNameAndLink, basePath,aquamapsId)+" file information inserted in DB");
				
			}
			
			JobUtils.updateAquaMapStatus(aquamapsId, Status.Completed);
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
}

package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.awt.Color;
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
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.ImageGeneratorRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.StyleGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
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
	private Set<String> species=new HashSet<String>();
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
		this.actualScope=scope;
	}

	public void setRelatedSpeciesList(Set<Species> ids){		
		for(Species s:ids)
			species.add(s.getId());		
	}
	public void setGis(boolean gis){
		gisEnabled=gis;
	}

	public void run() {
		logger.trace(this.getName()+" started");
		try {
			//Waiting for needed simulation data
			while(!JobManager.isSpeciesListReady(jobId,species)){
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {}			
				logger.trace("waiting for selected species to be ready");
			}

			boolean needToGenerate=true;
			List<String> publishedMaps=null;
			boolean hasCustomizations=JobManager.isSpeciesSetCustomized(jobId,species);
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
			SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Generating);
			String tableName=ServiceUtils.generateId("S", "");
			PreparedStatement prep=null;
			String creationSQL="CREATE TABLE "+tableName+" ("+SpeciesOccursumFields.SpeciesID+" varchar(50) PRIMARY KEY )";
			logger.trace("Going to execute query : "+creationSQL);

			session.executeUpdate(creationSQL);

			JobManager.addToDropTableList(jobId, tableName);	
			for(String specId: species){
				session.executeUpdate("INSERT INTO "+tableName+" VALUES('"+specId+"')");
				logger.trace("INSERT INTO "+tableName+" VALUES('"+specId+"')");
				;}
			logger.trace(this.getName()+" species temp table filled, gonna select relevant HSPEC records");
			HSPECName=JobManager.getWorkingHSPEC(jobId);
			SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Simulating);
			prep=session.preparedStatement(clusteringBiodiversityQuery(HSPECName,tableName));				
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
						String basePath=Publisher.getPublisher().publishImages(this.jobId, species, app.values(),actualScope,hasCustomizations);
						logger.trace(this.getName()+" files moved to public access location, inserting information in DB");
						logger.trace(this.getName()+" "+FileManager.linkImagesInDB(app, basePath,aquamapsId)+" file information inserted in DB");
						
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
				logger.trace(this.getName()+" "+FileManager.linkImagesInDB(imagesNameAndLink, basePath,aquamapsId)+" file information inserted in DB");
				
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
	
	
	
	public static String clusteringBiodiversityQuery(String hspecName, String tmpTable){
	String query= "Select "+HCAF_SFields.CSquareCode+", count("+hspecName+"."+SpeciesOccursumFields.SpeciesID+") AS MaxSpeciesCountInACell FROM "+hspecName+
			" INNER JOIN "+tmpTable+" ON "+hspecName+"."+SpeciesOccursumFields.SpeciesID+" = "+tmpTable+"."+SpeciesOccursumFields.SpeciesID+" where probability > ? GROUP BY "+HCAF_SFields.CSquareCode+" ORDER BY MaxSpeciesCountInACell DESC";
	logger.trace("clusteringBiodiversityQuery: "+query);
	return query;
}

	
}

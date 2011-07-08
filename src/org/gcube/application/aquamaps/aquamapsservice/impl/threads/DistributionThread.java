package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.PredictionLayerGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HSPECFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class DistributionThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(DistributionThread.class);
	private static final int waitTime=10*1000;

	private int aquamapsId;
	private String aquamapsName;	

	private Set<Species> selectedSpecies=new HashSet<Species>();
	private DBSession session;
	private int jobId;	
	private boolean gisEnabled=false;


	private Map<String,Perturbation> envelopeCustomization;
	private Map<EnvelopeFields,Field> envelopeWeights;
	private Set<Area> selectedAreas;
	private BoundingBox bb;
	
	
	public DistributionThread(ThreadGroup group,int jobId,int aquamapsId,String aquamapsName,
			Set<Area> selectedAreas,BoundingBox bb ) {
		super(group,"SAD_AquaMapObj:"+aquamapsName);
		this.aquamapsId=aquamapsId;
		this.aquamapsName=aquamapsName;
		this.jobId=jobId;
		this.selectedAreas=selectedAreas;
		this.bb=bb;
	}

	public void setRelatedSpeciesId(Species species, Map<String,Map<String,Perturbation>> envelopeCustomization, Map<String,Map<EnvelopeFields,Field>> envelopeWeights){
		selectedSpecies.add(species);
		this.envelopeCustomization=envelopeCustomization.get(species.getId());
		this.envelopeWeights=envelopeWeights.get(species.getId());
	}

	public void setGis(boolean gis){
		gisEnabled=gis;
	}

	public void run() {
		logger.trace(this.getName()+" started");
		
		try {
			Set<String> speciesId=new HashSet<String>();
			while(!JobManager.isSpeciesListReady(jobId, speciesId)){
				try{
					Thread.sleep(waitTime);
				}catch(InterruptedException e){}
				logger.trace("waiting for species to Be ready");
			}

			boolean hasCustomizations=JobManager.isSpeciesSetCustomized(jobId,speciesId);
			List<org.gcube.application.aquamaps.dataModel.enhanced.File> references=new ArrayList<org.gcube.application.aquamaps.dataModel.enhanced.File>();

			//*********************************** GENERATION

			logger.trace(this.getName()+" entering image generation phase");


			ResultSet rs=queryForProbabilities();
			SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Generating);
			String csvFile=null;
			if((ServiceContext.getContext().getPropertyAsBoolean(PropertiesConstants.GIS_MODE))&&(gisEnabled)){
				csvFile=generateCsvFile(rs);	
			}

			String header=jobId+"_"+aquamapsName+"_"+aquamapsId;
			String header_map = header+"_maps";
			StringBuilder[] csq_str;
			csq_str=JobUtils.clusterize(rs, 2, 1, 2,false);
			rs.close();
			session.close();

			if(csq_str==null) logger.trace(this.getName()+"Empty selection, nothing to render");
			else {

				///************************ PERL IMAGES GENERATION AND PUBBLICATION

				if(!JobUtils.createImages(aquamapsId,csq_str))
					throw new Exception("NO IMAGES GENERATED OR PUBLISHED");
				


				/// *************************** GIS GENERATION

				if((ServiceContext.getContext().getPropertyAsBoolean(PropertiesConstants.GIS_MODE))&&(gisEnabled)){

					Resource hcaf= new Resource (ResourceType.HCAF,SubmittedManager.getHCAFTableId(jobId));
					Resource hspen=new Resource (ResourceType.HSPEN,SubmittedManager.getHSPENTableId(jobId));
					ArrayList<String> layersId=new ArrayList<String>();
					ArrayList<String> layersUri=new ArrayList<String>();
					
					//TODO check algorithm
					PredictionLayerGenerationRequest request= new PredictionLayerGenerationRequest(aquamapsId,aquamapsName, selectedSpecies.iterator().next(), hcaf, hspen, 
							envelopeCustomization, envelopeWeights, selectedAreas, bb, csvFile, true);
					

					if(GeneratorManager.requestGeneration(request)){
						layersId.add(request.getGeneratedLayer());
						layersUri.add(request.getGeServerLayerId());
					}else {
						throw new Exception("Gis Generation returned false, request was "+request);
					}
					AquaMapsManager.updateGISReferences(aquamapsId, layersId, layersUri);
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



	private String generateCsvFile(ResultSet rs)throws Exception{
		String csvFile=ServiceContext.getContext().getPersistenceRoot()+File.separator+jobId+File.separator+aquamapsName+".csv";
		FileUtils.newFileUtils().createNewFile(new File(csvFile), true);
		GenerationUtils.ResultSetToCSVFile(rs, csvFile);
		return csvFile;
	}


	private ResultSet queryForProbabilities()throws Exception{
		String HSPECName=JobManager.getWorkingHSPEC(jobId);
		SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Simulating);
		session=DBSession.getInternalDBSession();

		String clusteringQuery=clusteringDistributionQuery(HSPECName);
		logger.trace("Gonna use query "+clusteringQuery);
		PreparedStatement ps= session.preparedStatement(clusteringQuery);
		ps.setString(1,selectedSpecies.iterator().next().getId());


		return ps.executeQuery();
	}


	public static String clusteringDistributionQuery(String hspecName){
		String query= "Select "+HCAF_SFields.csquarecode+", "+HSPECFields.probability+"  FROM "+hspecName+" where "+
		hspecName+"."+SpeciesOccursumFields.speciesid+"=?  ORDER BY "+HSPECFields.probability+" DESC";
		logger.trace("clusteringDistributionQuery: "+query);
		return query;
	}


}

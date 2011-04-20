package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
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
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BiodiversityThread extends Thread {	
	private static final GCUBELog logger=new GCUBELog(BiodiversityThread.class);
	private static final int waitTime=10*1000;
	

	private int aquamapsId;
	private int jobId;
	private String aquamapsName;
	private float threshold;
	private String HSPECName;	
	private Set<String> species=new HashSet<String>();
	private DBSession session;
	private boolean gisEnabled=false;
	private GCUBEScope actualScope;

	
	private Map<String,Map<String,Perturbation>> envelopeCustomization=new HashMap<String, Map<String,Perturbation>>();
	private Map<String,Map<EnvelopeFields,Field>> envelopeWeights= new HashMap<String, Map<EnvelopeFields,Field>>();
	private Set<Area> selectedAreas;
	private BoundingBox bb;

	public BiodiversityThread(ThreadGroup group,int jobId,int aquamapsId,String aquamapsName,float threshold,GCUBEScope scope,
			Set<Area> selectedAreas,BoundingBox bb) {
		super(group,"BioD_AquaMapObj:"+aquamapsName);	
		this.threshold=threshold;
		this.aquamapsId=aquamapsId;
		this.aquamapsName=aquamapsName;
		this.jobId=jobId;	
		logger.trace("Passed scope : "+scope.toString());
		this.actualScope=scope;
		this.selectedAreas=selectedAreas;
		this.bb=bb;
	}

	public void setRelatedSpeciesList(Set<Species> ids,Map<String,Map<String,Perturbation>> envelopeCustomization,
	Map<String,Map<EnvelopeFields,Field>> envelopeWeights){		
		for(Species s:ids){
			species.add(s.getId());
			this.envelopeCustomization.put(s.getId(), envelopeCustomization.get(s.getId()));
			this.envelopeWeights.put(s.getId(), envelopeWeights.get(s.getId()));
		}
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

			boolean hasCustomizations=JobManager.isSpeciesSetCustomized(jobId,species);
			
				//*********************************** GENERATION

				logger.trace(this.getName()+" entering image generation phase");

				session=DBSession.getInternalDBSession();
				SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Generating);
				String tableName=ServiceUtils.generateId("s", "");
				PreparedStatement prep=null;
				
				session.createTable(tableName, new String[]{
						SpeciesOccursumFields.speciesid+" varchar(50) PRIMARY KEY"
				});
				

				JobManager.addToDropTableList(jobId, tableName);	
				List<List<Field>> toInsertSpecies= new ArrayList<List<Field>>();
				for(String specId: species){
					List<Field> row=new ArrayList<Field>();
					row.add(new Field(SpeciesOccursumFields.speciesid+"",specId,FieldType.STRING));
					toInsertSpecies.add(row);
					;}
				session.insertOperation(tableName, toInsertSpecies);
				
				
				logger.trace(this.getName()+" species temp table filled, gonna select relevant HSPEC records");
				HSPECName=JobManager.getWorkingHSPEC(jobId);
				SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Simulating);
				prep=session.preparedStatement(clusteringBiodiversityQuery(HSPECName,tableName));				
				prep.setFloat(1,threshold);
				ResultSet rs=prep.executeQuery();
				SubmittedManager.updateStatus(aquamapsId, SubmittedStatus.Generating);
				List<org.gcube.application.aquamaps.dataModel.enhanced.File> references=new ArrayList<org.gcube.application.aquamaps.dataModel.enhanced.File>();
				
				if(rs.first()){
						//RS not empty
					String header=jobId+"_"+aquamapsName+"_"+aquamapsId;
					String header_map = header+"_maps";
					StringBuilder[] csq_str;
					csq_str=JobUtils.clusterize(rs, 2, 1, 2,true);
					rs.first();
					int maxValue=rs.getInt(2);
					rs.last();
					int minValue=rs.getInt(2);
//					String attributeName=rs.getMetaData().getColumnLabel(2);
					logger.trace(this.getName()+" Found minValue : "+minValue+"; maxValue : "+maxValue+" for AttributeName :"+AquaMapsManager.maxSpeciesCountInACell);
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
						
						references.addAll(JobUtils.createImages(aquamapsId, clusterFile, header_map, species, actualScope, hasCustomizations));
						
						/// *************************** GIS GENERATION

						if((ServiceContext.getContext().isGISMode())&&(gisEnabled)){
							logger.trace(this.getName()+"is gisEnabled");
							
							Resource hcaf= new Resource (ResourceType.HCAF,SubmittedManager.getHCAFTableId(jobId));
							Resource hspen=new Resource (ResourceType.HSPEN,SubmittedManager.getHSPENTableId(jobId));
							ArrayList<String> layersId=new ArrayList<String>();
							ArrayList<String> layersUri=new ArrayList<String>();

							PredictionLayerGenerationRequest request= new PredictionLayerGenerationRequest(aquamapsId,species,hcaf,hspen,envelopeCustomization,
									envelopeWeights,selectedAreas,bb,threshold,csvFile,aquamapsName,minValue,maxValue);
							if(!GeneratorManager.requestGeneration(request)){
								layersId.add(request.getGeneratedLayer());
								layersId.add(request.getGeServerLayerId());
							}else {
								throw new Exception("Gis Generation returned false, request was "+request);
							}
							AquaMapsManager.updateGISReferences(aquamapsId, layersId, layersUri);
						}

					}	
				}
				else{
					logger.trace("Query returned empty result set, nothing to generate");
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
		String query= "Select "+HCAF_SFields.csquarecode+", count("+hspecName+"."+SpeciesOccursumFields.speciesid+") AS "+AquaMapsManager.maxSpeciesCountInACell+" FROM "+hspecName+
		" INNER JOIN "+tmpTable+" ON "+hspecName+"."+SpeciesOccursumFields.speciesid+" = "+tmpTable+"."+SpeciesOccursumFields.speciesid+" where probability > ? GROUP BY "+HCAF_SFields.csquarecode+" ORDER BY MaxSpeciesCountInACell DESC";
		logger.trace("clusteringBiodiversityQuery: "+query);
		return query;
	}


}

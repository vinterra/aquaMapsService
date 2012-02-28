package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import java.awt.Image;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBConnectionParameters;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HSPECFields;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;

public class Analyzer {

	private static final GCUBELog logger=new GCUBELog(Analyzer.class);
	
	
	private BioClimateAnalysis bioClimate=null;
	
	private Integer internalId;
	
	
	public Analyzer(int i) {
		logger.trace("Created batch analyzer with ID "+internalId);
		internalId=i;
	}
	public EnvironmentalExecutionReportItem getReport(boolean getResources) {
		EnvironmentalExecutionReportItem toReturn=new EnvironmentalExecutionReportItem();
		toReturn.setPercent(new Double(bioClimate.getStatus()));
		return toReturn;
	}
	
	

	public void setConfiguration(String path, DBCredentialDescriptor credentials) throws Exception {
		logger.trace("***** SETTING ANALYZER GENERATOR CONFIGURATION (path : "+path+")");
		
		
		String user=credentials.getValue(DBConnectionParameters.user);
		String password=credentials.getValue(DBConnectionParameters.password);
		String url="jdbc:postgresql://"+credentials.getValue(DBConnectionParameters.host)+":"+
					credentials.getValue(DBConnectionParameters.port)+"/"+credentials.getValue(DBConnectionParameters.dbName);
		
		logger.trace("passed argument : user "+user);
		logger.trace("passed argument : password "+password);
		logger.trace("passed argument : url "+url);
		
		bioClimate=new BioClimateAnalysis(path, 
				ServiceContext.getContext().getFolderPath(FOLDERS.ANALYSIS),
				url, user, password, false);
		
	}
	
	public List<Image> produceImages(AnalysisRequest toPerform) throws Exception{
		switch(toPerform.getType()){
			case HCAF : bioClimate.hcafEvolutionAnalysis(toPerform.getHcafTables());
						break;
			case HSPEC : bioClimate.hspecEvolutionAnalysis(toPerform.getHspecTables(), HSPECFields.probability+"", HCAF_SFields.csquarecode+"");
							break;
			case MIXED : bioClimate.evolutionAnalysis(toPerform.getHcafTables(), toPerform.getHspecTables(), HSPECFields.probability+"", HCAF_SFields.csquarecode+"");
							break;
		}
		return bioClimate.getProducedImages();
	}
	
	
	public Integer getReportId() {
		return internalId;
	}
}

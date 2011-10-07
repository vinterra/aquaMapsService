package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.HashMap;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBConnectionParameters;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.ecomodelling.generators.aquamapsorg.MaxMinGenerator;
import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.EnvelopeModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.EnvelopeGenerator;
import org.gcube.common.core.utils.logging.GCUBELog;


public class BatchGenerator implements BatchGeneratorI {

	private static final GCUBELog logger=new GCUBELog(BatchGenerator.class);
	
	private EngineConfiguration e = new EngineConfiguration();
	
	private static final int NUM_OF_THREADS=2;
	
	private DistributionGenerator dg =null;
	private EnvelopeGenerator eg=null;
	private Integer internalId;
	
	
	public BatchGenerator(String path,DBCredentialDescriptor credentials) {
		setConfiguration(path, credentials);
	}
	
	
	@Override
	public String generateHSPECTable(String hcaf, String hspen,String filteredHSPEN,
			AlgorithmType type,Boolean iscloud,String endpoint) throws Exception {
		
		return generateHSPEC(hcaf, hspen, filteredHSPEN,
				type.equals(AlgorithmType.NativeRange)||type.equals(AlgorithmType.NativeRange2050),
				type.equals(AlgorithmType.SuitableRange2050)||type.equals(AlgorithmType.NativeRange2050), 
				NUM_OF_THREADS,
				"", "", "", new HashMap<String, String>(), GenerationModel.AQUAMAPS);
	}
	@Override
	public void setConfiguration(String path, DBCredentialDescriptor credentials) {
		logger.trace("***** SETTING BATCH GENERATOR CONFIGURATION (path : "+path+")");
		//path to the configuration directory
		e.setConfigPath(path);
		//remote db username (default defined in the configuration)
		
		e.setDatabaseUserName(credentials.getValue(DBConnectionParameters.user));
		logger.trace("user : "+credentials.getValue(DBConnectionParameters.user));
		//remote db password (default defined in the configuration)
		e.setDatabasePassword(credentials.getValue(DBConnectionParameters.password));
		logger.trace("user : "+credentials.getValue(DBConnectionParameters.password));
		//remote db URL (default defined in the configuration)
		String url= "jdbc:postgresql://"+credentials.getValue(DBConnectionParameters.host)+":"+
		credentials.getValue(DBConnectionParameters.port)+"/"+credentials.getValue(DBConnectionParameters.dbName);
		e.setDatabaseURL(url);
		//number of threads to use in the calculation
//		e.setNumberOfThreads(NUM_OF_THREADS);
		//create table if it doesn't exist
		e.setCreateTable(true);
		
		
		
		logger.trace("passed argument : user "+e.getDatabaseUserName());
		logger.trace("passed argument : password "+e.getDatabasePassword());
		logger.trace("passed argument : url "+e.getDatabaseURL());
		logger.trace("passed argument : threads num "+e.getNumberOfThreads());
	}
	public BatchGenerator(Integer internalId) {
		logger.trace("Created batch generator with ID "+internalId);
		this.internalId=internalId;
	}


	@Override
	public EnvironmentalExecutionReportItem getReport(boolean getResourceInfo) {
//		logger.trace("Forming report, my ID is "+getReportId());
//		logger.trace("DistributionGenerator = "+dg);
		EnvironmentalExecutionReportItem toReturn=null;
		if(dg!=null){
			toReturn= new EnvironmentalExecutionReportItem();
			toReturn.setPercent(dg.getStatus());
			if(getResourceInfo){
				toReturn.setResourceLoad(dg.getResourceLoad());
				toReturn.setResourcesMap(dg.getResources());
				toReturn.setElaboratedSpecies(dg.getSpeciesLoad());
			}
		}else if(eg!=null){
			toReturn= new EnvironmentalExecutionReportItem();
			toReturn.setPercent(eg.getStatus());
			if(getResourceInfo){
				toReturn.setResourceLoad(eg.getResourceLoad());
				toReturn.setResourcesMap(eg.getResources());
				toReturn.setElaboratedSpecies(eg.getSpeciesLoad());
			}
		}

		
		return toReturn;
	}


	@Override
	public int getReportId() {
		return internalId;
	}
	
	
	@Override
	public String generateTable(TableGenerationConfiguration configuration)
			throws Exception {
		if(configuration.getLogic().equals(LogicType.HSPEC))
		return generateHSPEC(configuration.getSources().get(ResourceType.HCAF).getTableName(),
				configuration.getSources().get(ResourceType.HSPEN).getTableName(),
				configuration.getMaxMinHspenTable(),
				configuration.getAlgorithm().equals(AlgorithmType.NativeRange)||configuration.getAlgorithm().equals(AlgorithmType.NativeRange2050),
				configuration.getAlgorithm().equals(AlgorithmType.SuitableRange2050)||configuration.getAlgorithm().equals(AlgorithmType.NativeRange2050),
				configuration.getPartitionsNumber(),
				configuration.getBackendUrl(),
				configuration.getAuthor(),
				configuration.getExecutionEnvironment(),
				configuration.getConfiguration(),
				configuration.getSubmissionBackend().equalsIgnoreCase(ServiceContext.getContext().getName())?GenerationModel.AQUAMAPS:GenerationModel.REMOTE_AQUAMAPS);
		else return generateHSPEN(configuration.getSources().get(ResourceType.HCAF).getTableName(),
				configuration.getSources().get(ResourceType.HSPEN).getTableName(),
				configuration.getSources().get(ResourceType.OCCURRENCECELLS).getTableName(),
				configuration.getPartitionsNumber(),
				configuration.getBackendUrl(),
				configuration.getAuthor(),
				configuration.getExecutionEnvironment(),
				configuration.getConfiguration(),
				EnvelopeModel.AQUAMAPS);
	}
	
	
	private String generateHSPEC(String hcafTable, String hspenTable,String maxMinHspen,boolean isNative,boolean is2050,int threadNum,
			String calculatorUrl,String calculationUser,String executioneEnvironment,HashMap<String,String> calculationConfig,GenerationModel model)throws Exception{
		
		String toGenerate=ServiceUtils.generateId("hspec", "");

		logger.trace("generating hspec : "+toGenerate);
		
		logger.trace("hspen : "+hspenTable);
		logger.trace("hcaf : "+hcafTable);
		logger.trace("native : "+isNative);
		logger.trace("2050 : "+is2050);
		logger.trace("thread N : "+threadNum);
		logger.trace("url : "+calculatorUrl);
		logger.trace("calculation user : "+calculationUser);
		logger.trace("model : "+model);
		logger.trace("environment : "+executioneEnvironment);
		logger.trace("config values : "+calculationConfig.size());
		
		
		
		
		//hspen reference table
		e.setHspenTable(hspenTable);
		//hcaf reference table
		e.setHcafTable(hcafTable);
		//output table - created if the CreateTable flag is true
		e.setDistributionTable(toGenerate);
		//native generation flag set to false - default value
		e.setNativeGeneration(isNative);
		//2050 generation flag set to false - default value
		e.setType2050(is2050);
		
		
		e.setMaxminLatTable(maxMinHspen);
		
		e.setGenerator(model);
		e.setRemoteCalculator(calculatorUrl);
		e.setServiceUserName(calculationUser);
		
		e.setRemoteEnvironment(executioneEnvironment);
		e.setNumberOfThreads(threadNum);
		e.setGeneralProperties(calculationConfig);
		e.setGenerator(model);
		
		
		dg= new DistributionGenerator(e);
		//calculation
		dg.generateHSPEC();
		
		return toGenerate;
	}
	
	private String generateHSPEN(String hcafTable, String hspenTable,String occurrenceCellsTable, int threadNum,
			String calculatorUrl,String calculationUser,String executioneEnvironment,HashMap<String,String> calculationConfig,EnvelopeModel model)throws Exception{
		
		
		String toGenerate=ServiceUtils.generateId("hspen", "");

		logger.trace("generating hspen : "+toGenerate);
		
		logger.trace("hspen : "+hspenTable);
		logger.trace("hcaf : "+hcafTable);
		
		logger.trace("thread N : "+threadNum);
		logger.trace("url : "+calculatorUrl);
		logger.trace("calculation user : "+calculationUser);
		logger.trace("model : "+model);
		logger.trace("environment : "+executioneEnvironment);
		logger.trace("config values : "+calculationConfig.size());
		
		
		
		
		//hspen reference table
		e.setOriginHspenTable(hspenTable);
		
		e.setHspenTable(toGenerate);
		//hcaf reference table
		e.setHcafTable(hcafTable);
		
		e.setOccurrenceCellsTable(occurrenceCellsTable);
		e.setEnvelopeGenerator(model);
		
		
		e.setRemoteCalculator(calculatorUrl);
		e.setServiceUserName(calculationUser);
		
		e.setRemoteEnvironment(executioneEnvironment);
		e.setNumberOfThreads(threadNum);
		e.setGeneralProperties(calculationConfig);
		
		
		
		eg=new EnvelopeGenerator(e);
		
		eg.reGenerateEnvelopes();
		
		logger.trace("Generating Max Min table..");
		
		MaxMinGenerator maxmin = new MaxMinGenerator(e);
		maxmin.populatemaxminlat(toGenerate);
		
		return toGenerate;
	}
	
}

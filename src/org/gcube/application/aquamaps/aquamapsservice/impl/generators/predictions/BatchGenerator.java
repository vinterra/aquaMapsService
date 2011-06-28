package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBConnectionParameters;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.enhanced.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.ecomodelling.generators.configuration.EngineConfiguration;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.processing.DistributionGenerator;
import org.gcube.common.core.utils.logging.GCUBELog;

public class BatchGenerator implements BatchGeneratorI {

	private static final GCUBELog logger=new GCUBELog(BatchGenerator.class);
	
	private EngineConfiguration e = new EngineConfiguration();
	
	private static final int NUM_OF_THREADS=2;
	
	private DistributionGenerator dg =null;
	private Integer internalId;
	
	
	public BatchGenerator(String path,DBCredentialDescriptor credentials) {
		setConfiguration(path, credentials);
	}
	
	
	@Override
	public String generateHSPECTable(String hcaf, String hspen,
			AlgorithmType type,Boolean iscloud,String endpoint,Integer resourceNumber) throws Exception {
		
		String toGenerate=ServiceUtils.generateId("hspec", "");

		logger.trace("generating hspec : "+toGenerate);
		logger.trace("hspen : "+hspen);
		logger.trace("hcaf : "+hcaf);
		logger.trace("algorithm : "+type);
		logger.trace("cloud : "+iscloud);
		logger.trace("endpoint : "+endpoint);
		logger.trace("resource number : "+resourceNumber);
		
		
		//hspen reference table
		e.setHspenTable(hspen);
		//hcaf reference table
		e.setHcafTable(hcaf);
		//output table - created if the CreateTable flag is true
		e.setDistributionTable(toGenerate);
		//native generation flag set to false - default value
		e.setNativeGeneration(type.equals(AlgorithmType.NativeRange)||type.equals(AlgorithmType.NativeRange2050));
		//2050 generation flag set to false - default value
		e.setType2050(type.equals(AlgorithmType.NativeRange2050)||type.equals(AlgorithmType.SuitableRange2050));
		
		e.setGenerator(iscloud?GenerationModel.REMOTE_AQUAMAPS:GenerationModel.AQUAMAPS);
		if(iscloud)e.setRemoteCalculator(endpoint);
		
		e.setNumberOfThreads(resourceNumber);
		
		dg= new DistributionGenerator(e);
		//calculation
		dg.generateHSPEC();
		
		return toGenerate;
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
	public EnvironmentalExecutionReportItem getReport() {
//		logger.trace("Forming report, my ID is "+getReportId());
//		logger.trace("DistributionGenerator = "+dg);
		if(dg==null) return null;
		EnvironmentalExecutionReportItem toReturn= new EnvironmentalExecutionReportItem();
		toReturn.setPercent(dg.getStatus());
		toReturn.setResourceLoad(dg.getResourceLoad());
		toReturn.setResourcesMap(dg.getResources());
		toReturn.setElaboratedSpecies(dg.getSpeciesLoad());
		return toReturn;
	}


	@Override
	public int getReportId() {
		return internalId;
	}
	
	
}

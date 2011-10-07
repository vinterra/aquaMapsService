package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;

public interface BatchGeneratorI {
	
	@Deprecated
	public String generateHSPECTable(String hcaf, String hspen,String filteredHSPEN, AlgorithmType type,Boolean isCloud,String endpoint)throws Exception;
	public void setConfiguration(String path,DBCredentialDescriptor credentials);
	public EnvironmentalExecutionReportItem getReport(boolean getResourceInfo);
	public int getReportId();
	
	public String generateTable(TableGenerationConfiguration config)throws Exception;
}

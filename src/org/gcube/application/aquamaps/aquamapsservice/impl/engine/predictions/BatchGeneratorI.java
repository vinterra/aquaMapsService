package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;

public interface BatchGeneratorI {
	
	@Deprecated
	public String generateHSPECTable(String hcaf, String hspen,String filteredHSPEN, AlgorithmType type,Boolean isCloud,String endpoint)throws Exception;
	public void setConfiguration(String path,DBCredentialDescriptor credentials);
	public EnvironmentalExecutionReportItem getReport(boolean getResourceInfo);
	public int getReportId();
	
	public void generateTable(TableGenerationConfiguration config)throws Exception;
}

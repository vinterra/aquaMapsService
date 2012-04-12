package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.enabling.model.DBDescriptor;

public interface BatchGeneratorI {
	
	@Deprecated
	public String generateHSPECTable(String hcaf, String hspen,String filteredHSPEN, AlgorithmType type,Boolean isCloud,String endpoint)throws Exception;
	public void setConfiguration(String path,DBDescriptor credentials);
	public EnvironmentalExecutionReportItem getReport(boolean getResourceInfo);
	public int getReportId();
	
	public void generateTable(TableGenerationConfiguration config)throws Exception;
}

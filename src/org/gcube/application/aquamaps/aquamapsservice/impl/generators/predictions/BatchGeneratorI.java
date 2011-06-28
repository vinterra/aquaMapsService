package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.enhanced.EnvironmentalExecutionReportItem;

public interface BatchGeneratorI {

	public String generateHSPECTable(String hcaf, String hspen, AlgorithmType type,Boolean isCloud,String endpoint,Integer resourceNumber)throws Exception;
	public void setConfiguration(String path,DBCredentialDescriptor credentials);
	public EnvironmentalExecutionReportItem getReport();
	public int getReportId();
}

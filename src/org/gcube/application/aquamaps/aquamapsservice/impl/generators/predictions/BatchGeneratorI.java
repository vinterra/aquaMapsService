package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCredentialDescriptor;
import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;

public interface BatchGeneratorI {

	public String generateHSPECTable(String hcaf, String hspen, AlgorithmType type,boolean isCloud)throws Exception;
	public void setConfiguration(String path,DBCredentialDescriptor credentials);
	public Report getReport();
	public int getReportId();
}

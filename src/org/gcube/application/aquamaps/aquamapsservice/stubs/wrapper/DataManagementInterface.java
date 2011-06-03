package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.enhanced.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.enhanced.HSPECGroupGenerationRequest;

public interface DataManagementInterface {

	public String submitRequest(HSPECGroupGenerationRequest request)throws Exception;
	public EnvironmentalExecutionReportItem getReport(Integer reportId) throws Exception;
	
}

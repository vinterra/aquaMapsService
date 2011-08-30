package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.List;

import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.environments.HSPECGroupGenerationRequest;

public interface DataManagementInterface {

	public String submitRequest(HSPECGroupGenerationRequest request)throws Exception;
	public EnvironmentalExecutionReportItem getReport(Integer reportId) throws Exception;
	public String getJSONSPECGroupGenreationRequests(PagedRequestSettings settings) throws Exception;
	public Integer generateMaps(String author,boolean enableGIS,Integer hspecId,List<Field> speciesFilter)throws Exception;
	public HSPECGroupGenerationRequest getRequest(String id) throws Exception;
	public String removeRequest(String id, boolean deleteData,boolean  deleteJobs)throws Exception;
	public void editRequest(HSPECGroupGenerationRequest requestDetails) throws Exception;
	public List<Field> getDefaultSources()throws Exception;
	public Resource updateResource(Resource toUpdate)throws Exception;
}

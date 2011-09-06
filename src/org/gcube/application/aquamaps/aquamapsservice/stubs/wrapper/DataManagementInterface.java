package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.io.File;
import java.util.List;

import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;

public interface DataManagementInterface {

	public String submitRequest(SourceGenerationRequest request)throws Exception;
	public EnvironmentalExecutionReportItem getReport(Integer reportId) throws Exception;
	public String getJSONSPECGroupGenreationRequests(PagedRequestSettings settings) throws Exception;
	public Integer generateMaps(String author,boolean enableGIS,Integer hspecId,List<Field> speciesFilter)throws Exception;
	public SourceGenerationRequest getRequest(String id) throws Exception;
	public String removeRequest(String id, boolean deleteData,boolean  deleteJobs)throws Exception;
	public void editRequest(SourceGenerationRequest requestDetails) throws Exception;
	public List<Field> getDefaultSources()throws Exception;
	public Resource updateResource(Resource toUpdate)throws Exception;
	public void deleteResource(int resourceId)throws Exception;
	public File exportResource(int resourceId)throws Exception;
	public String queryResource(String query, PagedRequestSettings settings)throws Exception;
}
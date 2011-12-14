package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.io.File;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;

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
	public void setCustomQuery(String userId,String queryString)throws Exception;
	public Boolean deleteCustomQuery(String userId)throws Exception;
	public String viewCustomQuery(String userId,PagedRequestSettings settings)throws Exception;
	public Integer importResource(File toImport, String userId,ResourceType type,String encoding, boolean[] fieldsMask, boolean hasHeader,char delimiter) throws Exception;
	public Integer getImportStatus(Integer resourceId)throws Exception;
	public List<Field> getCustomQueryFields(String userId)throws Exception;
	public File exportTableAsCSV(String table)throws Exception;
}

package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateMapsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedHSPECRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ImportResourceRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.SetUserCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ViewCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.DataManagementServiceAddressingLocator;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.types.VOID;

public class DataManagementCall extends AquaMapsCall implements DataManagementInterface {

	public static DataManagementInterface getWrapper(GCUBEScope scope, GCUBESecurityManager[] securityManager,String defaultURI)throws Exception{
		return new DataManagementCall(scope, securityManager,defaultURI);
	}
	
	/**
	 * Creates a call with a disabled security manager  
	 * 
	 * @param scope
	 * @param defaultURI
	 * @return
	 * @throws Exception
	 */
	public static DataManagementInterface getCall(GCUBEScope scope, String defaultURI)throws Exception{
		GCUBESecurityManager secMan= new GCUBESecurityManagerImpl(){

			@Override
			public boolean isSecurityEnabled() {
				return false;
			}
			
		};
		return new DataManagementCall(scope, new GCUBESecurityManager[]{secMan},defaultURI);
	}
	


	private DataManagementPortType pt;

	private DataManagementCall(GCUBEScope scope, GCUBESecurityManager[] securityManager,String defaultURI)throws Exception {
		super(scope,securityManager,defaultURI);
		pt=GCUBERemotePortTypeContext.getProxy(new DataManagementServiceAddressingLocator().getDataManagementPortTypePort(epr), scope, 120000, securityManager);
	}

	
	@Override
	protected String getPortTypeName() {
		return Constant.DATAMANAGEMENT_PT_NAME;
	}
	
	
	///****** INTERFACE IMPLEMENTATION
	
	@Override
	public String submitRequest(SourceGenerationRequest request)
			throws Exception {
		try{
//			SourceGenerationRequestType stubRequest=new SourceGenerationRequestType();
//			stubRequest.setAlgorithms(CSVUtils.listToCSV(request.getAlgorithms()));
//			stubRequest.setAuthor(request.getAuthor());
//			stubRequest.setGenerationName(request.getGenerationname());
//			stubRequest.setDescription(request.getDescription());
//			
//			stubRequest.setHcafSearchId(request.getHcafsearchid());
//			stubRequest.setHspenSearchId(request.getHspensearchid());
//			stubRequest.setSubmissionBackend(request.getSubmissionBackend());
//			stubRequest.setExecutionEnvironment(request.getExecutionEnvironment());
//			stubRequest.setBackendUrl(request.getBackendURL());
//			stubRequest.setEnvironmentConfiguration(AquaMapsXStream.getXMLInstance().toXML(request.getEnvironmentConfiguration()));
//			stubRequest.setLogic(request.getLogic()+"");
//			stubRequest.setNumPartitions(request.getNumPartitions());
//			stubRequest.setAlgorithms(CSVUtils.listToCSV(request.getAlgorithms()));
//			
//			stubRequest.setEnableImageGeneration(request.getEnableimagegeneration());
//			stubRequest.setEnableLayerGeneration(request.getEnablelayergeneration());
			return pt.generateHSPECGroup(request.toStubsVersion());
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public EnvironmentalExecutionReportItem getReport(Integer reportId)
			throws Exception {
		try{
			GetGenerationLiveReportResponseType report= pt.getGenerationLiveReportGroup(reportId);
			return new EnvironmentalExecutionReportItem(report.getPercent(), report.getResourceLoad(), report.getResourceMap(), report.getElaboratedSpecies());
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	

	@Override
	public String getJSONSPECGroupGenreationRequests(
			PagedRequestSettings settings) throws Exception {
		try{
			GetJSONSubmittedHSPECRequestType request=new GetJSONSubmittedHSPECRequestType();
			request.setLimit(settings.getLimit());
			request.setOffset(settings.getOffset());
			request.setSortColumn(settings.getOrderColumn());
			request.setSortDirection(settings.getOrderDirection()+"");
			return pt.getJSONSubmittedHSPECGroup(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public Integer generateMaps(String author,boolean enableGIS,Integer hspecId,List<Field> speciesFilter) throws Exception {
		try{
			GenerateMapsRequestType request= new GenerateMapsRequestType();
			request.setAuthor(author);
			request.setGenerateLayers(enableGIS);
			request.setHSPECId(hspecId);
			request.setSpeciesFilter(Field.toStubsVersion(speciesFilter));
			return pt.generateMaps(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public SourceGenerationRequest getRequest(String id) throws Exception {
		try{
			throw new GCUBEFault("not Ready");
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public String removeRequest(String id, boolean deleteData,
			boolean deleteJobs) throws Exception {
		try{
			RemoveHSPECGroupGenerationRequestResponseType request=new RemoveHSPECGroupGenerationRequestResponseType();
			request.setRequestId(id);
			request.setRemoveTables(deleteData);
			request.setRemoveJobs(deleteJobs);
			pt.removeHSPECGroup(request);
			return "Done";
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public void editRequest(SourceGenerationRequest request)
			throws Exception {
		try{
			throw new GCUBEFault("Not Ymplemented");
//			pt.editHSPECGroupDetails(stubRequest);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public List<Field> getDefaultSources() throws Exception {
		try{
			return Field.load(pt.getDefaultSources(new VOID()));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public Resource updateResource(Resource toUpdate) throws Exception {
		try{
			return new Resource(pt.editResource(toUpdate.toStubsVersion()));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public void deleteResource(int resourceId) throws Exception {
		try{
			pt.removeResource(resourceId);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public File exportResource(int resourceId) throws Exception {
		try{
			String locator=pt.exportResource(resourceId);						
			return RSWrapper.getStreamFromLocator(new URI(locator));			
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public Boolean deleteCustomQuery(String userId) throws Exception {
		try{									
			return pt.deleteCustomQuery(userId);			
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
	
	@Override
	public void setCustomQuery(String userId, String queryString)
			throws Exception {
		try{
			pt.setCustomQuery(new SetUserCustomQueryRequestType(queryString, userId));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
	
	@Override
	public String viewCustomQuery(String userId, PagedRequestSettings settings)
			throws Exception {
		try{									
			return pt.viewCustomQuery(new ViewCustomQueryRequestType(settings.getLimit(), 
					settings.getOffset(), settings.getOrderColumn(), settings.getOrderDirection()+"", userId));			
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
	
	
	@Override
	public Integer getImportStatus(Integer resourceId) throws Exception {
		try{									
			return pt.getImportStatus(resourceId);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
	
	@Override
	public Integer importResource(File toImport, String userId,
			ResourceType type) throws Exception {
		try{
			logger.trace("Caller scope is "+scope);
			RSWrapper wrapper=new RSWrapper(scope);
			wrapper.add(toImport);
			String locator = wrapper.getLocator().toString();
			logger.trace("Added file to locator "+locator);
			return pt.importResource(new ImportResourceRequestType(locator, type+"", userId));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
	
	@Override
	public List<Field> getCustomQueryFields(String userId) throws Exception {
		try{									
			return Field.load(pt.isCustomQueryReady(userId));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
}

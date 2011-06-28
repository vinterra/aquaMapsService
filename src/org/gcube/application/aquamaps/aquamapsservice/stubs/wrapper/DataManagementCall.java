package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateMapsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedHSPECRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.DataManagementServiceAddressingLocator;
import org.gcube.application.aquamaps.dataModel.enhanced.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;

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
	public String submitRequest(HSPECGroupGenerationRequest request)
			throws Exception {
		try{
			HspecGroupGenerationRequestType stubRequest=new HspecGroupGenerationRequestType();
			stubRequest.setAlgorithms(CSVUtils.listToCSV(request.getAlgorithms()));
			stubRequest.setAuthor(request.getAuthor());
			stubRequest.setCloud(request.getIsCloud());
			stubRequest.setDescription(request.getDescription());
			stubRequest.setEnableImageGeneration(request.getEnableimagegeneration());
			stubRequest.setEnableLayerGeneration(request.getEnablelayergeneration());
			stubRequest.setGenerationName(request.getGenerationname());
			stubRequest.setHcafSearchId(request.getHcafsearchid());
			stubRequest.setHspenSearchId(request.getHspensearchid());
			stubRequest.setBackend(request.getBackend());
			stubRequest.setEndpointUrl(request.getBackend_url());
			stubRequest.setResourceNumber(request.getResources());
			return pt.generateHSPECGroup(stubRequest);
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
			request.setSortDirection(settings.getOrderDirection());
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
	public HSPECGroupGenerationRequest getRequest(String id) throws Exception {
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
	public void editRequest(HSPECGroupGenerationRequest request)
			throws Exception {
		try{
			HspecGroupGenerationRequestType stubRequest=new HspecGroupGenerationRequestType();
			stubRequest.setAlgorithms(CSVUtils.listToCSV(request.getAlgorithms()));
			stubRequest.setAuthor(request.getAuthor());
			stubRequest.setCloud(request.getIsCloud());
			stubRequest.setDescription(request.getDescription());
			stubRequest.setEnableImageGeneration(request.getEnableimagegeneration());
			stubRequest.setEnableLayerGeneration(request.getEnablelayergeneration());
			stubRequest.setGenerationName(request.getGenerationname());
			stubRequest.setHcafSearchId(request.getHcafsearchid());
			stubRequest.setHspenSearchId(request.getHspensearchid());
			stubRequest.setBackend(request.getBackend());
			stubRequest.setEndpointUrl(request.getBackend_url());
			stubRequest.setResourceNumber(request.getResources());
			pt.editHSPECGroupDetails(stubRequest);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}
}

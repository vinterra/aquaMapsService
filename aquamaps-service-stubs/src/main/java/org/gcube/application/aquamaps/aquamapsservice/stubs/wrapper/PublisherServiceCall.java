package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.PublisherServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RetrieveMapsByCoverageRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.PublisherServiceAddressingLocator;
import org.gcube.application.aquamaps.datamodel.FieldArray;
import org.gcube.application.aquamaps.datamodel.Map;
import org.gcube.application.aquamaps.datamodel.MapArray;
import org.gcube.application.aquamaps.datamodel.PagedRequestSettings;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;
import org.gcube.common.gis.datamodel.utils.Utils;

public class PublisherServiceCall extends AquaMapsCall implements
		PublisherInterface {

	public static PublisherInterface getWrapper(GCUBEScope scope, GCUBESecurityManager[] securityManager,String defaultURI,boolean queryIS)throws Exception{
		return new PublisherServiceCall(scope, securityManager,defaultURI,queryIS);
	}
	
	/**
	 * Creates a call with a disabled security manager  
	 * 
	 * @param scope
	 * @param defaultURI
	 * @return
	 * @throws Exception
	 */
	public static PublisherInterface getCall(GCUBEScope scope, String defaultURI,boolean queryIS)throws Exception{
		GCUBESecurityManager secMan= new GCUBESecurityManagerImpl(){

			@Override
			public boolean isSecurityEnabled() {
				return false;
			}
			
		};
		return new PublisherServiceCall(scope, new GCUBESecurityManager[]{secMan},defaultURI,queryIS);
	}
	
	
	private PublisherServicePortType pt;

	private PublisherServiceCall(GCUBEScope scope, GCUBESecurityManager[] securityManager,String defaultURI,boolean queryIS)throws Exception {
		super(scope,securityManager,defaultURI,queryIS);
		pt=GCUBERemotePortTypeContext.getProxy(new PublisherServiceAddressingLocator().getPublisherServicePortTypePort(epr), scope, 120000, securityManager);
	}
	
	

	@Override
	protected String getPortTypeName() {
		return Constant.PUBLISHERSERVICE_PT_NAME;
	}

	//// ********************** INTERFACE IMPLEMENTATION
	
	@Override
	public List<AquaMap> getMapsBySpecies(String[] speciesIds, boolean includeGis, boolean includeCustom, List<Resource> resources) throws Exception {
		try{
			RetrieveMapsByCoverageRequestType request=new RetrieveMapsByCoverageRequestType();
			request.setIncludeCustomMaps(includeCustom);
			request.setIncludeGisLayers(includeGis);
			request.setResourceList(Resource.toStubsVersion(resources));
			request.setSpeciesList(new StringArray(speciesIds));
			return AquaMap.load(pt.retrieveMapsByCoverage(request));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public String getJsonSubmittedByFilters(List<Field> filters,
			PagedRequestSettings settings) throws Exception {
		try{
			GetJSONSubmittedByFiltersRequestType request=new GetJSONSubmittedByFiltersRequestType();
			request.setFilters(Field.toStubsVersion(filters));
			request.setSettings(settings);
			return pt.getJSONSubmittedByFilters(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public List<File> getFileSetById(String fileSetId) throws Exception {
		try{			
			return File.load(pt.getFileSetById(fileSetId));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public LayerInfo getLayerById(String layerId) throws Exception {
		try{			
			return new LayerInfo(pt.getLayerById(layerId));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public List<LayerInfo> getLayersByCoverage(Resource source,
			String parameters) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<File> getFileSetsByCoverage(Resource source, String parameters)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}

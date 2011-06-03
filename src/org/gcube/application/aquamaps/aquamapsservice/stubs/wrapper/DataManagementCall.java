package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import org.gcube.application.aquamaps.aquamapsservice.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.DataManagementServiceAddressingLocator;
import org.gcube.application.aquamaps.dataModel.enhanced.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.enhanced.HSPECGroupGenerationRequest;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
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
	public String submitRequest(HSPECGroupGenerationRequest request)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnvironmentalExecutionReportItem getReport(Integer reportId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getPortTypeName() {
		return Constant.DATAMANAGEMENT_PT_NAME;
	}
}

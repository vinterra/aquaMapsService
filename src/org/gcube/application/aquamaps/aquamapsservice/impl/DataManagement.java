package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.SourceGenerationThread;
import org.gcube.application.aquamaps.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.stubs.GenerateHCAFRequestType;
import org.gcube.application.aquamaps.stubs.GetHCAFgenerationReportRequestType;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;

public class DataManagement extends GCUBEPortType implements DataManagementPortType{

	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	public VOID generateHCAF(GenerateHCAFRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			logger.trace("Submitting request for "+arg0.getResultingHCAFName()+" generation submitted by "+arg0.getUserId());
			int id=SourceGenerationManager.insertHCAFRequest(arg0.getUserId(),Integer.parseInt(arg0.getSourceHCAFId()), arg0.getResultingHCAFName(), arg0.getUrls().getItems());
			logger.trace("Inserted request with id : "+id);
			SourceGenerationThread t=new SourceGenerationThread(id);
			ThreadManager.getExecutor().execute(t);
			return new VOID();
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Unexpected error");
		}
	}

	public String getHCAFGenerationReport(
			GetHCAFgenerationReportRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			 return SourceGenerationManager.getReport(arg0.getSortColumn(), arg0.getSortDirection(), arg0.getOffset(), arg0.getLimit());			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Unexpected error");
		}
		 
	}

	
	
}

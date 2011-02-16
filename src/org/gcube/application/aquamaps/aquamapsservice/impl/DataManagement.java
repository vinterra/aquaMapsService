package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.HSPECGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.SourceGenerationThread;
import org.gcube.application.aquamaps.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.stubs.GenerateHCAFRequestType;
import org.gcube.application.aquamaps.stubs.GenerateHSPECRequestType;
import org.gcube.application.aquamaps.stubs.GetGenerationReportByTypeRequestType;
import org.gcube.application.aquamaps.stubs.GetHCAFgenerationReportRequestType;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
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
			String[] sources=arg0.getUrls().getItems();
			for(String s:sources) logger.trace("found source : "+s);
			int id=SourceGenerationManager.insertHCAFRequest(arg0.getUserId(),Integer.parseInt(arg0.getSourceHCAFId()), arg0.getResultingHCAFName(), sources);
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

	public VOID generateHSPEC(GenerateHSPECRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			String hcaf=SourceManager.getSourceName(ResourceType.HCAF, arg0.getSourceHCAFId());
			String hspen=SourceManager.getSourceName(ResourceType.HSPEN, arg0.getSourceHSPENId());
			
			//TODO ASynchronous generation
			if(arg0.getSpeciesSelection()!=null && arg0.getSpeciesSelection().getItems().length>0){
				Set<Species> toInsert=new HashSet<Species>();
				for(String id: arg0.getSpeciesSelection().getItems())
					toInsert.add(new Species(id));
				hspen=SpeciesManager.getFilteredHSPEN(hspen, toInsert);
			}
			
			HSPECGenerator generator= new HSPECGenerator(hcaf,hspen,arg0.isGenerateNative(),arg0.isGenerateSuitable());	
			
			generator.generate();
			if(arg0.isGenerateNative()) logger.trace("generated Native Table "+generator.getNativeTable());
			if(arg0.isGenerateSuitable())logger.trace("generated Suitable Table"+generator.getSuitableTable());
			
			
			if(arg0.getSpeciesSelection()!=null && arg0.getSpeciesSelection().getItems().length>0){
				logger.trace("Dropping temp hspen table");
				DBSession.getInternalDBSession().dropTable(hspen);
			}
		return new VOID();
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Unexpected error");
		}
	}

	public String getGenerationReportByType(
			GetGenerationReportByTypeRequestType arg0) throws RemoteException,
			GCUBEFault {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}

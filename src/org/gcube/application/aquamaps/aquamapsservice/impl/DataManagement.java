package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.HSPECGroupGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.BatchGeneratorObjectFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables.TableGenerationExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateHCAFRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateHSPECRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GenerateMapsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetGenerationLiveReportResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetGenerationReportByTypeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetHCAFgenerationReportRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedHSPECRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.dataModel.FieldArray;
import org.gcube.application.aquamaps.dataModel.Resource;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.environments.HSPECGroupGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.GroupGenerationRequestFields;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
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
			throw new GCUBEFault("DISCONTINUED IMPLEMENTATION");
			//			logger.trace("Submitting request for "+arg0.getResultingHCAFName()+" generation submitted by "+arg0.getUserId());
			//			String[] sources=arg0.getUrls().getItems();
			//			for(String s:sources) logger.trace("found source : "+s);
			//			int id=SourceGenerationManager.insertHCAFRequest(arg0.getUserId(),Integer.parseInt(arg0.getSourceHCAFId()), arg0.getResultingHCAFName(), sources);
			//			logger.trace("Inserted request with id : "+id);
			//			return new VOID();
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Unexpected error");
		}
	}

	public String getHCAFGenerationReport(
			GetHCAFgenerationReportRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			return SourceGenerationManager.getReport(new PagedRequestSettings(arg0.getLimit(), arg0.getOffset(), arg0.getSortColumn(), PagedRequestSettings.OrderDirection.valueOf(arg0.getSortDirection())));			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Unexpected error");
		}

	}

	public VOID generateHSPEC(GenerateHSPECRequestType arg0)
	throws RemoteException, GCUBEFault {
		try{
			throw new Exception ("OBSOLETE METHOD");


			//			String hcaf=SourceManager.getSourceName(ResourceType.HCAF, arg0.getSourceHCAFId());
			//			String hspen=SourceManager.getSourceName(ResourceType.HSPEN, arg0.getSourceHSPENId());
			//
			//			//TODO ASynchronous generation
			//			if(arg0.getSpeciesSelection()!=null && arg0.getSpeciesSelection().getItems().length>0){
			//				Set<Species> toInsert=new HashSet<Species>();
			//				for(String id: arg0.getSpeciesSelection().getItems())
			//					toInsert.add(new Species(id));
			//				hspen=SpeciesManager.getFilteredHSPEN(hspen, toInsert);
			//			}
			//
			//			HSPECGenerator generator= new HSPECGenerator(hcaf,hspen,arg0.isGenerateNative(),arg0.isGenerateSuitable());	
			//
			//			generator.setEnableProbabilitiesLog(arg0.isEnableLog());
			//			
			//			generator.generate();
			//			if(arg0.isGenerateNative()){
			//				String generatedNative=generator.getNativeTable();
			//				try{
			//					logger.trace("generated Native Table "+generatedNative);
			//					String nativeTitle=arg0.getToGeneratePrefix().toLowerCase()+"_native";
			//					//FIXME Registration
			////					int id=SourceManager.registerSource(ResourceType.HSPEC, generatedNative, "GENERATED", arg0.getUserId(), arg0.getSourceHCAFId(), ResourceType.HCAF);
			////					SourceManager.setTableTitle(ResourceType.HSPEC, id, nativeTitle);
			//				}catch(Exception e){
			//					logger.error("UNABLE TO REGISTER GENERATED HSPEC NATIVE table name : "+generatedNative,e);
			//				}
			//			}
			//			if(arg0.isGenerateSuitable()){
			//				String generatedSuitable=generator.getSuitableTable();
			//				try{
			//					logger.trace("generated Suitable Table "+generatedSuitable);
			//					String suitableTitle=arg0.getToGeneratePrefix().toLowerCase()+"suitable";
			//					//FIXME Registration
			////					int id=SourceManager.registerSource(ResourceType.HSPEC, generatedSuitable, "GENERATED", arg0.getUserId(), arg0.getSourceHCAFId(), ResourceType.HCAF);
			////					SourceManager.setTableTitle(ResourceType.HSPEC, id, suitableTitle);
			//				}catch(Exception e){
			//					logger.error("UNABLE TO REGISTER GENERATED HSPEC SUITABLE table name : "+generatedSuitable,e);
			//				}
			//			}
			//			if(arg0.getSpeciesSelection()!=null && arg0.getSpeciesSelection().getItems().length>0){
			//				logger.trace("Dropping temp hspen table");
			//				DBSession.getInternalDBSession().dropTable(hspen);
			//			}
			//			if(arg0.isEnableLog())logger.trace("Log table is "+generator.getLogTable());
			//			return new VOID();
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


	@Override
	public int generateMaps(GenerateMapsRequestType arg0) throws RemoteException,GCUBEFault{

		try{
			return CommonServiceLogic.generateMaps_Logic(arg0.getHSPECId(),Field.load(arg0.getSpeciesFilter()),arg0.getAuthor(),arg0.isGenerateLayers());
		}catch(Exception e){
			logger.error("Unable to execute request ", e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public String generateHSPECGroup(HspecGroupGenerationRequestType arg0)
	throws RemoteException, GCUBEFault {
		try{//Inserting request into db
			long availableSpace=GHNContext.getContext().getFreeSpace(GHNContext.getContext().getLocation());
			long threshold=ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.MONITOR_FREESPACE_THRESHOLD);
			if(availableSpace<threshold)throw new Exception("NOT ENOUGH SPACE, REMAINING : "+availableSpace+", THRESHOLD : "+threshold);

			logger.trace("Received hspec group generation request, title : "+arg0.getGenerationName());
			String id=ServiceUtils.generateId("HGGR", "");
			logger.trace("Id will be "+id);
			logger.trace("Checking settings..");
			HSPECGroupGenerationRequest request=new HSPECGroupGenerationRequest(arg0);
			request.setSubmissiontime(System.currentTimeMillis());
			if(SourceManager.getById(request.getHcafsearchid())==null)throw new Exception("Invalid HCAF id "+request.getHcafsearchid());
			if(SourceManager.getById(request.getHspensearchid())==null)throw new Exception("Invalid HSPEN id "+request.getHspensearchid());
			logger.debug("Received request "+request.toXML());
			return TableGenerationExecutionManager.insertRequest(request);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public String getJSONSubmittedHSPECGroup(
			GetJSONSubmittedHSPECRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			PagedRequestSettings settings=new PagedRequestSettings(arg0.getLimit(), arg0.getOffset(), arg0.getSortColumn(), PagedRequestSettings.OrderDirection.valueOf(arg0.getSortDirection()));
			return HSPECGroupGenerationRequestsManager.getJSONList(new ArrayList<Field>(), settings);

		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public GetGenerationLiveReportResponseType getGenerationLiveReportGroup(
			int arg0) throws RemoteException, GCUBEFault {
		try{
			logger.trace("Serving get generation Live Report, generator ID is "+arg0);
			EnvironmentalExecutionReportItem report=BatchGeneratorObjectFactory.getReport(arg0,true);
			if(report==null) throw new Exception("Execution finished or not yet started");
			return new GetGenerationLiveReportResponseType(
					report.getElaboratedSpecies(), report.getPercent(), report.getResourceLoad(), report.getResourcesMap());
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public VOID editHSPECGroupDetails(HspecGroupGenerationRequestType arg0)
	throws RemoteException, GCUBEFault {
		try{
			//TODO implment
			throw new Exception("NOT YET IMPLEMENTED");
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public RemoveHSPECGroupGenerationRequestResponseType removeHSPECGroup(
			RemoveHSPECGroupGenerationRequestResponseType arg0)
	throws RemoteException, GCUBEFault {
		try{
			ArrayList<Field> filter=new ArrayList<Field>();
			filter.add(new Field(GroupGenerationRequestFields.id+"",arg0.getRequestId(),FieldType.STRING));
			HSPECGroupGenerationRequest request= HSPECGroupGenerationRequestsManager.getList(filter).get(0);
			//TODO complete method
			if(arg0.isRemoveTables()) throw new Exception("REMOVE TABLES NOT YET IMPLEMENTED");
			if(arg0.isRemoveJobs()) throw new Exception("REMOVE JOBS NOT YET IMPLEMENTED");
			HSPECGroupGenerationRequestsManager.delete(filter);
			return new RemoveHSPECGroupGenerationRequestResponseType(false,false,request.getId());
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public FieldArray getDefaultSources(VOID arg0) throws RemoteException,
	GCUBEFault {
		try{
			ArrayList<Field> toReturn=new ArrayList<Field>();
			for(ResourceType type:ResourceType.values())
				try{
					toReturn.add(new Field(type+"",SourceManager.getDefaultId(type)+"",FieldType.INTEGER));
				}catch(Exception e){
					logger.warn("Unable to locate default table for "+type,e);
				}
				return Field.toStubsVersion(toReturn);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public Resource editResource(Resource arg0) throws RemoteException,
	GCUBEFault {
		try{
			logger.trace("Editing resource "+arg0.getSearchId());
			org.gcube.application.aquamaps.dataModel.enhanced.Resource resource=new org.gcube.application.aquamaps.dataModel.enhanced.Resource(arg0);
			SourceManager.update(resource);
			return arg0;
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
}

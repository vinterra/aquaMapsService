package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
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
import org.gcube.application.aquamaps.aquamapsservice.stubs.ImportResourceRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.SetUserCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ViewCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.RSWrapper;
import org.gcube.application.aquamaps.dataModel.FieldArray;
import org.gcube.application.aquamaps.dataModel.Resource;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.LogicType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SourceGenerationPhase;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.dataModel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.dataModel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
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
			SourceGenerationRequest request=new SourceGenerationRequest(arg0);
			request.setSubmissiontime(System.currentTimeMillis());
			if(SourceManager.getById(request.getHcafId())==null)throw new Exception("Invalid HCAF id "+request.getHcafId());
			if(SourceManager.getById(request.getHspenId())==null)throw new Exception("Invalid HSPEN id "+request.getHspenId());
			if(SourceManager.getById(request.getOccurrenceCellId())==null)throw new Exception("Invalid Occurrence Cells  id "+request.getOccurrenceCellId());
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
			return SourceGenerationRequestsManager.getJSONList(new ArrayList<Field>(), settings);

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
	public RemoveHSPECGroupGenerationRequestResponseType removeHSPECGroup(
			RemoveHSPECGroupGenerationRequestResponseType arg0)
	throws RemoteException, GCUBEFault {
		try{
			ArrayList<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SourceGenerationRequestFields.id+"",arg0.getRequestId(),FieldType.STRING));
			SourceGenerationRequest request= SourceGenerationRequestsManager.getList(filter).get(0);
			//TODO complete method
			if(arg0.isRemoveTables()) throw new Exception("REMOVE TABLES NOT YET IMPLEMENTED");
			if(arg0.isRemoveJobs()) throw new Exception("REMOVE JOBS NOT YET IMPLEMENTED");
			SourceGenerationRequestsManager.delete(filter);
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
	public String exportResource(int arg0) throws RemoteException, GCUBEFault {
		DBSession session=null;
		try{
			logger.trace("Exporting resource id "+arg0);
			String table=SourceManager.getById(arg0).getTableName();
			
			session=DBSession.getInternalDBSession();
			File out=File.createTempFile(table, ".csv");
			
			logger.trace("Exporting table "+table+" to file "+out);
			CSVUtils.resultSetToCSVFile(session.executeQuery("Select * from "+table),out.getAbsolutePath(),true);
			
			GCUBEScope scope=ServiceContext.getContext().getScope();
			logger.trace("Caller scope is "+scope);
			RSWrapper wrapper=new RSWrapper(scope);
			wrapper.add(out);
			String locator = wrapper.getLocator().toString();
			logger.trace("Added file to locator "+locator);
			return locator;
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}finally{
			if(session!=null)
				try {
					session.close();
				} catch (Exception e) {
					logger.error("Unexpected error while closing session ",e);
				}
		}
	}

	@Override
	public VOID removeResource(int arg0) throws RemoteException, GCUBEFault {
		try{
			logger.trace("Removing resource "+arg0);
			org.gcube.application.aquamaps.dataModel.enhanced.Resource resource=SourceManager.getById(arg0);
			if(resource!=null){
				List<Submitted> relatedJobs=null;
				List<SourceGenerationRequest> relatedGenerations=null;
				if(!resource.getType().equals(ResourceType.OCCURRENCECELLS)){
					logger.trace("Resource type is "+resource.getType()+", gathering related jobs...");
					List<Field> jobFilter=new ArrayList<Field>();

					if(resource.getType().equals(ResourceType.HCAF))
						jobFilter.add(new Field(SubmittedFields.sourcehcaf+"",resource.getSearchId()+"",FieldType.INTEGER));
					else if(resource.getType().equals(ResourceType.HSPEN))
						jobFilter.add(new Field(SubmittedFields.sourcehspen+"",resource.getSearchId()+"",FieldType.INTEGER));
					else if(resource.getType().equals(ResourceType.HSPEC))
						jobFilter.add(new Field(SubmittedFields.sourcehspec+"",resource.getSearchId()+"",FieldType.INTEGER));
					
					jobFilter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
					
					relatedJobs=SubmittedManager.getList(jobFilter);
					
					logger.trace("Found "+relatedJobs.size()+" related jobs..");
					logger.trace("Checking jobs status..");
					for(Submitted j:relatedJobs){
						if(!j.getStatus().equals(SubmittedStatus.Completed)&&!j.getStatus().equals(SubmittedStatus.Error)) 
							throw new Exception("Found pending related jobs [ID : "+j.getSearchId()+"], unable to continue..");
					}
					logger.trace("OK");
				}
				
				if(!resource.getType().equals(ResourceType.HSPEC)){
					logger.trace("Checking for pending table generation..");
					
					ArrayList<Field> generationFilter=new ArrayList<Field>();
					
					if(resource.getType().equals(ResourceType.HCAF))
						generationFilter.add(new Field(SourceGenerationRequestFields.sourcehcafid+"",resource.getSearchId()+"",FieldType.INTEGER));
					else if(resource.getType().equals(ResourceType.HSPEN)){
						generationFilter.add(new Field(SourceGenerationRequestFields.sourcehspenid+"",resource.getSearchId()+"",FieldType.INTEGER));
					}
					else if(resource.getType().equals(ResourceType.OCCURRENCECELLS)){
						generationFilter.add(new Field(SourceGenerationRequestFields.logic+"",LogicType.HSPEN+"",FieldType.STRING));
						generationFilter.add(new Field(SourceGenerationRequestFields.sourceoccurrencecellsid+"",resource.getSearchId()+"",FieldType.INTEGER));
					}
					relatedGenerations=SourceGenerationRequestsManager.getList(generationFilter);

					logger.trace("Found "+relatedGenerations.size()+" related generations..");
					logger.trace("Checking status..");
					for(SourceGenerationRequest r:relatedGenerations){
						if(!r.getPhase().equals(SourceGenerationPhase.completed)&&!r.getPhase().equals(SourceGenerationPhase.error)) 
							throw new Exception("Found pending related requests [ID : "+r.getId()+"], unable to continue..");
					}
					logger.trace("OK");
				}
								
				logger.trace("Checks completed");
				
				logger.trace("Unregistering..");
				
				SourceManager.deleteSource(arg0,true);
				
				
				
				logger.trace("Removing Jobs.. ");
				if(relatedJobs!=null){
					for(Submitted toDelete:relatedJobs)
						try{
							SubmittedManager.delete(toDelete.getSearchId());
						}catch(Exception e){
							logger.warn("Unable to delete related job "+toDelete.getSearchId(),e);
						}
				}
				logger.trace("Removing generation Requests .. ");
				if(relatedGenerations!=null){
					for(SourceGenerationRequest toDelete:relatedGenerations)
						try{
							SourceGenerationRequestsManager.delete(toDelete.getId());
						}catch(Exception e){
							logger.warn("Unable to delete related generation request "+toDelete.getId(),e);
						}
				}
				logger.trace("Complete");
			}else throw new Exception("Resource not found, ID was "+arg0);
			return new VOID();
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
		
	}
	
//	@Override
//	public String queryResource(QueryResourceRequestType arg0)
//			throws RemoteException, GCUBEFault {
//		DBSession session=null;
//		try{
//			logger.trace("Direct Query request : ");
//			logger.trace("Query : "+arg0.getQueryString());
//			logger.trace("Sort "+arg0.getSortColumn()+" "+arg0.getSortDirection());
//			logger.trace("LIMIT "+arg0.getLimit()+" OFFSET "+arg0.getOffset());
//			session=DBSession.getInternalDBSession();
//			String query=arg0.getQueryString()+
//				((arg0.getSortColumn()!=null&&!arg0.getSortDirection().equalsIgnoreCase("null"))?" ORDER BY "+arg0.getSortColumn()+" "+arg0.getSortDirection():" ");
//			session.disableAutoCommit();
//			logger.trace("Gonna execute direct query : "+query);
//			return DBUtils.toJSon(session.executeQuery(query),arg0.getOffset(),arg0.getOffset()+arg0.getLimit());
//		}catch(Exception e){
//			logger.error("Unable to execute request ",e);
//			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
//		}finally{
//			if(session!=null)
//				try {
//					session.close();
//				} catch (Exception e) {
//					logger.error("Unexpected error while closing session ",e);
//				}
//		}
//	}
	
	
	@Override
	public VOID setCustomQuery(SetUserCustomQueryRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			logger.trace("Setting custom query, user : "+arg0.getUser()+", query : "+arg0.getQueryString());
			CustomQueryManager.setUserCustomQuery(arg0.getUser(), arg0.getQueryString());
			return new VOID();
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public boolean deleteCustomQuery(String arg0) throws RemoteException,
			GCUBEFault {
		try{
			logger.trace("Deleting custom query, user : "+arg0);
			logger.trace("Deleted "+CustomQueryManager.deleteUserQuery(arg0)+" references.");
			return true;
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	@Override
	public int getImportStatus(int arg0) throws RemoteException, GCUBEFault {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int importResource(ImportResourceRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{
			logger.trace("Importing resource , user : "+arg0);
			File csv=File.createTempFile("import", ".csv");
			FileUtils.getFileUtils().copyFile(RSWrapper.getStreamFromLocator(new URI(arg0.getLocator())), csv);
			
			return SourceManager.importFromCSVFile(csv, arg0.getUser(), ResourceType.valueOf(arg0.getResourceType()));
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
	@Override
	public String viewCustomQuery(ViewCustomQueryRequestType arg0)
			throws RemoteException, GCUBEFault {
		try{			
			
			return CustomQueryManager.getPagedResult(arg0.getUser(), 
					new PagedRequestSettings(arg0.getLimit(), arg0.getOffset(), arg0.getSortColumn(), OrderDirection.valueOf(arg0.getSortDirection())));
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	@Override
	public FieldArray isCustomQueryReady(String arg0) throws RemoteException,
			GCUBEFault {
		try{
			return Field.toStubsVersion(CustomQueryManager.isCustomQueryReady(arg0));
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
}

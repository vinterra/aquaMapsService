package org.gcube.application.aquamaps.aquamapsservice.impl;

import gr.uoa.di.madgik.commons.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AnalysisTableManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceGenerationRequestsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis.AnalysisManager;
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
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedAnalysisRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedHSPECRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ImportResourceRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RemoveHSPECGroupGenerationRequestResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.SetUserCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ViewCustomQueryRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.EnvironmentalExecutionReportItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils.RSWrapper;
import org.gcube.application.aquamaps.datamodel.FieldArray;
import org.gcube.application.aquamaps.datamodel.Resource;
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
		throw new GCUBEFault ("OBSOLETE METHOD");
	}

	public VOID generateHSPEC(GenerateHSPECRequestType arg0)
	throws RemoteException, GCUBEFault {
			throw new GCUBEFault ("OBSOLETE METHOD");
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
			for(Integer resId:request.getHcafIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.HCAF))throw new Exception("Invalid HCAF id "+resId);
			}
			for(Integer resId:request.getHspenIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.HSPEN))throw new Exception("Invalid HSPEN id "+resId);
			}
			for(Integer resId:request.getOccurrenceCellIds()){
				org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource r=SourceManager.getById(resId);
				if(r==null||!r.getType().equals(ResourceType.OCCURRENCECELLS))throw new Exception("Invalid Occurrence Cells id "+resId);
			}			
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
			org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource resource=new org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource(arg0);
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
			org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource resource=SourceManager.getById(arg0);
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
					
					SourceGenerationRequest sourceReq=new SourceGenerationRequest();
					sourceReq.addSource(resource);
					
					if(resource.getType().equals(ResourceType.HCAF))
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.sourcehcafids));
					else if(resource.getType().equals(ResourceType.HSPEN)){
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.sourcehspenids));
					}
					else if(resource.getType().equals(ResourceType.OCCURRENCECELLS)){
						sourceReq.setLogic(LogicType.HSPEN);
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.logic));
						generationFilter.add(sourceReq.getField(SourceGenerationRequestFields.sourceoccurrencecellsids));
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
			logger.trace("Importing resource , user : "+arg0.getUser()+", locator :"+arg0.getRsLocator());
			String csvLocation=ServiceContext.getContext().getFolderPath(FOLDERS.IMPORTS)+File.separator+ServiceUtils.generateId("import", ".csv");
			
			FileWriter writer=new FileWriter(csvLocation);
			FileInputStream is=new FileInputStream(RSWrapper.getStreamFromLocator(new URI(arg0.getRsLocator())));
			IOUtils.copy(is, writer, arg0.getEncoding());
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(is);
			logger.trace("CSV imported into "+csvLocation);
			return SourceManager.importFromCSVFile(csvLocation, arg0);
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
	
	@Override
	public String exportTableAsCSV(String arg0) throws RemoteException,
			GCUBEFault {
		DBSession session=null;
		try{
			
			session=DBSession.getInternalDBSession();
			File out=File.createTempFile(arg0, ".csv");
			
			logger.trace("Exporting table "+arg0+" to file "+out);
			CSVUtils.resultSetToCSVFile(session.executeQuery("Select * from "+arg0),out.getAbsolutePath(),true);
			
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
	public String analyzeTables(org.gcube.application.aquamaps.datamodel.Analysis arg0) throws RemoteException,
			GCUBEFault {
		try{
			return AnalysisManager.insertRequest(new Analysis(arg0));
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public String getJSONSubmittedAnalysis(
			GetJSONSubmittedAnalysisRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			PagedRequestSettings settings=new PagedRequestSettings(arg0.getLimit(), arg0.getOffset(), arg0.getSortColumn(), PagedRequestSettings.OrderDirection.valueOf(arg0.getSortDirection()));
			return AnalysisTableManager.getJSONList(new ArrayList<Field>(), settings);
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	@Override
	public String loadAnalysis(String arg0) throws RemoteException, GCUBEFault {
		try{
			Analysis analysis=AnalysisTableManager.getById(arg0);
			GCUBEScope scope=ServiceContext.getContext().getScope();
			logger.trace("Caller scope is "+scope);
			RSWrapper wrapper=new RSWrapper(scope);
			File temp=File.createTempFile("analysis",".tar.gz");
			FileUtils.Copy(new File(analysis.getArchiveLocation()), temp);
			temp.deleteOnExit();
			wrapper.add(temp);
			String locator = wrapper.getLocator().toString();
			logger.trace("Added file to locator "+locator);
			return locator;
		}catch(Exception e){
			logger.error("Unable to execute request ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}
	
}

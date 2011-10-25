package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.SimpleGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.SimpleGeneratorI;
import org.gcube.application.aquamaps.aquamapsservice.stubs.AquaMapsServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopefromCellSelectionRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetAquaMapsPerUserRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetOccurrenceCellsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetPhylogenyRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetResourceListRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Cell;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.HspenFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AreaType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.RSWrapper;
import org.gcube.application.aquamaps.datamodel.AquaMap;
import org.gcube.application.aquamaps.datamodel.FieldArray;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.types.VOID;


public class AquaMapsService extends GCUBEPortType implements AquaMapsServicePortType{






	protected GCUBEServiceContext getServiceContext() {		
		return ServiceContext.getContext();
	}

	public String getPhylogeny(GetPhylogenyRequestType req) throws GCUBEFault{
		try{
			Field toSelect= new Field(req.getToSelect());
			PagedRequestSettings settings= new PagedRequestSettings(req.getLimit(), req.getOffset(), req.getSortColumn(), PagedRequestSettings.OrderDirection.valueOf(req.getSortDirection()));
			return SpeciesManager.getJSONTaxonomy(toSelect, Field.load(req.getFieldList()), settings);
		}catch(Exception e){
			logger.error("Unable to get Taxonomy ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	public int deleteSubmitted(StringArray submittedIds)throws GCUBEFault{
		int toReturn=0;
		if((submittedIds!=null)&&(submittedIds.getItems()!=null))
			for(String id:submittedIds.getItems()){
				try{
				int toDelete=Integer.parseInt(id);
				toReturn+=SubmittedManager.delete(toDelete);
				}catch(Exception e){
					logger.error("cannot delete "+id, e);
				}
			}
			return toReturn;
	}




	public FieldArray calculateEnvelope(CalculateEnvelopeRequestType req)throws GCUBEFault{
		logger.trace("Serving calculateEnvelope");
		try{
			
			
			BoundingBox bb= new BoundingBox();
			if(req.isUseBounding()){
				bb.setE(req.getBoundingEast());
				bb.setW(req.getBoundingWest());
				bb.setN(req.getBoundingNorth());
				bb.setS(req.getBoundingSouth());
			}
			List<Area> areas= new ArrayList<Area>();
			if(req.isUseFAO()){
				for(String code:req.getFaoAreas().split(","))
					areas.add(new Area(AreaType.FAO, code.trim()));
			}
			
			Set<Cell> foundCells= CellManager.calculateGoodCells(bb,areas,req.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HCAF));
 			logger.trace("found "+foundCells.size()+" cells");

 			Species species=SpeciesManager.getSpeciesById(true,true,req.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HSPEN));
 			
			if(req.isUseBottomSeaTempAndSalinity())
				species.getFieldbyName(HspenFields.layer+"").setValue("b");
			else species.getFieldbyName(HspenFields.layer+"").setValue("u");
 			
			
			
			
//			SpEnvelope envelope=new SpEnvelope();
//			envelope.reCalculate(species, foundCells);
			
			SimpleGeneratorI generator=new SimpleGenerator(ServiceContext.getContext().getEcoligicalConfigDir().getAbsolutePath()+File.separator);
			
			
			for(Field f:generator.getEnvelope(species, foundCells))species.addField(f);
			
			
			return species.extractEnvelope().toFieldArray();
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	public FieldArray calculateEnvelopefromCellSelection(CalculateEnvelopefromCellSelectionRequestType request)throws GCUBEFault{
		logger.trace("Serving calculateEnvelopefromCellSelection for speciesID : "+request.getSpeciesID());
		try{
			
			
			Set<Cell> selected=CellManager.getCellsByIds(true,request.getSpeciesID(),true,SourceManager.getDefaultId(ResourceType.HCAF),
					request.getCellIds().getItems());
			Species spec=SpeciesManager.getSpeciesById(true,true,request.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HSPEN));
//			SpEnvelope envelope=new SpEnvelope();
//			envelope.reCalculate(spec, selected);
			
			SimpleGeneratorI generator=new SimpleGenerator(ServiceContext.getContext().getEcoligicalConfigDir().getAbsolutePath()+File.separator);
			
			
			for(Field f:generator.getEnvelope(spec, selected))spec.addField(f);
			
			
			return spec.extractEnvelope().toFieldArray();
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	public String getOccurrenceCells(GetOccurrenceCellsRequestType request)throws GCUBEFault{
		try{
			return CellManager.getJSONOccurrenceCells(request.getSpeciesID(), 
					new PagedRequestSettings(request.getLimit(), request.getOffset(), request.getSortColumn(), 
							PagedRequestSettings.OrderDirection.valueOf(request.getSortDirection())));
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	public String submitJob(org.gcube.application.aquamaps.datamodel.Job req)throws GCUBEFault{
		try{
			logger.trace("Serving submit job "+req.getName());
			logger.trace("Forcing group enabling if layers requested");
			Job job= new Job(req);
			boolean enableGis=false;
			for(AquaMapsObject obj : job.getAquaMapsObjectList())
				if(obj.getGis()) {
					enableGis=true;
					break;
				}
			
			job.setIsGis(enableGis);
			
			return JobExecutionManager.insertJobExecutionRequest(job,false)+"";
			
			
		}catch(Exception e){
			logger.error("Unable to execute Job "+req.getName(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}







	public FieldArray getSpeciesEnvelop(GetSpeciesEnvelopeRequestType arg0)throws GCUBEFault{
		logger.trace("serving get Species envelope");
		try{
			Species selected=SpeciesManager.getSpeciesById(true,true,arg0.getSpeciesId(),arg0.getHspenId());
			return Field.toStubsVersion(selected.getAttributesList());
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	/**
	 * return a list of species filtered by 3 groups of filters (species characteristics OR species names OR species codes) 
	 * 
	 * @param req
	 * @return
	 * @throws GCUBEFault
	 */



	public String getSpeciesByFilters(GetSpeciesByFiltersRequestType req) throws GCUBEFault{
		logger.trace("Serving getSpecies by filters");
		
		try{
			
			return SpeciesManager.getJsonList(req.getSortColumn(), req.getSortDirection(), req.getLimit(), req.getOffset(),
					Field.load(req.getCharacteristicFilters()), Filter.load(req.getNameFilters()), Filter.load(req.getCodeFilters()), req.getHspen());
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}

	}



	public org.gcube.application.aquamaps.datamodel.Resource getResourceInfo(org.gcube.application.aquamaps.datamodel.Resource myResource) throws GCUBEFault{
		Resource toReturn=new Resource(myResource);		
		
		try{
		
		return SourceManager.getById(toReturn.getSearchId()).toStubsVersion();
		}catch(Exception e){
			logger.error("Unable to load source details. id: "+myResource.getSearchId(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	public String getResourceList(GetResourceListRequestType req) throws GCUBEFault{
		logger.debug("entroin getResourceList");
		try{
			
			return SourceManager.getJsonList(Field.load(req.getFilters()),
					 new PagedRequestSettings(req.getLimit(), req.getOffset(), req.getSortColumn(), PagedRequestSettings.OrderDirection.valueOf(req.getSortDirection())));
		}catch(Exception e){
			logger.error("Errors while performing getResourceList operation",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}



	public String getAquaMapsPerUser(GetAquaMapsPerUserRequestType arg0)throws GCUBEFault{
		logger.trace("Serving get submitted ..");
		try{
		
			ArrayList<Field> parameters=new ArrayList<Field>();		
			
			parameters.add(new Field(SubmittedFields.author+"",arg0.getUserID(),FieldType.STRING));
			parameters.add(new Field(SubmittedFields.isaquamap+"",arg0.isAquamaps()+"",FieldType.BOOLEAN));
			parameters.add(new Field(SubmittedFields.todelete+"",false+"",FieldType.BOOLEAN));
			if(arg0.isJobIdEnabled()) {
				parameters.add(new Field(SubmittedFields.jobid+"",arg0.getJobIdValue()+"",FieldType.INTEGER));
			}
			if(arg0.isDateEnabled()) {
				parameters.add(new Field(SubmittedFields.submissiontime+"",arg0.getDateValue(),FieldType.INTEGER));
			}
			if(arg0.isObjectStatusEnabled()) {
				parameters.add(new Field(SubmittedFields.status+"",arg0.getObjectStatusValue(),FieldType.STRING));
			}
			if(arg0.isTypeEnabled()){
				parameters.add(new Field(SubmittedFields.type+"",arg0.getTypeValue(),FieldType.STRING));			
			}
			if(arg0.isJobStatusEnabled()){
				throw new GCUBEFault("JOB STATUS filter is not yet supported");
			}
			logger.trace("Filtering parameters : ");
			for(Field f:parameters)
				logger.trace(f.getName()+" = "+f.getValue()+" ("+f.getType()+")");
			
			return SubmittedManager.getJsonList(parameters,  
					new PagedRequestSettings(arg0.getLimit(), arg0.getOffset(), arg0.getSortColumn(), PagedRequestSettings.OrderDirection.valueOf(arg0.getSortDirection()))); 			
		}catch(Exception e ){
			logger.error("Exception while trying to serve -getAquaMapsPerUser : user = "+arg0.getUserID(),e);
			throw new GCUBEFault("ServerSide Msg: "+e.getMessage());
		} 
	}

	public VOID markSaved(StringArray ids)throws GCUBEFault{
		try{
			if((ids!=null)&&(ids.getItems()!=null))
				for(String id:ids.getItems())SubmittedManager.markSaved(Integer.parseInt(id));
			return new VOID();
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault();
		}
		
	}

	public org.gcube.application.aquamaps.datamodel.Submitted loadSubmittedById(int arg0) throws RemoteException,
			GCUBEFault {
		try{
			logger.trace("Loading submitted id : "+arg0);
			List<Field> conditions=new ArrayList<Field>();
			conditions.add(new Field(SubmittedFields.searchid+"", arg0+"", FieldType.INTEGER));
			return SubmittedManager.getList(conditions).get(0).toStubsVersion();			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Impossible to load submitted : "+e.getMessage());
		}
	}

	public AquaMap getObject(int arg0) throws RemoteException, GCUBEFault {
		try{
			AquaMapsObject obj=AquaMapsManager.loadObject(arg0,true,true);
//			logger.info("Object IS "+AquaMapsXStream.getXMLInstance().toXML(obj));
			return obj.toStubsVersion();
			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Impossible to load Object from Publisher : "+e.getMessage());
		}
	}

	@Override
	public String getSpeciesByFiltersASCSV(GetSpeciesByFiltersRequestType arg0)
			throws RemoteException, GCUBEFault {
		logger.trace("Serving getSpecies by filters");
		
		try{
			File toExport=SpeciesManager.getCSVList(Field.load(arg0.getCharacteristicFilters()), Filter.load(arg0.getNameFilters()), Filter.load(arg0.getCodeFilters()), arg0.getHspen());
			GCUBEScope scope=ServiceContext.getContext().getScope();
			logger.trace("Caller scope is "+scope);
			RSWrapper wrapper=new RSWrapper(scope);
			wrapper.add(toExport);
			String locator = wrapper.getLocator().toString();
			logger.trace("Added file to locator "+locator);
			return locator;
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	
}

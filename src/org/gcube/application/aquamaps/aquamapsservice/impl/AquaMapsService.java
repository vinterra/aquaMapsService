package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.envelope.SpEnvelope;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobSubmissionThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.SubmittedMonitorThread;
import org.gcube.application.aquamaps.aquamapsservice.stubs.AquaMapsServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopefromCellSelectionRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetAquaMapsPerUserRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetOccurrenceCellsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetPhylogenyRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetResourceListRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings;
import org.gcube.application.aquamaps.dataModel.AquaMap;
import org.gcube.application.aquamaps.dataModel.FieldArray;
import org.gcube.application.aquamaps.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Filter;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.types.VOID;


public class AquaMapsService extends GCUBEPortType implements AquaMapsServicePortType{






	protected GCUBEServiceContext getServiceContext() {		
		return ServiceContext.getContext();
	}

	public String getPhylogeny(GetPhylogenyRequestType req) throws GCUBEFault{
		try{
			Field toSelect= new Field(req.getToSelect());
			PagedRequestSettings settings= new PagedRequestSettings(req.getLimit(), req.getOffset(), req.getSortColumn(), req.getSortDirection());
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
 			
			SpEnvelope envelope=new SpEnvelope();
			envelope.reCalculate(species, foundCells);
			
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
			SpEnvelope envelope=new SpEnvelope();
			envelope.reCalculate(spec, selected);
			
			return spec.extractEnvelope().toFieldArray();
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	public String getOccurrenceCells(GetOccurrenceCellsRequestType request)throws GCUBEFault{
		try{
			return CellManager.getJSONOccurrenceCells(request.getSpeciesID(), request.getSortColumn(), request.getSortDirection(), request.getLimit(), request.getOffset());
			
		} catch (Exception e){
			logger.error("General Exception, unable to serve request",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	public String submitJob(org.gcube.application.aquamaps.dataModel.Job req)throws GCUBEFault{
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
			if(ServiceContext.getContext().isPostponeSubmission()){
			
			String file=SubmittedMonitorThread.getInstance().putInQueue(job);
			logger.trace("Queued with "+file);
			return file;}
			else{
				
				JobSubmissionThread thread=new JobSubmissionThread(job);
				ServiceContext.getContext().setScope(thread,ServiceContext.getContext().getStartScopes());
				ThreadManager.getExecutor().execute(thread);
				return "";
			}
		}catch(Exception e){
			logger.error("Unable to execute Job "+req.getName(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}







	public FieldArray getSpeciesEnvelop(GetSpeciesEnvelopeRequestType arg0)throws GCUBEFault{
		logger.trace("serving get Species envelope");
		try{
			Species selected=SpeciesManager.getSpeciesById(true,true,arg0.getSpeciesId(),arg0.getHspenId());
			return Field.toStubsVersion(selected.attributesList);
			
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



	public org.gcube.application.aquamaps.dataModel.Resource getResourceInfo(org.gcube.application.aquamaps.dataModel.Resource myResource) throws GCUBEFault{
		Resource toReturn=new Resource(myResource);		
		
		try{
		
		return SourceManager.getById(toReturn.getType(), toReturn.getSearchId()).toStubsVersion();
		}catch(Exception e){
			logger.error("Unable to load source details. id: "+myResource.getSearchId(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}

	public String getResourceList(GetResourceListRequestType req) throws GCUBEFault{
		logger.debug("entroin getResourceList");
		try{
	
			return SourceManager.getJsonList(ResourceType.valueOf(req.getType()),
					req.getSortColumn(), req.getSortDirection(), req.getLimit(), req.getOffset());
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
			if(arg0.isJobIdEnabled()) {
				parameters.add(new Field(SubmittedFields.jobid+"",arg0.getJobIdValue()+"",FieldType.INTEGER));
			}
			if(arg0.isDateEnabled()) {
				parameters.add(new Field(SubmittedFields.date+"",arg0.getDateValue(),FieldType.STRING));
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
			
			return SubmittedManager.getJsonList(parameters, arg0.getSortColumn(), arg0.getSortDirection(), arg0.getLimit(), arg0.getOffset()); 			
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

	public org.gcube.application.aquamaps.dataModel.Submitted loadSubmittedById(int arg0) throws RemoteException,
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
			AquaMapsObject toReturn=ServiceContext.getContext().getPublisher().getAquaMapsObjectById(arg0);
			if(toReturn==null){
				logger.trace("Object with id "+arg0+" not found");
				return null;
			}
			else return toReturn.toStubsVersion();
			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Impossible to load Object from Publisher : "+e.getMessage());
		}
	}


	
}

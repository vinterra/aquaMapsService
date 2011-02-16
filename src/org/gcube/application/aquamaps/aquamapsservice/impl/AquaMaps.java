package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.env.SpEnvelope;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobSubmissionThread;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.AquaMapsPortType;
import org.gcube.application.aquamaps.stubs.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.stubs.CalculateEnvelopefromCellSelectionRequestType;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.GetAquaMapsPerUserRequestType;
import org.gcube.application.aquamaps.stubs.GetOccurrenceCellsRequestType;
import org.gcube.application.aquamaps.stubs.GetPhylogenyRequestType;
import org.gcube.application.aquamaps.stubs.GetResourceListRequestType;
import org.gcube.application.aquamaps.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.stubs.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Resource;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.types.VOID;


public class AquaMaps extends GCUBEPortType implements AquaMapsPortType{






	protected GCUBEServiceContext getServiceContext() {		
		return ServiceContext.getContext();
	}

	public String getPhylogeny(GetPhylogenyRequestType req) throws GCUBEFault{
		//FIXME
		throw new GCUBEFault("Method not yet implemented");
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
			
			Set<Cell> foundCells= CellManager.getGoodCells(bb,areas,req.getSpeciesID(),SourceManager.getDefaultId(ResourceType.HCAF));
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

	public String getProfile(int id)throws GCUBEFault{
		logger.trace("getting profile for owner id : "+id);
		
		try{
			//TODO profile retrieval -> object retrieval
			throw new Exception ("Not Yet Implemented");
			
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

	public String submitJob(org.gcube.application.aquamaps.stubs.Job req)throws GCUBEFault{
		try{
			logger.trace("Serving submit job "+req.getName());
		JobSubmissionThread thread=new JobSubmissionThread(new Job(req),ServiceContext.getContext().getScope());		
		ThreadManager.getExecutor().execute(thread);
		return String.valueOf(thread.getId());
		}catch(Exception e){
			logger.error("Unable to execute Job "+req.getName(), e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


//	public FileArray getRelatedFiles(String owner)throws GCUBEFault{
//		logger.trace("getting file List for owner id : "+owner);
//		FileArray toReturn=null;
//		DBSession conn=null;
//		try{
//			conn = DBSession.openSession(PoolManager.DBType.mySql);			
//			ResultSet rs=conn.executeQuery("Select * from Files where owner = "+owner);
//			ArrayList<org.gcube.application.aquamaps.stubs.File> files=new ArrayList<org.gcube.application.aquamaps.stubs.File>();
//			while(rs.next()){
//				org.gcube.application.aquamaps.stubs.File f=new org.gcube.application.aquamaps.stubs.File();
//				f.setName(rs.getString(4));
//				f.setType(rs.getString(5));
//				f.setUrl(rs.getString(3));
//				files.add(f);			
//			}
//			toReturn=new FileArray(files.toArray(new org.gcube.application.aquamaps.stubs.File[files.size()]));
//			rs.close();			
//			conn.close();
//		} catch (Exception e){
//			logger.error("General Exception, unable to serve request",e);
//			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
//		}finally{try {conn.close();} 
//		catch (Exception e) {logger.error("Unrecoverable while attempitng to close session",e);}}
//		return toReturn;
//	}




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



	public org.gcube.application.aquamaps.stubs.Resource getResourceInfo(org.gcube.application.aquamaps.stubs.Resource myResource) throws GCUBEFault{
		Resource toReturn=new Resource(myResource);		
		
		try{
//		String title=SourceManager.getSourceTitle(toReturn.getType(), toReturn.getSearchId());
//		
//		toReturn.setTitle(title);
		
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

	public org.gcube.application.aquamaps.stubs.Submitted loadSubmittedById(int arg0) throws RemoteException,
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
			return PublisherImpl.getPublisher().getAquaMapsObjectById(arg0).toStubsVersion();
			
		}catch(Exception e){
			logger.error("",e);
			throw new GCUBEFault("Impossible to load Object from Publisher : "+e.getMessage());
		}
	}


	
}

package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.AquaMapsServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.CalculateEnvelopefromCellSelectionRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetAquaMapsPerUserRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetOccurrenceCellsRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetResourceListRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetSpeciesEnvelopeRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.AquaMapsServiceAddressingLocator;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Envelope;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Filter;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AquaMapsServiceWrapper {

	private static final GCUBELog logger=new GCUBELog(AquaMapsServiceWrapper.class);

	protected static ISClient isClient;


	static{
		try {
			isClient = GHNContext.getImplementation(ISClient.class);
		} catch (Exception e) {
			logger.error("Unable to get ISImplementation : "+e);
		}
	}


	private AquaMapsServicePortType pt;

	public AquaMapsServiceWrapper(GCUBEScope scope, String defaultURI)throws Exception {
		this.pt=getPortType(scope,defaultURI);		
	}

	
	
	
	
	
	private static AquaMapsServicePortType getPortType(GCUBEScope scope,String defaultURI) throws Exception{
		AquaMapsServiceAddressingLocator asal= new AquaMapsServiceAddressingLocator();
		EndpointReferenceType epr;
		GCUBERIQuery query = isClient.getQuery(GCUBERIQuery.class);		
		query.addAtomicConditions(new AtomicCondition("//Profile/ServiceClass","Application"));
		query.addAtomicConditions(new AtomicCondition("//Profile/ServiceName","AquaMapsService"));
		List<GCUBERunningInstance> toReturn= isClient.execute(query, scope);
		if(toReturn.isEmpty()) {				
			System.out.println("No runnning instance found, using default service @ : "+defaultURI);
			epr=new EndpointReferenceType();
			epr.setAddress(new AttributedURI(defaultURI));
		}else{
			epr= toReturn.get(0).getAccessPoint().getEndpoint("gcube/application/aquamaps/aquamapsservice/AquaMapsService");
			System.out.println("Found RI @ : "+epr.getAddress().getHost());
		}
		AquaMapsServicePortType aquamapsPT=asal.getAquaMapsServicePortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, scope);	
	}


	public Envelope calculateEnvelope(BoundingBox bb,List<Area> areas,String speciesId,boolean useBottom, boolean useBounding, boolean useFAO) throws Exception{
		try{
			CalculateEnvelopeRequestType request=new CalculateEnvelopeRequestType();
			request.setBoundingEast(bb.getE());
			request.setBoundingNorth(bb.getN());
			request.setBoundingSouth(bb.getS());
			request.setBoundingWest(bb.getW());

			StringBuilder areaSelection=new StringBuilder();
			for(Area a: areas) areaSelection.append(a.getCode()+",");
			areaSelection.deleteCharAt(areaSelection.lastIndexOf(","));
			request.setFaoAreas(areaSelection.toString());

			request.setSpeciesID(speciesId);
			request.setUseBottomSeaTempAndSalinity(useBottom);
			request.setUseBounding(useBounding);
			request.setUseFAO(useFAO);

			Species s=new Species(speciesId);
			s.getAttributesList().addAll(Field.load(pt.calculateEnvelope(request)));
			return s.extractEnvelope();
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public Envelope calculateEnvelopeFromCellSelection(List<String> cellIds,String speciesId)throws Exception{
		try{
			CalculateEnvelopefromCellSelectionRequestType request=new CalculateEnvelopefromCellSelectionRequestType();
			request.setCellIds(new StringArray(cellIds.toArray(new String[cellIds.size()])));
			request.setSpeciesID(speciesId);
			Species s=new Species(speciesId);
			s.getAttributesList().addAll(Field.load(pt.calculateEnvelopefromCellSelection(request)));
			return s.extractEnvelope();
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public int deleteSubmitted(List<Integer> ids)throws Exception{
		try{
			String[] array=new String[ids.size()];
			for(int i=0;i<ids.size();i++) array[i]=String.valueOf(ids.get(i));
			return pt.deleteSubmitted(new StringArray(array));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public String getJSONSubmitted(String userName, boolean showObjects,String date,Integer jobId,SubmittedStatus status,ObjectType objType, PagedRequestSettings settings)throws Exception{
		try{
			GetAquaMapsPerUserRequestType request=new GetAquaMapsPerUserRequestType();
			request.setUserID(userName);
			request.setAquamaps(showObjects);
			request.setDateEnabled(date!=null);
			if(date!=null)request.setDateValue(date);
			request.setJobIdEnabled(jobId!=null);
			if(jobId!=null)request.setJobIdValue(jobId);
			request.setJobStatusEnabled(false);
			request.setJobStatusValue(null);
			request.setLimit(settings.getLimit());
			request.setObjectStatusEnabled(status!=null);
			if(status!=null)request.setObjectStatusValue(status.toString());
			request.setOffset(settings.getOffset());
			request.setSortColumn(settings.getOrderColumn());
			request.setSortDirection(settings.getOrderDirection());
			request.setTypeEnabled(objType!=null);
			if(objType!=null)request.setTypeValue(objType.toString());
			return pt.getAquaMapsPerUser(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public String getJSONOccurrenceCells(String speciesId, PagedRequestSettings settings)throws Exception{
		try{
			GetOccurrenceCellsRequestType request= new GetOccurrenceCellsRequestType();
			request.setSpeciesID(speciesId);
			request.setOffset(settings.getOffset());
			request.setSortColumn(settings.getOrderColumn());
			request.setSortDirection(settings.getOrderDirection());
			request.setLimit(settings.getLimit());
			return pt.getOccurrenceCells(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	public String getJSONPhilogeny()throws Exception{
		try{
			throw new GCUBEFault("Not Implemented");
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	/**wraps getProfile
	 * 
	 * @return
	 * @throws Exception
	 */
	public AquaMapsObject loadObject(int objectId)throws Exception{
		try{
			return new AquaMapsObject(pt.getObject(objectId));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public Resource loadResource(int resId,ResourceType type)throws Exception{
		try{
			Resource request=new Resource(type,resId);
			return	new Resource(pt.getResourceInfo(request.toStubsVersion()));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public String getJSONResources(PagedRequestSettings settings, ResourceType type)throws Exception{
		try{
			GetResourceListRequestType request=new GetResourceListRequestType();
			request.setType(type.toString());
			request.setOffset(settings.getOffset());
			request.setSortColumn(settings.getOrderColumn());
			request.setSortDirection(settings.getOrderDirection());
			request.setLimit(settings.getLimit());
			return pt.getResourceList(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public String getJSONSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception{
		try{
			GetSpeciesByFiltersRequestType request=new GetSpeciesByFiltersRequestType();
			request.setCharacteristicFilters(Field.toStubsVersion(characteristcs));
			request.setCodeFilters(Filter.toStubsVersion(codes));
			request.setHspen(hspenId);
			request.setNameFilters(Filter.toStubsVersion(names));
			request.setOffset(settings.getOffset());
			request.setSortColumn(settings.getOrderColumn());
			request.setSortDirection(settings.getOrderDirection());
			request.setLimit(settings.getLimit());
			return	pt.getSpeciesByFilters(request);			
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public Species loadEnvelope(String speciesId, int hspenId)throws Exception{
		try{
			Species spec=new Species(speciesId);
			GetSpeciesEnvelopeRequestType req=new GetSpeciesEnvelopeRequestType(hspenId, speciesId);
			spec.attributesList.addAll(Field.load(pt.getSpeciesEnvelop(req)));
			//			System.out.println("Loaded Fields : ");
			//			for(Field f:spec.attributesList)
			//				System.out.println(f.getName()+" : "+f.getValue());
//			return spec.extractEnvelope();
			return spec;
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	public void markSaved(List<Integer> submittedIds)throws Exception{
		try{
			List<String> ids=new ArrayList<String>();
			for(Integer id:submittedIds)ids.add(String.valueOf(id));
			pt.markSaved(new StringArray(ids.toArray(new String[ids.size()])));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	public void submitJob(Job toSubmit) throws Exception{
		try{
			pt.submitJob(toSubmit.toStubsVersion());
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	public Submitted loadSubmittedById(int id)throws Exception{
		try{
			return new Submitted(pt.loadSubmittedById(id));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}
}

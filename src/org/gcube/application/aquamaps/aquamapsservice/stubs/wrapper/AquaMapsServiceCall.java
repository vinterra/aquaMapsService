package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.service.AquaMapsServiceAddressingLocator;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils.RSWrapper;
import org.gcube.application.aquamaps.datamodel.AquaMap;
import org.gcube.application.aquamaps.datamodel.PagedRequestSettings;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.security.GCUBESecurityManagerImpl;
import org.gcube.common.core.types.StringArray;

public class AquaMapsServiceCall extends AquaMapsCall implements AquaMapsServiceInterface{


	public static AquaMapsServiceInterface getCall(GCUBEScope scope, GCUBESecurityManager[] securityManager,String defaultURI,boolean queryIS)throws Exception{
		return new AquaMapsServiceCall(scope, securityManager,defaultURI,queryIS);
	}

	/**
	 * Creates a call with a disabled security manager  
	 * 
	 * @param scope
	 * @param defaultURI
	 * @return
	 * @throws Exception
	 */
	public static AquaMapsServiceInterface getCall(GCUBEScope scope, String defaultURI,boolean queryIS)throws Exception{
		GCUBESecurityManager secMan= new GCUBESecurityManagerImpl(){

			@Override
			public boolean isSecurityEnabled() {
				return false;
			}

		};
		return new AquaMapsServiceCall(scope, new GCUBESecurityManager[]{secMan},defaultURI,queryIS);
	}

	private AquaMapsServiceCall(GCUBEScope scope,
			GCUBESecurityManager[] securityManager,String defaultURI,boolean queryIS) throws Exception {
		super(scope, securityManager,defaultURI,queryIS);
		pt=GCUBERemotePortTypeContext.getProxy(new AquaMapsServiceAddressingLocator().getAquaMapsServicePortTypePort(epr), scope, 120000, securityManager);
	}


	@Override
	public String getPortTypeName() {
		return Constant.AQUAMAPSSERVICE_PT_NAME;
	}

	private AquaMapsServicePortType pt;




	@Override
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

	@Override
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

	@Override
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

	@Override
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
			request.setObjectStatusEnabled(status!=null);
			if(status!=null)request.setObjectStatusValue(status.toString());
			request.setPagedRequestSettings(settings);
			request.setTypeEnabled(objType!=null);
			if(objType!=null)request.setTypeValue(objType.toString());
			return pt.getAquaMapsPerUser(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public String getJSONOccurrenceCells(String speciesId, PagedRequestSettings settings)throws Exception{
		try{
			GetOccurrenceCellsRequestType request= new GetOccurrenceCellsRequestType();
			request.setSpeciesID(speciesId);
			request.setPagedRequestSettings(settings);
			return pt.getOccurrenceCells(request);
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
	@Override
	public AquaMapsObject loadObject(int objectId)throws Exception{
		try{
			AquaMap returned=pt.getObject(objectId);
//			logger.info("STUBS VERSION IS "+AquaMapsXStream.getXMLInstance().toXML(returned));
			return new AquaMapsObject(returned);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public Resource loadResource(int resId)throws Exception{
		try{
			Resource request=new Resource(ResourceType.HCAF,resId);
			return	new Resource(pt.getResourceInfo(request.toStubsVersion()));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public String getJSONResources(PagedRequestSettings settings, List<Field> filter) throws Exception{
		try{
			GetResourceListRequestType request=new GetResourceListRequestType();
			request.setFilters(Field.toStubsVersion(filter));
			request.setPagedRequestSettings(settings);
			return pt.getResourceList(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public String getJSONSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception{
		try{
			GetSpeciesByFiltersRequestType request=new GetSpeciesByFiltersRequestType();
			request.setCharacteristicFilters(Field.toStubsVersion(characteristcs));
			request.setCodeFilters(Filter.toStubsVersion(codes));
			request.setHspen(hspenId);
			request.setNameFilters(Filter.toStubsVersion(names));
			request.setPagedRequestSettings(settings);
			return	pt.getSpeciesByFilters(request);			
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

	@Override
	public Species loadEnvelope(String speciesId, int hspenId)throws Exception{
		try{
			Species spec=new Species(speciesId);
			GetSpeciesEnvelopeRequestType req=new GetSpeciesEnvelopeRequestType(hspenId, speciesId);
			spec.getAttributesList().addAll(Field.load(pt.getSpeciesEnvelop(req)));
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

	@Override
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

	@Override
	public void submitJob(Job toSubmit) throws Exception{
		try{
			pt.submitJob(toSubmit.toStubsVersion());
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	@Override
	public Submitted loadSubmittedById(int id)throws Exception{
		try{
			return new Submitted(pt.loadSubmittedById(id));
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	@Override
	public String getJSONPhilogeny(SpeciesOccursumFields level,
			ArrayList<Field> filters, PagedRequestSettings settings)
	throws Exception {
		try{
			GetPhylogenyRequestType request=new GetPhylogenyRequestType();
			request.setFieldList(Field.toStubsVersion(filters));
			request.setPagedRequestSettings(settings);
			request.setToSelect(new Field(level+"","",FieldType.STRING).toStubsVersion());
			return pt.getPhylogeny(request);
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}	
	}

	@Override
	public File getCSVSpecies(int hspenId, List<Field> characteristcs,
			List<Filter> names, List<Filter> codes) throws Exception {
		try{
			GetSpeciesByFiltersRequestType request=new GetSpeciesByFiltersRequestType();
			request.setCharacteristicFilters(Field.toStubsVersion(characteristcs));
			request.setCodeFilters(Filter.toStubsVersion(codes));
			request.setHspen(hspenId);
			request.setNameFilters(Filter.toStubsVersion(names));			
					
			String locator=pt.getSpeciesByFiltersASCSV(request);						
			return RSWrapper.getStreamFromLocator(new URI(locator));
			
		}catch(GCUBEFault f){
			logger.error("Service thrown Fault ",f);
			throw new ServiceException(f.getFaultMessage());
		}
	}

}

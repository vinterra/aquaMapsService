package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.AquaMapsPublisherPortType;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.service.AquaMapsPublisherServiceAddressingLocator;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.wrapper.AquaMapsPublisherWrapper;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.dataModel.LayerInfoType;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;
import org.gcube.common.gis.dataModel.enhanced.WMSContextInfo;
import org.gcube.common.gis.dataModel.types.LayersType;

public class PublisherImpl implements Publisher{

	private static String[] taxonomyInDB=new String[]{
		SpeciesManager.speciesOccurSum+"."+SpeciesOccursumFields.kingdom,
		SpeciesManager.speciesOccurSum+"."+SpeciesOccursumFields.phylum,
		SpeciesManager.speciesOccurSum+"."+SpeciesOccursumFields.classcolumn,
		SpeciesManager.speciesOccurSum+"."+SpeciesOccursumFields.order,
		SpeciesManager.speciesOccurSum+"."+SpeciesOccursumFields.family,		
	};
	
	
	
	private static PublisherImpl instance=new PublisherImpl();
	
	private PublisherImpl(){};
	
	public static Publisher getPublisher(){return instance;}
	
	private static GCUBELog logger= new GCUBELog(PublisherImpl.class);
	
	protected static ISClient isClient;
	
	static{		
		try {
			isClient = GHNContext.getImplementation(ISClient.class);
		} catch (Exception e) {
			logger.error("Unable to get ISImplementation : "+e);
		}
	}
	
	
	
	private AquaMapsPublisherPortType getPortType() throws Exception{
		GCUBEScope scope= ServiceContext.getContext().getScope();
		AquaMapsPublisherServiceAddressingLocator asal= new AquaMapsPublisherServiceAddressingLocator();
		EndpointReferenceType epr;
		String url=ServiceContext.getContext().getDefaultPublisherUrl();
		if(!ServiceContext.getContext().isStandAloneMode()){
			logger.trace("Looking up for Service RI...");
			GCUBERIQuery query = isClient.getQuery(GCUBERIQuery.class);		
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceClass","Application"));
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceName","AquaMapsPublisher"));
			List<GCUBERunningInstance> toReturn= isClient.execute(query, scope);			
			if(toReturn.size()<1) {
				logger.warn("No publisher runnning instance found, using default service @ : "+url);
				epr=new EndpointReferenceType();
				epr.setAddress(new AttributedURI(url));
			}else{
				epr= toReturn.get(0).getAccessPoint().getEndpoint("gcube/application/aquamaps/aquamapspublisher/AquaMapsPublisher");
				logger.trace("Found RI @ : "+epr.getAddress().getHost());
			}
		}else{
			epr=new EndpointReferenceType();
			epr.setAddress(new AttributedURI(url));
		}
				AquaMapsPublisherPortType aquamapsPT=asal.getAquaMapsPublisherPortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, scope);	
	}
	
	
//	public String publishImages(int submittedId,Set<String> coverageSpeciesId,Collection<String> toPublishSet,GCUBEScope scope, boolean hasCustomizations)throws Exception{
//		
//		StoreImageRequestType req= new StoreImageRequestType();
//		req.setTaxonomy(getTaxonomyCoverage(coverageSpeciesId));
//		req.setSourceHCAF(SourceManager.getSourceName(ResourceType.HCAF, JobManager.getHCAFTableId(submittedId)));
//		req.setSourceHSPEN(SourceManager.getSourceName(ResourceType.HSPEN, JobManager.getHSPENTableId(submittedId)));
//		if(hasCustomizations)
//			req.setAuthor(JobManager.getAuthor(submittedId));
//		File zipped=null;		
//		FileInputStream fis=null;
//		File base64Zipped=null;
//		try{
//			zipped=File.createTempFile("imgSet", ".zip");
//			ZipUtils.zipFiles(toPublishSet, zipped.getAbsolutePath());	
//			base64Zipped=File.createTempFile("imgSet", "base64");
//			Base64.encode(zipped, base64Zipped);
//			RSWrapper wrapper=null;
//			int attemptsCount=0;
//			while(wrapper==null){
//				try{
//					attemptsCount++;
//					logger.trace("Looking for ResultSet service in scope : "+scope.toString()+" attempt N "+attemptsCount);
//					wrapper=new RSWrapper(scope);				
//				}catch(Exception e){				
//					logger.debug("No ResultSet service found",e);
//					try {
//						Thread.sleep(20*1000);
//					} catch (InterruptedException e1) {}
//				}
//			}
//			req.setRsLocator(wrapper.getLocator());
//			AquaMapsPublisherPortType pt=getPortType(scope);			
//			fis=new FileInputStream(base64Zipped);
//			wrapper.add(fis);
//			wrapper.close();			
//			String basePath= pt.storeImage(req);
//			
//			if((coverageSpeciesId.size()==1)&&(!hasCustomizations)){
//				logger.trace("going to register maps in DB ");
//				int internalSpeciesId=getInternalSpeciesIds(coverageSpeciesId).iterator().next();
//				int alreadyMappedId=MapsManager.getDistributionMapId(1, 1, internalSpeciesId);
//				if(alreadyMappedId>0){
//					logger.trace("Found mapId "+alreadyMappedId);
//					MapsManager.updateDistributionMapBasePath(alreadyMappedId, basePath);
//				}else MapsManager.registerDistributionMap(1, 1, internalSpeciesId, basePath, null);				
//			}
//			return basePath;
//		}catch(Exception e){
//			logger.error("",e);
//			throw e;
//		}finally{
//			if(zipped!=null)FileUtils.forceDelete(zipped);
//			logger.trace("temp zip : "+zipped.getAbsolutePath());
//			if(base64Zipped!=null)FileUtils.forceDelete(base64Zipped);			
//			if(fis!=null)IOUtils.closeQuietly(fis);
//		}
//	}
//	
//	
//	private static TaxonomyType getTaxonomyCoverage(Set<String> speciesIds)throws Exception{
//		DBSession session= null;
//		TaxonomyType toReturn=new TaxonomyType();
//		try{
//			logger.trace("Retrieving taxonomy ..");
//			session=DBSession.openSession(PoolManager.DBType.mySql);
//			boolean isCommonTaxonomyValue=true;
//			int taxonomyLevelIndex=0;
//			
//			//iterates for all taxonomy levels or found incongruence
//			while((taxonomyLevelIndex<taxonomyInDB.length)&&(isCommonTaxonomyValue)){
//				String taxonomyField=taxonomyInDB[taxonomyLevelIndex];
//				PreparedStatement ps= session.preparedStatement("Select "+taxonomyField+" from "+SpeciesManager.speciesOccurSum+" where speciesId=?");
//				String taxonomyValue=null;
//				
//				//iterates for all species or found incongruence
//				
//				Iterator<String> it=speciesIds.iterator();
//				
//				while((it.hasNext())&&(isCommonTaxonomyValue)){
//					String speciesId=it.next();
//					ps.setString(1, speciesId);
//					ResultSet rs=ps.executeQuery();
//					if(rs.next()){
//						String currentTaxonomyValue=rs.getString(1);
//						if(taxonomyValue==null) taxonomyValue=currentTaxonomyValue;
//						else isCommonTaxonomyValue=(taxonomyValue.equalsIgnoreCase(currentTaxonomyValue));
//					}else{
//						logger.warn("Unable to find "+taxonomyInDB[taxonomyLevelIndex]+" for speciesId "+speciesId);
//						isCommonTaxonomyValue=false;
//					}
//				}
//				
//				if(isCommonTaxonomyValue){
//					//found common level, going to set it 
//					if(taxonomyLevelIndex==0)toReturn.setKingdom(taxonomyValue);
//					else if(taxonomyLevelIndex==1)toReturn.setPhylum(taxonomyValue);
//					else if(taxonomyLevelIndex==2)toReturn.set_class(taxonomyValue);
//					else if(taxonomyLevelIndex==3)toReturn.setOrder(taxonomyValue);
//					else if(taxonomyLevelIndex==4)toReturn.setFamily(taxonomyValue);
//					else throw new Exception("Wrong iteration, this sould never happend");
//				}
//				taxonomyLevelIndex++;				
//			}
//			if(speciesIds.size()==1) toReturn.setSpeciesId(speciesIds.iterator().next());
//			return toReturn;
//		}catch(Exception e){
//			logger.error("Unable to retrieve status",e);
//			throw e;
//		}finally{
//			if(!session.getConnection().isClosed())session.close();
//		}		
//	}
//	
//	public List<String> getPublishedMaps(Set<String> speciesIds,String HSPEN,String HCAF,GCUBEScope scope)throws Exception{
//		List<String> toReturn=new ArrayList<String>();
//		if(speciesIds.size()>1) return toReturn; //TODO Biodiversity registration management
//		
//		int internalSpeciesId=getInternalSpeciesIds(speciesIds).iterator().next();
//		//TODO hspen & hcaf id
//		int alreadyMappedId=MapsManager.getDistributionMapId(1, 1, internalSpeciesId);
//		if(alreadyMappedId>0){
//			logger.trace("Found mapId "+alreadyMappedId);
//			String mapsBasePath=MapsManager.getDistributionMapBasePath(alreadyMappedId);
//			if(mapsBasePath!=null){
//			AquaMapsPublisherPortType pt=getPortType(scope);
//			StringArray result=pt.getPublishedMapsPath(mapsBasePath);
//			if((result!=null)){
//				String[] urls= result.getItems();
//				if(urls!=null){
//					for(String url:urls) toReturn.add(url);
//				}
//			}else logger.trace("No base path found for mapId "+alreadyMappedId);
//		}
//		}else logger.trace("Map Id not found");
//		return toReturn;
//	}
	
	

//	private Set<Integer> getInternalSpeciesIds(Set<String> coverageSpeciesId)throws Exception{
//		DBSession session= null;
//		try{
//			Set<Integer> internals=new HashSet<Integer>();
//			session= DBSession.openSession(PoolManager.DBType.mySql);
//			PreparedStatement ps= session.preparedStatement("SELECT internalId from speciesoccursum where speciesID = ?");
//			for(String id:coverageSpeciesId){
//				ps.setString(1,id);
//				ResultSet rs = ps.executeQuery();
//				rs.first();
//				internals.add(rs.getInt(1));
//			}
//			return internals;
//		}catch (Exception e){
//			throw e;
//		}finally {
//			session.close();
//		}
//	}
//	
//	public String getPublishedLayer(Set<String> speciesIds,String HSPEN,String HCAF,GCUBEScope scope)throws Exception{
//		if(speciesIds.size()>1) return null;
//		int internalSpeciesIds=getInternalSpeciesIds(speciesIds).iterator().next();
//		//TODO hspen & hcaf id
//		int alreadyMappedId=MapsManager.getDistributionMapId(1, 1, internalSpeciesIds);
//		if(alreadyMappedId>0){
//			logger.trace("Found mapId "+alreadyMappedId);
//			return MapsManager.getDistributionMapLayerUri(alreadyMappedId);
//		}else return null; 
//	}
	
//	public void registerLayer(Set<String> speciesIds, String HSPEN, String HCAF,String layer)throws Exception{
//		if(speciesIds.size()==1){
//			int internalSpeciesIds=getInternalSpeciesIds(speciesIds).iterator().next();
//			int alreadyMappedId=MapsManager.getDistributionMapId(1, 1, internalSpeciesIds);
//			if(alreadyMappedId>0){
//				logger.trace("Found mapId "+alreadyMappedId);
//				MapsManager.updateDistributionMapLayerUri(alreadyMappedId, layer);
//			}else MapsManager.registerDistributionMap(1, 1, internalSpeciesIds, null, layer);
//			logger.trace("registered layer "+layer);
//		}
//	}

	
	private AquaMapsPublisherWrapper wrapper=null;
	
	private AquaMapsPublisherWrapper getWrapper() throws Exception{
		
		if(wrapper==null) {
			String url=ServiceContext.getContext().getDefaultPublisherUrl();
			logger.trace("Init publisher wrapper with default url : "+url);
			ASLSession session= SessionManager.getInstance().getASLSession("AquaMapsService", "AquaMapsService");
			session.setScope(ServiceContext.getContext().getScope().toString());
			wrapper=new AquaMapsPublisherWrapper(session,url);
		}
		return wrapper;
	}
	
	
	
	//******************** GET METHODS
	
	public Job getJobById(int id) throws Exception {return getWrapper().getJobById(id);}

	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception {return getWrapper().getAquaMapObjectById(id);}
	
	
	
	
	@Override
	public WMSContextInfo getWMSContextById(String id) throws Exception {return getWrapper().getWMSContextById(id);}
	
	
	public LayerInfo getBiodiversityLayer(Set<String> speciesCoverage,
			int hcafId, int hspenId, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds, float threshold) throws Exception {
		
		ArrayList<String> specs=new ArrayList<String>();
		for(String s : speciesCoverage) specs.add(s);
		return getWrapper().getLayer(bounds+"", envelopeCustomization, new Resource(ResourceType.HCAF,hcafId), new Resource(ResourceType.HSPEN,hspenId),
				areaSelection,specs,threshold,envelopeWeights);
	}

	public LayerInfo getDistributionLayer(String speciesId, int hcafId,
			int hspenId, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds, boolean getNative) throws Exception {
		return getWrapper().getLayer(bounds+"", envelopeCustomization, new Resource(ResourceType.HCAF,hcafId), new Resource(ResourceType.HSPEN,hspenId),
				areaSelection,speciesId,getNative,envelopeWeights);
	}

	public LayerInfo getEnvironmentalLayer(EnvelopeFields parameter,
			int hcafId) throws Exception {
		//TODO call new method on stubs
		throw new Exception ("NOT IMPLEMENTED");
	}

	public LayerInfo getLayerByIdAndType(String id,LayersType type) throws Exception {return getWrapper().getLayerById(id,type);}
		

	public LayerInfo getOccurrenceLayer(String speciesId) throws Exception {
		//TODO call new method on stubs
		throw new Exception ("NOT IMPLEMENTED");
	}


	
	public LayerInfo getLayerById(String id) throws Exception{
		//TODO call new method on stubs
		throw new Exception ("NOT IMPLEMENTED");
	}
	
	
	
	
	
	
	
	
	//******************** STORE METHODS
	
	public int publishJob(Job toPublish) throws Exception {return getWrapper().storeJob(toPublish);}
	
	public boolean publishAquaMapsObject(AquaMapsObject toPublish) throws Exception {return getWrapper().updateAquaMapObject(toPublish);}

	public List<org.gcube.application.aquamaps.dataModel.enhanced.File> publishImages(
			int mapId, Set<String> speciesCoverage,
			Map<String, String> toPublishList, 
			boolean hasCustomizations) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	

	public String publishLayer(int objId,LayersType type,
			ArrayList<String> styles, int defaultStyleIndex, String title, String url)throws Exception {
		
		
		
		LayerInfo info= getWrapper().getLayerTemplate(type);
		info.setStyles(styles);
		info.setTitle(title);
		info.setUrl(url);
		info.setDefaultStyle(styles.get(defaultStyleIndex));
		
		return getWrapper().storeLayer(info,objId);
	}

	
	public LayerInfoType publishOccurrenceLayer(String speciesId,LayersType type,
			ArrayList<String> styles, int defaultStyleIndex, String title, String url)throws Exception{
		
		LayerInfo info= getWrapper().getLayerTemplate(type);
		info.setStyles(styles);
		info.setTitle(title);
		info.setUrl(url);
		info.setDefaultStyle(styles.get(defaultStyleIndex));
		
		return getWrapper().storeLayer(info,speciesId);
	}
	
	public LayerInfoType publishEnvironmentalLayer(int hcafId,EnvelopeFields parameter,LayersType type,
			ArrayList<String> styles, int defaultStyleIndex, String title, String url)throws Exception{
		
		LayerInfo info= getWrapper().getLayerTemplate(type);
		info.setStyles(styles);
		info.setTitle(title);
		info.setUrl(url);
		info.setDefaultStyle(styles.get(defaultStyleIndex));
		
		return getWrapper().storeLayer(info,new Resource(ResourceType.HCAF,hcafId),parameter+"");
	}
	
	public String publishWMSContext(String groupName,
			ArrayList<LayerInfo> layers) throws Exception {
		WMSContextInfo context= getWrapper().getWMSContextTemplate(); 
		context.setLayers(layers);
		context.setTitle(groupName);
		return getWrapper().storeWMSContext(context);
	}

	
	//***************************** REMOVE METHODS
	
	public void removeAquaMapsObject(int id) throws Exception {	getWrapper().deleteAquaMapObject(id);}

	public void removeJob(int Id) throws Exception {getWrapper().deleteJob(Id);}

	public void removeLayer(String id)throws Exception{getWrapper().deleteLayer(id);}
	
	public void removeWMSContext(String id)throws Exception{getWrapper().deleteWMSContext(id);}

}

package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.AquaMapsPublisherPortType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.LayerInfoType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.WMSContextInfoType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.service.AquaMapsPublisherServiceAddressingLocator;

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
				epr= toReturn.get(0).getAccessPoint().getEndpoint("gcube/application/aquamapspublisher/AquaMapsPublisher");
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

	public Job getJobById(int id) throws Exception {
		AquaMapsPublisherPortType pt=getPortType();
		return new Job((org.gcube.application.aquamaps.stubs.Job)(Object)pt.getJobById(String.valueOf(id)));
	}

	public int publishAquaMapsObject(AquaMapsObject toPublish) throws Exception {
		AquaMapsPublisherPortType pt=getPortType();
		throw new Exception ("Not Yet Implemented");
	}

	public int publishJob(Job toPublish) throws Exception {
		AquaMapsPublisherPortType pt=getPortType();
		return pt.storeJob((org.gcube.application.aquamaps.aquamapspublisher.stubs.Job)(Object)toPublish.toStubsVersion());
	}

	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception {
		AquaMapsPublisherPortType pt=getPortType();
		return new AquaMapsObject((org.gcube.application.aquamaps.stubs.AquaMap)(Object)pt.getAquaMapObjectById(String.valueOf(id)));		
	}

	public LayerInfoType getExistingLayer(Set<String> speciesCoverage,
			int hcafId, int hspenId,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			Set<Area> selectedAreas, BoundingBox bb, float threshold)
			throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public WMSContextInfoType getExistingWMSContext(List<LayerInfoType> layers)
			throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public LayerInfoType getStandardSpeciesLayer(LayerType type)
			throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public List<org.gcube.application.aquamaps.stubs.dataModel.File> publishImages(
			int mapId, Set<String> speciesCoverage,
			Map<String, String> toPublishList, GCUBEScope scope,
			boolean hasCustomizations) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public LayerInfoType publishNewLayer(Set<String> speciesCoverage,
			int hcafId, int hspenId,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			Set<Area> selectedAreas, BoundingBox bb, float threshold,
			String mapName, ArrayList<String> generatedStyles, int i)throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public WMSContextInfoType publishNewWMSContext(String groupName,
			List<LayerInfoType> layers) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public void removeAquaMapsObject(int id) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}

	public void removeJob(int Id) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("Not Yet Implemented");
	}
	
	
	
	
}

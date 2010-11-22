package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.utils.RSWrapper;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.utils.ZipUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.types.StringArray;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.AquaMapsPublisherPortType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.StoreImageRequestType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.TaxonomyType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.service.AquaMapsPublisherServiceAddressingLocator;

import com.Ostermiller.util.Base64;

public class Publisher{

	private static String[] taxonomyInDB=new String[]{
		DBCostants.speciesOccurSum+".Kingdom",
		DBCostants.speciesOccurSum+".Phylum",
		DBCostants.speciesOccurSum+".Class",
		DBCostants.speciesOccurSum+".Order",
		DBCostants.speciesOccurSum+".Family",		
	};
	
	
	
	private static Publisher instance=new Publisher();
	
	private Publisher(){};
	
	public static Publisher getPublisher(){return instance;}
	
	private static GCUBELog logger= new GCUBELog(Publisher.class);
	
	protected static ISClient isClient;
	
	static{		
		try {
			isClient = GHNContext.getImplementation(ISClient.class);
		} catch (Exception e) {
			logger.error("Unable to get ISImplementation : "+e);
		}
	}
	
	public AquaMapsPublisherPortType getPortType(GCUBEScope scope) throws Exception{
		AquaMapsPublisherServiceAddressingLocator asal= new AquaMapsPublisherServiceAddressingLocator();
		EndpointReferenceType epr;
		
			logger.trace("Looking up for Service RI...");
			GCUBERIQuery query = isClient.getQuery(GCUBERIQuery.class);		
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceClass","Application"));
			query.addAtomicConditions(new AtomicCondition("//Profile/ServiceName","AquaMapsPublisher"));
			List<GCUBERunningInstance> toReturn= isClient.execute(query, scope);			
			if(toReturn.size()<1) {
				String url=ServiceContext.getContext().getDefaultPublisherUrl();
				logger.warn("No publisher runnning instance found, using default service @ : "+url);
				epr=new EndpointReferenceType();
				epr.setAddress(new AttributedURI(url));
			}else{
				epr= toReturn.get(0).getAccessPoint().getEndpoint("gcube/application/aquamapspublisher/AquaMapsPublisher");
				logger.trace("Found RI @ : "+epr.getAddress().getHost());
			}
				AquaMapsPublisherPortType aquamapsPT=asal.getAquaMapsPublisherPortTypePort(epr);
		return GCUBERemotePortTypeContext.getProxy(aquamapsPT, scope);	
	}
	
	
	public String publishImages(int submittedId,String[] coverageSpeciesId,Collection<String> toPublishSet,GCUBEScope scope, boolean hasCustomizations)throws Exception{
		
		StoreImageRequestType req= new StoreImageRequestType();
		req.setTaxonomy(getTaxonomyCoverage(coverageSpeciesId));
		req.setSourceHCAF(SourceManager.getSourceName(SourceType.HCAF, JobManager.getHCAFTableId(submittedId)));
		req.setSourceHSPEN(SourceManager.getSourceName(SourceType.HSPEN, JobManager.getHSPENTableId(submittedId)));
		if(hasCustomizations)
			req.setAuthor(JobManager.getAuthor(submittedId));
		File zipped=null;		
		FileInputStream fis=null;
		File base64Zipped=null;
		try{
			zipped=File.createTempFile("imgSet", ".zip");
			ZipUtils.zipFiles(toPublishSet, zipped.getAbsolutePath());	
			base64Zipped=File.createTempFile("imgSet", "base64");
			Base64.encode(zipped, base64Zipped);
			RSWrapper wrapper=null;
			int attemptsCount=0;
			while(wrapper==null){
				try{
					attemptsCount++;
					logger.trace("Looking for ResultSet service in scope : "+scope.toString()+" attempt N "+attemptsCount);
					wrapper=new RSWrapper(scope);				
				}catch(Exception e){				
					logger.debug("No ResultSet service found",e);
					try {
						Thread.sleep(20*1000);
					} catch (InterruptedException e1) {}
				}
			}
			req.setRsLocator(wrapper.getLocator());
			AquaMapsPublisherPortType pt=getPortType(scope);			
			fis=new FileInputStream(base64Zipped);
			wrapper.add(fis);
			wrapper.close();			
			String basePath= pt.storeImage(req);
			
			if((coverageSpeciesId.length==1)&&(!hasCustomizations)){
				logger.trace("going to register maps in DB ");
				int[] internalSpeciesIds=getInternalSpeciesIds(coverageSpeciesId);
				int alreadyMappedId=MapRegistration.getDistributionMapId(1, 1, internalSpeciesIds[0]);
				if(alreadyMappedId>0){
					logger.trace("Found mapId "+alreadyMappedId);
					MapRegistration.updateDistributionMapBasePath(alreadyMappedId, basePath);
				}else MapRegistration.registerDistributionMap(1, 1, internalSpeciesIds[0], basePath, null);				
			}
			return basePath;
		}catch(Exception e){
			logger.error("",e);
			throw e;
		}finally{
			if(zipped!=null)FileUtils.forceDelete(zipped);
			logger.trace("temp zip : "+zipped.getAbsolutePath());
			if(base64Zipped!=null)FileUtils.forceDelete(base64Zipped);			
			if(fis!=null)IOUtils.closeQuietly(fis);
		}
	}
	
	
	private static TaxonomyType getTaxonomyCoverage(String[] speciesIds)throws Exception{
		DBSession session= null;
		TaxonomyType toReturn=new TaxonomyType();
		try{
			logger.trace("Retrieving taxonomy ..");
			session=DBSession.openSession(PoolManager.DBType.mySql);
			boolean isCommonTaxonomyValue=true;
			int taxonomyLevelIndex=0;
			
			//iterates for all taxonomy levels or found incongruence
			while((taxonomyLevelIndex<taxonomyInDB.length)&&(isCommonTaxonomyValue)){
				String taxonomyField=taxonomyInDB[taxonomyLevelIndex];
				PreparedStatement ps= session.preparedStatement("Select "+taxonomyField+" from "+DBCostants.speciesOccurSum+" where speciesId=?");
				String taxonomyValue=null;
				int speciesIndex=0;				
				
				//iterates for all species or found incongruence
				while((speciesIndex<speciesIds.length)&&(isCommonTaxonomyValue)){
					String speciesId=speciesIds[speciesIndex];
					ps.setString(1, speciesId);
					ResultSet rs=ps.executeQuery();
					if(rs.next()){
						String currentTaxonomyValue=rs.getString(1);
						if(taxonomyValue==null) taxonomyValue=currentTaxonomyValue;
						else isCommonTaxonomyValue=(taxonomyValue.equalsIgnoreCase(currentTaxonomyValue));
						speciesIndex++;
					}else{
						logger.warn("Unable to find "+taxonomyInDB[taxonomyLevelIndex]+" for speciesId "+speciesIds[speciesIndex]);
						speciesIndex++;
						isCommonTaxonomyValue=false;
					}
				}
				
				if(isCommonTaxonomyValue){
					//found common level, going to set it 
					if(taxonomyLevelIndex==0)toReturn.setKingdom(taxonomyValue);
					else if(taxonomyLevelIndex==1)toReturn.setPhylum(taxonomyValue);
					else if(taxonomyLevelIndex==2)toReturn.set_class(taxonomyValue);
					else if(taxonomyLevelIndex==3)toReturn.setOrder(taxonomyValue);
					else if(taxonomyLevelIndex==4)toReturn.setFamily(taxonomyValue);
					else throw new Exception("Wrong iteration, this sould never happend");
				}
				taxonomyLevelIndex++;				
			}
			if(speciesIds.length==1) toReturn.setSpeciesId(speciesIds[0]);
			return toReturn;
		}catch(Exception e){
			logger.error("Unable to retrieve status",e);
			throw e;
		}finally{
			if(!session.getConnection().isClosed())session.close();
		}		
	}
	
	public List<String> getPublishedMaps(String[] speciesIds,String HSPEN,String HCAF,GCUBEScope scope)throws Exception{
		List<String> toReturn=new ArrayList<String>();
		if(speciesIds.length>1) return toReturn; //TODO Biodiversity registration management
		
		int[] internalSpeciesIds=getInternalSpeciesIds(speciesIds);
		//TODO hspen & hcaf id
		int alreadyMappedId=MapRegistration.getDistributionMapId(1, 1, internalSpeciesIds[0]);
		if(alreadyMappedId>0){
			logger.trace("Found mapId "+alreadyMappedId);
			String mapsBasePath=MapRegistration.getDistributionMapBasePath(alreadyMappedId);
			if(mapsBasePath!=null){
			AquaMapsPublisherPortType pt=getPortType(scope);
			StringArray result=pt.getPublishedMapsPath(mapsBasePath);
			if((result!=null)){
				String[] urls= result.getItems();
				if(urls!=null){
					for(String url:urls) toReturn.add(url);
				}
			}else logger.trace("No base path found for mapId "+alreadyMappedId);
		}
		}else logger.trace("Map Id not found");
		return toReturn;
	}
	
	

	private int[] getInternalSpeciesIds(String[] speciesIds)throws Exception{
		DBSession session= null;
		try{
			int[] internals=new int[speciesIds.length];
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("SELECT internalId from speciesoccursum where speciesID = ?");
			for(int i=0;i<speciesIds.length;i++){
				ps.setString(1,speciesIds[i]);
				ResultSet rs = ps.executeQuery();
				rs.first();
				internals[i]=rs.getInt(1);
			}
			return internals;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	public String getPublishedLayer(String[] speciesIds,String HSPEN,String HCAF,GCUBEScope scope)throws Exception{
		if(speciesIds.length>1) return null;
		int[] internalSpeciesIds=getInternalSpeciesIds(speciesIds);
		//TODO hspen & hcaf id
		int alreadyMappedId=MapRegistration.getDistributionMapId(1, 1, internalSpeciesIds[0]);
		if(alreadyMappedId>0){
			logger.trace("Found mapId "+alreadyMappedId);
			return MapRegistration.getDistributionMapLayerUri(alreadyMappedId);
		}else return null; 
	}
	
	public void registerLayer(String[] speciesIds, String HSPEN, String HCAF,String layer)throws Exception{
		if(speciesIds.length==1){
			int[] internalSpeciesIds=getInternalSpeciesIds(speciesIds);
			int alreadyMappedId=MapRegistration.getDistributionMapId(1, 1, internalSpeciesIds[0]);
			if(alreadyMappedId>0){
				logger.trace("Found mapId "+alreadyMappedId);
				MapRegistration.updateDistributionMapLayerUri(alreadyMappedId, layer);
			}else MapRegistration.registerDistributionMap(1, 1, internalSpeciesIds[0], null, layer);
			logger.trace("registered layer "+layer);
		}
	}
	
}

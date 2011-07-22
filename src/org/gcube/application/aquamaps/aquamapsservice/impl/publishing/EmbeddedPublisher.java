package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapspublisher.impl.AquaMapsPublisherWrapperL;
import org.gcube.application.aquamaps.aquamapspublisher.impl.PublisherContext;
import org.gcube.application.aquamaps.aquamapspublisher.stubs.TemplateLayerType;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;
import org.gcube.common.gis.dataModel.enhanced.WMSContextInfo;
import org.gcube.common.gis.dataModel.types.LayersType;

public class EmbeddedPublisher implements Publisher{

	private static GCUBELog logger= new GCUBELog(EmbeddedPublisher.class);
	
	private AquaMapsPublisherWrapperL wrapper=null;
	
	private AquaMapsPublisherWrapperL getWrapper() throws Exception{
		
		if(wrapper==null) {
			wrapper=new AquaMapsPublisherWrapperL();
			logger.trace("Wrapper initialized");
		}
		return wrapper;
	}
	
	public static void stop(){
		PublisherContext.getContext().stop();
	}
	
	public EmbeddedPublisher(String persistenceRoot, String etc,String basPath,Integer port) throws Exception{
		logger.trace("Init Publisher Context");
		logger.trace("persistance Root : "+persistenceRoot);
		logger.trace("etc Folder : "+etc);
		logger.trace("base path : "+basPath);
		logger.trace("port : "+port);
		PublisherContext.getContext().start(new File(persistenceRoot), new File(etc), basPath, port);
		logger.trace("Constructing wrapper..");
		
		wrapper=new AquaMapsPublisherWrapperL();
		logger.trace("Wrapper initialized");
	}
	
	//******************** GET METHODS
	
	public Job getJobById(int id) throws Exception {return getWrapper().getJobById(id);}

	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception {return getWrapper().getAquaMapObjectById(id);}
	
	
	@Override
	public WMSContextInfo getWMSContextById(String id) throws Exception {return getWrapper().getWMSContextById(id);}
	
	@Override
	public LayerInfo getBiodiversityLayer(Set<String> speciesCoverage,
			Resource hcaf,Resource hspen, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,float threshold) throws Exception {
		
		ArrayList<String> specs=new ArrayList<String>();
		for(String s : speciesCoverage) specs.add(s);
		
		
		if(bounds==null)bounds=new BoundingBox();
		if(envelopeWeights==null) envelopeWeights= new HashMap<String, Map<EnvelopeFields,Field>>();
 		//****purging null species ids
		//Weights
		logger.trace("Checking weights...");
		Set<String> toPurge=new HashSet<String>();
		for(String specId: envelopeWeights.keySet()){
			if(envelopeWeights.get(specId)==null) toPurge.add(specId);
		}
		logger.trace("purging "+toPurge.size()+" incorrect ids ..");
		for(String s: toPurge)
			envelopeWeights.remove(s);

		
		//Customization
		logger.trace("Checking customizations ...");
		toPurge=new HashSet<String>();
		for(String specId: envelopeCustomization.keySet()){
			if(envelopeCustomization.get(specId)==null) toPurge.add(specId);
		}
		logger.trace("purging "+toPurge.size()+" incorrect ids ..");
		for(String s: toPurge)
			envelopeCustomization.remove(s);

		
		areaSelection.remove(null);
		
		
		
		
		
		
		return getWrapper().getLayer(bounds+"", envelopeCustomization, hcaf, hspen,areaSelection,specs,threshold,envelopeWeights);
	}
	@Override
	public LayerInfo getDistributionLayer(String speciesId,
			Resource hcaf,Resource hspen, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,boolean getNative) throws Exception {
		if(bounds==null)bounds=new BoundingBox();
		if(envelopeWeights==null) envelopeWeights= new HashMap<String, Map<EnvelopeFields,Field>>();
		//****purging null species ids
		//Weights
		logger.trace("Checking weights...");
		Set<String> toPurge=new HashSet<String>();
		for(String specId: envelopeWeights.keySet()){
			if(envelopeWeights.get(specId)==null) toPurge.add(specId);
		}
		logger.trace("purging "+toPurge.size()+" incorrect ids ..");
		for(String s: toPurge)
			envelopeWeights.remove(s);

		
		//Customization
		logger.trace("Checking customizations ...");
		toPurge=new HashSet<String>();
		for(String specId: envelopeCustomization.keySet()){
			if(envelopeCustomization.get(specId)==null) toPurge.add(specId);
		}
		logger.trace("purging "+toPurge.size()+" incorrect ids ..");
		for(String s: toPurge)
			envelopeCustomization.remove(s);

		
		areaSelection.remove(null);
		
				
		return getWrapper().getLayer(bounds+"", envelopeCustomization, hcaf, hspen,	areaSelection,speciesId,getNative,envelopeWeights);
	}

	public LayerInfo getEnvironmentalLayer(EnvelopeFields parameter,
			Resource hcaf) throws Exception {
		//TODO call new method on stubs
		throw new Exception ("NOT IMPLEMENTED");
	}

	public LayerInfo getLayerByIdAndType(String id,LayersType type) throws Exception {return getWrapper().getLayerById(id,type);}
		

	public LayerInfo getPointMapLayer(String speciesId) throws Exception {
		//TODO call new method on stubs
		throw new Exception ("NOT IMPLEMENTED");
	}

	public LayerInfo getLayerById(String id) throws Exception{
		return getWrapper().getLayerById(id);
	}
	
	
	//******************** STORE METHODS
	
	public Job publishJob(Job toPublish) throws Exception {
		logger.trace("Publishing Job "+toPublish.getName()+" - "+toPublish.getId());
//		logger.trace("Checking job species taxonomy ..");
//		for(Species s: toPublish.getSelectedSpecies())
//			logger.trace(AquaMapsXStream.getXMLInstance().toXML(s));
//		logger.trace("Checking object species taxonomy..");
//		for(AquaMapsObject object : toPublish.getAquaMapsObjectList())
//			for(Species s: object.getSelectedSpecies())
//				logger.trace(AquaMapsXStream.getXMLInstance().toXML(s));
		
//		logger.debug("Submitted Job Object Types :");
//		for(AquaMapsObject obj : toPublish.getAquaMapsObjectList())
//			logger.debug(obj.getName()+"\t"+obj.getId()+"\t"+obj.getType());

		Job toReturn=getWrapper().getJobById(getWrapper().storeJob(toPublish));
		
//		getWrapper().storeJobAsync(toPublish,getScope());
//		while(!getWrapper().isJobReady(toPublish.getId())){
//			logger.debug("Waiting for publisher to store job "+toPublish.getName()+" ("+toPublish.getId()+")");
//			try{Thread.sleep(1000);}catch(InterruptedException e){}			
//		}
//		
//		Job toReturn=getWrapper().getJobById(toPublish.getId());
//		
		
		
//		logger.debug("Returned Job  Object Types : " );
		if(toReturn==null) throw new Exception("Returned Null job after storing, job id was "+toPublish.getId());
		if(toReturn.getAquaMapsObjectList()==null) throw new Exception ("Null object list in returned Job Id "+toReturn.getId());
//		for(AquaMapsObject obj : toReturn.getAquaMapsObjectList()){
//			logger.debug(obj.getName()+"\t"+obj.getId()+"\t"+obj.getType());
//		}
			
		return toReturn;}
	
	public boolean publishAquaMapsObject(AquaMapsObject toPublish) throws Exception {return getWrapper().updateAquaMapObject(toPublish);}

	public boolean publishImages(
			int objectId,Map<String, String> toPublishList) throws Exception {
		logger.trace("Received request for publishing images for objId :"+objectId);

		List<File> imgSet=new ArrayList<File>();
		for(String s:toPublishList.values())
			imgSet.add(new File(s));
		
		 getWrapper().storeImage(imgSet, objectId);
		 return true;
	}

	

	public String publishLayer(int objId,LayersType type,
			List<String> styles, int defaultStyleIndex, String title, String layerName)throws Exception {
		LayerInfo layer=getLayer(type, layerName, title, "", styles, defaultStyleIndex);
		logger.trace("Requesting store layer...");
		long start=System.currentTimeMillis();
		String found= getWrapper().storeLayer(layer,objId);
		logger.trace("Received layer id "+found+" after "+(System.currentTimeMillis()-start)+"ms");
		return found;
	}

	
	public String publishPointMapLayer(String speciesId,
			List<String> list, int defaultStyleIndex, String title, String table)throws Exception{
		LayerInfo layer=getLayer(LayersType.PointMap, table, title, "", list, defaultStyleIndex);
		//TODO IMPLEMENT
		throw new Exception("NOT YET IMPLEMENTED");
//		return getWrapper().storeLayer(layer,speciesId);
	}
	
	public String publishEnvironmentalLayer(Resource hcaf,EnvelopeFields parameter,
			List<String> styles, int defaultStyleIndex, String title, String table)throws Exception{
		LayerInfo layer=getLayer(LayersType.Environment, table, title, "", styles, defaultStyleIndex);
		//TODO IMPLEMENT
		throw new Exception("NOT YET IMPLEMENTED");
//		return getWrapper().storeLayer(info,new Resource(ResourceType.HCAF,hcafId),parameter+"");
	}
	
	public String publishWMSContext(String groupName,
			List<LayerInfo> layers) throws Exception {
		WMSContextInfo context= getWrapper().getWMSContextTemplate(); 
		context.getLayers().addAll(layers);
		context.setTitle(groupName);
		return getWrapper().storeWMSContext(context);
	}

	
	//***************************** REMOVE METHODS
	
	public void removeAquaMapsObject(int id) throws Exception {	getWrapper().deleteAquaMapObject(id,true);}

	public void removeJob(int Id) throws Exception {getWrapper().deleteJob(Id,true);}

	public void removeLayer(String id)throws Exception{getWrapper().deleteLayer(id);}
	
	public void removeWMSContext(String id)throws Exception{getWrapper().deleteWMSContext(id,true);}

	
	//******************************* UTILS
	
	
	private LayerInfo getLayer(LayersType type, String name, String title, String abstractDescription,List<String> styles, int defaultStyleIndex)throws Exception{
		LayerInfo layer=null;
		switch(type){
		case Biodiversity:{
			layer=getWrapper().getLayerTemplate(TemplateLayerType.Biodiversity);
			break;}
		case Environment :{
			layer=getWrapper().getLayerTemplate(TemplateLayerType.Environment);
			break;}
		case PointMap : {
			layer=getWrapper().getLayerTemplate(TemplateLayerType.PointMap);
			break;}
		default : layer=getWrapper().getLayerTemplate(TemplateLayerType.SpeciesDistribution);
					layer.setType(type);
		break;
		}
		
		
		
		layer.setName(name);
        layer.setTitle(title);
        layer.set_abstract(abstractDescription);
        //GEOSERVER
        layer.setUrl(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_URL));
        layer.setServerProtocol("OGC:WMS");
        layer.setServerLogin(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_USER));
        layer.setServerPassword(ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_PASSWORD));
        layer.setServerType("geoserver");
        layer.setSrs("EPSG:4326");
        
        layer.setOpacity(1.0);
        layer.setStyles(new ArrayList<String>());
        layer.getStyles().addAll(styles);
        layer.setDefaultStyle(styles.get(defaultStyleIndex));
        ArrayList<String> fields = new ArrayList<String>();
        //TODO Transect Info
		return layer;
	}

}

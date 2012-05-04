package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetJSONSubmittedByFiltersRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.PublisherServicePortType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.RetrieveMapsByCoverageRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FileType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.datamodel.FileArray;
import org.gcube.application.aquamaps.datamodel.MapArray;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.impl.model.CoverageDescriptor;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.gisdatamodel.stubs.LayerInfoType;

public class PublisherService extends GCUBEPortType implements
PublisherServicePortType {

	@Override
	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}


	@Override
	public MapArray retrieveMapsByCoverage(
			RetrieveMapsByCoverageRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			long starttime=System.currentTimeMillis();
			if(arg0.getSpeciesList()==null||arg0.getSpeciesList().getItems()==null||arg0.getSpeciesList().getItems().length==0) throw new Exception("No species specified");
			String[] speciesArray=arg0.getSpeciesList().getItems();
			
			Publisher publisher=ServiceContext.getContext().getPublisher();

			
			
			List<FileSet> foundFileSet=new ArrayList<FileSet>();
			List<Layer> foundLayers=new ArrayList<Layer>();
			
			
			
			
			
			//**************** Load layers by species
			logger.debug("Checking maps by coverage from Publisher, species Selection is "+Arrays.toString(speciesArray));
			
			foundLayers.addAll(publisher.getLayersBySpeciesIds(speciesArray[0]));
			
			foundFileSet.addAll(publisher.getFileSetsBySpeciesIds(speciesArray[0]));
			
			//*************** Load by submitted
//			ArrayList<Field> filter=new ArrayList<Field>();
//			Submitted s=new Submitted(0);
//			s.setIsAquaMap(true);
//			s.setGisEnabled(true);
//			s.setStatus(SubmittedStatus.Completed);
//			filter.add(s.getField(SubmittedFields.isaquamap));
//			filter.add(s.getField(SubmittedFields.gisenabled));
//			filter.add(s.getField(SubmittedFields.status));
//			
//			
//			for(Submitted found: SubmittedManager.getList(filter)){
//				try{
//					foundFileSet.add(publisher.getById(FileSet.class, found.getFileSetId()));
//				}catch(Exception e){logger.error("Exception while loading fileSet "+found.getFileSetId(),e);}
//				try{
//					foundLayers.add(publisher.getById(Layer.class, found.getGisPublishedId()));
//				}catch(Exception e){logger.error("Exception while loading layer "+found.getGisPublishedId(),e);}
//				
//			}
//						

			logger.debug("Found "+foundFileSet.size()+" related FileSet and "+foundLayers.size()+" layers, gonna form maps information..");
			
			
			HashMap<CoverageDescriptor,AquaMap> formedMaps=new HashMap<CoverageDescriptor, AquaMap>();
			
//			logger.debug("Loading FileSets...");
			
			
			
			for(FileSet fSet:foundFileSet){
				CoverageDescriptor descr=new CoverageDescriptor(fSet.getTableId(), fSet.getParameters());
//				logger.debug("Coverage : "+descr+", tableID "+fSet.getTableId()+", params "+fSet.getParameters());
				if(formedMaps.containsKey(descr)){
					logger.warn("Multiple FileSet found for Coverage, current FS ID :  "+fSet.getId()+", previous : "+formedMaps.get(descr).getFileSetId());
//					ArrayList<File> files=new ArrayList<File>();
//					for(org.gcube.application.aquamaps.publisher.impl.model.File f: fSet.getFiles())
//						files.add(new File(FileType.valueOf(f.getType()+""),publisherHost+f.getStoredUri(),f.getName()));
//					formedMaps.get(descr).getFiles().addAll(files);
				}else{
					formedMaps.put(descr, formMap(fSet));
				}
			}

//			logger.debug("Loading layers...");
			
			for(Layer l:foundLayers){				
				CoverageDescriptor descr=new CoverageDescriptor(l.getTableId(), l.getParameters());
//				logger.debug("Coverage : "+descr+", tableID "+l.getTableId()+", params "+l.getParameters());
				if(formedMaps.containsKey(descr)){
					if(formedMaps.get(descr).isGis())
						logger.warn("Multiple Layer found for Coverage, current layer ID :  "+l.getId()+", previous : "+formedMaps.get(descr).getLayerId());
					else{
						formedMaps.get(descr).setLayer(l.getLayerInfo());
						formedMaps.get(descr).setGis(true);
						formedMaps.get(descr).setLayerId(l.getId());
					}
				}else{
					formedMaps.put(descr, formMap(l));
				}
			}
			logger.debug("Found "+formedMaps.size()+" Maps in "+(System.currentTimeMillis()-starttime)+" ms");
			return AquaMap.toStubsVersion(formedMaps.values());			
		}catch(Exception e){
			logger.error("Unable to get Maps ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public FileArray getFileSetById(String arg0) throws RemoteException,
			GCUBEFault {
		try{
			Publisher publisher=ServiceContext.getContext().getPublisher();
			String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();
			FileSet fSet=publisher.getById(FileSet.class, arg0);
			if(fSet!=null){
				ArrayList<File> list=new ArrayList<File>();
				for(org.gcube.application.aquamaps.publisher.impl.model.File f: fSet.getFiles())
					list.add(new File(FileType.valueOf(f.getType()+""),publisherHost+f.getStoredUri(),f.getName()));
				return File.toStubsVersion(list);
			}else throw new Exception("FileSet with Id "+arg0+" not found");
		}catch(Exception e){
			logger.error("Unable to get FileSet ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public String getJSONSubmittedByFilters(
			GetJSONSubmittedByFiltersRequestType arg0) throws RemoteException,
			GCUBEFault {
		try{
			return SubmittedManager.getJsonList(Field.load(arg0.getFilters()), arg0.getSettings());			
		}catch(Exception e){
			logger.error("Unable to get Submitted ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	@Override
	public LayerInfoType getLayerById(String arg0) throws RemoteException,
			GCUBEFault {
		try{
			Publisher publisher=ServiceContext.getContext().getPublisher();
			Layer layer=publisher.getById(Layer.class, arg0);
			if(layer!=null){
				return layer.getLayerInfo().toStubsVersion();
			}else throw new Exception("Layer with Id "+arg0+" not found");
		}catch(Exception e){
			logger.error("Unable to get Layer ",e);
			throw new GCUBEFault("ServerSide msg: "+e.getMessage());
		}
	}


	protected AquaMap formMap(CoverageDescriptor desc)throws Exception{
		AquaMap toAdd=new AquaMap();		
		//****** FAKE DATA TODO
		toAdd.setCreationDate(System.currentTimeMillis());
		toAdd.setTitle("No Title has been set");
		toAdd.setMapType(ObjectType.SpeciesDistribution);
		toAdd.setSpeciesCsvList("dummySpecies");

		
		
		
		toAdd.setCoverage(desc.getParameters());
		Resource source=SourceManager.getById(SourceManager.getDefaultId(ResourceType.HSPEC));
		try{
			source=SourceManager.getById(Integer.parseInt(desc.getTableId()));
		}catch(Exception e){logger.warn("Unable to load resource from coverage , "+desc.getTableId(),e);}
		toAdd.setResource(source);		
		
		
		
		if(desc instanceof FileSet){
			String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();
			FileSet fSet=(FileSet)desc;
			toAdd.setAuthor(fSet.getMetaInfo().getAuthor());
			toAdd.setFileSetId(fSet.getId());
			ArrayList<File> files=new ArrayList<File>();
			for(org.gcube.application.aquamaps.publisher.impl.model.File f: fSet.getFiles())
				files.add(new File(FileType.valueOf(f.getType()+""),publisherHost+f.getStoredUri(),f.getName()));
			toAdd.setFiles(files);
		}else if(desc instanceof Layer){
			Layer l=(Layer)desc;
			toAdd.setAuthor(l.getMetaInfo().getAuthor());
			toAdd.setGis(true);
			toAdd.setLayer(l.getLayerInfo());
			toAdd.setSpeciesCsvList(CSVUtils.listToCSV(Arrays.asList(l.getSpeciesIds())));
			toAdd.setLayerId(l.getId());
		}
		return toAdd;
	}
}

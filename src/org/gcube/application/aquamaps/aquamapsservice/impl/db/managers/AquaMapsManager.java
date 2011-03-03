package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.stubs.LayerInfoType;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.File;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FileType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;

public class AquaMapsManager extends SubmittedManager{

	public static String maxSpeciesCountInACell="maxspeciescountinacell";
	public static int deleteObject(int objectId)throws Exception{
		logger.trace("Deleting obj "+objectId);
		
		PublisherImpl.getPublisher().removeAquaMapsObject(objectId);
		//TODO get from publisher layers and styles to delete
		return deleteFromTables(objectId);

	}

	public static void updateGISReferences(int objId,List<String> layerIds,List<String> layerReference)throws Exception{
		//FIXME Comment
//		setGisPublishedId(objId, layerId);
		
		setGisReference(objId, layerReference);
	}
	
	
//	public static void updateLayerAndImagesReferences(int objId,List<File> images,List<LayerInfoType> layers)throws Exception{
//		AquaMapsObject obj =PublisherImpl.getPublisher().getAquaMapsObjectById(objId);
//		
//		for(File f: references){
//			if(f.getType().equals(FileType.Layer)){
//				obj.setLayerId(f.getUuri());
//				break;
//			}
//		}
//		obj.getRelatedResources().addAll(references);
//		obj.setStatus(SubmittedStatus.Completed);
//		PublisherImpl.getPublisher().publishAquaMapsObject(obj);
//		updateStatus(objId, SubmittedStatus.Completed);
//		
//		//TODO 
//		throw new Exception("NOT IMPLEMENTED");
//		
//	}
//	

}

package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.File;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FileType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;

public class AquaMapsManager extends SubmittedManager{

	public static String maxSpeciesCountInACell="maxspeciescountinacell";
	public static int deleteObject(int objectId)throws Exception{
		logger.trace("Deleting obj "+objectId);

//		//*************** check for local files to delete
//		//*************** NB to remove after meta pubblication
//		try{
//			deletelocalFiles(objectId);
//		}catch(Exception e){
//			logger.error("Unable to delete files for object "+objectId,e);
//		}
//		try{
//			int mapId=getMapId(objectId);
//
//			List<Submitted> sharingMapObjects=getObjectByMapId(mapId);
//
//			if(sharingMapObjects.size()<2){
//				//ok to delete
//				MapsManager.delete(mapId);
//			}
//		}catch(Exception e){
//			logger.warn("Unable to find mapId or to delete references for obj: "+objectId ,e);
//		}
		
		PublisherImpl.getPublisher().removeAquaMapsObject(objectId);
		
		return deleteFromTables(objectId);

	}

//	public static List<Submitted> getObjectByMapId(int mapId)throws Exception{		
//		List<Field> filters=new ArrayList<Field>();
//		Field f=new Field();
//		f.setName(SubmittedFields.mapId.toString());
//		f.setValue(mapId+"");
//		f.setType(FieldType.INTEGER);
//		return getList(filters);
//	}


	public static void updateLayerAndImagesReferences(int objId,List<File> references)throws Exception{
		AquaMapsObject obj =PublisherImpl.getPublisher().getAquaMapsObjectById(objId);
		for(File f: references){
			if(f.getType().equals(FileType.Layer)){
				obj.setLayerId(f.getUuri());
				break;
			}
		}
		obj.getRelatedResources().addAll(references);
		obj.setStatus(SubmittedStatus.Completed);
		PublisherImpl.getPublisher().publishAquaMapsObject(obj);
		updateStatus(objId, SubmittedStatus.Completed);
	}
	

}

package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Submitted;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;

public class AquaMapsManager extends SubmittedManager{


	public static int deleteObject(int objectId)throws Exception{
		logger.trace("Deleting obj "+objectId);

		//*************** check for local files to delete
		//*************** NB to remove after meta pubblication
		try{
			deletelocalFiles(objectId);
		}catch(Exception e){
			logger.error("Unable to delete files for object "+objectId,e);
		}
		try{
			int mapId=getMapId(objectId);

			List<Submitted> sharingMapObjects=getObjectByMapId(mapId);

			if(sharingMapObjects.size()<2){
				//ok to delete
				MapsManager.delete(mapId);
			}
		}catch(Exception e){
			logger.warn("Unable to find mapId or to delete references for obj: "+objectId ,e);
		}
		return deleteFromTables(objectId);

	}

	public static List<Submitted> getObjectByMapId(int mapId)throws Exception{		
		List<Field> filters=new ArrayList<Field>();
		Field f=new Field();
		f.setName(SubmittedFields.mapId.toString());
		f.setValue(mapId+"");
		f.setType(FieldType.INTEGER);
		return getList(filters);
	}



}

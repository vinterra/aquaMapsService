package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;

public class AquaMapsManager extends SubmittedManager{

	public static String maxSpeciesCountInACell="maxspeciescountinacell";
	public static int deleteObject(int objectId)throws Exception{
		logger.trace("Deleting obj "+objectId);
		
		PublisherImpl.getPublisher().removeAquaMapsObject(objectId);
		//TODO get from publisher layers and styles to delete
		return deleteFromTables(objectId);

	}

	public static void updateGISReferences(int objId,List<String> layerIds,List<String> layerReference)throws Exception{
		setGisPublishedId(objId, layerIds);
		setGisReference(objId, layerReference);
	}

}

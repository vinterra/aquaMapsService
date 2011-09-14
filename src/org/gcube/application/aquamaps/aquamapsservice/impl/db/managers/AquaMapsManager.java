package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.AquaMapsObjectExecutionRequest;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;

public class AquaMapsManager extends SubmittedManager{

	public static String maxSpeciesCountInACell="maxspeciescountinacell";
	public static int deleteObject(int objectId)throws Exception{
		logger.trace("Deleting obj "+objectId);
		try{
		ServiceContext.getContext().getPublisher().removeAquaMapsObject(objectId);
		//TODO get from publisher layers and styles to delete
		}catch(Exception e){
			logger.warn("Unable to delete from publisher object Id "+objectId, e);
		}
		return deleteFromTables(objectId);
	}

	public static void updateGISReferences(int objId,List<String> layerIds,List<String> layerReference)throws Exception{
		setGisPublishedId(objId, layerIds);
		setGisReference(objId, layerReference);
	}

	public static int insertRequests(
			List<AquaMapsObjectExecutionRequest> requests) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			ArrayList<List<Field>> keys=new ArrayList<List<Field>>();
			for(AquaMapsObjectExecutionRequest request:requests){
				ArrayList<Field> fields=new ArrayList<Field>();
				fields.add(request.getObject().getField(SubmittedFields.serializedpath));
				fields.add(request.getObject().getField(SubmittedFields.status));
				rows.add(fields);
				ArrayList<Field> key=new ArrayList<Field>();
				key.add(request.getObject().getField(SubmittedFields.searchid));
				keys.add(key);
			}
			int toReturn= session.updateOperation(submittedTable, keys, rows);
			session.commit();
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null)session.close();}
		
	}

}

package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;

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

			List<Integer> sharingMapObjects=getObjectByMapId(mapId);

			if(sharingMapObjects.size()<2){
				//ok to delete
				MapsManager.delete(mapId);
			}
		}catch(Exception e){
			logger.warn("Unable to find mapId or to delete references for obj: "+objectId ,e);
		}
		return deleteFromTables(objectId);

	}

	public static List<Integer> getObjectByMapId(int mapId)throws Exception{		
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select "+searchId+" from "+submittedTable+" where "+AquaMapsManager.mapId+"=? ");
			ps.setInt(1, mapId);
			ArrayList<Integer> toReturn=new ArrayList<Integer>();
			ResultSet rs=ps.executeQuery();
			while(rs.next())
				toReturn.add(rs.getInt(JobManager.jobId));
			return toReturn;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}



}

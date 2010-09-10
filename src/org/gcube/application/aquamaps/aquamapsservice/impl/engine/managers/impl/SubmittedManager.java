package org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.impl;

import java.io.IOException;
import java.sql.SQLException;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.AquaMapsManagerI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.SubmittedManagerI;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.structure.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.structure.SubmittedTable;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SubmittedManager implements SubmittedManagerI {

	private static GCUBELog logger= new GCUBELog(SubmittedManager.class);
	
	protected SubmittedManager(){}
	private static SubmittedManager instance =new SubmittedManager();
	public static SubmittedManagerI get(){return instance;}
	
	public String getAuthor(int submittedId) throws Exception {
		return ManagersUtils.getString(SubmittedTable.TABLE_NAME,SubmittedTable.author,submittedId, SubmittedTable.searchId);
	}

	public String getGIS(int submittedId) throws Exception {
		return ManagersUtils.getString(SubmittedTable.TABLE_NAME,SubmittedTable.gis,submittedId, SubmittedTable.searchId);
	}

	public String getHCAFTable(int submittedId) throws Exception {
		return ManagersUtils.getString(SubmittedTable.TABLE_NAME,SubmittedTable.HCAF,submittedId, SubmittedTable.searchId);
	}

	public String getHSPECTable(int jobId) throws Exception {
		return ManagersUtils.getString(SubmittedTable.TABLE_NAME,SubmittedTable.HSPEC,jobId, SubmittedTable.searchId);
	}

	public String getHSPENTable(int jobId) throws Exception {
		return ManagersUtils.getString(SubmittedTable.TABLE_NAME,SubmittedTable.HSPEN,jobId, SubmittedTable.searchId);
	}

	public void setHCAFTable(String HCAFId, int jobId) throws Exception {
		ManagersUtils.setString(SubmittedTable.TABLE_NAME,SubmittedTable.HCAF,jobId, HCAFId,SubmittedTable.searchId);
	}

	public void setHSPECTable(String HCAFId, int jobId) throws Exception {
		ManagersUtils.setString(SubmittedTable.TABLE_NAME,SubmittedTable.HSPEC,jobId, HCAFId,SubmittedTable.searchId);
	}

	public void setHSPENTable(String HCAFId, int jobId) throws Exception {
		ManagersUtils.setString(SubmittedTable.TABLE_NAME,SubmittedTable.HSPEN,jobId, HCAFId,SubmittedTable.searchId);
	}

	public void updateGISData(int submittedId, String GeoId) throws Exception {
		ManagersUtils.setString(SubmittedTable.TABLE_NAME,SubmittedTable.gis,submittedId, GeoId,SubmittedTable.searchId);
	}

	public void updateStatus(int jobId, SubmittedStatus status) throws SQLException,
			IOException, Exception {
		ManagersUtils.setString(SubmittedTable.TABLE_NAME, SubmittedTable.status, jobId, status.toString(), SubmittedTable.searchId);
	}

	public boolean isAquaMaps(int submittedId)throws Exception{
		return (Boolean) ManagersUtils.getObject(SubmittedTable.TABLE_NAME, SubmittedTable.isAquaMap, submittedId, SubmittedTable.searchId);
	}
	
	
	public void delete(int submittedId) throws Exception {
		if(isAquaMaps(submittedId)) AquaMapsManager.get().delete(submittedId);
		else JobManager.get().delete(submittedId);
	}
	
	
	
	
	
	
}

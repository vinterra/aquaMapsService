package org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.structure.SubmittedStatus;


public interface SubmittedManagerI {

	public void setHCAFTable(String HCAFId,int jobId)throws Exception;
	public void setHSPENTable(String HCAFId,int jobId)throws Exception;
	public void setHSPECTable(String HCAFId,int jobId)throws Exception;
	public void updateStatus(int jobId,SubmittedStatus status)throws Exception;
	public void updateGISData(int submittedId,String GeoId)throws Exception;
	
	public String getHCAFTable(int jobId)throws Exception;
	public String getHSPENTable(int jobId)throws Exception;
	public String getHSPECTable(int jobId)throws Exception;
	
	public String getGIS(int submittedId) throws Exception;
	public String getAuthor(int submittedId)throws Exception;
	
	public void delete(int submittedId)throws Exception;
	
	
}

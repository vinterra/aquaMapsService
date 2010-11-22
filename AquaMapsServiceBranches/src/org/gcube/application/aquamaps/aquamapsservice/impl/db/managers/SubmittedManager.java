package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobUtils;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SubmittedManager {

	protected static GCUBELog logger= new GCUBELog(SubmittedManager.class);	
	
	protected static final String title="title";
	protected static final String author="author";
	protected static final String jobId="jobId";
	protected static final String selectionCriteria="selectionCriteria";
	protected static final String date="date";
	protected static final String status="status";
	protected static final String searchId="searchId";
	protected static final String type="type";
	protected static final String isAquaMap="isAquaMap";
	protected static final String saved="saved";
	protected static final String sourceHCAF="sourceHCAF";
	protected static final String sourceHSPEN="sourceHSPEN";
	protected static final String sourceHSPEC="sourceHSPEC";
	protected static final String gis="gis";
	protected static final String mapId="mapId";
	
	protected static final String submittedTable="submitted";
	
	protected static Object getField(int id, String field)throws Exception{
		DBSession session=null;
		try{			
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from "+submittedTable+" where "+searchId+" = ?");
			ps.setInt(1, id);
			ResultSet rs= ps.executeQuery();
			if(rs.next())
				return rs.getObject(field);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	protected static int updateField(int id,String field,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update "+submittedTable+" set "+field+" = ? where "+searchId+" = ?");
			ps.setObject(1, value);
			ps.setInt(2, id);
			return ps.executeUpdate();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	@Deprecated
	protected static int deletelocalFiles(int submittedId)throws Exception{
		DBSession session=null;
		try{
			int count=0;
			session=DBSession.openSession(DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from Files where owner=?");
			ps.setInt(1, submittedId);
			ResultSet rs=ps.executeQuery();
			String publishedBasePath=JobUtils.getBasePublicPath();
			String localBasePath=JobUtils.getBaseLocalPath();
			while(rs.next()){
				String path=rs.getString("Path");
				path.replaceFirst(publishedBasePath, localBasePath);
//				path.replace(path.indexOf(publishedBasePath), publishedBasePath.length(), localBasePath);
				logger.trace("Trying to delete "+path);
				File f=new File(path);
				try{
					if(f.exists()){
						count++;
						if(f.exists()) FileUtils.forceDelete(f);
						File dir=f.getParentFile();
						if(dir.listFiles().length==0) FileUtils.forceDelete(dir);
					}
				}catch(IOException err){
					logger.error("unable to delete file or parent directory",err);
				}
			}
			return count;
		}catch(Exception e){
			logger.warn("impossible to retrieve files for obj "+submittedId);
			throw e;
		}
	}
	
	
	protected static int deleteFromTables(int submittedId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("DELETE from "+submittedTable+" where "+searchId+" = ? ");
			ps.setObject(1, submittedId);
			return ps.executeUpdate();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	
	public static int delete(int submittedId)throws Exception{
		if(isAquaMap(submittedId)) return AquaMapsManager.deleteObject(submittedId);
		else return JobManager.deleteJob(submittedId);
	}
	
	
	// **************************************** getters *********************************
	
	public static int getHCAFTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,sourceHCAF);		
	}
	public static int getHSPENTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,sourceHSPEN);		
	}
	public static int getHSPECTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,sourceHSPEC);		
	}
	public static String getGIS(int submittedId) throws Exception{
		return (String) getField(submittedId,gis);
	}
	public static SubmittedStatus getStatus(int submittedId)throws Exception{
		return SubmittedStatus.valueOf((String) getField(submittedId,status));
	}
	public static String getAuthor(int submittedId)throws Exception{
		return (String) getField(submittedId,author);
	}
	
	public static Boolean isAquaMap(int submittedId)throws Exception{
		return (Boolean) getField(submittedId,isAquaMap);
	}
	
	public static int getMapId(int submittedId)throws Exception{
		return (Integer) getField(submittedId,mapId);
	}
	
	//**************************************** setters **********************************
	
	public static int setHCAFTable(int HCAFId,int jobId)throws Exception{
		return updateField(jobId,sourceHCAF,HCAFId);
	}
	public static int setHSPENTable(int HCAFId,int jobId)throws Exception{
		return updateField(jobId,sourceHSPEN,HCAFId);
	}
	public static int setHSPECTable(int HCAFId,int jobId)throws Exception{
		return updateField(jobId,sourceHCAF,HCAFId);
	}
	public static int updateGISData(int submittedId,String GeoId)throws Exception{
		return updateField(submittedId,gis,GeoId);
	}
	
	public static void updateStatus(int jobId,SubmittedStatus statusValue)throws SQLException, IOException, Exception{
		updateField(jobId,status,statusValue.toString());		
		//		updateProfile(toUpdate.getName(),toUpdate.getId(),makeJobProfile(toUpdate),generationDetails.getFirstLevelDirName(),generationDetails.getSecondLevelDirName(),c);
		logger.trace("done Job status updateing status : "+statusValue.toString());
	}
	

	
}

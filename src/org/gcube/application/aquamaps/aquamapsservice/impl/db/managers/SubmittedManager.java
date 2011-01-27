package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Submitted;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class SubmittedManager {

	protected static GCUBELog logger= new GCUBELog(SubmittedManager.class);

	protected static final String submittedTable="submitted";

	protected static Object getField(int id, SubmittedFields field)throws Exception{
		DBSession session=null;
		try{			
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from "+submittedTable+" where "+SubmittedFields.searchId+" = ?");
			ps.setInt(1, id);
			ResultSet rs= ps.executeQuery();
			if(rs.next())
				return rs.getObject(field.toString());
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	protected static int updateField(int id,SubmittedFields field,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update "+submittedTable+" set "+field+" = ? where "+SubmittedFields.searchId+" = ?");
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
	public static String getProfile(int submitted)throws Exception{
		//FIXME to change behaviour
		DBSession session=null;
		try{
			logger.trace("Retrieving profile for "+submitted);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select * from Files where owner=?");
			ps.setInt(1,submitted);
			ResultSet rs=ps.executeQuery();
			String publishedBasePath=FileManager.getBasePublicPath();
			String localBasePath=FileManager.getBaseLocalPath();
			String path=null;
			while(rs.next()){
				if(rs.getString("Type").equalsIgnoreCase("xml")){
					path=rs.getString("Path");
					break;
				}
			}
			path.replaceFirst(publishedBasePath, localBasePath);
			logger.trace("trying to read "+path);
			return ServiceUtils.URLtoString(path);			
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
			String publishedBasePath=FileManager.getBasePublicPath();
			String localBasePath=FileManager.getBaseLocalPath();
			while(rs.next()){
				String path=rs.getString("Path");
				path.replaceFirst(publishedBasePath, localBasePath);
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
			PreparedStatement ps= session.preparedStatement("DELETE from "+submittedTable+" where "+SubmittedFields.searchId+" = ? ");
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
		return (Integer) getField(jobId,SubmittedFields.sourceHCAF);		
	}
	public static int getHSPENTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourceHSPEN);		
	}
	public static int getHSPECTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourceHSPEC);		
	}
	public static String getGIS(int submittedId) throws Exception{
		return (String) getField(submittedId,SubmittedFields.gis);
	}
	public static SubmittedStatus getStatus(int submittedId)throws Exception{
		return SubmittedStatus.valueOf((String) getField(submittedId,SubmittedFields.status));
	}
	public static String getAuthor(int submittedId)throws Exception{
		return (String) getField(submittedId,SubmittedFields.author);
	}

	public static Boolean isAquaMap(int submittedId)throws Exception{
		return (Boolean) getField(submittedId,SubmittedFields.isAquaMap);
	}

	public static int getMapId(int submittedId)throws Exception{
		return (Integer) getField(submittedId,SubmittedFields.mapId);
	}



	//**************************************** setters **********************************

	public static int setHCAFTable(int HCAFId,int jobId)throws Exception{
		return updateField(jobId,SubmittedFields.sourceHCAF,HCAFId);
	}
	public static int setHSPENTable(int HCAFId,int jobId)throws Exception{
		return updateField(jobId,SubmittedFields.sourceHSPEN,HCAFId);
	}
	public static int setHSPECTable(int HCAFId,int jobId)throws Exception{
		return updateField(jobId,SubmittedFields.sourceHCAF,HCAFId);
	}
	public static int updateGISData(int submittedId,String GeoId)throws Exception{
		return updateField(submittedId,SubmittedFields.gis,GeoId);
	}

	public static int markSaved(int submittedId)throws Exception{
		return updateField(submittedId,SubmittedFields.saved,true);
	}

	//******** Logic


	public static void updateStatus(int jobId,SubmittedStatus statusValue)throws SQLException, IOException, Exception{
		updateField(jobId,SubmittedFields.status,statusValue.toString());		
		//		updateProfile(toUpdate.getName(),toUpdate.getId(),makeJobProfile(toUpdate),generationDetails.getFirstLevelDirName(),generationDetails.getSecondLevelDirName(),c);
		logger.trace("done Job status updateing status : "+statusValue.toString());
	}






	public static List<Submitted> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			return Submitted.loadResultSet(session.executeFilteredQuery(filters, submittedTable,null,null));
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	public static String getJsonList(List<Field> filters,String orderBy, String orderDir, int limit, int offset)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			return DBUtils.toJSon(session.executeFilteredQuery(filters, submittedTable,null,null),offset, offset+limit);
		}catch(Exception e){throw e;}
		finally{session.close();}
	}

	@Deprecated
	public static void updateProfile(String resName,int resId,String resProfile,String firstLevelDir,String secondLevelDir) throws Exception{
		DBSession c=DBSession.openSession(PoolManager.DBType.mySql);
		Collection<String> toUpdateProfile=new ArrayList<String>();
		File dir=new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+resName);
		dir.mkdirs();
		File file=new File(dir.getAbsolutePath(),resName+".xml");
		//file.mkdirs();
		FileWriter writer=new FileWriter(file);
		writer.write(resProfile);
		writer.close();
		toUpdateProfile.add(file.getAbsolutePath());
		String path=FileManager.publishInternal(firstLevelDir,secondLevelDir,toUpdateProfile);
		logger.trace("Profile for "+resName+" created, gonna update DB");
		PreparedStatement ps=c.preparedStatement("Update Files set Path=? where owner=? and type ='XML'");
		ps.setString(1, path+file.getName());
		ps.setInt(2, resId);		
		if(ps.executeUpdate()==0){
			logger.trace("Entry not found for profile, gonna create it");
			PreparedStatement pps =c.preparedStatement("INSERT INTO Files (published, nameHuman , Path, Type, owner) VALUE(?, ?, ?, ?, ?)");
			pps.setBoolean(1,true);
			pps.setString(2,"Metadata");
			pps.setString(3, path+file.getName());
			pps.setString(4,"XML");
			pps.setInt(5,resId);
			pps.execute();			
		}
		c.close();
	}
}

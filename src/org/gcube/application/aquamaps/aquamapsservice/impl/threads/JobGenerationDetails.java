package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import it.cnr.isti.geoserverInteraction.GeoserverCaller;
import it.cnr.isti.geoserverInteraction.bean.LayerRest;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GroupGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.TaxonomyType;

public class JobGenerationDetails {
	private static GCUBELog logger= new GCUBELog(JobGenerationDetails.class);
	//	public static final String Biodiversity="Biodiversity";
	//	public static final String SpeciesDistribution="SpeciesDistribution";

	public enum SpeciesStatus{
		toCustomize,toGenerate,Ready
	}

	public enum Status {
		Pending,Simulating,Generating,Publishing,Completed,Error
	}
	private enum Sources{
		HCAF,HSPEC,HSPEN
	}


	private static void updateSource(Sources sourceField,String source,int jobId)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update submitted set "+sourceField.toString()+" = ? where searchId = ?");
			ps.setString(1, source);
			ps.setInt(2,jobId);
			ps.execute();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}	
	private static String getSource(Sources source,int jobId)throws Exception{
		DBSession session = null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select "+source.toString()+" from submitted where searchId = ?");
			ps.setInt(1, jobId);
			ResultSet rs =ps.executeQuery();
			rs.first();
			String toReturn=rs.getString(1);
			//		session.close();
			return toReturn;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static void setHCAFTable(String HCAFId,int jobId)throws Exception{
		String toSet=(HCAFId==null)?DBCostants.HCAF_D:HCAFId;
		updateSource(Sources.HCAF,toSet,jobId);
	}

	public static void setHSPENTable(String HCAFId,int jobId)throws Exception{
		String toSet=(HCAFId==null)?DBCostants.HSPEN:HCAFId;
		updateSource(Sources.HSPEN,toSet,jobId);
	}
	public static void setHSPECTable(String HCAFId,int jobId)throws Exception{
		String toSet=(HCAFId==null)?DBCostants.HSPEC:HCAFId;
		updateSource(Sources.HSPEC,toSet,jobId);
	}

	public static String getHCAFTable(int jobId)throws Exception{
		String toReturn= getSource(Sources.HCAF, jobId);
		return (toReturn==null)?DBCostants.HCAF_D:toReturn;
	}

	public static String getHSPENTable(int jobId)throws Exception{
		String toReturn= getSource(Sources.HSPEN, jobId);
		return (toReturn==null)?DBCostants.HSPEN:toReturn;		
	}

	public static String getHSPECTable(int jobId)throws Exception{
		String toReturn= getSource(Sources.HSPEC, jobId);
		return (toReturn==null)?DBCostants.HSPEC:toReturn;
	}
	public static void addToDropTableList(int jobId,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Insert into "+DBCostants.toDropTables+" (jobId,tableName) VALUE(?,?)");
			ps.setInt(1, jobId);
			ps.setString(2, tableName);
			ps.executeUpdate();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void addToDeleteTempFolder(int jobId,String folderName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Insert into "+DBCostants.tempFolders+" (jobId,folderName) VALUE(?,?)");
			ps.setInt(1, jobId);
			ps.setString(2, folderName);
			ps.executeUpdate();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void updateStatus(int jobId,Status status)throws SQLException, IOException, Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);		
			//		toUpdate.setStatus(status.toString());
			PreparedStatement ps=session.preparedStatement(DBCostants.submittedStatusUpdating);
			ps.setString(1, status.toString());		
			ps.setInt(2,jobId);
			ps.execute();		
			//		updateProfile(toUpdate.getName(),toUpdate.getId(),makeJobProfile(toUpdate),generationDetails.getFirstLevelDirName(),generationDetails.getSecondLevelDirName(),c);
			logger.trace("done Job status updateing status : "+status.toString());
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void updateSpeciesStatus(int jobId,String speciesId[],SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Update "+DBCostants.selectedSpecies+" set status = ? where jobId=? AND speciesId=?");
			ps.setString(1, status.toString());
			ps.setInt(2, jobId);
			for(String specId:speciesId){
				ps.setString(3,specId);
				ps.execute();
			}
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=null;
			if(status!=null){
				ps=session.preparedStatement("Select speciesId from "+DBCostants.selectedSpecies+" where jobId=? AND status=?");
				ps.setInt(1, jobId);
				ps.setString(2,status.toString());
			}else{
				ps=session.preparedStatement("Select speciesId from "+DBCostants.selectedSpecies+" where jobId=?");
				ps.setInt(1, jobId);
			}
			ResultSet rs=ps.executeQuery();
			ArrayList<String> toReturn=new ArrayList<String>(); 
			while(rs.next()){
				toReturn.add(rs.getString("speciesId"));
			}
			//		session.close();
			return toReturn.toArray(new String[toReturn.size()]);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static boolean isJobComplete(int jobId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);		
			PreparedStatement ps =session.preparedStatement("Select count(*) from submitted where jobId=? AND status!=? AND status!=?");
			ps.setInt(1, jobId);
			ps.setString(2, Status.Error.toString());
			ps.setString(3, Status.Completed.toString());
			ResultSet rs=ps.executeQuery();
			rs.first();
			boolean toReturn=rs.getInt(1)==0;
			//		session.close();
			return toReturn;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void cleanTemp(int jobId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			logger.debug("cleaning tables for : "+jobId);
			PreparedStatement ps=session.preparedStatement("Select tableName from "+DBCostants.toDropTables+" where jobId=?");
			ps.setInt(1, jobId);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				String table=rs.getString(1);
				session.executeUpdate("DROP TABLE IF EXISTS "+table);
			}
			ps=session.preparedStatement("Delete from "+DBCostants.toDropTables+" where jobId=?");
			ps.setInt(1, jobId);
			ps.execute();			
			logger.debug("cleaning folders for : "+jobId);
			ps=session.preparedStatement("Select folderName from "+DBCostants.tempFolders+" where jobId=?");
			ps.setInt(1, jobId);
			rs=ps.executeQuery();
			while(rs.next()){
				String folder=rs.getString(1);
				try{					
					File tempDir=new File(folder);
					if(tempDir.exists()){
						FileUtils.cleanDirectory(tempDir);
						FileUtils.deleteDirectory(tempDir);
					}else logger.warn("Wrong file name "+folder);

				}catch(Exception e1){
					logger.debug("unable to delete temp Folder : "+folder,e1);
				}
			}
			ps=session.preparedStatement("Delete from "+DBCostants.tempFolders+" where jobId=?");
			ps.setInt(1, jobId);
			ps.execute();
			logger.debug("cleaning speceisSelection for : "+jobId);
			ps=session.preparedStatement("Delete from "+DBCostants.selectedSpecies+" where jobId=?");
			ps.setInt(1, jobId);
			ps.execute();
			
			
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static boolean isSpeciesListReady(int jobId,String[] toCheck)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select status from "+DBCostants.selectedSpecies+" where jobId =? and speciesId=?");
			ps.setInt(1, jobId);
			for(String id:toCheck){
				ps.setString(2, id);
				ResultSet rs =ps.executeQuery();
				rs.first();
				if(!(rs.getString("status").equalsIgnoreCase(SpeciesStatus.Ready.toString())))
					return false;
			}
			//		session.close();
			return true;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	public static void updateGISData(int submittedId,String GeoId)throws Exception{
		DBSession session=null;
		try{
			logger.trace("Setting GIS data "+GeoId+" for submitted Id "+submittedId);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Update submitted set gis=? where searchId=?");
			ps.setString(1, GeoId);
			ps.setInt(2, submittedId);
			logger.trace("updated "+ps.executeUpdate()+" entries");
		}catch(Exception e ){
			logger.error("Unexpected Error", e);
			throw e;
		}finally{
			session.close();
		}
	}
	
	public static String getGIS(int submittedId) throws Exception{
		DBSession session=null;
		try{
			logger.trace("Getting gis for submitted "+submittedId);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select gis from submitted where searchId =?");
			ps.setInt(1, submittedId);
			ResultSet rs= ps.executeQuery();
			rs.first();
			return rs.getString(1);
		}catch(Exception e ){
			logger.error("Unexpected Error", e);
			throw e;
		}finally{
			session.close();
		}
	}
	
	
	public static void createGroup (int jobId)throws Exception{
		DBSession session=null;
		try{
			logger.trace("Starting job Id : "+jobId+" layers group creation ..");
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps =session.preparedStatement(DBCostants.AquaMapsListPerJob);
			ps.setInt(1, jobId);
			ResultSet rs=ps.executeQuery();
			logger.trace("Looking for generated layers");
			ArrayList<String> layers=new ArrayList<String>();
			while(rs.next()){
				String objectFeature=rs.getString("gis");
				if((objectFeature!=null)&&(!objectFeature.equalsIgnoreCase("null")))
					layers.add(objectFeature);
			}
			session.close();
			if(layers.size()>0){
				logger.trace("found "+layers.size()+" generated layer(s), looking for related style(s)");
				GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
				GroupGenerationRequest req=new GroupGenerationRequest();
				req.setLayers(layers);
				for(String layerId:layers){
					LayerRest lRest=caller.getLayer(layerId);
					req.getStyles().put(layerId, lRest.getDefaultStyle());
				}								
				req.setName(String.valueOf(jobId));				
				req.setSubmittedId(jobId);
				if(GeneratorManager.requestGeneration(req))logger.trace("Generation of jobId "+jobId+" layers group complete");
				else throw new Exception("Unable to generate Group");
			}else logger.trace("No generated layers found for job Id "+jobId);
		}catch(Exception e){
			logger.error("Unable to complete group "+jobId+" generation",e);
			throw e;
		}finally{
			if(!session.getConnection().isClosed())session.close();
		}
	}
	
	
	public static Status getStatus(int submittedId)throws Exception{
		DBSession session=null;
		try{
			logger.trace("Retrieving status for submitted id "+submittedId);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select status from submitted where searchId=?");
			ps.setInt(1, submittedId);
			ResultSet rs= ps.executeQuery();
			if(rs.next()) return Status.valueOf(rs.getString(1));
			else throw new Exception("Status not found");			
		}catch(Exception e){
			logger.error("Unable to retrieve status",e);
			throw e;
		}finally{
			if(!session.getConnection().isClosed())session.close();
		}
	}
	
	public static boolean isSpeciesSetCustomized(int submittedId,String[] ids)throws Exception{
		DBSession session=null;		
		try{
			logger.trace("Checking species customizations flag..");
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select isCustomized from "+DBCostants.selectedSpecies+" where jobId=? AND speciesId=?");
			ps.setInt(1, submittedId);
			for(String id:ids){
				ps.setString(2, id);
				ResultSet rs= ps.executeQuery();
				if(rs.next()){
					if(!rs.getBoolean(1)) return false;
				}else throw new Exception("customized flag not found for species "+id+" under "+submittedId+" selection");
			}			
			return true;
		}catch(Exception e){
			logger.error("unable to check species customization flag",e);
			throw e;
		}finally{
			if(!session.getConnection().isClosed())session.close();
		}
	}
	
	public static String getAuthor(int submittedId)throws Exception{
		DBSession session=null;
		try{
			logger.trace("Retrieving status for submitted id "+submittedId);
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select author from submitted where searchId=?");
			ps.setInt(1, submittedId);
			ResultSet rs= ps.executeQuery();
			if(rs.next())
				return rs.getString(1);
			else throw new Exception("Author not found for "+submittedId);
		}catch(Exception e){
			logger.error("Unable to retrieve status",e);
			throw e;
		}finally{
			if(!session.getConnection().isClosed())session.close();
		}
	}
	
}

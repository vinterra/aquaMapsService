package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.common.core.utils.logging.GCUBELog;

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
			session= DBSession.openSession();
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
			session= DBSession.openSession();
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

	public synchronized static void setHCAFTable(String HCAFId,int jobId)throws Exception{
		String toSet=(HCAFId==null)?DBCostants.HCAF_D:HCAFId;
		updateSource(Sources.HCAF,toSet,jobId);
	}

	public synchronized static void setHSPENTable(String HCAFId,int jobId)throws Exception{
		String toSet=(HCAFId==null)?DBCostants.HSPEN:HCAFId;
		updateSource(Sources.HSPEN,toSet,jobId);
	}
	public synchronized static void setHSPECTable(String HCAFId,int jobId)throws Exception{
		String toSet=(HCAFId==null)?DBCostants.HSPEC:HCAFId;
		updateSource(Sources.HSPEC,toSet,jobId);
	}

	public synchronized static String getHCAFTable(int jobId)throws Exception{
		String toReturn= getSource(Sources.HCAF, jobId);
		return (toReturn==null)?DBCostants.HCAF_D:toReturn;
	}

	public synchronized static String getHSPENTable(int jobId)throws Exception{
		String toReturn= getSource(Sources.HSPEN, jobId);
		return (toReturn==null)?DBCostants.HSPEN:toReturn;		
	}

	public synchronized static String getHSPECTable(int jobId)throws Exception{
		String toReturn= getSource(Sources.HSPEC, jobId);
		return (toReturn==null)?DBCostants.HSPEC:toReturn;
	}
	public synchronized static void addToDropTableList(int jobId,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();
			PreparedStatement ps= session.preparedStatement("Insert into "+DBCostants.toDropTables+" (jobId,tableName) VALUE(?,?)");
			ps.setInt(1, jobId);
			ps.setString(2, tableName);
			ps.execute();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public synchronized static void updateStatus(int jobId,Status status)throws SQLException, IOException, Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();		
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
	public synchronized static void updateSpeciesStatus(int jobId,String speciesId[],SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();
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
	public synchronized static String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();
			PreparedStatement ps=session.preparedStatement("Select speciesId from "+DBCostants.selectedSpecies+" where jobId=? AND status=?");
			ps.setInt(1, jobId);
			ps.setString(2,status.toString());
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
	public synchronized static boolean isJobComplete(int jobId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();		
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
	public synchronized static void cleanTemp(int jobId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();
			PreparedStatement ps=session.preparedStatement("Select tableName from "+DBCostants.toDropTables+" where jobId=?");
			ps.setInt(1, jobId);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				String table=rs.getString(1);
				session.executeUpdate("DROP TABLE IF EXISTS "+table);
			}
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public synchronized static boolean isSpeciesListReady(int jobId,String[] toCheck)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession();
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
}

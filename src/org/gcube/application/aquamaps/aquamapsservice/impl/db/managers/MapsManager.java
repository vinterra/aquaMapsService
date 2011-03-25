package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.common.core.utils.logging.GCUBELog;

public class MapsManager {

	
	private static GCUBELog logger= new GCUBELog(MapsManager.class);
	
	
		public static final String name="distributionMaps";
		public static final String Id="mapId";
		public static final String hspen="hspenId";
		public static final String hcaf="hcafId";
		public static final String species="speciesId";
		public static final String mapBasePath="mapBasePath";
		public static final String layerURI="layerURI";
		
		
	

	public static void delete(int mapId)throws Exception{
		//TODO implement deletion from geoserver && publisher
	}

	public static int registerDistributionMap(int hspenId,int hcafId, int internalSpeciesId, String mapBasePath, String layerURI)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			String insertionQuery="INSERT INTO "+name+" ("+hcaf+","+hspen+","+species+","+mapBasePath+","+layerURI+
			") values (?,?,?,?,?)";
			PreparedStatement ps=session.getConnection().prepareStatement(insertionQuery, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, hcafId);
			ps.setInt(2, hspenId);
			ps.setInt(3, internalSpeciesId);
			ps.setString(4,mapBasePath);
			ps.setString(5, layerURI);
			ps.executeUpdate();
			ResultSet rs=ps.getGeneratedKeys();
			rs.first();			
			return rs.getInt(1);			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static void updateDistributionMapLayerUri(int mapId, String layerUri)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update "+name+" set "+layerURI+" = ? where "+Id+"= ?");
			ps.setString(1, layerUri);
			ps.setInt(2,mapId);
			ps.execute();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static void updateDistributionMapBasePath(int mapId, String basePath)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Update "+name+" set "+mapBasePath+" = ? where "+Id+"= ?");
			ps.setString(1, basePath);
			ps.setInt(2,mapId);
			ps.execute();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	/**
	 * @return 0 if the map is not yet generated
	 */
	
	
	public static int getDistributionMapId(int hcafvalue,int hspenvalue , int speciesvalue)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			logger.trace("looking for mapId for speciesId = "+speciesvalue+", hcaf = "+hcafvalue+", hspen = "+hspenvalue);
			PreparedStatement ps= session.preparedStatement("SELECT "+Id+" from "+name+" where "+hcaf+"=? AND "+hspen+"=? AND "+species+"=?");
			ps.setInt(1, hcafvalue);
			ps.setInt(2, hspenvalue);
			ps.setInt(3,speciesvalue);
			ResultSet rs= ps.executeQuery();
			int toReturn=0;
			if(rs.next()) {
				toReturn=rs.getInt(Id);
				logger.trace("found id "+toReturn);
			}else logger.trace(" no maps found");
			return toReturn;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static String getDistributionMapBasePath(int mapId)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select "+mapBasePath+" from "+name+" where "+Id+"=?");			
			ps.setInt(1,mapId);
			ResultSet rs=ps.executeQuery();
			rs.first();
			return rs.getString(mapBasePath);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static String getDistributionMapLayerUri(int mapId)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select "+layerURI+" from "+name+" where "+Id+"=?");			
			ps.setInt(1,mapId);
			ResultSet rs=ps.executeQuery();
			rs.first();
			return rs.getString(layerURI);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	
}

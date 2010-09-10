package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.db.PoolManager;

public class MapRegistration {

	private static class distributionTable{
		static final String name="distributionMaps";
		static final String Id="mapId";
		static final String hspen="hspenId";
		static final String hcaf="hcafId";
		static final String species="speciesId";
		static final String mapBasePath="mapBasePath";
		static final String layerURI="layerURI";
		
		
	}
	
	
	
	public static int registerDistributionMap(int hspenId,int hcafId, int internalSpeciesId, String mapBasePath, String layerURI)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			String insertionQuery="INSERT INTO "+distributionTable.name+" ("+distributionTable.hcaf+","+distributionTable.hspen+","+distributionTable.species+","+distributionTable.mapBasePath+","+distributionTable.layerURI+
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
			PreparedStatement ps= session.preparedStatement("Update "+distributionTable.name+" set "+distributionTable.layerURI+" = ? where "+distributionTable.Id+"= ?");
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
			PreparedStatement ps= session.preparedStatement("Update "+distributionTable.name+" set "+distributionTable.mapBasePath+" = ? where "+distributionTable.Id+"= ?");
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
	
	
	public static int getDistributionMapId(int hcaf,int hspen, int species)throws Exception{
		DBSession session=null;
		try{
			session= DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("SELECT "+distributionTable.Id+" from "+distributionTable.name+" where "+distributionTable.hcaf+"=? AND "+distributionTable.hspen+"=? AND "+distributionTable.species+"=?");
			ps.setInt(1, hcaf);
			ps.setInt(2, hspen);
			ps.setInt(3,species);
			ResultSet rs= ps.executeQuery();
			if(rs.first()) return rs.getInt(distributionTable.Id);
			else return 0;
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
			PreparedStatement ps= session.preparedStatement("Select "+distributionTable.mapBasePath+" from "+distributionTable.name+" where "+distributionTable.Id+"=?");			
			ps.setInt(1,mapId);
			ResultSet rs=ps.executeQuery();
			rs.first();
			return rs.getString(distributionTable.mapBasePath);
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
			PreparedStatement ps= session.preparedStatement("Select "+distributionTable.layerURI+" from "+distributionTable.name+" where "+distributionTable.Id+"=?");			
			ps.setInt(1,mapId);
			ResultSet rs=ps.executeQuery();
			rs.first();
			return rs.getString(distributionTable.layerURI);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	} 
}

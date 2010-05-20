package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;

public class PoolManager {

	public static enum DBType{
		mySql,postGIS
	}
	
	private static GenericObjectPool mySqlconnectionPool; 
	private static ConnectionFactory mySqlconnectionFactory;
	private static PoolableConnectionFactory mySqlpoolableConnectionFactory;
	private static PoolingDriver mySqldriver;
	
	private static final String mySqlPoolName="mySqlPool";
	private static final String postGISPoolName="postGISPool";
	//TODO load from properties 
	public static final String postGis_dbtype = "postgis";
	public static final String postGis_host = "geoserver.d4science-ii.research-infrastructures.eu";
	public static final String postGis_port = "5432";
	public static final String postGis_database = "aquamapsdb";
	public static final String postGis_user = "postgres";
	public static final String postGis_passwd = "d4science2";

	
	private static GenericObjectPool postGISconnectionPool; 
	private static ConnectionFactory postGISconnectionFactory;
	private static PoolableConnectionFactory postGISpoolableConnectionFactory;
	private static PoolingDriver postGISdriver;
	
	
	
	static{
		//MYSQL
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mySqlconnectionPool = new GenericObjectPool(null);
		mySqlconnectionPool.setMaxActive(30);		
//		mySqlconnectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/aquamaps_DB",  ServiceContext.getContext().getDbUsername(), ServiceContext.getContext().getDbPassword());
		mySqlconnectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://wn06.research-infrastructures.eu:3306/aquamaps_DB",  "root", "mybohemian");
		mySqlpoolableConnectionFactory = new PoolableConnectionFactory(mySqlconnectionFactory,mySqlconnectionPool,
				new StackKeyedObjectPoolFactory(),null,false,true);
		mySqldriver = new PoolingDriver();
		mySqldriver.registerPool(mySqlPoolName,mySqlconnectionPool);
		
		//POSTGIS
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		postGISconnectionPool = new GenericObjectPool(null);
		postGISconnectionPool.setMaxActive(30);
		
		postGISconnectionFactory = new DriverManagerConnectionFactory("jdbc:"+"postgresql"+"://"+postGis_host+":"+
				postGis_port+"/"+postGis_database,postGis_user, postGis_passwd);
		
//		connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/aquamaps_DB",  ServiceContext.getContext().getDbUsername(), ServiceContext.getContext().getDbPassword());
//		connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/prova",  "root","rootpwd");
		postGISpoolableConnectionFactory = new PoolableConnectionFactory(postGISconnectionFactory,postGISconnectionPool,
				new StackKeyedObjectPoolFactory(),null,false,true);
		postGISdriver = new PoolingDriver();
		postGISdriver.registerPool(postGISPoolName,postGISconnectionPool);
	}
	
	
	public static Connection getConnection(DBType type)throws Exception{
		switch(type){
		case mySql : return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+mySqlPoolName);
		case postGIS : return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+postGISPoolName);
		}
		throw new Exception("wrong type "+type.toString());
	}
	
}

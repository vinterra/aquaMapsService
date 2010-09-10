package org.gcube.application.aquamaps.aquamapsservice.impl.engine.db;

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
	

	
	private static GenericObjectPool postGISconnectionPool; 
	private static ConnectionFactory postGISconnectionFactory;
	private static PoolableConnectionFactory postGISpoolableConnectionFactory;
	private static PoolingDriver postGISdriver;
	
	private static String validationQUERY="Select 1";
	
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
		mySqlconnectionPool.setTestOnBorrow(true);
		mySqlconnectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/aquamaps_DB",  ServiceContext.getContext().getDbUsername(), ServiceContext.getContext().getDbPassword());
		
//		mySqlconnectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://wn06.research-infrastructures.eu:3306/aquamaps_DB",  "root", "mybohemian");
		mySqlpoolableConnectionFactory = new PoolableConnectionFactory(mySqlconnectionFactory,mySqlconnectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
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
		postGISconnectionPool.setTestOnBorrow(true);
		postGISconnectionFactory = new DriverManagerConnectionFactory("jdbc:"+"postgresql"+"://"+ServiceContext.getContext().getPostGis_host()+":"+
				ServiceContext.getContext().getPostGis_port()+"/"+ServiceContext.getContext().getPostGis_database(),ServiceContext.getContext().getPostGis_user(), ServiceContext.getContext().getPostGis_passwd());
		
//		connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/aquamaps_DB",  ServiceContext.getContext().getDbUsername(), ServiceContext.getContext().getDbPassword());
//		connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/prova",  "root","rootpwd");
		postGISpoolableConnectionFactory = new PoolableConnectionFactory(postGISconnectionFactory,postGISconnectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
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

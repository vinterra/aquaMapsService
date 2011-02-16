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
		mySql,postgreSQL
	}
	
	private static GenericObjectPool internalDBconnectionPool; 
	private static ConnectionFactory internalDBconnectionFactory;
	private static PoolableConnectionFactory internalDBpoolableConnectionFactory;
	private static PoolingDriver internalDBdriver;
	
	private static final String internalDBPoolName="mySqlPool";
	private static final String postGISPoolName="postGISPool";
	//TODO load from properties 
	

	
	private static GenericObjectPool postGISconnectionPool; 
	private static ConnectionFactory postGISconnectionFactory;
	private static PoolableConnectionFactory postGISpoolableConnectionFactory;
	private static PoolingDriver postGISdriver;
	
	private static String validationQUERY="Select 1";
	
	private static String internalDBconnectionString=null; 
	
	
	static{
		//MYSQL
		
		
		
		try {
			switch(ServiceContext.getContext().getInternalDBType()){
			case mySql:Class.forName("com.mysql.jdbc.Driver");
						internalDBconnectionString="jdbc:mysql://"+ServiceContext.getContext().getInternalDBHost()+":"+ServiceContext.getContext().getInternalDBPort()+"/"+ServiceContext.getContext().getInternalDBName();
			break;
			case postgreSQL:Class.forName("org.postgresql.Driver");
			internalDBconnectionString="jdbc:postgresql://"+ServiceContext.getContext().getInternalDBHost()+":"+ServiceContext.getContext().getInternalDBPort()+"/"+ServiceContext.getContext().getInternalDBName();
			break;
			default : throw new ClassNotFoundException("Not Valid internal DB Type "+ServiceContext.getContext().getInternalDBType());
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		internalDBconnectionPool = new GenericObjectPool(null);
		internalDBconnectionPool.setMaxActive(30);
		internalDBconnectionPool.setTestOnBorrow(true);
		internalDBconnectionFactory = new DriverManagerConnectionFactory(internalDBconnectionString,  ServiceContext.getContext().getInternalDbUsername(), ServiceContext.getContext().getInternalDbPassword());
		
//		mySqlconnectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://wn06.research-infrastructures.eu:3306/aquamaps_DB",  "root", "mybohemian");
		internalDBpoolableConnectionFactory = new PoolableConnectionFactory(internalDBconnectionFactory,internalDBconnectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
		internalDBdriver = new PoolingDriver();
		internalDBdriver.registerPool(internalDBPoolName,internalDBconnectionPool);
		
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
	
	
	public static Connection getInternalDBConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+internalDBPoolName);
	}
	public static Connection getPostGisDBConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+postGISPoolName);
	}
	
	
	public static String getInternalConnectionString(){return internalDBconnectionString;}
	
}

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
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;

public class PoolManager {



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

		try{

		try {
			internalDBconnectionString=ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_HOST)+
										":"+ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_PORT)+
										"/"+ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_NAME);
			
			DBType dbType=DBType.valueOf(ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_TYPE));
			switch(dbType){
			case mySql:	Class.forName("com.mysql.jdbc.Driver");
						internalDBconnectionString="jdbc:mysql://"+internalDBconnectionString;
						break;
			case postgreSQL:Class.forName("org.postgresql.Driver");
			internalDBconnectionString="jdbc:postgresql://"+internalDBconnectionString;
			break;
			default : throw new ClassNotFoundException("Not Valid internal DB Type "+ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_TYPE));
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		internalDBconnectionPool = new GenericObjectPool(null);
		internalDBconnectionPool.setMaxActive(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.INTERNAL_DB_MAX_CONNECTION));
//		internalDBconnectionPool.setMaxIdle(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.INTERNAL_DB_MAX_IDLE));
		internalDBconnectionPool.setTestOnBorrow(true);
		internalDBconnectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		internalDBconnectionFactory = new DriverManagerConnectionFactory(internalDBconnectionString,  ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_USERNAME), 
				ServiceContext.getContext().getProperty(PropertiesConstants.INTERNAL_DB_PASSWORD));

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
		postGISconnectionPool.setMaxActive(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_DB_MAX_CONNECTION));
//		postGISconnectionPool.setMaxIdle(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_DB_MAX_IDLE));
		postGISconnectionPool.setTestOnBorrow(true);
		postGISconnectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		postGISconnectionFactory = new DriverManagerConnectionFactory("jdbc:"+"postgresql"+"://"+ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_HOST)+":"+
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_PORT)+"/"+
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_NAME),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_USERNAME),
				ServiceContext.getContext().getProperty(PropertiesConstants.GEOSERVER_DB_PASSWORD));

		//		connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/aquamaps_DB",  ServiceContext.getContext().getDbUsername(), ServiceContext.getContext().getDbPassword());
		//		connectionFactory = new DriverManagerConnectionFactory("jdbc:mysql://localhost:3306/prova",  "root","rootpwd");
		postGISpoolableConnectionFactory = new PoolableConnectionFactory(postGISconnectionFactory,postGISconnectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
		postGISdriver = new PoolingDriver();
		postGISdriver.registerPool(postGISPoolName,postGISconnectionPool);
		
	}catch(Exception e){
		e.printStackTrace();
	}
}


	public static Connection getInternalDBConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+internalDBPoolName);
	}
	public static Connection getPostGisDBConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+postGISPoolName);
	}


	public static String getInternalConnectionString(){return internalDBconnectionString;}

}

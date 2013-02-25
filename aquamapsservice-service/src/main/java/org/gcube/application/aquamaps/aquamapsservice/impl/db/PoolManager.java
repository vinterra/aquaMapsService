package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.StackKeyedObjectPoolFactory;
import org.gcube.application.aquamaps.aquamapsservice.client.model.vo.DBDescriptor;
import org.gcube.application.aquamaps.aquamapsservice.impl.config.ConfigurationManager;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;

public class PoolManager {

	protected static GCUBELog logger= new GCUBELog(PoolManager.class);

	private static PoolingDriver internalDBdriver;
	
	
	
	private static String validationQUERY="Select 1";
	private static final String dbPoolSuffix="dbPool";
	
	
	
	private static ConcurrentHashMap<GCUBEScope, PoolManager> poolMap=new ConcurrentHashMap<GCUBEScope, PoolManager>();
	
	public static synchronized PoolManager get(GCUBEScope scope) throws ClassNotFoundException{
		if(!poolMap.containsKey(scope))poolMap.put(scope, new PoolManager(scope));
		return poolMap.get(scope);
	}
	
//	private static GenericObjectPool internalDBconnectionPool; 
//	private static ConnectionFactory internalDBconnectionFactory;
//	private static PoolableConnectionFactory internalDBpoolableConnectionFactory;

//	private static final String internalDBPoolName="mySqlPool";
//	private static final String postGISPoolName="postGISPool";
//	//TODO load from properties 
//
//
//
//	private static GenericObjectPool postGISconnectionPool; 
//	private static ConnectionFactory postGISconnectionFactory;
//	private static PoolableConnectionFactory postGISpoolableConnectionFactory;
//	private static PoolingDriver postGISdriver;
//
//
//	private static String internalDBconnectionString=null; 
//
//
//	static{
//		//MYSQL
//
//		try{
//
//			DBDescriptor internalDBDescriptor=DBSession.getInternalCredentials();
//		try {
//			switch(internalDBDescriptor.getType()){
//			case mysql:	Class.forName("com.mysql.jdbc.Driver");
//						internalDBconnectionString="jdbc:mysql:";
//						break;
//			case postgres:Class.forName("org.postgresql.Driver");
//						internalDBconnectionString="jdbc:postgresql:";
//			break;
//			default : throw new ClassNotFoundException("Not Valid internal DB Type "+internalDBDescriptor.getType());
//			}
//
//			internalDBconnectionString+=internalDBDescriptor.getEntryPoint();
//		} catch (ClassNotFoundException e) {
//			logger.fatal("Unable to instantiate driver", e);
//			throw e;
//		}
//		internalDBconnectionPool = new GenericObjectPool(null);
//		internalDBconnectionPool.setMaxActive(internalDBDescriptor.getMaxConnection());
////		internalDBconnectionPool.setMaxIdle(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.INTERNAL_DB_MAX_IDLE));
//		internalDBconnectionPool.setTestOnBorrow(true);
//		internalDBconnectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
//		internalDBconnectionFactory = new DriverManagerConnectionFactory(internalDBconnectionString,internalDBDescriptor.getUser(), 
//				internalDBDescriptor.getPassword());
//
//		internalDBpoolableConnectionFactory = new PoolableConnectionFactory(internalDBconnectionFactory,internalDBconnectionPool,
//				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
//		internalDBdriver = new PoolingDriver();
//		internalDBdriver.registerPool(internalDBPoolName,internalDBconnectionPool);
//
//		//POSTGIS
//
//		DBDescriptor postgisDBDescriptor=DBSession.getPostGisCredentials();
//		
//		try {
//			Class.forName("org.postgresql.Driver");
//		} catch (ClassNotFoundException e) {
//			logger.fatal("Unable to instantiate driver", e);
//			throw e;
//		}
//		postGISconnectionPool = new GenericObjectPool(null);
//		postGISconnectionPool.setMaxActive(postgisDBDescriptor.getMaxConnection());
////		postGISconnectionPool.setMaxIdle(ServiceContext.getContext().getPropertyAsInteger(PropertiesConstants.GEOSERVER_DB_MAX_IDLE));
//		postGISconnectionPool.setTestOnBorrow(true);
//		postGISconnectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
//		postGISconnectionFactory = new DriverManagerConnectionFactory("jdbc:postgresql:"+postgisDBDescriptor.getEntryPoint(),
//				postgisDBDescriptor.getUser(),
//				postgisDBDescriptor.getPassword());
//
//		postGISpoolableConnectionFactory = new PoolableConnectionFactory(postGISconnectionFactory,postGISconnectionPool,
//				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
//		postGISdriver = new PoolingDriver();
//		postGISdriver.registerPool(postGISPoolName,postGISconnectionPool);
//		
//	}catch(Exception e){
//		logger.fatal(e);
//	}
//}

	//************************ INSTANCE
	private GenericObjectPool connectionPool; 
	private ConnectionFactory connectionFactory;
	private PoolableConnectionFactory poolableConnectionFactory;
	private PoolingDriver DBdriver;
	private String scopeName=null;
	private String connectionString=null; 
	
	public PoolManager(GCUBEScope scope) throws ClassNotFoundException {
		DBDescriptor dbDesc=ConfigurationManager.getConfiguration().getInternalDB(scope);
		scopeName=scope.getName();
		Class.forName("org.postgresql.Driver");
		connectionString="jdbc:postgresql:"+dbDesc.getEntryPoint();
		connectionPool = new GenericObjectPool(null);
		connectionPool.setMaxActive(dbDesc.getMaxConnection());
		connectionPool.setTestOnBorrow(true);
		connectionPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
		connectionFactory = new DriverManagerConnectionFactory(connectionString,dbDesc.getUser(),dbDesc.getPassword());
		poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,
				new StackKeyedObjectPoolFactory(),validationQUERY,false,true);
		if(internalDBdriver==null) internalDBdriver = new PoolingDriver();
		internalDBdriver.registerPool(getPoolName(),connectionPool);
	}
	
	private String getPoolName(){return dbPoolSuffix+scopeName;}

	public Connection getConnection()throws Exception{
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:"+getPoolName());
	}

}

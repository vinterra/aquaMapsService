package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class DBCredentialDescriptor {

	private Map<DBConnectionParameters,String> values=new HashMap<DBConnectionParameters, String>();

	public DBCredentialDescriptor(Set<Entry<Object,Object>> toSetValues) throws Exception{
		for(Entry<Object,Object> toSet:toSetValues)
		try{
			values.put(DBConnectionParameters.valueOf(toSet.getKey()+""),toSet.getValue()+"");
		}catch (Exception e){
			//Unexpected parameters skipped
		}
	
		for(DBConnectionParameters expectedValue:DBConnectionParameters.values())
			if(!values.containsKey(expectedValue))throw new Exception("Mandatory value "+expectedValue);
	}
	
	public DBCredentialDescriptor(String fileName) throws Exception{
		FileInputStream fis=null;
		try{
			Properties prop =new Properties();
		fis=new FileInputStream(fileName);
		prop.load(fis);
		for(DBConnectionParameters expectedValue:DBConnectionParameters.values())
			if(prop.containsKey(expectedValue+""))values.put(expectedValue, prop.getProperty(expectedValue+"").trim());
			else throw new Exception("Mandatory value "+expectedValue);
		}catch(Exception e) {throw e;}
		finally{
			if(fis!=null)fis.close();
		}
	}
	
	@Deprecated
	public DBCredentialDescriptor(String host,String port,String dbName,String userName,String password){
		values.put(DBConnectionParameters.dbName, dbName);
		values.put(DBConnectionParameters.host, host);
		values.put(DBConnectionParameters.password, password);
		values.put(DBConnectionParameters.port, port);
		values.put(DBConnectionParameters.user, userName);
	}
	
	public String getValue(DBConnectionParameters param){return values.get(param);}
	
	
}

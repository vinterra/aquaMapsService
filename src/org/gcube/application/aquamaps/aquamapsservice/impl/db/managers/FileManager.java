package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.common.core.utils.logging.GCUBELog;

public class FileManager {

	protected static GCUBELog logger= new GCUBELog(FileManager.class);
	
	public static int linkImagesInDB(Map<String,String> imagesNameAndLink, String basePath,int aquamapsId) throws Exception{
		DBSession session=DBSession.openSession(PoolManager.DBType.mySql);
		PreparedStatement pps =session.preparedStatement("INSERT INTO Files (published, nameHuman , Path, Type, owner) VALUE(?, ?, ?, ?, ?)");
		int count=0;
		for(String mapName:imagesNameAndLink.keySet()){
			File f= new File(imagesNameAndLink.get(mapName));
			pps.setBoolean(1,true);
			pps.setString(2,mapName);
			pps.setString(3, basePath+f.getName());
			pps.setString(4,"IMG");
			pps.setInt(5,aquamapsId);
			count+=pps.executeUpdate();
		}
		session.close();

		return count;
	}
	
	/**
	 * publish a certain number of files on the webserver and returns the base URL
	 * 
	 * 
	 * @param firstLevelDir
	 * @param secondLevelDir
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public static String publishInternal(String firstLevelDir, String secondLevelDir, Collection<String> filesPath) throws Exception{
		// Destination directory
	    File dir = new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+ServiceContext.getContext().getHttpServerBasePath()+
	    			File.separator+firstLevelDir+File.separator+secondLevelDir+File.separator);
	    logger.debug("path: "+dir.getAbsolutePath());
	    dir.mkdirs();
	    // Move file to new directory
	    for (String path : filesPath){
	    	File file=new File(path);
	    	if (!file.exists()){
	    		logger.debug("the file "+file.getName() +" doesn't exists");
	    		continue;
	    	}
	    	File dest=new File(dir, file.getName());
	    	
	    	//Using ioutils
	    	try{
	    	FileUtils.copyFile(file,dest);
	    	FileUtils.forceDelete(file);
//	    	logger.debug(path + "successfully moved to "+dest.getAbsolutePath());
	    	}catch(Exception e){
	    		logger.error("Unable to move (copy and delete) "+path,e);
	    	}
	    }
	    logger.debug(ServiceContext.getContext().getWebServiceURL()+firstLevelDir+"/"+secondLevelDir);
	    return ServiceContext.getContext().getWebServiceURL()+firstLevelDir+"/"+secondLevelDir+"/";

	}
	
	public static String getBasePublicPath(){return ServiceContext.getContext().getWebServiceURL();}
	public static String getBaseLocalPath(){return ServiceContext.getContext().getPersistenceRoot()+File.separator+ServiceContext.getContext().getHttpServerBasePath();}
		
		
}

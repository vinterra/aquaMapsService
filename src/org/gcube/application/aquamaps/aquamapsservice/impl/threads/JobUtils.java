package org.gcube.application.aquamaps.aquamapsservice.impl.threads;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.AquaMap;
import org.gcube.application.aquamaps.stubs.Area;
import org.gcube.application.aquamaps.stubs.AreasArray;
import org.gcube.application.aquamaps.stubs.Cell;
import org.gcube.application.aquamaps.stubs.Field;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Resource;
import org.gcube.application.aquamaps.stubs.Specie;
import org.gcube.application.aquamaps.stubs.Weight;
import org.gcube.application.aquamaps.stubs.dataModel.util.StubsToModel;
import org.gcube.common.core.utils.logging.GCUBELog;


public class JobUtils {

	private static GCUBELog logger= new GCUBELog(JobUtils.class);
//	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	public static final String xmlHeader="<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
	
/**
 *  Clusterize rs in N stringBuilders compliant with CsquareCode convention
 * @param rs 			
 * @param maxIndex				the column index to retrieve max value 
 * @param toClusterIndex 		the column index of CsquareCodes
 * @param probabilityIndex		the column index of probability
 * @return
 * @throws SQLException
 */
	public static StringBuilder[] clusterize(ResultSet rs,int maxIndex,int toClusterIndex,int probabilityIndex,boolean bioDiversity) throws SQLException{
		StringBuilder csq_str1 = new StringBuilder();
		StringBuilder csq_str2 = new StringBuilder();
		StringBuilder csq_str3 = new StringBuilder();
		StringBuilder csq_str4 = new StringBuilder();
		StringBuilder csq_str5 = new StringBuilder();
		if(rs.first()){	
			double max=0;
			double r1=0;
			double r2=0;
			double r3=0;
			double r4=0;
			if(bioDiversity){
				max=rs.getDouble(maxIndex);
				r1 = Math.round(Math.pow(10,(Math.log10(max)/5)));
				r2 = Math.round(Math.pow(10,(2*Math.log10(max)/5)));
				r3 = Math.round(Math.pow(10,(3*Math.log10(max)/5)));
				r4 = Math.round(Math.pow(10,(4*Math.log10(max)/5)));}
			else{								
				r1= 0.2;
				r2= 0.4;
				r3= 0.6;
				r4= 0.8;
			}
			logger.debug("Clustering by "+r1+" , "+r2+" , "+r3+" , "+r4);
			
			
			do{
				double currentValue=rs.getDouble(probabilityIndex);
				String toAppendCode=rs.getString(toClusterIndex);
				if((currentValue>0)&&(currentValue<=r1)){
					if(csq_str1.length()>1){
						csq_str1.append("|"+toAppendCode);
					}else{
						csq_str1.append(toAppendCode);
					}
				}else{
					if((currentValue>r1)&&(currentValue<=r2)){
						if(csq_str2.length()>1){
							csq_str2.append("|"+toAppendCode);
						}else{
							csq_str2.append(toAppendCode);
						}
					}else{
						if((currentValue>r2)&&(currentValue<=r3)){
							if(csq_str3.length()>1){
								csq_str3.append("|"+toAppendCode);
							}else{
								csq_str3.append(toAppendCode);
							}
						}else{
							if((currentValue>r3)&&(currentValue<=r4)){
								if(csq_str4.length()>1){
									csq_str4.append("|"+toAppendCode);
								}else{
									csq_str4.append(toAppendCode);
								}
							}else{
								if(currentValue>r4){
									if(csq_str5.length()>1){
										csq_str5.append("|"+toAppendCode);
									}else{
										csq_str5.append(toAppendCode);
									}
								}
							}
						}
					}
				}
			}while(rs.next());
		}else return null;
		logger.trace("Clustering complete : cluster 1 size "+csq_str1.length()+"cluster 2 size "+csq_str2.length()+
				"cluster 3 size "+csq_str3.length()+"cluster 4 size "+csq_str4.length()+"cluster 5 size "+csq_str5.length());
		return new StringBuilder[]{csq_str1,csq_str2,csq_str3,csq_str4,csq_str5};
	}


//	public static int generateImages(String File)throws Exception{
//		throw new Exception("Do not use this method!");
//	}
	
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
	
	
		
	
	public static void updateProfile(String resName,String resId,String resProfile,String firstLevelDir,String secondLevelDir) throws Exception{
		DBSession c=DBSession.openSession(PoolManager.DBType.mySql);
		Collection<String> toUpdateProfile=new ArrayList<String>();
		File dir=new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+resName);
		dir.mkdirs();
		File file=new File(dir.getAbsolutePath(),resName+".xml");
		//file.mkdirs();
		FileWriter writer=new FileWriter(file);
		writer.write(resProfile);
		writer.close();
		toUpdateProfile.add(file.getAbsolutePath());
		String path=publishInternal(firstLevelDir,secondLevelDir,toUpdateProfile);
		logger.trace("Profile for "+resName+" created, gonna update DB");
		PreparedStatement ps=c.preparedStatement(DBCostants.profileUpdate);
		ps.setString(1, path+file.getName());
		ps.setInt(2, Integer.parseInt(resId));		
		if(ps.executeUpdate()==0){
			logger.trace("Entry not found for profile, gonna create it");
			PreparedStatement pps =c.preparedStatement(DBCostants.fileInsertion);
				pps.setBoolean(1,true);
				pps.setString(2,"Metadata");
				pps.setString(3, path+file.getName());
				pps.setString(4,"XML");
				pps.setString(5,resId);
				pps.execute();			
		}
		c.close();
	}
	

	
	
	
	public static void updateAquaMapStatus(int aquamapsId,JobGenerationDetails.Status status)throws SQLException,IOException, Exception{
//		toUpdate.setStatus(status.toString());
		DBSession c=DBSession.openSession(PoolManager.DBType.mySql);
		PreparedStatement ps=c.preparedStatement(DBCostants.submittedStatusUpdating);
		ps.setString(1, status.toString());
		ps.setInt(2,aquamapsId);		
		ps.execute();	
		c.close();
//		updateProfile(toUpdate.getName(),toUpdate.getId(),makeAquaMapProfile(toUpdate),generationDetails.getFirstLevelDirName(),generationDetails.getSecondLevelDirName(),c);
		logger.trace("done AquaMap status updateing status : "+status.toString());
		
	}
	

	
	public static String projectCitation=	"Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
 	"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
 	"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";
	
	public static String makeAquaMapProfile(AquaMap obj){
		return StubsToModel.translateToClient(obj).toXML();
	}
	
	public static String makeJobProfile(Job obj){
		return "<Job></Job>"; //TODO implement job profile
	}
	
	public static String resourceToXML(Resource toParse){
		StringBuilder doc=new StringBuilder();
		doc.append("<Resource>");
		doc.append("<Type>"+toParse.getType()+"</Type>");
		doc.append("<Attributes>");
		FieldArray fields=toParse.getAdditionalField();
		if((fields!=null)&&(fields.getFields()!=null))
		for(Field field:fields.getFields())
			doc.append(fieldToXML(field));
		doc.append("</Attributes>");		
		doc.append("</Resource>");
		return doc.toString();
	}
	
	public static String fieldToXML(Field field){
		StringBuilder doc=new StringBuilder();
		doc.append("<Field>");
		doc.append("<Type>"+field.getType()+"</Type>");
		doc.append("<Name>"+field.getName()+"</Name>");
		doc.append("<Value>"+field.getValue()+"</Value>");
		doc.append("</Field>");
		return doc.toString();
	}
	public static String weightToXML(Weight weight){
		StringBuilder doc=new StringBuilder();
		doc.append("<Field>");
		doc.append("<Type>"+"float"+"</Type>");
		doc.append("<Name>"+weight.getParameterName()+"</Name>");
		//doc.append("<Value>"+weight.getChosenWeight()+"</Value>");
		doc.append("</Field>");
		return doc.toString();
	}
	public static String areaToXML(Area area){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Area>");
		toReturn.append("<code>"+area.getCode()+"</code>");
		toReturn.append("<type>"+area.getType()+"</type>");
		toReturn.append("<name>"+area.getName()+"</name>");
		toReturn.append("<Attributes>");
		FieldArray fields=area.getAdditionalField();
		if((fields!=null)&&(fields.getFields()!=null))
		for(Field field:fields.getFields())
			toReturn.append(fieldToXML(field));
		toReturn.append("</Attributes>");
		toReturn.append("</Area>");
		return toReturn.toString();
	}
	
	public static String cellToXML(Cell cell){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Cell>");
		toReturn.append("<"+DBCostants.cSquareCode+">"+cell.getCode()+"</"+DBCostants.cSquareCode+">");
		toReturn.append("<Attributes>");
		FieldArray fields=cell.getAdditionalField();
		if((fields!=null)&&(fields.getFields()!=null))
		for(Field field:fields.getFields())
			toReturn.append(fieldToXML(field));
		toReturn.append("</Attributes>");
		toReturn.append("</Cell>");
		return toReturn.toString();
	}
	public static String speciesToXML(Specie spec){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Species>");
		toReturn.append("<"+DBCostants.SpeciesID+">"+spec.getId()+"</"+DBCostants.SpeciesID+">");
		toReturn.append("<Attributes>");
		FieldArray fields=spec.getAdditionalField();
		if((fields!=null)&&(fields.getFields()!=null))
		for(Field field:fields.getFields())
			toReturn.append(fieldToXML(field));
		toReturn.append("</Attributes>");
		toReturn.append("</Species>");
		return toReturn.toString();
	}
	
	
	/**
	 * returns a new HSPEC table name filtering selected HSPEC against Area selection if any
	 * 
	 * @param details
	 * @return
	 * @throws SQLException
	 */
	
	
	public static String filterByArea(int jobId,AreasArray areaSelection,String hspec)throws Exception{
		String toReturn;		
		logger.trace(" filtering simulation data on area selection for jobId:"+jobId);
		
		if((areaSelection!=null)&&(areaSelection.getAreasList()!=null)
				&&(areaSelection.getAreasList().length>0)){
			DBSession conn = DBSession.openSession(PoolManager.DBType.mySql);
			String areaTmpTable=ServiceUtils.generateId("A", "");//"A"+(uuidGen.nextUUID()).replaceAll("-", "_");
			conn.executeUpdate("CREATE TABLE "+areaTmpTable+" ( code varchar(50) PRIMARY KEY , type varchar(5))");
			for(Area area: areaSelection.getAreasList())			
				conn.executeUpdate("INSERT INTO "+areaTmpTable+" VALUES('"+area.getCode()+"','"+area.getType()+"')");
			
			logger.trace(" area temp table created");
			JobGenerationDetails.addToDropTableList(jobId,areaTmpTable);
			String filteredTable=ServiceUtils.generateId("A", "");//"A"+(uuidGen.nextUUID()).replaceAll("-", "_");
			conn.executeUpdate("CREATE TABLE "+filteredTable+"(like "+DBCostants.HSPEC+" )");
			JobGenerationDetails.addToDropTableList(jobId,filteredTable);
			String filterQuery=DBCostants.filterCellByAreaQuery(filteredTable,hspec,areaTmpTable);
			logger.trace("Going to use sql query "+filterQuery);			
			conn.executeQuery(filterQuery);
			/*ps.setString(1, filteredTable);
			ps.setString(2, areaTmpTable);
			ps.setString(3, areaTmpTable+".code");
			ps.setString(4, areaTmpTable+".code");
			ps.setString(5, areaTmpTable+".code");
			ps.execute();*/
			
			logger.trace(" table filtered, dropping area temp table");
			conn.executeUpdate("drop table "+areaTmpTable);
			conn.close();
			toReturn=filteredTable;
		}else {
			toReturn=hspec;
			logger.trace(jobId+" no area selected");
		}
		logger.trace("area filtering completed for jobid"+jobId);
		return toReturn;
	}
	
	public static String createClusteringFile(String objectName,StringBuilder[] csq_str,String header,String header_map,String dirName) throws FileNotFoundException{
		
		String to_out = "color=FFFF84 fill=Y color2=FFDE6B fill2=Y color3=FFAD6B fill3=Y color4=FF6B6B fill4=Y color5=DE4242 fill5=Y "+
		((csq_str[0].toString().compareTo("")!=0)?" csq="+csq_str[0].toString():" csq=0000:000:0")+
		((csq_str[1].toString().compareTo("")!=0)?" csq2="+csq_str[1].toString():"")+
		((csq_str[2].toString().compareTo("")!=0)?" csq3="+csq_str[2].toString():"")+
		((csq_str[3].toString().compareTo("")!=0)?" csq4="+csq_str[3].toString():"")+
		((csq_str[4].toString().compareTo("")!=0)?" csq5="+csq_str[4].toString():"")+
		" header="+header+" enlarge=7200 title="+header_map+
		" dilate=N cSub popup=Y landmask=1 filedesc=map_pic legend=  mapsize=large";

		String fileName=objectName+"_clustering";
		File dir=new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+dirName);
		dir.mkdirs();
		File file=new File(dir.getAbsolutePath(),fileName);
		//file.mkdirs();
		FileOutputStream myStream = new FileOutputStream(file);
		PrintStream myOutput = new PrintStream(myStream);

		myOutput.print(to_out);
		String toReturn=file.getAbsolutePath();		
		return toReturn;
	}
	
	public static Map<String,String> getToPublishList(String basePath,String aquamapName){
		Map<String,String> toReturn=new HashMap<String, String>();
		File f1 = new File(basePath+"csq_map127.0.0.1_"+aquamapName+"_map_pic.jpg");
		if (f1.exists())
			toReturn.put(aquamapName+" Earth",f1.getAbsolutePath());			
				
		File f2 = new File(basePath+aquamapName+"/"+aquamapName+"_afr.jpg");
		if (f2.exists())
			toReturn.put(aquamapName+" Continent View : Africa", f2.getAbsolutePath());						
				
		File f3 = new File(basePath+aquamapName+"/"+aquamapName+"_asia.jpg");
		if (f3.exists())
			toReturn.put(aquamapName+" Continent View : Asia", f3.getAbsolutePath());			
				
		File f4 = new File(basePath+aquamapName+"/"+aquamapName+"_aus.jpg");
		if (f4.exists())
			toReturn.put(aquamapName+" Continent View : Australia", f4.getAbsolutePath());			
				
		File f5 = new File(basePath+aquamapName+"/"+aquamapName+"_eur.jpg");
		if (f5.exists())			
			toReturn.put(aquamapName+" Continent View : Europa", f5.getAbsolutePath());
				
		File f6 = new File(basePath+aquamapName+"/"+aquamapName+"_nAm.jpg");
		if (f6.exists())			
			toReturn.put(aquamapName+" Continent View : North America", f6.getAbsolutePath());
		
		File f7 = new File(basePath+aquamapName+"/"+aquamapName+"_sAm.jpg");
		if (f7.exists())			
			toReturn.put(aquamapName+" Continent View : South America", f7.getAbsolutePath());
		
		File f8 = new File(basePath+aquamapName+"/"+aquamapName+"_xmapAtlan.jpg");
		if (f8.exists())			
			toReturn.put(aquamapName+" Ocean View : Atlantic", f8.getAbsolutePath());
		
		File f9 = new File(basePath+aquamapName+"/"+aquamapName+"_xmapI.jpg");
		if (f9.exists())
			toReturn.put(aquamapName+" Ocean View : Indian", f9.getAbsolutePath());			
		
		File f10 = new File(basePath+aquamapName+"/"+aquamapName+"_xmapN.jpg");
		if (f10.exists())			
			toReturn.put(aquamapName+" Pole View : Artic", f10.getAbsolutePath());
		
		File f11= new File(basePath+aquamapName+"/"+aquamapName+"_xmapNAtlan.jpg");
		if (f11.exists())			
			toReturn.put(aquamapName+" Ocean View : North Atlantic", f11.getAbsolutePath());
				
		File f12= new File(basePath+aquamapName+"/"+aquamapName+"_xmapP.jpg");
		if (f12.exists())
			toReturn.put(aquamapName+" Ocean View : Pacific", f12.getAbsolutePath());			
		
		File f13= new File(basePath+aquamapName+"/"+aquamapName+"_xmapS.jpg");
		if (f13.exists())			
			toReturn.put(aquamapName+" Pole View : Antarctic", f13.getAbsolutePath());
				
		File f14= new File(basePath+aquamapName+"/"+aquamapName+"_xmapSAtlan.jpg");
		if (f14.exists())	
			toReturn.put(aquamapName+" Ocean View : South Atlantic", f14.getAbsolutePath());
		
		return toReturn;
	}
	
	
	
}
package org.gcube.application.aquamaps.aquamapsservice.impl.threads;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.common.core.utils.logging.GCUBELog;



public class JobUtils {

	private static GCUBELog logger= new GCUBELog(JobUtils.class);
//	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	public static final String xmlHeader="<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>";
	
public static final Map<String,String> imageFileAndName= new HashMap<String, String>();
	
	static {
		imageFileAndName.put("_map_pic.jpg", "Earth");
		imageFileAndName.put("_afr.jpg", "Continent View : Africa");
		imageFileAndName.put("_asia.jpg", "Continent View : Asia");
		imageFileAndName.put("_aus.jpg", "Continent View : Australia");
		imageFileAndName.put("_eur.jpg", "Continent View : Europa");
		imageFileAndName.put("_nAm.jpg", "Continent View : North America");
		imageFileAndName.put("_sAm.jpg", "Continent View : South America");
		imageFileAndName.put("_xmapAtlan.jpg", "Ocean View : Atlantic");
		imageFileAndName.put("_xmapI.jpg", "Ocean View : Indian");
		imageFileAndName.put("_xmapN.jpg", "Pole View : Artic");
		imageFileAndName.put("_xmapNAtlan.jpg", "Ocean View : North Atlantic");
		imageFileAndName.put("_xmapP.jpg", "Ocean View : Pacific");
		imageFileAndName.put("_xmapS.jpg", "Pole View : Antarctic");
		imageFileAndName.put("_xmapSAtlan.jpg", "Ocean View : South Atlantic");		
	}
	

	
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
	
	
	
	
	

	
	
	
//	public static void updateAquaMapStatus(int aquamapsId,JobManager.Status status)throws SQLException,IOException, Exception{
////		toUpdate.setStatus(status.toString());
//		DBSession c=DBSession.openSession(PoolManager.DBType.mySql);
//		PreparedStatement ps=c.preparedStatement(DBCostants.submittedStatusUpdating);
//		ps.setString(1, status.toString());
//		ps.setInt(2,aquamapsId);		
//		ps.execute();	
//		c.close();
////		updateProfile(toUpdate.getName(),toUpdate.getId(),makeAquaMapProfile(toUpdate),generationDetails.getFirstLevelDirName(),generationDetails.getSecondLevelDirName(),c);
//		logger.trace("done AquaMap status updateing status : "+status.toString());
//		
//	}
	

	
//	public static String projectCitation=	"Kaschner, K., J. S. Ready, E. Agbayani, J. Rius, K. Kesner-Reyes, P. D. Eastwood, A. B. South, "+
// 	"S. O. Kullander, T. Rees, C. H. Close, R. Watson, D. Pauly, and R. Froese. 2008 AquaMaps: "+
// 	"Predicted range maps for aquatic species. World wide web electronic publication, www.aquamaps.org, Version 10/2008.";
	
//	public static String makeAquaMapProfile(AquaMap obj){
//		return StubsToModel.translateToClient(obj).toXML();
//	}
	
	public static String makeJobProfile(Job obj){
		return "<Job></Job>"; //TODO implement job profile
	}
	
//	public static String resourceToXML(Resource toParse){
//		StringBuilder doc=new StringBuilder();
//		doc.append("<Resource>");
//		doc.append("<Type>"+toParse.getType()+"</Type>");
//		doc.append("<Attributes>");
////		FieldArray fields=toParse.getAdditionalField();
//		if((fields!=null)&&(fields.getFields()!=null))
//		for(Field field:fields.getFields())
//			doc.append(fieldToXML(field));
//		doc.append("</Attributes>");		
//		doc.append("</Resource>");
//		return doc.toString();
//	}
	
//	public static String fieldToXML(Field field){
//		StringBuilder doc=new StringBuilder();
//		doc.append("<Field>");
//		doc.append("<Type>"+field.getType()+"</Type>");
//		doc.append("<Name>"+field.getName()+"</Name>");
//		doc.append("<Value>"+field.getValue()+"</Value>");
//		doc.append("</Field>");
//		return doc.toString();
//	}
//	public static String weightToXML(Weight weight){
//		StringBuilder doc=new StringBuilder();
//		doc.append("<Field>");
//		doc.append("<Type>"+"float"+"</Type>");
//		doc.append("<Name>"+weight.getParameterName()+"</Name>");
//		//doc.append("<Value>"+weight.getChosenWeight()+"</Value>");
//		doc.append("</Field>");
//		return doc.toString();
//	}
//	public static String areaToXML(Area area){
//		StringBuilder toReturn=new StringBuilder();
//		toReturn.append("<Area>");
//		toReturn.append("<code>"+area.getCode()+"</code>");
//		toReturn.append("<type>"+area.getType()+"</type>");
//		toReturn.append("<name>"+area.getName()+"</name>");
//		toReturn.append("<Attributes>");
//		FieldArray fields=area.getAdditionalField();
//		if((fields!=null)&&(fields.getFields()!=null))
//		for(Field field:fields.getFields())
//			toReturn.append(fieldToXML(field));
//		toReturn.append("</Attributes>");
//		toReturn.append("</Area>");
//		return toReturn.toString();
//	}
	
//	public static String cellToXML(Cell cell){
//		StringBuilder toReturn=new StringBuilder();
//		toReturn.append("<Cell>");
//		toReturn.append("<"+DBCostants.cSquareCode+">"+cell.getCode()+"</"+DBCostants.cSquareCode+">");
//		toReturn.append("<Attributes>");
//		FieldArray fields=cell.getAdditionalField();
//		if((fields!=null)&&(fields.getFields()!=null))
//		for(Field field:fields.getFields())
//			toReturn.append(fieldToXML(field));
//		toReturn.append("</Attributes>");
//		toReturn.append("</Cell>");
//		return toReturn.toString();
//	}
//	public static String speciesToXML(Specie spec){
//		StringBuilder toReturn=new StringBuilder();
//		toReturn.append("<Species>");
//		toReturn.append("<"+DBCostants.SpeciesID+">"+spec.getId()+"</"+DBCostants.SpeciesID+">");
//		toReturn.append("<Attributes>");
//		FieldArray fields=spec.getAdditionalField();
//		if((fields!=null)&&(fields.getFields()!=null))
//		for(Field field:fields.getFields())
//			toReturn.append(fieldToXML(field));
//		toReturn.append("</Attributes>");
//		toReturn.append("</Species>");
//		return toReturn.toString();
//	}
//	
	

	
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
			toReturn.put("Earth",f1.getAbsolutePath());			
		
		for(String suffix:imageFileAndName.keySet()){
			File f2 = new File(basePath+aquamapName+"/"+aquamapName+suffix);
			if (f2.exists())
				toReturn.put(imageFileAndName.get(suffix), f2.getAbsolutePath());
		}
		
		
		
		return toReturn;
	}
	
	public static Map<String,String> parsePublished(List<String> publishedUrls){
		Map<String,String> toReturn=new HashMap<String, String>();		
		for(String url : publishedUrls){
			for(String suffix: imageFileAndName.keySet()){
				if(url.endsWith(suffix)){
					toReturn.put(imageFileAndName.get(suffix), url);
					break;
				}
			}
		}
		return toReturn;
	}
	
	
	
	
}
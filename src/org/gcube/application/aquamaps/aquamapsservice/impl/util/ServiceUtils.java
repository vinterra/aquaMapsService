package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;

public class ServiceUtils {

	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();

	private static DateFormat dateFormatter= new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
	private static DateFormat shortDateFormatter=new SimpleDateFormat("yyyy_MM_dd");


	public static String fileToString(String path) throws IOException {


		BufferedReader filebuf = null;
		String nextStr = null;
		String ret = new String();

		filebuf = new BufferedReader(new FileReader(path));
		nextStr = filebuf.readLine(); // legge una riga dal file
		while (nextStr != null) {
			ret += nextStr ;
			nextStr = filebuf.readLine(); // legge la prossima riga 
		}
		filebuf.close(); // chiude il file 

		return ret;
	}

	public static String URLtoString(String path) throws IOException{
		URL yahoo = new URL(path);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						yahoo.openStream()));

		String inputLine;
		StringBuilder toReturn=new StringBuilder(); 


		while ((inputLine = in.readLine()) != null)
			toReturn.append(inputLine);

		in.close();
		return toReturn.toString();
	}


	public static void deleteFile(String path) throws IOException {
		File f=new File(path);
		File dir = f.getParentFile();
		f.delete();
		if(dir.list().length==0)dir.delete(); 
	}

	public static String generateId(String prefix,String suffix){
		//		return prefix+(uuidGen.nextUUID()).replaceAll("-", "_")+suffix;
		return prefix+getTimeStamp().replaceAll("-", "_")+suffix;
	}
	public static String getTimeStamp(){
		return dateFormatter.format(System.currentTimeMillis());
	}
	public static String getDate(){
		return shortDateFormatter.format(System.currentTimeMillis());
	}


}

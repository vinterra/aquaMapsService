package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ServiceUtils {

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
	
	public static void deleteFile(String path) throws IOException {
		File f=new File(path);
		File dir = f.getParentFile();
		f.delete();
		if(dir.list().length==0)dir.delete(); 
	}
	
}

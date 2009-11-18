package org.gcube.application.aquamaps.aquamapsservice.impl.util;

import java.io.BufferedReader;
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
	
}

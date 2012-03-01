package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;
import org.gcube.common.core.utils.logging.GCUBELog;

public class ArchiveManager {

	/** 
	 * Class logger. 
	 */
	protected static final GCUBELog logger = new GCUBELog(ArchiveManager.class);

	/**
	 * Download Archive method
	 * 
	 * @param targetDirectory directory to save the package (the directory will be created)
	 * @param fromURL where download the archive
	 * @return File dowloaded
	 * @throws Exception if local store of Service Archive fails
	 */
	public static File downloadArchive(final File targetDirectory, final String fromURL) throws Exception {

		logger.debug("Trying to download Archive from: " + fromURL + ", to = " + targetDirectory);

		if(targetDirectory.exists()){
			logger.debug ("Deleting old tmp directory" + "\ntmp directory path = " +  targetDirectory.getAbsolutePath());
			FileUtils.deleteDirectory(targetDirectory);
		}
		logger.debug ("Creating tmp directory" + "\ntmp directory path = " +  targetDirectory.getAbsolutePath());
		targetDirectory.mkdirs();

		File archive = null;

		try {			
			logger.debug("Opening URL connection to " + fromURL);

			final URL url = new URL(fromURL);
			final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);


			logger.debug("Getting data from given URL " + fromURL);
			final DataInputStream dataInputStream = new DataInputStream(httpURLConnection.getInputStream());
			logger.debug("Connection return code: " + httpURLConnection.getResponseCode());
			logger.debug("Bytes available: " + dataInputStream.available());


			archive = new File(targetDirectory, url.getFile().substring(url.getFile().lastIndexOf("/")));


			final DataOutputStream o = new DataOutputStream (new FileOutputStream(archive));
			logger.debug("Saving service archive to = " + archive.getAbsolutePath());


			try{
				while (true){
					byte b = dataInputStream.readByte();
					o.writeByte(b);
				}
			} catch (EOFException ee){
				logger.debug("Remote Archive read and stored locally");
				o.close();
			}
		} catch(Exception e){
			logger.error("Unable to store Archive locally",e);
			throw e;
		}

		return archive;
	}

	/**
	 * Uncompress Archive Method ()
	 * @param sourceArchive source archive
	 * @throws Exception if the uncompress operation fails
	 */
	public static List<File> unTarGz(final File sourceArchive) throws Exception {

		logger.debug("Uncompressing TAR GZ archive : " + sourceArchive.getAbsolutePath());
		ArrayList<File> extracted=new ArrayList<File>();
		try {

			final GZIPInputStream in = new GZIPInputStream(new FileInputStream(sourceArchive));
			
			String name = sourceArchive.getName();
			if(name.contains(".tar.gz")){
				name = name.replace(".gz", "");
			}else if(name.contains(".tgz")){
				name = name.replace("tgz", "tar");
			}
			
			final File tarArchive = new File(sourceArchive.getParentFile(),name);
			final OutputStream out = new FileOutputStream(tarArchive);			

			final byte[] buf = new byte[1024];

			int len;
			logger.debug("Unzipping tar.gz file");
			while((len = in.read(buf)) > 0) {
				out.write(buf,0, len);
			}
			in.close();
			out.close();

			logger.debug("Untar TAR file");
			final TarInputStream tis = new TarInputStream(new FileInputStream(tarArchive));
			TarEntry te = null;
			while((te = tis.getNextEntry()) != null) {
				logger.debug("Processing " + (te.isDirectory()?"directory : ":"file : ") + te.getName());
				File file = new File(sourceArchive.getParent(),te.getName());
				extracted.add(file);
				if (te.isDirectory()) {
					file.mkdirs();
				} else {
					tis.copyEntryContents(new FileOutputStream(file));
				}
			}

			tarArchive.delete();
			tis.close();
		} catch (Exception e) {
			logger.error("Unable to uncompress tar.gz",e);
			throw e;
		}

		logger.debug("TAR GZ file uncompressed successfully");
		return extracted;
				
	}

	/**
	 * Compress Archive Method
	 * @param targetArchive archive file
	 * @param archiveFiles List of files to include in TAR GZ
	 * @throws Exception if the compression fails
	 */
	public static void createTarGz(final File targetArchive ,final List<File> archiveFiles) throws Exception {
		File[] arrayArchiveFiles = new File[archiveFiles.size()];
		for(int i=0; i<archiveFiles.size(); i++){
			arrayArchiveFiles[i] = archiveFiles.get(i);
		}
		createTarGz(targetArchive,arrayArchiveFiles);
	}
	
	
	/**
	 * Compress Archive Method. The first entry in the 
	 * @param targetArchive archive file
	 * @param archiveFiles Array of files to include in TAR GZ. The first entry 
	 * in the array is the source root directory. It is used to calculate relative path.
	 * If it is not supplied the archive will not have directory tree.
	 * @throws Exception if the compression fails
	 */
	public static void createTarGz(final File targetArchive ,final File[] archiveFiles) throws Exception {

		logger.debug("Creating TAR GZ file");

		final File parentDirectory = targetArchive.getParentFile();

		
		File rootDirectory = null;
		if(archiveFiles[0].isDirectory()){
			rootDirectory = archiveFiles[0];
		}
		
		String name = targetArchive.getName();
		if(name.contains(".tar.gz")){
			name = name.replace(".gz", "");
		}else if(name.contains(".tgz")){
			name = name.replace("tgz", "tar");
		} else {
			Exception e = new Exception("The archive should have .tar.gz or .tgz extention");
			logger.error(e);
			throw e;
		}

		final File tarFile = new File(parentDirectory,name);

		logger.debug("Opening TAR file: " + tarFile.getName());

		final FileOutputStream stream = new FileOutputStream(tarFile);
		final TarOutputStream out = new TarOutputStream(stream);
		out.setLongFileMode(TarOutputStream.LONGFILE_GNU);

		byte buffer[] = new byte[1024];

		for(File item : archiveFiles) {
					
			if(rootDirectory!=null && item==rootDirectory){
				continue;
			}
			
			logger.debug("Adding " + item.getName() + " to TAR");

			final TarEntry tarEntry = new TarEntry(item);
			//tarAdd.setModTime(file.lastModified());
			String relativePath = item.getName();
			if(rootDirectory!=null){
				relativePath = item.getAbsolutePath().replace(rootDirectory.getAbsolutePath()+File.separator, "");
			}
			tarEntry.setName(relativePath);
			tarEntry.setSize(item.length());
			
			out.putNextEntry(tarEntry);

			FileInputStream in = new FileInputStream(item);
			while (true) {
				int nRead = in.read(buffer, 0, buffer.length);
				if (nRead <= 0)
					break;
				out.write(buffer, 0, nRead);
			}
			in.close();				

			out.closeEntry();
			
		}

		logger.debug("Closing TAR file: " + tarFile.getName());
		out.close();
		stream.close();


		logger.debug ("Compressing tar file = " + tarFile.getName());
		final String outFileName = tarFile.getAbsoluteFile() + ".gz";
		final GZIPOutputStream outGZ = new GZIPOutputStream(new FileOutputStream(outFileName));
		final FileInputStream inTAR = new FileInputStream(tarFile);

		int len;
		while((len = inTAR.read(buffer)) > 0) {
			outGZ.write(buffer, 0, len);
		}
		inTAR.close();
		outGZ.finish();
		outGZ.close();


		logger.debug("Deleting TAR file");
		logger.debug(tarFile.getName() + " " + (tarFile.delete()?"deleted":"not deleted"));

		
		logger.debug(targetArchive.getName() + " successfull created");

	}
	
}

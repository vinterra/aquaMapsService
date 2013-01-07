package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.utils;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;
import gr.uoa.di.madgik.grs.reader.ForwardReader;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.FileField;
import gr.uoa.di.madgik.grs.record.field.FileFieldDefinition;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;



public class RSWrapper {

	public static GCUBELog logger= new GCUBELog(RSWrapper.class);
	
	static {
		try{
			List<PortRange> ports=new ArrayList<PortRange>(); //The ports that the TCPConnection manager should use
			ports.add(new PortRange(3000, 3050));             //Any in the range between 3000 and 3050
			TCPConnectionManager.Init(
					new TCPConnectionManagerConfig(GHNContext.getContext().getHostname(), //The hostname by which the machine is reachable 
							ports,                                    //The ports that can be used by the connection manager
							true                                      //If no port ranges were provided, or none of them could be used, use a random available port
					));
			TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());      //Register the handler for the gRS2 incoming requests
			TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler()); //Register the handler for the gRS2 store incoming requests
		}catch(Exception e){logger.warn("error initilaizing the RSWrapper",e);}
	}
	
	
	
	
	public static File getStreamFromLocator(URI locator) throws Exception{
		ForwardReader<GenericRecord> reader=new ForwardReader<GenericRecord>(locator);
		File importedFile=null;
		reader.setIteratorTimeout(300);
		for(GenericRecord rec : reader){
			//In case a timeout occurs while optimistically waiting for more records form an originally open writer
			if(rec==null) break;
			logger.trace("record is not null");
			//Retrieve the required field of the type available in the gRS definitions
			rec.getField("fileToImport").makeAvailable();
			BufferedInputStream bin=new BufferedInputStream(rec.getField("fileToImport").getMediatingInputStream());
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			importedFile= File.createTempFile("import", ".zip");
			FileOutputStream fos= new FileOutputStream(importedFile);
			while ((bytesRead = bin.read(buffer)) != -1) 
				fos.write(buffer, 0, bytesRead);
			fos.close();
			//importedFile= ((FileField)rec.getField("fileToImport").ge).getPayload();
			//Close the reader to release and dispose any resources in boith reader and writer sides
			reader.close();
		}
		return importedFile;
	}
	
	
	private RecordWriter<GenericRecord> writer=null;
		
	public RSWrapper(GCUBEScope scope) throws Exception{
		FileFieldDefinition fileFieldDefinition = new FileFieldDefinition("fileToImport");
		fileFieldDefinition.setDeleteOnDispose(true);
		RecordDefinition[] defs=new RecordDefinition[]{          //A gRS can contain a number of different record definitions
		        new GenericRecordDefinition((new FieldDefinition[] { //A record can contain a number of different field definitions
		        fileFieldDefinition				        //The definition of the field
		      }))
		    };
		 this.writer=new RecordWriter<GenericRecord>(
		       new TCPWriterProxy(), //The proxy that defines the way the writer can be accessed
		       defs   //The definitions of the records the gRS handles
		 );
	}
	
	public void add(File fileInput) throws Exception{
		GenericRecord gr=new GenericRecord();
		gr.setFields(new FileField[]{new FileField(fileInput)});
		this.writer.put(gr);
	}
	
	public void close() throws Exception{
		this.writer.close();
	}
	
	public URI getLocator() throws GRS2WriterException{
		logger.trace(this.writer.getLocator().toString());
		return this.writer.getLocator();
	}

}



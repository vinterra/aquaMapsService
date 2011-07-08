package org.gcube.application.aquamaps.aquamapsservice.impl.engine.request;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.application.aquamaps.aquamapspublisher.utils.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.utils.logging.GCUBELog;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

public class Worker<T> extends Thread{
	private static GCUBELog logger= new GCUBELog(Worker.class);

	private static Semaphore semaphore;
	private static String persistencePath;
	private static boolean ready=false;



	public static void init(int maxResourceCount,String persistenceFolder)throws Exception{
		semaphore=new Semaphore(maxResourceCount);
		persistencePath=persistenceFolder;
		ready=true;
	}

	public static String postpone(Request toPostpone)throws Exception{
		if(!ready) throw new Exception("Workers are not initialized.");
		String file=ServiceUtils.generateId("", ".xml");
		ObjectOutputStream stream=null;
		try{
			stream=AquaMapsXStream.getXMLInstance().createObjectOutputStream(
					new FileWriter(persistencePath+File.separator+file));
			stream.writeObject(toPostpone.getTheRequest());
			return file;
		}catch(Exception e){throw e;}
		finally{
			if(stream!=null){
				stream.flush();
				stream.close();
			}
		}
	}

	public static Object load(String filePath)throws Exception{
		if(!ready) throw new Exception("Workers are not initialized");
		ObjectInputStream is=null;
		try{
			is=AquaMapsXStream.getXMLInstance().createObjectInputStream(new FileReader(filePath));
			return is.readObject();
		}catch(Exception e ){throw e;}
		finally{
			if(is!=null)is.close();
		}
	}
	
	public static int getCount(){return semaphore.availablePermits();}
	
	public static Worker acquireWorker(Request theRequest){
		try{
			semaphore.acquire();
		}catch(InterruptedException e){
		}
		return new Worker(theRequest);
	}
	
	
	public static void releaseWorker(){
		semaphore.release();
	}
	

	
	//*************************** INSTANCE 
	
	
	private Request<T> theRequest;
	
	protected Worker(Request<T> theRequest) {
		super("Worker "+theRequest.getReferenceID());
		this.theRequest=theRequest;
	}
	
	
	@Override
	public void run() {
		try{
			perform();
		}catch(Exception e){
			logger.error("Unexpected Exception while working..",e);
		}finally{
			releaseWorker();
		}
	}
	
	protected void perform()throws Exception{
		throw new Exception("Workers must implement this method");
	}
}

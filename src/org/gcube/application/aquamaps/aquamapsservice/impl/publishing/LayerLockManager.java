package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.LayerGenerationRequest;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.utils.logging.GCUBELog;

import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantReadWriteLock;

public class LayerLockManager {
	private static GCUBELog logger= new GCUBELog(LayerLockManager.class);
	
	static class LockObject{
		private long timestamp;
		private ReentrantReadWriteLock lock= new ReentrantReadWriteLock();
		
		public LockObject() {
			timestamp=System.currentTimeMillis();
			lock.writeLock().lock();
		}
		
		public long getTimeStamp(){return timestamp;}
		
		public void release(){
			lock.writeLock().unlock();
		}
		public void lockRead(){
			lock.readLock().lock();
		}
	}
	
	public static class Ticket{
		private String md5;
		private Boolean booked;
		public Ticket(String md5, Boolean booked) {
			super();
			this.md5 = md5;
			this.booked = booked;
		}
		public String getMD5(){return md5;}
		public Boolean isBooked(){return booked;}		
	}
	
	public static String requestToMD5(LayerGenerationRequest theRequest)throws Exception{
		MessageDigest md=MessageDigest.getInstance("MD5");
		String toDigest=AquaMapsXStream.getXMLInstance().toXML(theRequest);
		return md.digest(toDigest.getBytes())+"";
	}
	
	private static Map<String,LockObject> lockRequests=new ConcurrentHashMap<String, LayerLockManager.LockObject>();
	
	public static synchronized Ticket isLayerGenerationBooked(LayerGenerationRequest request) throws Exception{
		String ticket=requestToMD5(request);
		if(lockRequests.containsKey(request)){
			lockRequests.get(ticket).lockRead();
			return new Ticket(ticket,true);
		}else{
			lockRequests.put(ticket, new LockObject());
			return new Ticket(ticket,false);
		}
		
	}
	
	public static synchronized void releaseLocks(Ticket ticket) throws Exception{
		if(lockRequests.containsKey(ticket.getMD5())){
			lockRequests.get(ticket.getMD5()).release();
			lockRequests.remove(ticket.getMD5());
		}
	}
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.util;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.gcube.common.core.utils.logging.GCUBELog;



public class MyPooledExecutor {

	private static final GCUBELog logger=new GCUBELog(MyPooledExecutor.class);
	
	/**
	 * Uses java.util.concurrent.Executors.defaultThreadFactory() setting threadLabel and priority (optional)
	 * 
	 * @author fabio
	 *
	 */
	
	protected static class MyThreadFactory implements ThreadFactory{
		
		private String label;
		private int priority;
		private boolean setPriority;
		
		public MyThreadFactory(String threadLabel,int priority) {
			super();			
			this.label=threadLabel;
			this.priority=priority;
			setPriority=true;
		}
		public MyThreadFactory(String threadLabel){
			super();
			this.label=threadLabel;
			setPriority=false;
		}
		
		@Override
		public Thread newThread(Runnable arg0) {
			Thread toReturn=Executors.defaultThreadFactory().newThread(arg0);
			toReturn.setName(ServiceUtils.generateId(label, ""));
			if(setPriority)toReturn.setPriority(priority);
			return toReturn;
		}
	}
	
	public static ExecutorService getExecutor(String threadLabel,int maxThread){
		return Executors.newFixedThreadPool(maxThread, new MyThreadFactory(threadLabel));
	}
	

	
	private static class BlockingRejectionHandler implements RejectedExecutionHandler{

		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			 BlockingQueue<Runnable> queue = executor.getQueue();
	         if (executor.isShutdown()) {
	                    throw new RejectedExecutionException(
	                        "ThreadPoolExecutor has shutdown while attempting to offer a new task.");
	                }
	         boolean sent=false;
	         while(!sent){
	        	 try{
	        		 sent=queue.offer(r,executor.getKeepAliveTime(TimeUnit.MILLISECONDS),TimeUnit.MILLISECONDS);	
	        	 }catch(Exception e){
	        		 logger.debug("Timeout while waiting for execution retry.. ");
	        	 }
	         }
		}
		
	}
	
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;



import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.common.core.utils.logging.GCUBELog;



public class MyPooledExecutor extends ThreadPoolExecutor {

	private static final GCUBELog logger=new GCUBELog(MyPooledExecutor.class);
	
	/**
	 * Uses java.util.concurrent.Executors.defaultThreadFactory() setting threadLabel and priority (optional)
	 * 
	 * @author fabio
	 *
	 */
	
	protected class MyThreadFactory implements ThreadFactory{
		
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
	
	
	public MyPooledExecutor(String threadLabel,int threadPriority,int maxThread,int minThread,int keepalive) {
		super(minThread,maxThread,Long.MAX_VALUE,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(maxThread),new BlockingRejectionHandler());
		setThreadFactory(new MyThreadFactory(threadLabel, threadPriority));
		
//		super(new BoundedBuffer(maxThread));
//		setKeepAliveTime(keepalive);
//		setMinimumPoolSize(minThread);
//		setMaximumPoolSize(maxThread);
//		setThreadFactory(new MyThreadFactory(threadLabel, threadPriority));
//		waitWhenBlocked();
	}
	
	public MyPooledExecutor(String threadLabel,int maxThread,int minThread,int keepalive) {
		super(minThread,maxThread,Long.MAX_VALUE,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(maxThread), new BlockingRejectionHandler());
		setThreadFactory(new MyThreadFactory(threadLabel));
		
//		
//		super(new BoundedBuffer(maxThread));
//		setKeepAliveTime(keepalive);
//		setMinimumPoolSize(minThread);
//		setMaximumPoolSize(maxThread);
//		setThreadFactory();
//		waitWhenBlocked();
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

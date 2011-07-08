package org.gcube.application.aquamaps.aquamapsservice.impl;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

public class ThreadManager {

//	private static BlockingQueue<Runnable> queue =  new ArrayBlockingQueue<Runnable>(ServiceContext.getContext().getQueueSize(), true);
//	private static ThreadPoolExecutor executor= new ThreadPoolExecutor(ServiceContext.getContext().getCoreSize(), ServiceContext.getContext().getMaxSize(), ServiceContext.getContext().getWaitIdleTime(), TimeUnit.MILLISECONDS, queue);
	
	private static PooledExecutor pool=null;
	
	static{
//		executor.prestartAllCoreThreads();
		
		try{
		pool=new PooledExecutor(new BoundedBuffer(ServiceContext.getContext().getPropertyAsInteger("maxSize")),ServiceContext.getContext().getPropertyAsInteger("queueSize"));
		pool.setKeepAliveTime(ServiceContext.getContext().getPropertyAsInteger("waitIdleTime"));
		pool.setMinimumPoolSize(ServiceContext.getContext().getPropertyAsInteger("30"));		
		pool.waitWhenBlocked();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Executor getExecutor(){
		return pool;
	}
	
}

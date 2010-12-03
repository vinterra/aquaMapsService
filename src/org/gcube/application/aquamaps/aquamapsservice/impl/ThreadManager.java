package org.gcube.application.aquamaps.aquamapsservice.impl;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.Executor;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

public class ThreadManager {

//	private static BlockingQueue<Runnable> queue =  new ArrayBlockingQueue<Runnable>(ServiceContext.getContext().getQueueSize(), true);
//	private static ThreadPoolExecutor executor= new ThreadPoolExecutor(ServiceContext.getContext().getCoreSize(), ServiceContext.getContext().getMaxSize(), ServiceContext.getContext().getWaitIdleTime(), TimeUnit.MILLISECONDS, queue);
	
	private static PooledExecutor pool=new PooledExecutor(new BoundedBuffer(ServiceContext.getContext().getMaxSize()),ServiceContext.getContext().getQueueSize());
	
	static{
//		executor.prestartAllCoreThreads();
		pool.setKeepAliveTime(ServiceContext.getContext().getWaitIdleTime());
		pool.setMinimumPoolSize(ServiceContext.getContext().getCoreSize());		
		pool.waitWhenBlocked();
	}
	
	public static Executor getExecutor(){
		return pool;
	}
	
}

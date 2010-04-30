package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadManager {

	private static BlockingQueue<Runnable> queue =  new ArrayBlockingQueue<Runnable>(ServiceContext.getContext().getQueueSize(), true);
	private static ThreadPoolExecutor executor= new ThreadPoolExecutor(ServiceContext.getContext().getCoreSize(), ServiceContext.getContext().getMaxSize(), ServiceContext.getContext().getWaitIdleTime(), TimeUnit.MILLISECONDS, queue);
	
	static{
		executor.prestartAllCoreThreads();
	}
	
	public static ExecutorService getExecutor(){
		return executor;
	}
	
}

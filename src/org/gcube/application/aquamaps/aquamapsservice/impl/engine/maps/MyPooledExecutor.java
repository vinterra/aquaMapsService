package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;

import EDU.oswego.cs.dl.util.concurrent.BoundedBuffer;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

public class MyPooledExecutor extends PooledExecutor {

	protected class ThreadFactory extends DefaultThreadFactory{
		
		private String label;
		private int priority;
		private boolean setPriority;
		
		public ThreadFactory(String threadLabel,int priority) {
			super();			
			this.label=threadLabel;
			this.priority=priority;
			setPriority=true;
		}
		public ThreadFactory(String threadLabel){
			super();
			this.label=threadLabel;
			setPriority=false;
		}
		
		@Override
		public Thread newThread(Runnable arg0) {
			Thread toReturn=super.newThread(arg0);
			toReturn.setName(ServiceUtils.generateId(label, ""));
			if(setPriority)toReturn.setPriority(priority);
			return toReturn;
		}
	}
	
	
	public MyPooledExecutor(String threadLabel,int threadPriority,int maxThread,int minThread,int keepalive) {
		super(new BoundedBuffer(maxThread));
		setKeepAliveTime(keepalive);
		setMinimumPoolSize(minThread);
		setMaximumPoolSize(maxThread);
		setThreadFactory(new ThreadFactory(threadLabel, threadPriority));
		waitWhenBlocked();
	}
	
	public MyPooledExecutor(String threadLabel,int maxThread,int minThread,int keepalive) {
		super(new BoundedBuffer(maxThread));
		setKeepAliveTime(keepalive);
		setMinimumPoolSize(minThread);
		setMaximumPoolSize(maxThread);
		setThreadFactory(new ThreadFactory(threadLabel));
		waitWhenBlocked();
	}
}

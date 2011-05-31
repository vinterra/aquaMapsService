package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ThreadManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.utils.logging.GCUBELog;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentLinkedQueue;

public class SubmittedMonitorThread extends Thread {

	private static final GCUBELog logger=new GCUBELog(SubmittedMonitorThread.class);
	
	private static ConcurrentLinkedQueue queue= new ConcurrentLinkedQueue();
	private static SubmittedMonitorThread instance;
	
	private final long mills;
	private final String path;
	
	public SubmittedMonitorThread(String path, long mills) throws Exception{
		instance=this;
		this.path=path;
		this.mills=mills;
		logger.trace("Created Submitted Monitor path : "+path+", routine time : "+mills);
		File dir=new File(path);
		dir.mkdirs();
	}
	
	@Override
	public void run() {
		try{
			while(true){
				while(!queue.isEmpty()){
					String fileName=(String) queue.poll();
					try{
					Job job=(Job) AquaMapsXStream.getXMLInstance().fromXML(new FileReader(path+File.separator+fileName));
					JobSubmissionThread thread=new JobSubmissionThread(job);
					ServiceContext.getContext().setScope(thread,ServiceContext.getContext().getStartScopes());
					ThreadManager.getExecutor().execute(thread);
					FileUtils.forceDelete(new File(fileName));
					}catch(Exception e){
						logger.error("Unable to load/ submit job from file : "+fileName,e);
					}
					
					try{
						logger.debug("Submitted Monitor going to sleep, awaking in "+ mills);
						Thread.sleep(mills);
					}catch(InterruptedException e){
						logger.debug("Submitted Monitor woken up..");
					}
					
				}
				
			}
			
			
		}catch(Exception e){
			logger.error("",e);
		}
	}
	
	
	public String putInQueue(Job job)throws Exception{
		String id=ServiceUtils.generateId("job", ".xml");
		
		File f= new File(new File(path),id);
		BufferedWriter out = new BufferedWriter(new FileWriter(f));
		out.write(AquaMapsXStream.getXMLInstance().toXML(job));
		out.close();
		queue.add(id);
		return id;
	}
	
	
	public static SubmittedMonitorThread getInstance(){return instance;}
	
}

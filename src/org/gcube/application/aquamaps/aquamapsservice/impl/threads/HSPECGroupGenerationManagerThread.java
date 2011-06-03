package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

import org.gcube.application.aquamaps.aquamapsservice.impl.ThreadManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.HSPECGroupGenerationRequestsManager;
import org.gcube.application.aquamaps.dataModel.Types.HSPECGroupGenerationPhase;
import org.gcube.application.aquamaps.dataModel.enhanced.HSPECGroupGenerationRequest;
import org.gcube.common.core.utils.logging.GCUBELog;

public class HSPECGroupGenerationManagerThread extends Thread {


	private static final GCUBELog logger=new GCUBELog(HSPECGroupGenerationManagerThread.class);


	private long mills;


	public HSPECGroupGenerationManagerThread(long mills) throws Exception{
		this.mills=mills;
	}


	@Override
	public void run() {
		while(true){
//			logger.trace("Looking for submitted");
			HSPECGroupGenerationRequest request=null;
			try{
				do{
					request=HSPECGroupGenerationRequestsManager.getFirst();
					if(request!=null){
						logger.debug("Found request with id "+request.getId());
						HSPECGroupGenerationThread thread=new HSPECGroupGenerationThread(request);
						ThreadManager.getExecutor().execute(thread);
					} 
				}while(request!=null);
			}catch(Exception e){
				if(request==null) logger.error("Unexpected exception, request was null",e);
				else{
					logger.error("Unexpeceted error while generating HSPEC group : request id = "+request.getId(),e);
					try{
						HSPECGroupGenerationRequestsManager.setPhase(HSPECGroupGenerationPhase.Error, request.getId());
					}catch(Exception e1){
						logger.error("Unable to update phase for request id "+request.getId(),e1);
					}
				}
			}finally{
//				logger.debug("Waiting .. "+mills);
				try{					
					Thread.sleep(mills);
				}catch(Exception e){
//					logger.debug("Woken up..");
					}
			}

		}
	}

}

package org.gcube.application.aquamaps.aquamapsservice.impl.engine.request;

import org.gcube.application.aquamaps.dataModel.enhanced.Job;

public class JobWorker extends Worker<Job>{

	protected JobWorker(Request<Job> theRequest) {
		super(theRequest);
	}

	
	@Override
	protected void perform() throws Exception {
		//TODO IMPLEMENT
		
		//check if data available (store Job into publisher and check status)
				//Y update job references and insert obj references
		
			//N prepare HSPEC
				// per ogni Object call ObjectRequestManager.submitRequest
				// wait for completion semaphore
			
		
		// update job references and published JOB 
	}
	
	
}

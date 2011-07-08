package org.gcube.application.aquamaps.aquamapsservice.impl.engine.request;

import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;

public class AquaMapsObjectWorker extends Worker<AquaMapsObject>{

	protected AquaMapsObjectWorker(Request<AquaMapsObject> theRequest) {
		super(theRequest);
	}

	
	@Override
	protected void perform() throws Exception {
		//TODO implement
		
		//get ResultSet
			//if (needImages) generate Images
			//if (needLayers) generate Layers
		
		//update ref and stored obj
		//signal 
	}
	
}

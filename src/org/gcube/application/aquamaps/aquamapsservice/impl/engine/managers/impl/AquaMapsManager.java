package org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.impl;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.managers.AquaMapsManagerI;
import org.gcube.common.core.utils.logging.GCUBELog;

public class AquaMapsManager extends SubmittedManager implements
		AquaMapsManagerI {

	private static GCUBELog logger= new GCUBELog(AquaMapsManager.class);
	
	private AquaMapsManager(){}
	private static AquaMapsManagerI instance =new AquaMapsManager();
	public static AquaMapsManagerI get(){return instance;}
	
	
	@Override
	public void delete(int submittedId) throws Exception {
		// TODO Auto-generated method stub
		super.delete(submittedId);
	}
	
	
	

}

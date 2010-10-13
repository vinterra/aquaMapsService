package org.gcube.application.aquamaps.aquamapsservice.impl.threads;

public class SourceGenerationThread extends Thread {

	private static int requestId;
	
	public SourceGenerationThread(int id) {
		requestId=id;
	}
	
	@Override
	public void run() {
		//FIXME Import
		//FIXME Merge
		//FIXME Register
		super.run();
	}
	
}

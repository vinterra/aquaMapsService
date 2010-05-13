package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import org.gcube.common.core.utils.logging.GCUBELog;

public class GeneratorTest extends Thread{
	private static GCUBELog logger= new GCUBELog(GeneratorTest.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int numThread=Integer.parseInt(args[0]);
		for(int i=0;i<numThread;i++){
			GeneratorTest test=new GeneratorTest(String.valueOf(i));
			test.start();
		}
	}

	public GeneratorTest(String name) {
		super(name);
	}
	
	@Override
	public void run() {
		System.out.println("Starting thread +"+this.getName());
		ImageGeneratorRequest request=new ImageGeneratorRequest(this.getName());
		try{
		GeneratorManager.requestGeneration(request);
		System.out.println("Thread "+this.getName()+" completed");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

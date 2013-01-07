package testClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ExtendedExecutor;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.MyPooledExecutor;

public class ExecutorTesting {


	private static final int CORE_SIZE=4;
	private static final int POOL_NUMBER=4;
	private static int finished=0;


	public static void main(String[] args){
		final ArrayList<ExtendedExecutor> executors=new ArrayList<ExtendedExecutor>();
		int toComplete=POOL_NUMBER*CORE_SIZE;

		for(int i=0;i<POOL_NUMBER;i++){
			final int index=i;
			executors.add(MyPooledExecutor.getExecutor(index+"_Pool_worker", CORE_SIZE));
			Thread monitor=new Thread(){
				@Override
				public void run() {
					System.out.println("Started monitor "+index);
					for(int j=0;j<CORE_SIZE*5;j++){
						
						System.out.println("Submitting request to pool "+index+" stats : "+executors.get(index).getDetails());
						executors.get(index).execute(new Thread(){
							@Override
							public void run() {
								System.out.println(Thread.currentThread().getName()+" Start");
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {}
								System.out.println(Thread.currentThread().getName()+" DONE");
								finished++;
							}
						});
					}
				}
			};
			monitor.start();
		}

		



		while(finished<toComplete){
			try{
				System.out.println("MAIN : WAITING FOR "+(toComplete-finished));
				Thread.sleep(1*60*1000);
			}catch(InterruptedException e){

			}
		}
		System.out.println("MAIN : FINISHED");
		System.exit(0);
	}

}

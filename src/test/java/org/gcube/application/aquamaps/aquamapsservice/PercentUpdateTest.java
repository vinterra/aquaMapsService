package testClient;

import java.util.ArrayList;

public class PercentUpdateTest {

	public static class MyThread extends Thread{
		private double percent=0d;
		public double getPercent(){return percent;}
		@Override
		public void run() {
			while(percent<=1){				
				try{
					sleep(500);
				}catch(InterruptedException e){}
				percent+=0.2;
			}
			percent=1;
		}
	}
	
	public static void main(String[] args){
		ArrayList<MyThread> threads=new ArrayList<MyThread>();
		for(int i=0;i<5;i++)threads.add(new MyThread());
		System.out.println("Starting");
		for(MyThread t:threads)t.start();
		while(true){
			Double percent=0d;
			for(MyThread t:threads)percent+=t.getPercent()/threads.size();
			System.out.println("Percent "+percent);
			try {Thread.sleep(400);
			} catch (InterruptedException e) {}
		}
	}
}

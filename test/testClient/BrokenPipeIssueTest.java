package testClient;

import org.gcube.application.aquamaps.stubs.AquaMapsPortType;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.json.JSONObject;

public class BrokenPipeIssueTest {

	/**
	 * @param args
	 */
	
	public static int waitTime=60*60*1000;
	
	
	public static void main(String[] args) {
//		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
//		session.setScope("/gcube/devsec");
//		AquaMapsPortType pt;
//		try{
//			pt=AquaMapsServiceTester.getPortType(session);
//			int run=0;
//			while(true){
//				String jsonString=pt.getJobList("fabio.sinibaldi");
//				JSONObject obj=new JSONObject(jsonString);
//				run++;
//				System.out.println("run : "+run+"\ttotalcount : "+obj.getInt("totalcount"));
//				try{
//					Thread.sleep(waitTime);
//				}catch(InterruptedException e1){}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}

	}

}

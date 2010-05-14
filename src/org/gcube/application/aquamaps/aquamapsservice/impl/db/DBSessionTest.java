package org.gcube.application.aquamaps.aquamapsservice.impl.db;

import junit.framework.TestCase;

public class DBSessionTest extends TestCase {
	
	public void testOpenSession() {
		for(int i=0;i<1000;i++){
		try{
			System.out.println("Session "+i);
			MySqlDBSession session=MySqlDBSession.openSession();
			session.executeQuery("Select * from EVENTS");
			session.close();
		}catch(Exception e){
			fail("run"+i+" "+e.getLocalizedMessage());
		}
		}
	}

}

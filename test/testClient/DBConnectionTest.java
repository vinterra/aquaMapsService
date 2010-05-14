package testClient;

import java.sql.ResultSet;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.MySqlDBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBUtils;

public class DBConnectionTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		MySqlDBSession session=MySqlDBSession.openSession();
		String query=(args.length>0)&&(args[0]!=null)?args[0]:"Select * from EVENTS";
		ResultSet rs=session.executeQuery(query);
		System.out.println(DBUtils.toJSon(rs));	
		session.close();
	}

}

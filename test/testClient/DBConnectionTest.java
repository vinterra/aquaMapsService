package testClient;

import java.sql.ResultSet;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;

public class DBConnectionTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DBSession session=DBSession.openSession(PoolManager.DBType.mySql);
		String query=(args.length>0)&&(args[0]!=null)?args[0]:"Select * from selectedSpecies";
		ResultSet rs=session.executeQuery(query);
		System.out.println(DBUtils.toJSon(rs));	
		session.close();
	}

}

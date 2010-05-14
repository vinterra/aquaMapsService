package testClient;

import java.sql.ResultSet;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GroupGenerator;

public class LayerPubblicationTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{		
		DBSession mysqlSession=DBSession.openSession(DBType.mySql);
		System.out.println("Querying..");
		ResultSet rs= mysqlSession.executeQuery("Select CsquareCode,Probability from HSPEC where SpeciesId='Fis-131546'");
		String outFile="csvFile";
		System.out.println("toCSV..");
		GeneratorManager.ResultSetToCSVFile(rs, outFile);
		GroupGenerator generator= new GroupGenerator();
		System.out.println("to POSTGIS..");
		String tableName=generator.createLayerData(outFile);
		System.out.println("generated table "+tableName);
	}

}

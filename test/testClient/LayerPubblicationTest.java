package testClient;

import java.sql.ResultSet;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GenerationUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GISGenerator;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.LayerGenerationRequest;

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
		GenerationUtils.ResultSetToCSVFile(rs, outFile);
		GISGenerator generator= new GISGenerator();
		System.out.println("to POSTGIS..");
		LayerGenerationRequest request= new LayerGenerationRequest();
		request.setCsvFile(outFile);
		request.setFeatureLabel("Probability");
		request.setFeatureDefinition("real");
		request.setLayerName("LayerCreationTest_withCRS");
		boolean result=generator.createLayer(request);		
		System.out.println("generated table :"+result);
	}

}

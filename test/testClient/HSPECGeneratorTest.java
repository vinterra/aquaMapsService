package testClient;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.stubs.GenerateHSPECRequestType;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.types.StringArray;

public class HSPECGeneratorTest {

	static String csvPath="/home/fabio/Desktop/SpeciesList.csv";
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		System.out.println("Loading csv...");
		List<String> ids=new ArrayList<String>(); 
		for(List<Field> row: ServiceUtils.loadCSV(csvPath, ','))
			for(Field f : row) if(f.getName().equalsIgnoreCase(SpeciesOccursumFields.speciesid+""))ids.add(f.getValue());
		
		System.out.println("Loaded "+ids.size()+" ids");
		
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/gcube/devsec");
		DataManagementPortType pt=HCAFGenerationTest.getPortType(session);
		GenerateHSPECRequestType req= new GenerateHSPECRequestType();
		req.setGenerateNative(true);
		req.setGenerateSuitable(true);
		req.setSourceHCAFId(1);
		req.setSourceHSPENId(1);
		req.setToGeneratePrefix("TestingHSPEC");
		req.setUserId(session.getUsername());
		req.setSpeciesSelection(new StringArray(ids.toArray(new String[ids.size()])));
//		req.setSpeciesSelection(new StringArray(new String[]{ids.get(0)}));
		pt.generateHSPEC(req);
		
	}

}

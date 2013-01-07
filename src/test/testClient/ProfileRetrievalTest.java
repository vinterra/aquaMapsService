package testClient;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;

public class ProfileRetrievalTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException {
		String profile=ServiceUtils.URLtoString("http://wn06.research-infrastructures.eu:6900/HSPEC/286/Default.xml");
		System.out.println(profile);
//		System.out.println(new AquaMapsObject(profile).toXML());
	}

}

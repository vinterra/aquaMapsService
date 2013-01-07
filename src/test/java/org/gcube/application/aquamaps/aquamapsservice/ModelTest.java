package testClient;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.json.JSONException;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;

public class ModelTest {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	
	static int resId=215;
	
	static int occurId=124;
	
	
	public static void main(String[] args) throws Exception {
		DataManagementInterface dmInterface=WrapperTest.getDM();
		AquaMapsServiceInterface amInterface=WrapperTest.getAM();
		
		Resource r=amInterface.loadResource(resId);
		System.out.println(r.toXML());
		
		r.addSource(amInterface.loadResource(occurId));
		System.out.println(r.toXML());
	}

}

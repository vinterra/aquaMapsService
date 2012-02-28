package testClient;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.json.JSONException;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;

public class ModelTest {

	/**
	 * @param args
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws JSONException {
		ArrayList<String> list=new ArrayList<String>();
		list.add(null);
		String csv=CSVUtils.listToCSV(list);
		System.out.println(csv);
		list=CSVUtils.CSVToStringList(csv);
		list.add("");
		list.add(1+"");
		list.add(null);
		list.add(2+"");
		csv=CSVUtils.listToCSV(list);
		System.out.println(csv);
		list=CSVUtils.CSVToStringList(csv);
		csv=CSVUtils.listToCSV(list);
		System.out.println(csv);
		System.out.println("DONE");
		
		Resource r=new Resource(ResourceType.HCAF,1);
		r.setTableName("default");
		r.addSource(r);
		Resource r2=new Resource(ResourceType.HCAF,3);
		r2.setTableName("franco");
		r.addSource(r2);
		for(MetaSourceFields f: MetaSourceFields.values())
			System.out.println(r.getField(f).toJSONObject());
		
	}

}

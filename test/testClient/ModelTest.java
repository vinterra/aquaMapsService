package testClient;

import java.util.ArrayList;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;

public class ModelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> list=new ArrayList<String>();
		list.add(null);
		String csv=CSVUtils.listToCSV(list);
		System.out.println(csv);
		list=CSVUtils.CSVToList(csv);
		list.add("");
		list.add(1+"");
		list.add(null);
		list.add(2+"");
		csv=CSVUtils.listToCSV(list);
		System.out.println(csv);
		list=CSVUtils.CSVToList(csv);
		csv=CSVUtils.listToCSV(list);
		System.out.println(csv);
		System.out.println("DONE");
	}

}

package testClient;

import org.gcube.application.aquamaps.aquamapsservice.impl.monitor.ReportItem;

import com.thoughtworks.xstream.XStream;

public class XStreamTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XStream xstream = new XStream();
		xstream.alias("ReportItem", org.gcube.application.aquamaps.aquamapsservice.impl.monitor.ReportItem.class);
		ReportItem rep=new ReportItem();
		rep.setActualValue(10);
		rep.setOvercomesInLast10Hours(15);
		rep.setOvercomesInLast24Hours(24);
		rep.setOvercomesTotal(100);
		rep.setThreshold(5);
		rep.setValueName("Disk Free Space");
		System.out.println(xstream.toXML(rep));
	}

}

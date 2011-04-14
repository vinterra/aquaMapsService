package testClient;

import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;

public class XStreamTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		XStream xstream = new XStream();
//		xstream.alias("ReportItem", org.gcube.application.aquamaps.aquamapsservice.impl.monitor.ReportItem.class);
//		ReportItem rep=new ReportItem();
//		rep.setActualValue(10);
//		rep.setOvercomesInLast10Hours(15);
//		rep.setOvercomesInLast24Hours(24);
//		rep.setOvercomesTotal(100);
//		rep.setThreshold(5);
//		rep.setValueName("Disk Free Space");
//		System.out.println(xstream.toXML(rep));
		
		Species spec= new Species("Fis-XXX");
		spec.addField(new Field(SpeciesOccursumFields.english_name+"","Pisci friscu",FieldType.STRING));
		
//		System.out.println(AquaMapsXStream.getJSONInstance().toXML(spec));
		
	}

}

package testClient;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.HSPECGenerator;
import org.gcube.application.aquamaps.stubs.DataManagementPortType;
import org.gcube.application.aquamaps.stubs.GenerateHSPECRequestType;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Envelope;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_DFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.stubs.dataModel.xstream.EnvelopeConverter;
import org.gcube.application.aquamaps.stubs.wrapper.AquaMapsServiceWrapper;
import org.gcube.application.aquamaps.stubs.wrapper.WrapperUtils;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.core.types.StringArray;

import com.thoughtworks.xstream.XStream;

public class HSPECGeneratorTest {

	static String csvPath="/home/fabio/Desktop/SpeciesList.csv";
	
	/**
	 * @param args
	 */
	public static void main(String[] args)throws Exception {
		fromSpeciesAndCell();
//		fromCSVList();
		System.out.println("END");
	}
	
	
	private static void fromSpeciesAndCell()throws Exception{
		ASLSession session = SessionManager.getInstance().getASLSession(String.valueOf(Math.random()), "Tester");		
		session.setScope("/gcube/devsec");
		AquaMapsServiceWrapper wrapper=new AquaMapsServiceWrapper(session, AquaMapsServiceTester.SERVICE_URI);
		
		Species s=wrapper.loadEnvelope("Fis-22836",1);
//		Envelope env=wrapper.loadEnvelope(s.getId(), 1);
//		s.getAttributesList().addAll(Field.load(env.toFieldArray()));
//		s.addField(new Field(HspenFields.layer+"",env.isUseBottomSeaTempAndSalinity()?"b":"s",FieldType.STRING));
//		s.addField(new Field(HspenFields.pelagic+"",env.isPelagic()?"1":"0",FieldType.STRING));
//		s.addField(new Field(HspenFields.meandepth+"",env.isUseBottomSeaTempAndSalinity()?"b":"s",FieldType.STRING));
		Cell c=new Cell("1000:100:1");
		
		c.getAttributesList().add(new Field(HCAF_DFields.sstanmean+"","27.27",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.sbtanmean+"","1.89",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.depthmin+"","4760",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.depthmean+"","46.90",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.depthmax+"","5014",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.salinitybmean+"","34.826",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.salinitymean+"","34.76",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.primprodmean+"","450",FieldType.DOUBLE));
		c.getAttributesList().add(new Field(HCAF_DFields.iceconann+"","0",FieldType.DOUBLE));
		
		
		Properties prop= new Properties();
		System.out.println("Loading configuration File...");
		prop.load(new FileInputStream("etc/config.properties"));
		System.out.println("Found properties : "+prop.toString());
		
		Map<EnvelopeFields,Boolean> defaultWeights= new HashMap<EnvelopeFields, Boolean>();
		defaultWeights.put(EnvelopeFields.Depth, Boolean.parseBoolean(prop.getProperty("evaluateDepth","true").trim()));
		defaultWeights.put(EnvelopeFields.IceConcentration,Boolean.parseBoolean(prop.getProperty("evaluateIceConcentration","true").trim())); 
		defaultWeights.put(EnvelopeFields.LandDistance, Boolean.parseBoolean(prop.getProperty("evaluateLandDistance","false").trim()));
		defaultWeights.put(EnvelopeFields.PrimaryProduction,Boolean.parseBoolean(prop.getProperty("evaluatePrimaryProduction","true").trim()));
		defaultWeights.put(EnvelopeFields.Salinity,Boolean.parseBoolean(prop.getProperty("evaluateSalinity","true").trim()));
		defaultWeights.put(EnvelopeFields.Temperature, Boolean.parseBoolean(prop.getProperty("evaluateTemperature","true").trim()));
		
		List<Field> result= HSPECGenerator.getProbability(s, c, defaultWeights);
		XStream stream = new XStream();
		stream.processAnnotations(Envelope.class);
		stream.processAnnotations(Field.class);
		stream.registerConverter(new EnvelopeConverter());
		
		for(Field f:result)
			System.out.println(stream.toXML(f));
	}

	private static void fromCSVList()throws Exception{
		List<String> ids=new ArrayList<String>();
//		ids.add("Fis-22836");
//		
		
		System.out.println("Loading csv...");
		 
		for(List<Field> row: WrapperUtils.loadCSV(csvPath, ','))
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
		req.setEnableLog(true);
		req.setSpeciesSelection(new StringArray(ids.toArray(new String[ids.size()])));
//		req.setSpeciesSelection(new StringArray(new String[]{ids.get(0)}));
		pt.generateHSPEC(req);
	}
	
	
}
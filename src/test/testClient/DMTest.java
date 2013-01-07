package testClient;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.AquaMapsServiceInterface;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.Constant;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.DataManagementInterface;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;

public class DMTest {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws MalformedScopeExpressionException 
	 */
	
	
	private static DataManagementInterface dmInterface=null;
	private static AquaMapsServiceInterface amInterface=null;
	
	
	public static void main(String[] args) throws MalformedScopeExpressionException, Exception {
		
		
		amInterface =WrapperTest.getAM();
		dmInterface = WrapperTest.getDM();
		
		
		//******************* GENERATE MAPS
		
		int hspecId=275;
		
		dmInterface.generateMaps("fabio.sinibaldi", false, hspecId, Arrays.asList(new Field[]{
				new Field(SpeciesOccursumFields.familycolumn+"","Gadilidae",FieldType.STRING)
		}), true);
		
		
		
		//PROD
//		dmInterface=DataManagementCall.getCall(
//				GCUBEScope.getScope("/d4science.research-infrastructures.eu/Ecosystem/TryIt"),
//				"http://node49.p.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/application/aquamaps/aquamapsservice/DataManagement");
		
//
//		System.out.println("Query Resource :");
//		System.out.println(dmInterface.queryResource("Select * from hspen where pelagic = 1", new PagedRequestSettings(2, 0, null, OrderDirection.ASC)));
//		
		
		
		
//		System.out.println("Import Resource :");
//
//		System.out.println("ID IS "+dmInterface.importResource(new File("/home/fabio/Desktop/MiniOccurrence.csv"), "fabio.sinibaldi", ResourceType.OCCURRENCECELLS,
//				"UTF-8",new boolean[]{true,true,true,true,true,true,true,true,true,true},true,'\t'));
//		
		
		
		
//		System.out.println("Delete Resource : ");
//		dmInterface.deleteResource(95);
//		System.out.println("DONE");

		
//		System.out.println("GENERATE MAPS");
//		ArrayList<Field> filter= new ArrayList<Field>();
////		filter.add(new Field(SpeciesOccursumFields.classcolumn+"","Bivalvia",FieldType.STRING)); // ~ 300 species
//		filter.add(new Field(SpeciesOccursumFields.classcolumn+"","Holothuroidea",FieldType.STRING)); // 21 species
////		filter.add(new Field(SpeciesOccursumFields.classcolumn+"","Mammalia",FieldType.STRING)); // 53 species (NB  pelagic hspen)
////		filter.add(new Field(SpeciesOccursumFields.kingdom+"","Animalia",FieldType.STRING)); // ~ 11500 species
//		System.out.println("JOB ID IS "+dmInterface.generateMaps("fabio.sinibaldi", true, 262, filter, true));
		

		
		
		
		
		
//		System.out.println("ID IS "+fromRequestToGeneralEnvironment());
	
		
//		EnvironmentalExecutionReportItem report=dmInterface.getReport(0);
//		System.out.println("");
//		
		
//		
//		for(Field f:dmInterface.getDefaultSources())
//			System.out.println(f.toJSONObject());
		
//		System.out.println("Done");
		
		
		//***************** INTERPOLATE HCAFs
		
//		SourceGenerationRequest request=new SourceGenerationRequest();
//		request.getAlgorithms().add(AlgorithmType.NativeRange);
//		request.getAlgorithms().add(AlgorithmType.SuitableRange);
//		request.setAuthor("fabio.sinibaldi");
//		request.setGenerationname("Multi HSPEC");
//		request.setDescription("Testing");
////		Resource firstHCAF=amInterface.loadResource(126, ResourceType.HSPEN);
////		Resource secondHCAF=amInterface.loadResource(175, ResourceType.HSPEN);
//		
//		request.addSource(amInterface.loadResource(126, ResourceType.HSPEN));
//		request.addSource(amInterface.loadResource(175, ResourceType.HSPEN));
//		request.addSource(amInterface.loadResource(173, ResourceType.HCAF));
//		request.addSource(amInterface.loadResource(174, ResourceType.HCAF));
////		request.getParameters().add(new Field(SourceGenerationRequest.FIRST_HCAF_ID,firstHCAF.getSearchId()+"",FieldType.INTEGER));
////		request.getParameters().add(new Field(SourceGenerationRequest.FIRST_HCAF_TIME,2005+"",FieldType.INTEGER));
////		request.getParameters().add(new Field(SourceGenerationRequest.SECOND_HCAF_ID,secondHCAF.getSearchId()+"",FieldType.INTEGER));
////		request.getParameters().add(new Field(SourceGenerationRequest.SECOND_HCAF_TIME,2010+"",FieldType.INTEGER));
////		request.getParameters().add(new Field(SourceGenerationRequest.NUM_INTERPOLATIONS,4+"",FieldType.INTEGER));
//		
//		request.setExecutionEnvironment(Constant.AQUAMAPSSERVICE_PT_NAME);
//		request.setBackendURL("");
//		request.setSubmissionBackend(Constant.SERVICE_NAME);
//		request.setEnvironmentConfiguration(new HashMap<String, String>());
//		request.setLogic(LogicType.HSPEC);
//		request.setNumPartitions(16);
//		
//		request.setEnableimagegeneration(false);
//		request.setEnablelayergeneration(false);
//		
//		System.out.println(dmInterface.submitRequest(request));
		
		
		//***************** GENERATE HSPEC
		
//		SourceGenerationRequest request=new SourceGenerationRequest();		
//		request.getAlgorithms().add(AlgorithmType.SuitableRange);
//		request.setAuthor("fabio.sinibaldi");
//		request.setGenerationname("Test Regeneration");
//		request.setDescription("Testing");
//		
//		int[] resources=new int[]{
//			216,236
//		};
//		
//		for(int id:resources){
//			Resource toAdd=amInterface.loadResource(id);
//			System.out.println("Adding resource "+toAdd);
//			request.addSource(toAdd);
//		}
//			
//		
//		
//		
//		
//		request.setExecutionEnvironment("AquaMaps VRE");
//		request.setBackendURL("");
//		request.setSubmissionBackend(Constant.AQUAMAPSSERVICE_PT_NAME);
//		request.setEnvironmentConfiguration(new HashMap<String, String>());
//		request.setLogic(LogicType.HSPEC);
//		request.setNumPartitions(20);
//		
//		request.setExecutionParameters(new ArrayList<Field>(Arrays.asList(new Field[]{
//				new Field(SourceGenerationRequest.COMBINE_MATCHING,false+"",FieldType.BOOLEAN),
//				new Field(SourceGenerationRequest.FORCE_MAPS_REGENERATION,false+"",FieldType.BOOLEAN),
//				new Field(SourceGenerationRequest.GENERATE_MAPS,true+"",FieldType.BOOLEAN),
//				new Field(SourceGenerationRequest.GIS_ENABLED,true+"",FieldType.BOOLEAN),
//				})));
//		
//		System.out.println(dmInterface.submitRequest(request));
//		
		
		
		//************************** RESUBMISSION
		
//		System.out.println(dmInterface.resubmitGeneration("HGGR2012_03_08_12_35_48_745"));
		
		
		//************************* ANALYZE TABLES
		
//		Analysis toSend=new Analysis();
//		toSend.setAuthor("fabio.sinibaldi");
//		toSend.setDescription("Just a simple execution");
//		toSend.setSources(Arrays.asList(new Integer[]{
//			215,216,217,218,219,220, //HCAFs
//			235,236,237,238,239,240, //HSPENs
//			257,258,259,260,261,262  //HSPECs
//		}));
//		toSend.setTitle("Complete analysis");
//		toSend.setType(new ArrayList<AnalysisType>(Arrays.asList(new AnalysisType[]{
//				AnalysisType.GEOGRAPHIC_HCAF,
//				AnalysisType.HCAF
//		})));
//		System.out.println(dmInterface.analyzeTables(toSend));
		
		
//		//************************** Retrieve tar
//		File destinationDir=new File(System.getProperty("java.io.tmpdir"),"An2012_03_19_19_03_52_612");
//		
//		destinationDir.mkdirs();
//		int count=AppZip.unzipToDirectory(dmInterface.loadAnalysisResults("An2012_03_19_19_03_52_612").getAbsolutePath(), destinationDir);
//		System.out.println("Stored "+count+" files into "+destinationDir.getAbsolutePath());
		
//		FileInputStream fis=new FileInputStream();
//		File temp=File.createTempFile("analysis", ".tar.gz");
//		
//		
//		System.out.println("File is "+temp.getAbsolutePath());
//		FileOutputStream fos=new FileOutputStream( temp);
//		
//		IOUtils.copy(fis, fos);
//		IOUtils.closeQuietly(fis);
//		IOUtils.closeQuietly(fos);
//		
//		
//		List<File> files=ArchiveManager.unTarGz(temp);
//		
//		FileUtils.delete(temp);
//		
//		System.out.println("Done");
//		System.out.println("Extracted Files : "+Arrays.toString(files.toArray()));
		
		
		
//		System.out.println(dmInterface.getJSONSPECGroupGenreationRequests(new PagedRequestSettings(5, 0, GroupGenerationRequestFields.submissiontime+"", "ASC")));
//		EnvironmentalExecutionReportItem report= dmInterface.getReport(0);
//		System.out.println("**** REPORT *****");
//		System.out.println("resources Map : "+report.getResourcesMap());
//		System.out.println("resources Load : "+report.getResourceLoad());
//		System.out.println("percent : "+report.getPercent());
//		System.out.println("Species : "+report.getElaboratedSpecies());
		
		
		//************************ EXPORT TABLE
//		int resourceId =128;
//		System.out.println("Export Resource : "+resourceId);
//
//		File csv=dmInterface.exportResource(resourceId);
//		
//		csv.renameTo(new File("exported_mini.csv"));
//		
//		System.out.println("Exported to exported_mini.csv"); 
		
		
	}

	
	private static String fromRequestToGeneralEnvironment(List<Resource> selection)throws Exception{
		SourceGenerationRequest request=new SourceGenerationRequest();
		request.setAuthor("fabio.sinibaldi");
		request.setGenerationname("Initial execution");
		request.setDescription("First execution for suitable default hspec data");
		for(Resource r:selection)request.addSource(r);
		request.setSubmissionBackend(Constant.SERVICE_NAME);
//		request.setSubmissionBackend("RainyCloud");
		
		return dmInterface.submitRequest(request);
	}
	
	
	
}

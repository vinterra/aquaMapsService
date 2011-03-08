package org.gcube.application.aquamaps.aquamapsservice.impl.generators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CellManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_DFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HSPECFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.OccurrenceCellsFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * 
 * @author lucio
 *
 */
public class HSPECGenerator {

	private static GCUBELog logger= new GCUBELog(HSPECGenerator.class);

	private final Map<EnvelopeFields,Boolean> defaultWeights=new HashMap<EnvelopeFields, Boolean>();


	public static enum GENERATION_MODE{
		COMPLETE,LAZY
	}

	private static DecimalFormat formatter = new DecimalFormat("0.00");

	private static Double probabilityThresholdForInsert=0.01;


	private GENERATION_MODE mode;

	//******** GENERAL PARAMETERS

	private String hcafViewTable=null;
	private String hcafStaticTable;
	private String hcafDynamicTable;
	private String hspenTable;
	//	private String resultsTable=null;
	private String resultsNative=null;
	private String resultsSuitable=null;

	private String occurenceCellsTable;
	private String occurrenceCellsViewTable=null;
	private String hspecTableStructure=SourceManager.getSourceName(ResourceType.HSPEC, SourceManager.getDefaultId(ResourceType.HSPEC));


	//******** LAZY MODE PARAMETERS

	private String hspecNativeTable=null;
	private String hspecSuitableTable=null;


	private int jobId;

	private Map<String,Map<EnvelopeFields,Field>> jobWeights; 


	//******* GENERATION Variables

	Set<String> toGenerateSpeciesIds=new HashSet<String>();

	private DBSession session;



	private long startGeneration= System.currentTimeMillis();

	private ResultSet hspenRes;
	private ResultSet hcafRes;


	private long hspenLoops=0;

	private PreparedStatement psInsertSuitable;
	private PreparedStatement psInsertNative;

	private PreparedStatement psBound;

	private PreparedStatement psSeaIce;

	private int insertedNativeCellCount=0;
	private int insertedSuitableCellCount=0;

	//	private boolean suitableRange=false;

	private boolean generateSuitable=false;
	private boolean generateNative=false;


	private String currentSpeciesID;

	private Map<EnvelopeFields,Boolean> currentWeights;

	private PreparedStatement psCopyNative;
	private PreparedStatement psCopySuitable;

	private List<Field> probabilityRow=new ArrayList<Field>();


	//************** EXTENSIVE LOG

	private boolean enableProbabilitiesLog=false;
	private String probabilitiesLogTable;
	private PreparedStatement probabilitiesLogInsert;
	private List<Field>probabilityLogRow=new ArrayList<Field>();


	//*** CONSTRUCTORS
	//TODO change for allow suitable/native 

	public HSPECGenerator(int jobId,String HCAF_D,String HCAF_S,String HSPEN,Map<String,Map<EnvelopeFields,Field>> envelopeWeights) throws Exception {
		super();
		this.hcafDynamicTable=HCAF_D;
		this.hcafStaticTable=HCAF_S;
		this.hspenTable = HSPEN;
		this.hspecNativeTable= SourceManager.getSourceName(ResourceType.HSPEC, JobManager.getHSPECTableId(jobId));
		this.occurenceCellsTable = SpeciesManager.GOOD_CELLS;
		this.jobId=jobId;
		this.jobWeights=envelopeWeights;
		this.generateNative=true;
		this.generateSuitable=false;
		this.mode=GENERATION_MODE.LAZY;
	}


	public HSPECGenerator(String hcaf_d,String hspen,boolean generateNative,boolean generateSuitable)throws Exception{
		super();


		this.hcafDynamicTable=hcaf_d;
		this.hcafStaticTable=CellManager.HCAF_S;
		this.hspenTable=hspen;
		this.occurenceCellsTable=SpeciesManager.GOOD_CELLS;
		currentWeights=defaultWeights;

		this.generateNative=generateNative;
		this.generateSuitable=generateSuitable;

		if(generateNative==false && generateSuitable==false) throw new Exception("Both native and suitable option where false!!");

		this.mode=GENERATION_MODE.COMPLETE;	

		logger.trace("Created new Generator ");
		logger.trace("Mode : "+mode);
		logger.trace("HCAF_D :"+hcafDynamicTable);
		logger.trace("HSPEC  :"+hspecTableStructure);
		logger.trace("HSPEN  :"+hspenTable);
		logger.trace("generate suitable :"+generateSuitable);
		logger.trace("generate Native :"+generateNative);
		logger.trace("Weight Settings :");
		for(Entry<EnvelopeFields,Boolean> w:currentWeights.entrySet())
			logger.trace(w.getKey()+" : "+w.getValue());

	}


	public String getNativeTable(){return resultsNative;}
	public String getSuitableTable(){return resultsSuitable;}

	//************* GENERATION


	/**
	 * For testing purpose
	 */

	public static List<Field> getProbability(Species s,Cell c,Map<EnvelopeFields,Boolean> currentWeights)throws Exception{
		List<Field> toReturn=new ArrayList<Field>();
		String currentSpeciesID=s.getId();
		//		Double preparedSeaIce= prepareSeaIceForSpecies(
		//				currentSpeciesID,Double.parseDouble(c.getFieldbyName((HspenFields.iceconmin+"")).getValue()),session);




		Double preparedSeaIce=-9999.0;

		Double landValue=(currentWeights.get(EnvelopeFields.LandDistance))?1.0:1.0; //to understand why is not calculated
		Double sstValue=(currentWeights.get(EnvelopeFields.Temperature))?
				getSST(Double.parseDouble(c.getFieldbyName(HCAF_DFields.sstanmean+"").getValue()),
						Double.parseDouble(c.getFieldbyName(HCAF_DFields.sbtanmean+"").getValue()),
						Double.parseDouble(s.getFieldbyName(HspenFields.tempmin+"").getValue()),						
						Double.parseDouble(s.getFieldbyName(HspenFields.tempmax+"").getValue()),
						Double.parseDouble(s.getFieldbyName(HspenFields.tempprefmin+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.tempprefmax+"").getValue()), 
						s.getFieldbyName(HspenFields.layer+"").getValue().toCharArray()[0]):1.0;
		Double depthValue=(currentWeights.get(EnvelopeFields.Depth))?
				getDepth(Double.parseDouble(c.getFieldbyName(HCAF_DFields.depthmax+"").getValue()), 
						Double.parseDouble(c.getFieldbyName(HCAF_DFields.depthmin+"").getValue()), 
						Integer.parseInt(s.getFieldbyName(HspenFields.pelagic+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.depthmax+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.depthmin+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.depthprefmax+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.depthprefmin+"").getValue()),
						Integer.parseInt(s.getFieldbyName(HspenFields.meandepth+"").getValue()),
						Double.parseDouble(c.getFieldbyName(HCAF_DFields.depthmean+"").getValue())):1.0;
		Double salinityValue=(currentWeights.get(EnvelopeFields.Salinity))?
				getSalinity(Double.parseDouble(c.getFieldbyName(HCAF_DFields.salinitymean+"").getValue()),
						Double.parseDouble(c.getFieldbyName(HCAF_DFields.salinitybmean+"").getValue()),
						s.getFieldbyName(HspenFields.layer+"").getValue().toCharArray()[0], 
						Double.parseDouble(s.getFieldbyName(HspenFields.salinitymin+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.salinitymax+"").getValue()),				
						Double.parseDouble(s.getFieldbyName(HspenFields.salinityprefmin+"").getValue()),
						Double.parseDouble(s.getFieldbyName(HspenFields.salinityprefmax+"").getValue())):1.0;
		Double primaryProductsValue=(currentWeights.get(EnvelopeFields.PrimaryProduction))?
				getPrimaryProduction(Integer.parseInt(c.getFieldbyName(HCAF_DFields.primprodmean+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.primprodmin+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.primprodprefmin+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.primprodmax+"").getValue()), 
						Double.parseDouble(s.getFieldbyName(HspenFields.primprodprefmax+"").getValue())):1.0;
		Double seaIceConcentration=(currentWeights.get(EnvelopeFields.IceConcentration))?
				getSeaIceConcentration(Double.parseDouble(c.getFieldbyName(HCAF_DFields.iceconann+"").getValue()), 
								(preparedSeaIce!=-9999.0)?preparedSeaIce:Double.parseDouble(s.getFieldbyName(HspenFields.iceconmin+"").getValue()),
										Double.parseDouble(s.getFieldbyName(HspenFields.iceconprefmin+"").getValue()), 
										Double.parseDouble(s.getFieldbyName(HspenFields.iceconmax+"").getValue()), 
										Double.parseDouble(s.getFieldbyName(HspenFields.iceconprefmax+"").getValue()),currentSpeciesID):1.0;
		Double probability=landValue*sstValue*depthValue*salinityValue*primaryProductsValue*seaIceConcentration;


		toReturn.add(new Field(EnvelopeFields.Depth+"",depthValue+"",FieldType.DOUBLE));
		toReturn.add(new Field(EnvelopeFields.Salinity+"",salinityValue+"",FieldType.DOUBLE));
		toReturn.add(new Field(EnvelopeFields.IceConcentration+"",seaIceConcentration+"",FieldType.DOUBLE));
		toReturn.add(new Field(EnvelopeFields.LandDistance+"",landValue+"",FieldType.DOUBLE));
		toReturn.add(new Field(EnvelopeFields.PrimaryProduction+"",primaryProductsValue+"",FieldType.DOUBLE));
		toReturn.add(new Field(EnvelopeFields.Temperature+"",sstValue+"",FieldType.DOUBLE));
		toReturn.add(new Field(HSPECFields.probability+"",probability+"",FieldType.DOUBLE));
		return toReturn;
	}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public void generate() throws Exception{
		defaultWeights.put(EnvelopeFields.Depth, ServiceContext.getContext().isEvaluateDepth());
		defaultWeights.put(EnvelopeFields.IceConcentration, ServiceContext.getContext().isEvaluateIceConcentration());
		defaultWeights.put(EnvelopeFields.LandDistance, ServiceContext.getContext().isEvaluateLandDistance());
		defaultWeights.put(EnvelopeFields.PrimaryProduction, ServiceContext.getContext().isEvaluatePrimaryProduction());
		defaultWeights.put(EnvelopeFields.Salinity, ServiceContext.getContext().isEvaluateSalinity());
		defaultWeights.put(EnvelopeFields.Temperature, ServiceContext.getContext().isEvaluateTemperature());




		logger.trace("Started Generation");

		long generationStart= System.currentTimeMillis();

		if(mode.equals(GENERATION_MODE.LAZY))
			for(String id: JobManager.getSpeciesByStatus(jobId, SpeciesStatus.toGenerate)) toGenerateSpeciesIds.add(id);


		if(mode.equals(GENERATION_MODE.COMPLETE)||(toGenerateSpeciesIds.size()>0)){
			try{
				session=DBSession.getInternalDBSession();

				initTablesAndRS();
				logger.trace("Init phase took "+(System.currentTimeMillis()-generationStart));
				while (hspenRes.next()){					
					long startHspenLoop= System.currentTimeMillis();
					currentSpeciesID= hspenRes.getString(SpeciesOccursumFields.speciesid+"");

					//****** FOR LAZY MODE if no generation needed just copy species probs
					if(mode.equals(GENERATION_MODE.LAZY)&&!toGenerateSpeciesIds.contains(currentSpeciesID)){
						if(generateNative){
							psCopyNative.setString(1, currentSpeciesID);
							if(psCopyNative.executeUpdate()==0) 
								logger.warn("Unable to copy "+currentSpeciesID+" into "+resultsNative);
						}
						if(generateSuitable){
							psCopySuitable.setString(1, currentSpeciesID);
							if(psCopySuitable.executeUpdate()==0) 
								logger.warn("Unable to copy "+currentSpeciesID+" into "+resultsSuitable);
						}
					}else{
						if(mode.equals(GENERATION_MODE.LAZY)){
							//** Looking for weights
							currentWeights=new HashMap<EnvelopeFields, Boolean>();						
							for(EnvelopeFields f: EnvelopeFields.values()){
								if((jobWeights.containsKey(currentSpeciesID)))
									currentWeights.put(f, Boolean.parseBoolean(jobWeights.get(currentSpeciesID).get(f).getValue()));
								else currentWeights.put(f, defaultWeights.get(f));
							}
						}
						iterateCells();
						logger.trace("HSPEN loop number "+hspenLoops+" took "+(System.currentTimeMillis()-startHspenLoop));
					}

					if(mode.equals(GENERATION_MODE.LAZY))
						JobManager.updateSpeciesStatus(jobId,new String[]{hspenRes.getString(SpeciesOccursumFields.speciesid+"")}, SpeciesStatus.Ready);

					hspenLoops++;
				}


			}catch (Exception e) {
				logger.error("error in generate method",e);
				if(resultsNative!=null)session.dropTable(resultsNative);
				if(resultsSuitable!=null)session.dropTable(resultsSuitable);

			}finally{
				if(hcafViewTable!=null) session.dropTable(hcafViewTable);
				if(occurrenceCellsViewTable!=null) session.dropTable(occurrenceCellsViewTable);
				session.close();
			}
			logger.trace("generation of HSPEC finished in "+((System.currentTimeMillis()-generationStart)/1000)+"secs");
		}
		else {
			logger.trace("No species to re-generate");
			if(generateNative)resultsNative=hspecNativeTable;
			if(generateSuitable)resultsSuitable=hspecSuitableTable;
		}
	}

	//************************** ROUTINES




	private void initTablesAndRS()throws Exception{
		//*********** INIT TABLES
		if(hcafViewTable==null){			
			hcafViewTable=ServiceUtils.generateId("hcaf", "");
			logger.trace("Creating HCAF Table :"+hcafViewTable);
			session.executeUpdate("CREATE TABLE "+this.hcafViewTable+" AS " +
					"SELECT s."+HCAF_SFields.csquarecode+",s."+HCAF_SFields.oceanarea+",s."+HCAF_SFields.centerlat+",s."+HCAF_SFields.centerlong+
					",s."+HCAF_SFields.faoaream+",s."+HCAF_SFields.eezfirst+",s."+HCAF_SFields.lme+",d."+HCAF_DFields.depthmin+",d."+HCAF_DFields.depthmax+",d."+HCAF_DFields.sstanmean+",d."+HCAF_DFields.sbtanmean+
					",d."+HCAF_DFields.salinitymean+",d."+HCAF_DFields.salinitybmean+",d."+HCAF_DFields.primprodmean+",d."+HCAF_DFields.iceconann+",s."+HCAF_SFields.landdist+
					",d."+HCAF_DFields.depthmean+" FROM "+this.hcafStaticTable+" as s INNER JOIN "+this.hcafDynamicTable+" as d ON s."+HCAF_SFields.csquarecode+"=d."+HCAF_SFields.csquarecode);

			session.createIndex(this.hcafViewTable, HCAF_SFields.csquarecode+"");
		}

		if(occurrenceCellsViewTable==null){
			occurrenceCellsViewTable=ServiceUtils.generateId("occ", "");
			logger.trace("Creating occurrenceCells table :"+occurrenceCellsViewTable);
			session.executeUpdate("CREATE TABLE "+occurrenceCellsViewTable+" AS " +
					" SELECT h."+HCAF_SFields.centerlat+", h."+HCAF_SFields.oceanarea+", h."+HCAF_DFields.iceconann+", h."+HCAF_SFields.csquarecode+
					" , o."+SpeciesOccursumFields.speciesid+", o."+OccurrenceCellsFields.goodcell+
					" FROM "+occurenceCellsTable+" AS o INNER JOIN "+hcafViewTable+" AS h ON o."+HCAF_SFields.csquarecode+" = h."+HCAF_SFields.csquarecode+
					" WHERE h."+HCAF_SFields.oceanarea+" > 0");

			session.createIndex(this.occurrenceCellsViewTable,HCAF_SFields.csquarecode+"");
			session.createIndex(this.occurrenceCellsViewTable,SpeciesOccursumFields.speciesid+"");


			psBound=session.preparedStatement("SELECT distinct Max("+HCAF_SFields.centerlat+") AS maxclat, Min("+HCAF_SFields.centerlat+") AS minclat " +
					"FROM "+occurrenceCellsViewTable+" WHERE "+SpeciesOccursumFields.speciesid+"= ? AND "+OccurrenceCellsFields.goodcell+" <> 0");


			//			session.executeUpdate("Select distinct Max("+this.hcafViewTable+".CenterLat) AS maxCLat, Min("+this.hcafViewTable+".CenterLat) AS minCLat" +
			//					" FROM "+this.occurenceCellsTable+" INNER JOIN "+this.hcafViewTable+" ON "+this.occurenceCellsTable+".CsquareCode = "+this.hcafViewTable+".CsquareCode" +
			//					" Where ((("+this.hcafViewTable+".OceanArea > 0))) AND "+this.occurenceCellsTable+".SpeciesID = '"+speciesId+"' AND "+this.occurenceCellsTable+".GoodCell <> 0");

			psSeaIce=session.preparedStatement("SELECT distinct "+HCAF_SFields.csquarecode+", "+SpeciesOccursumFields.speciesid+", "+HCAF_DFields.iceconann+
					" FROM "+occurrenceCellsViewTable+" WHERE "+SpeciesOccursumFields.speciesid+"= ? AND "+HCAF_DFields.iceconann+"<> -9999 AND "+HCAF_DFields.iceconann+" is not null "
					+" AND "+OccurrenceCellsFields.goodcell+" = -1 order by "+HCAF_DFields.iceconann);

			//			
			//			
			//			String query="SELECT distinct "+this.occurenceCellsTable+".CsquareCode, "+this.occurenceCellsTable+".SpeciesID, "+this.hcafViewTable+".IceConAnn" +
			//			" FROM "+this.occurenceCellsTable+" INNER JOIN "+this.hcafViewTable+" ON "+this.occurenceCellsTable+".CsquareCode = "+this.hcafViewTable+".CsquareCode" +
			//			" WHERE (  (("+this.hcafViewTable+".OceanArea)>0))" +
			//			" and "+this.occurenceCellsTable+".SpeciesID = '" +speciesID+ "'" +
			//			" and "+this.hcafViewTable+".IceConAnn <> -9999" +
			//			" and "+this.hcafViewTable+".IceConAnn is not null" +
			//			" and "+this.occurenceCellsTable+".goodcell = -1" +
			//			" order by "+this.hcafViewTable+".IceConAnn";
			//			
		}

		if(generateNative){
			resultsNative=ServiceUtils.generateId("hspec_native", "");
			logger.trace("Generated Native Table will be "+resultsNative);		
			session.createLikeTable(this.resultsNative, this.hspecTableStructure);
		}

		if(generateSuitable){
			resultsSuitable=ServiceUtils.generateId("hspec_suitable", "");
			logger.trace("Generated Suitable Table will be "+resultsSuitable);		
			session.createLikeTable(this.resultsSuitable, this.hspecTableStructure);
		}

		//************ QUERY HSPEN


		//		String queryhspen="SELECT * FROM "+this.hspenTable;
		//		logger.trace("species query is "+queryhspen);

		List<Field> filter=new ArrayList<Field>(); 
		hspenRes=session.executeFilteredQuery(filter, hspenTable, SpeciesOccursumFields.speciesid+"", "ASC");

		logger.trace("HSPEN query took "+(System.currentTimeMillis()-startGeneration));

		//********** QUERY HCAF

		//I can execute it here cause it not depends on hspen
		long startHcafQuery= System.currentTimeMillis();

		String hcafQuery= "SELECT "+HCAF_SFields.csquarecode+","+HCAF_SFields.oceanarea+","+HCAF_SFields.centerlat+","+HCAF_SFields.centerlong+","+HCAF_SFields.faoaream+
		","+HCAF_DFields.depthmin+","+HCAF_DFields.depthmax+","+HCAF_DFields.sstanmean+","+HCAF_DFields.sbtanmean+","+HCAF_DFields.salinitymean
		+","+HCAF_DFields.salinitybmean+","+HCAF_DFields.primprodmean+","+HCAF_DFields.iceconann+","+HCAF_SFields.landdist+","+HCAF_SFields.eezfirst+","+HCAF_SFields.lme+","+
		HCAF_DFields.depthmean+"	FROM "+this.hcafViewTable+" WHERE "+HCAF_SFields.oceanarea+" > 0";
		logger.trace("HCAF query is "+hcafQuery);
		hcafRes=session.executeQuery(hcafQuery);
		logger.trace("HCAF query took "+(System.currentTimeMillis()-startHcafQuery));


		//*********** Prepare statement for insert && Copy

		//		psInsert=session.preparedStatement("INSERT INTO "+this.resultsTable+" values('"+hspenRes.getString("SpeciesID")+"','"+hcafRes.getString("CsquareCode")+"',"+formatter.format(totalCountProbability)+","+inBox+","+inFAO+",'"+hcafRes.getString("FAOAreaM")+"','"+hcafRes.getString("EEZFirst")+"','"+hcafRes.getString("LME")+"')");

		probabilityRow.add(new Field(SpeciesOccursumFields.speciesid+"","",FieldType.STRING));
		probabilityRow.add(new Field(HCAF_SFields.csquarecode+"","",FieldType.STRING));
		probabilityRow.add(new Field(HSPECFields.probability+"","",FieldType.DOUBLE));
		probabilityRow.add(new Field(HSPECFields.boundboxyn+"","",FieldType.BOOLEAN));
		probabilityRow.add(new Field(HSPECFields.faoareayn+"","",FieldType.BOOLEAN));
		probabilityRow.add(new Field(HSPECFields.faoaream+"","",FieldType.INTEGER));
		probabilityRow.add(new Field(HSPECFields.eezall+"","",FieldType.STRING));
		probabilityRow.add(new Field(HSPECFields.lme+"","",FieldType.INTEGER));

		List<Field> copyFields=new ArrayList<Field>();
		copyFields.add(new Field(SpeciesOccursumFields.speciesid+"","",FieldType.STRING));


		if(generateSuitable){


			psInsertSuitable=session.getPreparedStatementForInsert(probabilityRow, resultsSuitable);
			psCopySuitable=session.getPreparedStatementForInsertFromSelect(copyFields, resultsSuitable, hspecSuitableTable);
		}
		if(generateNative){
			psInsertNative=session.getPreparedStatementForInsert(probabilityRow, resultsNative);
			psCopyNative=session.getPreparedStatementForInsertFromSelect(copyFields, resultsNative, hspecNativeTable);
		}

		if(isEnableProbabilitiesLog()){
			logger.trace("Init table for extensive logging..");
			probabilitiesLogTable=ServiceUtils.generateId("log", "");
			session.createTable(probabilitiesLogTable, new String[]{
					SpeciesOccursumFields.speciesid+" varchar(50)",
					HCAF_SFields.csquarecode+" varchar(50)",
					EnvelopeFields.Depth.toString().toLowerCase()+" float",
					EnvelopeFields.IceConcentration.toString().toLowerCase()+" float",
					EnvelopeFields.LandDistance.toString().toLowerCase()+" float",
					EnvelopeFields.PrimaryProduction.toString().toLowerCase()+" float",
					EnvelopeFields.Salinity.toString().toLowerCase()+" float",
					EnvelopeFields.Temperature.toString().toLowerCase()+" float",
					HSPECFields.probability+" float",
					"PRIMARY KEY ("+SpeciesOccursumFields.speciesid+","+HCAF_SFields.csquarecode+")"
			});

			probabilityLogRow.add(new Field(SpeciesOccursumFields.speciesid+"","",FieldType.STRING));
			probabilityLogRow.add(new Field(HCAF_SFields.csquarecode+"","",FieldType.STRING));
			probabilityLogRow.add(new Field(EnvelopeFields.Depth.toString().toLowerCase(),"",FieldType.DOUBLE));
			probabilityLogRow.add(new Field(EnvelopeFields.IceConcentration.toString().toLowerCase(),"",FieldType.DOUBLE));
			probabilityLogRow.add(new Field(EnvelopeFields.LandDistance.toString().toLowerCase(),"",FieldType.DOUBLE));
			probabilityLogRow.add(new Field(EnvelopeFields.PrimaryProduction.toString().toLowerCase(),"",FieldType.DOUBLE));
			probabilityLogRow.add(new Field(EnvelopeFields.Temperature.toString().toLowerCase(),"",FieldType.DOUBLE));
			probabilityLogRow.add(new Field(HSPECFields.probability+"","",FieldType.DOUBLE));
			probabilitiesLogInsert=session.getPreparedStatementForInsert(probabilityLogRow, probabilitiesLogTable);
			logger.trace("Log table : "+probabilitiesLogTable);
		}


	}


	/**
	 * iterates hcaf and insert probability in HSPEC
	 * 
	 * @return
	 */





	private void iterateCells()throws Exception{
		logger.trace("Analizing "+currentSpeciesID+"...");

		probabilityRow.get(0).setValue(currentSpeciesID);
		if(isEnableProbabilitiesLog())
			probabilityLogRow.get(0).setValue(currentSpeciesID);

		Bounduary bounds=getBounduary(hspenRes.getDouble(HspenFields.nmostlat+""),hspenRes.getDouble(HspenFields.smostlat+""),
				hspenRes.getDouble(HspenFields.emostlong+""),hspenRes.getDouble(HspenFields.wmostlong+""),currentSpeciesID);

		//************ Insert if inFAO && inBox
		//************ NB for suita
		insertProbability(true,true,bounds,true);

		logger.trace("inserted "+insertedNativeCellCount+" native entries and "+insertedSuitableCellCount+" suitable entries for "+currentSpeciesID+" species id");

		//****************** Insert if inFAO && ! in BBox
		//****************** Only if native

		if (insertedNativeCellCount==0 && generateNative) {
			insertProbability(false,false, bounds,true);
			logger.trace("inserted "+insertedNativeCellCount+" native entries with inbox false for "+currentSpeciesID+" species id");
		}

	}

	private void insertProbability(boolean insertSuitable,boolean expectedInBox,Bounduary bounds,boolean expectedInFAO)throws Exception{
		hcafRes.beforeFirst();
		insertedNativeCellCount=0;
		if(insertSuitable)insertedSuitableCellCount=0;
		if((!insertSuitable)&&(generateSuitable)){
			//Copy from suitable
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(SpeciesOccursumFields.speciesid+"",currentSpeciesID,FieldType.STRING));			
			ResultSet rs =session.executeFilteredQuery(filters, resultsSuitable, HCAF_SFields.csquarecode+"", "ASC");

			while(rs.next()){
				boolean inBox=rs.getBoolean(HSPECFields.boundboxyn+"");
				boolean inFAO=rs.getBoolean(HSPECFields.faoareayn+"");
				if((expectedInFAO==inFAO) && (expectedInBox==inBox)){
					probabilityRow.get(1).setValue(rs.getString(HCAF_SFields.csquarecode+""));

					probabilityRow.get(3).setValue(inBox+"");
					probabilityRow.get(4).setValue(inFAO+"");
					probabilityRow.get(5).setValue(rs.getString(HCAF_SFields.faoaream+""));
					probabilityRow.get(6).setValue(rs.getString(HCAF_SFields.eezfirst+""));
					probabilityRow.get(7).setValue(rs.getString(HCAF_SFields.lme+""));
					probabilityRow.get(8).setValue(rs.getString(HSPECFields.probability+""));
				}					
			}
		}else		
			//Normal Execution

			while(hcafRes.next())
				try{
					Double probability=null;
					boolean inFAO= this.getInFao(hcafRes.getInt(HCAF_SFields.faoaream+""),hspenRes.getString(HspenFields.faoareas+""));
					boolean inBox= this.getInBox(hcafRes.getDouble(HCAF_SFields.centerlat+""), bounds);

					probabilityRow.get(1).setValue(hcafRes.getString(HCAF_SFields.csquarecode+""));

					probabilityRow.get(3).setValue(inBox+"");
					probabilityRow.get(4).setValue(inFAO+"");
					probabilityRow.get(5).setValue(hcafRes.getString(HCAF_SFields.faoaream+""));
					probabilityRow.get(6).setValue(hcafRes.getString(HCAF_SFields.eezfirst+""));
					probabilityRow.get(7).setValue(hcafRes.getString(HCAF_SFields.lme+""));


					if(insertSuitable && generateSuitable){
						probability=getOverallProbability();
						probabilityRow.get(2).setValue(formatter.format(probability));
						if ((probability>=probabilityThresholdForInsert)){
							insertedSuitableCellCount+=session.fillParameters(probabilityRow, psInsertSuitable).executeUpdate();
						}
					}

					if(generateNative&&((expectedInFAO==inFAO) && (expectedInBox==inBox))){

						if(probability==null){
							probability=getOverallProbability();
							probabilityRow.get(2).setValue(formatter.format(probability));
						}



						if ((probability>=probabilityThresholdForInsert)){
							insertedNativeCellCount+=session.fillParameters(probabilityRow, psInsertNative).executeUpdate();
						}
					}

				}catch(Exception e){try {
					logger.warn("error in data found : "+e.getMessage()+" speciesID : "+currentSpeciesID+", cell: "+hcafRes.getString(HCAF_SFields.csquarecode+""));
				} catch (SQLException e1) {
					logger.error("UNEXPECTED ERROR ",e1);
				}}
	}





	private Double getOverallProbability() throws Exception{
		Double preparedSeaIce= prepareSeaIceForSpecies(currentSpeciesID,hspenRes.getDouble(HspenFields.iceconmin+""),session);
		Double landValue=(currentWeights.get(EnvelopeFields.LandDistance))?1.0:1.0; //to understand why is not calculated
		Double sstValue=(currentWeights.get(EnvelopeFields.Temperature))?
				getSST(hcafRes.getDouble(HCAF_DFields.sstanmean+""),
						hcafRes.getDouble(HCAF_DFields.sbtanmean+""),
						hspenRes.getDouble(HspenFields.tempmin+""),						
						hspenRes.getDouble(HspenFields.tempmax+""),
						hspenRes.getDouble(HspenFields.tempprefmin+""), 
						hspenRes.getDouble(HspenFields.tempprefmax+""), 
						hspenRes.getString(HspenFields.layer+"").toCharArray()[0]):1.0;
				Double depthValue=(currentWeights.get(EnvelopeFields.Depth))?getDepth(hcafRes.getDouble(HCAF_DFields.depthmax+""), hcafRes.getDouble(HCAF_DFields.depthmin+""), hspenRes.getInt(HspenFields.pelagic+""), hspenRes.getDouble(HspenFields.depthmax+""), hspenRes.getDouble(HspenFields.depthmin+""), 
						hspenRes.getDouble(HspenFields.depthprefmax+""), hspenRes.getDouble(HspenFields.depthprefmin+""),hspenRes.getInt(HspenFields.meandepth+""),hcafRes.getDouble(HCAF_DFields.depthmean+"")):1.0;
				Double salinityValue=(currentWeights.get(EnvelopeFields.Salinity))?getSalinity(hcafRes.getDouble(HCAF_DFields.salinitymean+""), hcafRes.getDouble(HCAF_DFields.salinitybmean+""), hspenRes.getString(HspenFields.layer+"").toCharArray()[0], hspenRes.getDouble(HspenFields.salinitymin+""), hspenRes.getDouble(HspenFields.salinitymax+""), 
						hspenRes.getDouble(HspenFields.salinityprefmin+""), hspenRes.getDouble(HspenFields.salinityprefmax+"")):1.0;
				Double primaryProductsValue=(currentWeights.get(EnvelopeFields.PrimaryProduction))?getPrimaryProduction(hcafRes.getInt(HCAF_DFields.primprodmean+""), hspenRes.getDouble(HspenFields.primprodmin+""), hspenRes.getDouble(HspenFields.primprodprefmin+""), hspenRes.getDouble(HspenFields.primprodmax+""), hspenRes.getDouble(HspenFields.primprodprefmax+"")):1.0;
				Double seaIceConcentration=(currentWeights.get(EnvelopeFields.IceConcentration))?getSeaIceConcentration(hcafRes.getDouble(HCAF_DFields.iceconann+""), (preparedSeaIce!=-9999.0)?preparedSeaIce:hspenRes.getDouble(HspenFields.iceconmin+""), hspenRes.getDouble(HspenFields.iceconprefmin+""), hspenRes.getDouble(HspenFields.iceconmax+""), hspenRes.getDouble(HspenFields.iceconprefmax+""), 
						currentSpeciesID):1.0;
				Double probability=landValue*sstValue*depthValue*salinityValue*primaryProductsValue*seaIceConcentration;
				if(isEnableProbabilitiesLog()){
					try{
						probabilityLogRow.get(1).setValue(hcafRes.getString(HCAF_SFields.csquarecode+""));
						probabilityLogRow.get(2).setValue(depthValue+"");
						probabilityLogRow.get(3).setValue(seaIceConcentration+"");
						probabilityLogRow.get(4).setValue(landValue+"");
						probabilityLogRow.get(5).setValue(primaryProductsValue+"");
						probabilityLogRow.get(6).setValue(sstValue+"");
						probabilityLogRow.get(7).setValue(probability+"");
						session.fillParameters(probabilityLogRow, probabilitiesLogInsert).executeUpdate();
					}catch(Exception e){
						// duplicate key on second run per species
					}
				}			
				return probability; 
	}



	private Bounduary getBounduary(Double north, Double south, Double east, Double west, String speciesId) throws Exception{
		Bounduary bounduary= new Bounduary(north, south, east, west);
		if (north==null || south==null || east==null || west==null){
			if (north!=null && south==null) bounduary.passedNS=true;
			else if (north!=null) bounduary.passedN=true;
			else if (south!=null) bounduary.passedS=true;
			else{
				psBound.setString(1, speciesId);
				ResultSet rsBond=psBound.executeQuery();

				//				ResultSet rsBond=session.executeQuery("Select distinct Max("+this.hcafViewTable+".CenterLat) AS maxCLat, Min("+this.hcafViewTable+".CenterLat) AS minCLat" +
				//						" FROM "+this.occurenceCellsTable+" INNER JOIN "+this.hcafViewTable+" ON "+this.occurenceCellsTable+".CsquareCode = "+this.hcafViewTable+".CsquareCode" +
				//						" Where ((("+this.hcafViewTable+".OceanArea > 0))) AND "+this.occurenceCellsTable+".SpeciesID = '"+speciesId+"' AND "+this.occurenceCellsTable+".GoodCell <> 0");
				rsBond.next();
				double maxCLat=rsBond.getDouble("maxclat");
				double minCLat=rsBond.getDouble("minclat");
				if (minCLat>10){
					bounduary.setSouth(0.0);
					bounduary.setSouthernEmisphereAdjusted(true);
				}
				if (maxCLat<-10){
					bounduary.setNorth(0.0);
					bounduary.setNorthenEmisphereAdjusted(true);
				}
			}
		}
		return bounduary;
	}


	/**
	 * 
	 * 
	 * @param hcafDepthMax
	 * @param hcafDepthMin
	 * @param hspenPelagic
	 * @param hspenMaxDepth
	 * @param hspenMinDepth
	 * @param hspenDepthPrefMax
	 * @param hspenDepthPrefMin
	 * @return depth
	 */
	public static Double getDepth(Double hcafDepthMax, Double hcafDepthMin, int hspenPelagic, Double hspenMaxDepth,Double hspenMinDepth,Double hspenDepthPrefMax, Double hspenDepthPrefMin, int hspenMeanDepth, Double hcafDepthMean ){
		// Check on hspenMeanDepth added from HSPEC version 2 (used from release 1.7)
		if(hspenMinDepth == null) return 1.0;
		if(hspenMeanDepth == 1) {
			hcafDepthMax = hcafDepthMean;
			hcafDepthMin = hcafDepthMean;
		}
		if (hcafDepthMax == -9999 || hspenMinDepth == null ) return 1.0;
		if (hcafDepthMax < hspenMinDepth) return 0.0;
		if 	((hcafDepthMax < hspenDepthPrefMin) &&(hcafDepthMax >= hspenMinDepth)) return (hcafDepthMax - hspenMinDepth) / (hspenDepthPrefMin - hspenMinDepth);
		//TODO check vprovider value (meanDepth)
		if (hspenPelagic != 0) {return 1.0;}		
		if (hspenDepthPrefMax!=null)
		{
			if 	(hcafDepthMax >= hspenDepthPrefMin && hcafDepthMin <= hspenDepthPrefMax) return 1.0;        
			if (hcafDepthMin >= hspenDepthPrefMax){
				if 	((hcafDepthMax.intValue()) - hspenDepthPrefMax.intValue() != 0){
					Double tempdepth=(hspenMaxDepth - hcafDepthMin) / (hspenMaxDepth.intValue() - hspenDepthPrefMax.intValue());
					return tempdepth<0?0.0:tempdepth;
				}else return 0.0;
			}else return 0.0;
		}else return 0.0;
	}

	/**
	 * 
	 * @param hcafSSTAnMean
	 * @param hcafSBTAnMean
	 * @param hspenTempMin
	 * @param hspenTempMax
	 * @param hspenTempPrefMin
	 * @param hspenTempPrefMax
	 * @param hspenLayer
	 * @return
	 */
	public static Double getSST( Double hcafSSTAnMean, Double hcafSBTAnMean, Double hspenTempMin, Double hspenTempMax, Double hspenTempPrefMin, Double hspenTempPrefMax, char hspenLayer ){
		Double tempFld=-9999.0;
		if (hspenLayer=='s') tempFld = hcafSSTAnMean;
		else if (hspenLayer=='b') tempFld = hcafSBTAnMean;

		if (tempFld == -9999 || hspenTempMin == null)return 1.0;
		if (tempFld < hspenTempMin)return  0.0;
		if (tempFld >= hspenTempMin && tempFld < hspenTempPrefMin) return  (tempFld - hspenTempMin) / (hspenTempPrefMin - hspenTempMin);
		if (tempFld >= hspenTempPrefMin && tempFld <= hspenTempPrefMax) return 1.0;
		if (tempFld > hspenTempPrefMax && tempFld <= hspenTempMax)
			return (hspenTempMax - tempFld) / (hspenTempMax - hspenTempPrefMax);
		else return 0.0;
	}

	/**
	 * 
	 * @param hcafSSTAnMean
	 * @param hcafSBTAnMean
	 * @param hspenLayer
	 * @param hspenSalinityMin
	 * @param hspenSalinityMax
	 * @param hspenSalinityPrefMin
	 * @param hspenSalinityPrefMax
	 * @return
	 */
	public static Double getSalinity(Double hcafSalinityMean, Double hcafSalinityBMean, char hspenLayer, Double hspenSalinityMin,Double hspenSalinityMax, Double hspenSalinityPrefMin, Double hspenSalinityPrefMax){
		Double tempFld=-9999.0;
		if (hspenLayer=='s') tempFld = hcafSalinityMean;
		else if (hspenLayer=='b') tempFld = hcafSalinityBMean;

		if (tempFld == -9999 || hspenSalinityMin == null ) return 1.0;
		if (tempFld < hspenSalinityMin)return 0.0;
		if (tempFld >= hspenSalinityMin && tempFld < hspenSalinityPrefMin)
			return (tempFld - hspenSalinityMin) / (hspenSalinityPrefMin - hspenSalinityMin);
		if (tempFld >= hspenSalinityPrefMin && tempFld <= hspenSalinityPrefMax)return 1.0;
		if (tempFld > hspenSalinityPrefMax && tempFld <= hspenSalinityMax)
			return (hspenSalinityMax - tempFld) / (hspenSalinityMax - hspenSalinityPrefMax);
		else return 0.0;
	}

	/**
	 * 
	 * @param hcafPrimProdMean
	 * @param hspenPrimProdMin
	 * @param hspenPrimProdPrefMin
	 * @param hspenPrimProdMax
	 * @param hspenPrimProdPrefMax
	 * @return
	 */
	public static Double getPrimaryProduction(int hcafPrimProdMean,Double hspenPrimProdMin, Double hspenPrimProdPrefMin,Double hspenPrimProdMax, Double hspenPrimProdPrefMax){
		if (hcafPrimProdMean == 0) return 1.0;
		if (hcafPrimProdMean < hspenPrimProdMin ) return 0.0;
		if ((hcafPrimProdMean >= hspenPrimProdMin) && (hcafPrimProdMean < hspenPrimProdPrefMin ))
			return (hcafPrimProdMean - hspenPrimProdMin) / (hspenPrimProdPrefMin - hspenPrimProdMin);
		if ((hcafPrimProdMean >= hspenPrimProdPrefMin) && (hcafPrimProdMean <= hspenPrimProdPrefMax)) return 1.0;
		if ((hcafPrimProdMean > hspenPrimProdPrefMax) && (hcafPrimProdMean <= hspenPrimProdMax))
			return (hspenPrimProdMax - hcafPrimProdMean) / (hspenPrimProdMax - hspenPrimProdPrefMax);
		else return 0.0;
	}

	private Double prepareSeaIceForSpecies(String speciesID, Double hspenIceConMin, DBSession session) throws Exception{
		if(hspenIceConMin == 0){
			Double sumIce = 0.0, meanIce = 0.0, adjVal = -1.0;
			//			String query="SELECT distinct "+this.occurenceCellsTable+".CsquareCode, "+this.occurenceCellsTable+".SpeciesID, "+this.hcafViewTable+".IceConAnn" +
			//			" FROM "+this.occurenceCellsTable+" INNER JOIN "+this.hcafViewTable+" ON "+this.occurenceCellsTable+".CsquareCode = "+this.hcafViewTable+".CsquareCode" +
			//			" WHERE (  (("+this.hcafViewTable+".OceanArea)>0))" +
			//			" and "+this.occurenceCellsTable+".SpeciesID = '" +speciesID+ "'" +
			//			" and "+this.hcafViewTable+".IceConAnn <> -9999" +
			//			" and "+this.hcafViewTable+".IceConAnn is not null" +
			//			" and "+this.occurenceCellsTable+".goodcell = -1" +
			//			" order by "+this.hcafViewTable+".IceConAnn";
			////			logger.debug("Sea Ice Concentration query: "+query);
			//
			//			ResultSet iceConRes=session.executeQuery(query);

			psSeaIce.setString(1,speciesID);
			ResultSet iceConRes=psSeaIce.executeQuery();

			int recordCount=0;
			while (iceConRes.next()){
				sumIce+=iceConRes.getDouble(HCAF_DFields.iceconann+"");
				recordCount++;
			}

			if(recordCount != 0)	{meanIce = sumIce / recordCount;}
			else				{meanIce = 0.0;}

			return adjVal + meanIce;
		}else return -9999.0;

	}

	/**
	 * 
	 * @param hcafIceConAnn
	 * @param hspenIceConMin
	 * @param hspenIceConPrefMin
	 * @param hspenIceConMax
	 * @param hspenIceConPrefMax
	 * @param speciesId
	 * @param session
	 * @return
	 * @throws Exception
	 */
	public static Double getSeaIceConcentration(Double hcafIceConAnn,Double hspenIceConMin,Double hspenIceConPrefMin, Double hspenIceConMax,Double hspenIceConPrefMax, String speciesId) throws Exception{
		if(hcafIceConAnn != null){
			if (hcafIceConAnn < hspenIceConMin) return 0.0;
			if ((hcafIceConAnn >= hspenIceConMin) && (hcafIceConAnn < hspenIceConPrefMin)	)
				return (hcafIceConAnn - hspenIceConMin) /((hspenIceConPrefMin - hspenIceConMin));
			if ((hcafIceConAnn >= hspenIceConPrefMin) && (hcafIceConAnn <= hspenIceConPrefMax))
				return 1.0;
			if ((hcafIceConAnn > hspenIceConPrefMax) && (hcafIceConAnn <= hspenIceConMax))
				return ((hspenIceConMax - hcafIceConAnn)) / ((hspenIceConMax - hspenIceConPrefMax));
			if (hcafIceConAnn > hspenIceConMax)
				return 0.0;
		}

		return 0.0;
	}

	/**
	 * 
	 * @param hspecCenterLat
	 * @param bounds
	 * @return
	 */
	public static boolean getInBox(Double hcafCenterLat, Bounduary bounds){
		if(bounds.isPassedNS()){
			if	(	hcafCenterLat >= bounds.getSouth()	&&	hcafCenterLat <= bounds.getNorth()) return true; 
		}else 
			if (bounds.isPassedN()){
				if	(hcafCenterLat <= bounds.getNorth()) return true;
			}else
				if (bounds.isPassedS()){
					if	(hcafCenterLat >= bounds.getSouth()) return true; 
				}else
					if (bounds.isSouthernEmisphereAdjusted()){
						if(hcafCenterLat > 0) return true;
					}else
						if (bounds.isNorthenEmisphereAdjusted()){
							if(hcafCenterLat < 0) return true;
						}else
							return false;
		return false;
	}

	public static boolean getInFao(Integer hcafFAOAreaM, String hspenFAOAreas){
		if (hcafFAOAreaM==null) return false;
		if (hspenFAOAreas.contains(hcafFAOAreaM.toString()))
			return true;
		else return false;
	}

	public void setEnableProbabilitiesLog(boolean enableProbabilitiesLog) {
		this.enableProbabilitiesLog=enableProbabilitiesLog;
	}


	public boolean isEnableProbabilitiesLog() {
		return enableProbabilitiesLog;
	}

	public String getLogTable(){return probabilitiesLogTable;}

	/**
	 * 
	 * @author lucio
	 *
	 */
	public class Bounduary{
		private Double north, south, east, west;

		private boolean southernEmisphereAdjusted=false;
		private boolean northenEmisphereAdjusted=false;

		private boolean passedNS= false;
		private boolean passedN= false;
		private boolean passedS= false;

		public Bounduary(Double north, Double south, Double east, Double west) throws Exception {
			this.north = north;
			this.south = south;
			this.east = east;
			this.west = west;
		}

		public Double getNorth() {
			return north;
		}

		public Double getSouth() {
			return south;
		}

		public Double getEast() {
			return east;
		}

		public Double getWest() {
			return west;
		}

		public boolean isSouthernEmisphereAdjusted() {
			return southernEmisphereAdjusted;
		}

		public boolean isNorthenEmisphereAdjusted() {
			return northenEmisphereAdjusted;
		}

		public boolean isPassedNS() {
			return passedNS;
		}

		public boolean isPassedN() {
			return passedN;
		}

		public boolean isPassedS() {
			return passedS;
		}

		public void setSouthernEmisphereAdjusted(boolean southernEmisphereAdjusted) {
			this.southernEmisphereAdjusted = southernEmisphereAdjusted;
		}

		public void setNorthenEmisphereAdjusted(boolean northenEmisphereAdjusted) {
			this.northenEmisphereAdjusted = northenEmisphereAdjusted;
		}

		public void setPassedNS(boolean passedNS) {
			this.passedNS = passedNS;
		}

		public void setPassedN(boolean passedN) {
			this.passedN = passedN;
		}

		public void setPassedS(boolean passedS) {
			this.passedS = passedS;
		}

		public void setNorth(Double north) {
			this.north = north;
		}

		public void setSouth(Double south) {
			this.south = south;
		}

	}





}

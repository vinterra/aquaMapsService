package org.gcube.application.aquamaps.aquamapsservice.impl.perturbation;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails.SpeciesStatus;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.stubs.EnvelopeWeightArray;
import org.gcube.application.aquamaps.stubs.EnvelopeWeights;
import org.gcube.application.aquamaps.stubs.Weight;
import org.gcube.application.aquamaps.stubs.WeightArray;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * 
 * @author lucio
 *
 */
public class HSPECGenerator {

	private static GCUBELog logger= new GCUBELog(HSPECGenerator.class);
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	private String hcafViewTable;
	private String hcafStaticTable;
	private String hcafDynamicTable;
	private String hspenTable;
	private String hspecTable;
	private String resultsTable;
	private String occurenceCellsTable;
	private JobGenerationDetails details;
	private Map<String,WeightArray> weights=new HashMap<String, WeightArray>(); 
	
/*	private double sstWeight;
	private double depthWeight;
	private double salinityWeight;
	private double primaryProductsWeight;
	private double seaIceConcentrationWeight;
	*/
	public HSPECGenerator(JobGenerationDetails details) {
		super();
		this.hcafViewTable = "HCAF"+uuidGen.nextUUID().replace("-", "_");
		this.hcafDynamicTable=details.getHcafTable();
		this.hcafStaticTable=DBCostants.HCAF_S;
		this.hspenTable = details.getHspenTable();
		this.hspecTable = DBCostants.HSPEC;
		this.occurenceCellsTable = DBCostants.GOOD_CELLS;
		
		this.details=details;
		/*this.depthWeight = 1.0;
		this.salinityWeight = 1.0;
		this.primaryProductsWeight = 1.0;
		this.seaIceConcentrationWeight =1.0;
		this.sstWeight =1.0;
				

		if ((details.getToPerform().getWeights()!=null)&&(details.getToPerform().getWeights().getWeightList()!=null))
			for (Weight weight:details.getToPerform().getWeights().getWeightList()){
				if(weight.getParameterName().compareTo("Primary Production")==0) this.primaryProductsWeight=weight.getChosenWeight();
				if(weight.getParameterName().compareTo("Sea Surface Temp.")==0) this.sstWeight=  weight.getChosenWeight();
				if(weight.getParameterName().compareTo("Ice Concentration")==0) this.seaIceConcentrationWeight=  weight.getChosenWeight();
				if(weight.getParameterName().compareTo("Salinity")==0) this.salinityWeight=  weight.getChosenWeight();
				if(weight.getParameterName().compareTo("Depth")==0) this.depthWeight=  weight.getChosenWeight();
			}
			*/	
		EnvelopeWeightArray envelopeWeights=details.getToPerform().getWeights();
		if((envelopeWeights!=null)&&(envelopeWeights.getEnvelopeWeightList()!=null)&&(envelopeWeights.getEnvelopeWeightList().length>0)){
			for(EnvelopeWeights envW : envelopeWeights.getEnvelopeWeightList()){
				weights.put(envW.getSpeciesId(), envW.getWeights());
			}
		}
		
		this.resultsTable= "HSPEC"+uuidGen.nextUUID().replace("-", "_");
		details.setHspecTable(this.resultsTable);
		//logger.trace("Weights: "+this.depthWeight+" "+this.salinityWeight+" "+this.primaryProductsWeight+" "+this.seaIceConcentrationWeight+" "+this.sstWeight);
	}

	
	
	
	
		
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String generate() throws Exception{
		DBSession session= DBSession.openSession();
		long generationStart= System.currentTimeMillis();
		try{
			long startGeneration= System.currentTimeMillis();
			DecimalFormat formatter = new DecimalFormat("0.00");
			session.executeUpdate("CREATE TABLE "+this.hcafViewTable+" AS SELECT s.CsquareCode,s.OceanArea,s.CenterLat,s.CenterLong,FAOAreaM,DepthMin,DepthMax,SSTAnMean,SBTAnMean,SalinityMean, SalinityBMean,PrimProdMean,IceConAnn,LandDist,s.EEZFirst,s.LME FROM "+this.hcafStaticTable+" as s INNER JOIN "+this.hcafDynamicTable+" as d ON s.CSquareCode=d.CSquareCode");
			session.createLikeTable(this.resultsTable, this.hspecTable);
			String queryhspen="SELECT Layer,SpeciesID,FAOAreas,Pelagic,NMostLat,SMostLat,WMostLong,EMostLong,DepthMin,DepthMax,DepthPrefMin," +
			"DepthPrefMax,TempMin,TempMax,TempPrefMin,TempPrefMax,SalinityMin,SalinityMax,SalinityPrefMin,SalinityPrefMax,PrimProdMin," +
			"PrimProdMax,PrimProdPrefMin,PrimProdPrefMax,IceConMin,IceConMax,IceConPrefMin,IceConPrefMax,LandDistMin,LandDistMax,LandDistPrefMin," +
			"LandDistPrefMax FROM "+this.hspenTable;
			logger.trace("species qeury is "+queryhspen);
			ResultSet hspenRes= session.executeQuery(queryhspen);
						
			logger.trace("HSPEN query took "+(System.currentTimeMillis()-startGeneration));
			
			//I can execute it here cause it not depends on hspen
			long startHcafQuery= System.currentTimeMillis();
			String hcafQuery= "SELECT CsquareCode,OceanArea,CenterLat,CenterLong,FAOAreaM,DepthMin,DepthMax,SSTAnMean,SBTAnMean,SalinityMean," +
			"SalinityBMean,PrimProdMean,IceConAnn,LandDist,EEZFirst,LME	FROM "+this.hcafViewTable+" WHERE OceanArea > 0";
			logger.trace("hspec query is "+hcafQuery);
			ResultSet hcafRes=session.executeQuery(hcafQuery);
			logger.trace("HCAF query took "+(System.currentTimeMillis()-startHcafQuery));
			
			//looping on HSPEN
			
			int hspenLoops=1;
			while (hspenRes.next()){
				long startHspenLoop= System.currentTimeMillis();
				String speciesId= hspenRes.getString("SpeciesID");
				boolean sstWeight=true;
				boolean depthWeight=true;
				boolean salinityWeight=true;
				boolean primaryProductionWeight=true;
				boolean seaIceConcentrationWeight=true;
				boolean landWeight=true;
				if(weights.containsKey(speciesId)){
					WeightArray weightArray = weights.get(speciesId);
					if((weightArray!=null)&&(weightArray.getWeightList()!=null))
						for(Weight w : weightArray.getWeightList()){
							if(w.getParameterName().equalsIgnoreCase("Salinity")) salinityWeight=w.isChosenWeight();
							else if(w.getParameterName().equalsIgnoreCase("PrimProd")) primaryProductionWeight=w.isChosenWeight();
							else if(w.getParameterName().equalsIgnoreCase("Depth")) depthWeight=w.isChosenWeight();
							else if(w.getParameterName().equalsIgnoreCase("Temp")) sstWeight=w.isChosenWeight();
							else if(w.getParameterName().equalsIgnoreCase("IceCon")) seaIceConcentrationWeight=w.isChosenWeight();
							else if(w.getParameterName().equalsIgnoreCase("LandDist")) landWeight=w.isChosenWeight();
							
						}
				}
				
				Bounduary bounds=getBounduary(hspenRes.getDouble("NMostLat"),hspenRes.getDouble("SMostLat"),hspenRes.getDouble("EMostLong"),hspenRes.getDouble("WMostLong"), hspenRes.getString("SpeciesID"), session);
				hcafRes.beforeFirst();

				Double preparedSeaIce= prepareSeaIceForSpecies(hspenRes.getString("SpeciesID"),hspenRes.getDouble("IceConMin"),session);
				int i =0;
				//looping on HCAF filter1
				while (hcafRes.next()){
					try{
						boolean inFAO= this.getInFao(hcafRes.getInt("FAOAreaM"),hspenRes.getString("FAOAreas"));
						boolean inBox= this.getInBox(hcafRes.getDouble("CenterLat"), bounds);
						if (inFAO && inBox){
							//checking if weights are customized
							
														
							Double landValue=(landWeight)?1.0:1.0; //to understand why is not calculated
							Double sstValue=(sstWeight)?this.getSST(hcafRes.getDouble("SSTAnMean"), hcafRes.getDouble("SBTAnMean"), hspenRes.getDouble("TempMin"), hspenRes.getDouble("TempMax"), hspenRes.getDouble("TempPrefMin"), hspenRes.getDouble("TempPrefMax"), hspenRes.getString("Layer").toCharArray()[0]):1.0;
							Double depthValue=(depthWeight)?this.getDepth(hcafRes.getDouble("DepthMax"), hcafRes.getDouble("DepthMin"), hspenRes.getInt("Pelagic"), hspenRes.getDouble("DepthMax"), hspenRes.getDouble("DepthMin"), hspenRes.getDouble("DepthPrefMax"), hspenRes.getDouble("DepthPrefMin")):1.0;
							Double salinityValue=(salinityWeight)?this.getSalinity(hcafRes.getDouble("SalinityMean"), hcafRes.getDouble("SalinityBMean"), hspenRes.getString("Layer").toCharArray()[0], hspenRes.getDouble("SalinityMin"), hspenRes.getDouble("SalinityMax"), hspenRes.getDouble("SalinityPrefMin"), hspenRes.getDouble("SalinityPrefMax")):1.0;
							Double primaryProductsValue=(primaryProductionWeight)?this.getPrimaryProduction(hcafRes.getInt("PrimProdMean"), hspenRes.getDouble("PrimProdMin"), hspenRes.getDouble("PrimProdPrefMin"), hspenRes.getDouble("PrimProdMax"), hspenRes.getDouble("PrimProdPrefMax")):1.0;
							Double seaIceConcentration=(seaIceConcentrationWeight)?this.getSeaIceConcentration(hcafRes.getDouble("IceConAnn"), (preparedSeaIce!=-9999.0)?preparedSeaIce:hspenRes.getDouble("IceConMin"), hspenRes.getDouble("IceConPrefMin"), hspenRes.getDouble("IceConMax"), hspenRes.getDouble("IceConPrefMax"), hspenRes.getString("SpeciesID"), session):1.0;
							Double totalCountProbability=landValue*sstValue*depthValue*salinityValue*primaryProductsValue*seaIceConcentration;

							if (totalCountProbability>0){
								String insertQuery = "INSERT INTO "+this.resultsTable+" values('"+hspenRes.getString("SpeciesID")+"','"+hcafRes.getString("CsquareCode")+"',"+formatter.format(totalCountProbability)+","+inBox+","+inFAO+",'"+hcafRes.getString("FAOAreaM")+"','"+hcafRes.getString("EEZFirst")+"','"+hcafRes.getString("LME")+"')";
								//logger.trace("executing insertQuery "+insertQuery);
								session.executeUpdate(insertQuery);
								i++;
							}
						}
					}catch(Exception e){logger.warn("error in data found",e);}
				}

				logger.trace("inserted "+i+" entries for "+hspenRes.getString("SpeciesID")+" species id");
				
				int k=0;
				if (i==0) /*no entry inserted in hspec*/{
					hcafRes.beforeFirst();
					//looping on HCAF filter2
					while (hcafRes.next()){
						try{
							boolean inFAO= this.getInFao(hcafRes.getInt("FAOAreaM"),hspenRes.getString("FAOAreas"));
							boolean inBox= this.getInBox(hcafRes.getDouble("CenterLat"), bounds);
							if (inFAO && !inBox){
								
								Double landValue=(landWeight)?1.0:1.0; //to understand why is not calculated
								Double sstValue=(sstWeight)?this.getSST(hcafRes.getDouble("SSTAnMean"), hcafRes.getDouble("SBTAnMean"), hspenRes.getDouble("TempMin"), hspenRes.getDouble("TempMax"), hspenRes.getDouble("TempPrefMin"), hspenRes.getDouble("TempPrefMax"), hspenRes.getString("Layer").toCharArray()[0]):1.0;
								Double depthValue=(depthWeight)?this.getDepth(hcafRes.getDouble("DepthMax"), hcafRes.getDouble("DepthMin"), hspenRes.getInt("Pelagic"), hspenRes.getDouble("DepthMax"), hspenRes.getDouble("DepthMin"), hspenRes.getDouble("DepthPrefMax"), hspenRes.getDouble("DepthPrefMin")):1.0;
								Double salinityValue=(salinityWeight)?this.getSalinity(hcafRes.getDouble("SalinityMean"), hcafRes.getDouble("SalinityBMean"), hspenRes.getString("Layer").toCharArray()[0], hspenRes.getDouble("SalinityMin"), hspenRes.getDouble("SalinityMax"), hspenRes.getDouble("SalinityPrefMin"), hspenRes.getDouble("SalinityPrefMax")):1.0;
								Double primaryProductsValue=(primaryProductionWeight)?this.getPrimaryProduction(hcafRes.getInt("PrimProdMean"), hspenRes.getDouble("PrimProdMin"), hspenRes.getDouble("PrimProdPrefMin"), hspenRes.getDouble("PrimProdMax"), hspenRes.getDouble("PrimProdPrefMax")):1.0;
								Double seaIceConcentration=(seaIceConcentrationWeight)?this.getSeaIceConcentration(hcafRes.getDouble("IceConAnn"), (preparedSeaIce!=-9999.0)?preparedSeaIce:hspenRes.getDouble("IceConMin"), hspenRes.getDouble("IceConPrefMin"), hspenRes.getDouble("IceConMax"), hspenRes.getDouble("IceConPrefMax"), hspenRes.getString("SpeciesID"), session):1.0;
								Double totalCountProbability=landValue*sstValue*depthValue*salinityValue*primaryProductsValue*seaIceConcentration;

								if (totalCountProbability!=0){
									String insertQeury= "INSERT INTO "+this.resultsTable+" values('"+hspenRes.getString("SpeciesID")+"','"+hcafRes.getString("CsquareCode")+"',"+formatter.format(totalCountProbability)+","+inBox+","+inFAO+",'"+hcafRes.getString("FAOAreaM")+"','"+hcafRes.getString("EEZFirst")+"','"+hcafRes.getString("LME")+"')";
									//logger.trace("executing insertQuery "+insertQeury);
									session.executeUpdate(insertQeury);
									k++;
								}	
							}
						}catch(Exception e){logger.warn("error in data found",e);}
					}
				}
				logger.trace("inserted "+k+" entries whit inbox false for "+hspenRes.getString("SpeciesID")+" species id");
				logger.trace("HSPEN loop number "+hspenLoops+" took "+(System.currentTimeMillis()-startHspenLoop));
				this.details.getSpeciesHandling().put(hspenRes.getString("SpeciesID"), SpeciesStatus.Ready);
				hspenLoops++;
			}
			
		}catch (Exception e) {
			logger.error("error in generate method",e);
			try{
				session.executeUpdate("DROP TABLE "+this.resultsTable);
			}catch (Exception e1){logger.trace("error deleting resultTable");}	
			throw e;
		}finally{
			try{
				session.executeUpdate("DROP TABLE "+this.hcafViewTable);
			}catch (Exception e){logger.trace("error deleting the hcafTempTable");}
			session.close();
		}
		
		logger.trace("generation of "+this.details.getToPerform().getName()+" finished in "+((System.currentTimeMillis()-generationStart)/1000)+"secs");
		return this.resultsTable;
	}
		
	
	private Bounduary getBounduary(Double north, Double south, Double east, Double west, String speciesId, DBSession session) throws Exception{
		Bounduary bounduary= new Bounduary(north, south, east, west);
		if (north==null || south==null || east==null || west==null){
			if (north!=null && south==null) bounduary.passedNS=true;
			else if (north!=null) bounduary.passedN=true;
			else if (south!=null) bounduary.passedS=true;
			else{
				ResultSet rsBond=session.executeQuery("Select distinct Max("+this.hcafViewTable+".CenterLat) AS maxCLat, Min("+this.hcafViewTable+".CenterLat) AS minCLat" +
						" FROM "+this.occurenceCellsTable+" INNER JOIN "+this.hcafViewTable+" ON "+this.occurenceCellsTable+".CsquareCode = "+this.hcafViewTable+".CsquareCode" +
						" Where ((("+this.hcafViewTable+".OceanArea > 0))) AND "+this.occurenceCellsTable+".SpeciesID = '"+speciesId+"' AND "+this.occurenceCellsTable+".GoodCell <> 0");
				rsBond.next();
				double maxCLat=rsBond.getDouble("maxCLat");
				double minCLat=rsBond.getDouble("minCLat");
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
	public Double getDepth(Double hcafDepthMax, Double hcafDepthMin, int hspenPelagic, Double hspenMaxDepth,Double hspenMinDepth,Double hspenDepthPrefMax, Double hspenDepthPrefMin ){
		if (hcafDepthMax == -9999 || hspenMinDepth == null ) return 1.0;
		if (hcafDepthMax < hspenMinDepth) return 0.0;
		if 	((hcafDepthMax < hspenDepthPrefMin) &&(hcafDepthMax >= hspenMinDepth)) return (hcafDepthMax - hspenMinDepth) / (hspenDepthPrefMin - hspenMinDepth);
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
	public Double getSST( Double hcafSSTAnMean, Double hcafSBTAnMean, Double hspenTempMin, Double hspenTempMax, Double hspenTempPrefMin, Double hspenTempPrefMax, char hspenLayer ){
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
	public Double getSalinity(Double hcafSalinityMean, Double hcafSalinityBMean, char hspenLayer, Double hspenSalinityMin,Double hspenSalinityMax, Double hspenSalinityPrefMin, Double hspenSalinityPrefMax){
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
	public Double getPrimaryProduction(int hcafPrimProdMean,Double hspenPrimProdMin, Double hspenPrimProdPrefMin,Double hspenPrimProdMax, Double hspenPrimProdPrefMax){
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
			String query="SELECT distinct "+this.occurenceCellsTable+".CsquareCode, "+this.occurenceCellsTable+".SpeciesID, "+this.hcafViewTable+".IceConAnn" +
			" FROM "+this.occurenceCellsTable+" INNER JOIN "+this.hcafViewTable+" ON "+this.occurenceCellsTable+".CsquareCode = "+this.hcafViewTable+".CsquareCode" +
			" WHERE (  (("+this.hcafViewTable+".OceanArea)>0))" +
			" and "+this.occurenceCellsTable+".SpeciesID = '" +speciesID+ "'" +
			" and "+this.hcafViewTable+".IceConAnn <> -9999" +
			" and "+this.hcafViewTable+".IceConAnn is not null" +
			" and "+this.occurenceCellsTable+".goodcell = -1" +
			" order by "+this.hcafViewTable+".IceConAnn";
			logger.trace("Sea Ice Concentration query: "+query);
			
			ResultSet iceConRes=session.executeQuery(query);
			int recordCount=0;
			while (iceConRes.next()){
				sumIce+=iceConRes.getDouble("IceConAnn");
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
	public Double getSeaIceConcentration(Double hcafIceConAnn,Double hspenIceConMin,Double hspenIceConPrefMin, Double hspenIceConMax,Double hspenIceConPrefMax, String speciesId, DBSession session) throws Exception{
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
	public boolean getInBox(Double hcafCenterLat, Bounduary bounds){
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
	
	public boolean getInFao(Integer hcafFAOAreaM, String hspenFAOAreas){
		if (hcafFAOAreaM==null) return false;
		if (hspenFAOAreas.contains(hcafFAOAreaM.toString()))
			return true;
		else return false;
	}
	
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

package org.gcube.application.aquamaps.aquamapsservice.impl.env;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.dataModel.Cell;
import org.gcube.application.aquamaps.dataModel.Species;

//###################################################################################
//This file re-computes the primary productivity values (Min, PrefMin, Max, PrefMax based on 
//area restriction parameters set by the user
//###################################################################################
public class Env_PrimProd extends EnvEngine {

	private double prodMin;
	private double prodMax;
	private double prodPMin;
	private double prodPMax;
	
	public Env_PrimProd() {
		super();
	}

	@Override
	public void re_computes(Species species, List<Cell> goodCells) throws Exception {
		
		/*
		String strSQL="SELECT DISTINCT "+ocVar+".CsquareCode, "+ocVar+".SpeciesID, HCAF.PrimProdMean ";
		strSQL+="FROM "+ocVar+" INNER JOIN HCAF ON "+ocVar+".CsquareCode = HCAF.CsquareCode ";
		strSQL+="WHERE "+ocVar+".SpeciesID = '" + hspen.getSpeciesID() + "' ";
		strSQL+="AND HCAF.PrimProdMean is not null ";
		strSQL+="AND HCAF.OceanArea > 0 ";
		strSQL+="AND "+ocVar+".inc = 'y' ";
		strSQL+="ORDER BY HCAF.PrimProdMean";
*/
		List<Cell> filterCells = new ArrayList<Cell>();
		for (Cell cell: goodCells) {
			if (cell.attributes.get(Cell.Tags.PrimProdMean).getValue() != null && 
				Double.parseDouble(cell.attributes.get(Cell.Tags.OceanArea).getValue()) > 0) {
				filterCells.add(cell);
			}
		}
		
		this.fillData(filterCells, Cell.Tags.PrimProdMean);
		
		this.prodMin = this.getMin();
		this.prodMax = this.getMax();					
		this.prodPMin = this.getpMin();
		this.prodPMax = this.getpMax();
	    
		if (this.getParaAdjMax() < EnvCostants.prodUpper && 
				this.getParaAdjMax() > this.prodMax) 
					this.prodMax = this.getParaAdjMax();
		
		
		if (this.getParaAdjMin() > EnvCostants.prodLower && 
				this.getParaAdjMin() < this.prodMin) 
					this.prodMin = this.getParaAdjMin();
		
		//check if envelope is as broad as pre-defined minimum
		if (this.prodPMax - this.prodPMin < 2) {
			double paraMid = (this.prodPMin + this.prodPMax) / 2;               
			double pMinTmp = paraMid - 1;
			double pMaxTmp = paraMid + 1;
			    
			//enforce a minimum preferred range as long as it doesn't extrapolate outer limits
			if (pMinTmp >= this.prodMin)	//preferred Min value as is 
				this.prodPMin = pMinTmp;
			
			if (pMaxTmp <= this.prodMax)	//preferred Max value as is
				this.prodPMax = pMaxTmp;
			            
			//check difference between min/max and pref. min/max
			if (this.prodPMin - this.prodMin < 1) {
			    double minTmp = prodPMin - 1;
			    if (minTmp > EnvCostants.prodLower) this.prodMin = minTmp;
			    else this.prodMin = EnvCostants.prodLower;
			}
			            
			if (prodMax - prodPMax < 1) {
			    double maxTmp = prodPMax + 1;
			    if (maxTmp < EnvCostants.prodUpper) this.prodMax = maxTmp;
			    else this.prodMax = EnvCostants.prodUpper;
			}
		}
		
		species.getFieldbyName(Species.Tags.PrimProdMin).setValue(""+this.prodMin);
		species.getFieldbyName(Species.Tags.PrimProdMax).setValue(""+this.prodMax);
		species.getFieldbyName(Species.Tags.PrimProdPrefMin).setValue(""+this.prodPMin);
		species.getFieldbyName(Species.Tags.PrimProdPrefMax).setValue(""+this.prodPMax);
	}
}
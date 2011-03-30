package org.gcube.application.aquamaps.aquamapsservice.impl.env;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;


//###################################################################################
//This file re-computes the salinity values (Min, PrefMin, Max, PrefMax based on 
//area restriction parameters set by the user
//###################################################################################
public class Env_Salinity extends EnvEngine {

	private double salinUp;
	private double salinLow;
	private double salinMin;					
	private double salinMax;					  
	private double salinPMin;
	private double salinPMax;

	public Env_Salinity() {
		super();
	}

	public void re_computes(Species species, Set<Cell> goodCells) throws Exception {
		
		String fld;
		if (species.getFieldbyName(HspenFields.layer+"").equals("b")) {
			fld = "SalinityBMean";			
			this.salinUp = EnvCostants.salinBUpper; 	//reset absolute min and max for bottom
			this.salinLow = EnvCostants.salinBLower;
		} else {
			fld = "SalinityMean";			
		    this.salinUp = EnvCostants.salinUpper;
		    this.salinLow = EnvCostants.salinLower;
		}
		
		/*
		String strSQL="SELECT DISTINCT "+goodCells+".CsquareCode, ";
			strSQL+=goodCells+".SpeciesID, HCAF."+fld+" FROM $oc_var ";
			strSQL+="INNER JOIN HCAF ON "+goodCells+".CsquareCode = HCAF.CsquareCode ";
			strSQL+="WHERE "+goodCells+".SpeciesID = '" + species.getSpeciesID() + "' "; 
			strSQL+="AND HCAF."+fld+" <> -9999 ";
			strSQL+="AND HCAF."+fld+" is not null ";
			strSQL+="AND HCAF.OceanArea > 0 ";
			strSQL+="AND "+goodCells+".inc = 'y' ";
			strSQL+="ORDER BY HCAF."+fld;
*/
		List<Cell> filterCells = new ArrayList<Cell>();
		for (Cell cell: goodCells) {
			
			if (cell.getFieldbyName(fld).getValue() != null &&
					Double.parseDouble(cell.getFieldbyName(fld).getValue()) != -9999 &&
					Double.parseDouble(cell.getFieldbyName(HCAF_SFields.oceanarea+"").getValue()) > 0) {
				filterCells.add(cell);
			}
		}
		
		this.fillData(filterCells, fld);
		
		this.salinMin = this.getMin();
		this.salinMax = this.getMax();					  
		this.salinPMin = this.getpMin();            
		this.salinPMax = this.getpMax();
		
		if (this.getParaAdjMax() < EnvCostants.salinUpper && 
				this.getParaAdjMax() > this.salinMax) 
					this.salinMax = this.getParaAdjMax();
		
		
		if (this.getParaAdjMin() > EnvCostants.salinLower && 
				this.getParaAdjMin() < this.salinMin) 
					this.salinMin = this.getParaAdjMin();

		//check if envelope is as broad as pre-defined minimum
		if (this.salinPMax - this.salinPMin < 1) {
			double paraMid = (this.salinPMin + this.salinPMax) / 2;               
		    double pMinTmp = paraMid - 0.5;
		    double pMaxTmp = paraMid + 0.5;
		    
			//enforce a minimum preferred range as long as it doesn't extrapolate outer limits
		    if (pMinTmp >= this.salinMin)	// preferred Min value as is
		    	this.salinPMin = pMinTmp;
			         
		    
			if (pMaxTmp <= this.salinMax)	
				this.salinPMax = pMaxTmp;   			   
		}

		//check difference between min/max and pref. min/max
		if (this.salinPMin - this.salinMin < 0.5) {     
			double minTmp = this.salinPMin - 0.5;
		    if (minTmp > EnvCostants.salinLower) this.salinMin = minTmp;
		    else this.salinMin = EnvCostants.salinLower;
		}
		
		if ((this.salinMax - this.salinPMax) < 0.5) {     
			double maxTmp = this.salinPMax + 0.5;
		    maxTmp = 0;
			if (maxTmp < EnvCostants.salinUpper) this.salinMax = maxTmp;
		    else this.salinMax = EnvCostants.salinUpper;
		}
			
		species.getFieldbyName(HspenFields.salinitymin+"").setValue(""+this.salinMin);
		species.getFieldbyName(HspenFields.salinitymax+"").setValue(""+this.salinMax);
		species.getFieldbyName(HspenFields.salinityprefmin+"").setValue(""+this.salinPMin);
		species.getFieldbyName(HspenFields.salinityprefmax+"").setValue(""+this.salinPMax);
	}
}

package org.gcube.application.aquamaps.aquamapsservice.impl.env;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;


//###################################################################################
//This file re-computes the distance to land values (Min, PrefMin, Max, PrefMax 
//based on area restriction parameters set by the user
//###################################################################################
public class Env_LandDist extends EnvEngine {
	
	private double landMin;
	private double landMax;
	private double landPMin;
	private double landPMax;
	
	public Env_LandDist() {
		super();
	}
	
	public void re_computes(Species species, Set<Cell> goodCells) throws Exception {
		
		/*
		String strSQL = "SELECT DISTINCT "+ocVar+".CsquareCode, "+ocVar+".SpeciesID, HCAF.LandDist ";
		strSQL+="FROM "+ocVar+" INNER JOIN HCAF ON "+ocVar+".CsquareCode = HCAF.CsquareCode ";
		strSQL+="WHERE "+ocVar+".SpeciesID = '" + hspen.getSpeciesID() + "' "; 
		strSQL+="AND HCAF.LandDist <> -9999 ";
		strSQL+="AND HCAF.LandDist is not null ";
		strSQL+="AND HCAF.OceanArea > 0 ";
		strSQL+="AND "+ocVar+".inc = 'y' "; 
		strSQL+="ORDER BY HCAF.LandDist";
*/
		List<Cell> filterCells = new ArrayList<Cell>();
		for (Cell cell: goodCells) {
			if (cell.getFieldbyName(HCAF_SFields.landdist+"").getValue() != null && 
				Double.parseDouble(cell.getFieldbyName(HCAF_SFields.landdist+"").getValue()) != -9999 && 
				Double.parseDouble(cell.getFieldbyName(HCAF_SFields.oceanarea+"").getValue()) > 0) {
				filterCells.add(cell);
			}
		}
		
		this.fillData(filterCells, HCAF_SFields.landdist+"");
		
		this.landMin = this.getMin();
		this.landMax = this.getMax();
		this.landPMin = this.getpMin();
		this.landPMax = this.getpMax();
		
		if (this.getParaAdjMax() < EnvCostants.landUpper && 
				this.getParaAdjMax() > this.landMax) 
					this.landMax = this.getParaAdjMax();
		
		
		if (this.getParaAdjMin() > EnvCostants.landlower && 
				this.getParaAdjMin() < this.landMin) 
					this.landMin = this.getParaAdjMin();
		
		//check if envelope is as broad as pre-defined minimum
		if (this.landPMax - this.landPMin < 2) {
			double paraMid = (landPMin + landPMax) / 2;               
			double pMinTmp = paraMid - 1;
			double pMaxTmp = paraMid + 1;
			    
			//enforce a minimum preferred range as long as it doesn't extrapolate outer limits
			if (pMinTmp >= this.landMin)	//preferred Min value as is 
				this.landPMin = pMinTmp;
			
			if (pMaxTmp <= this.landMax)	//preferred Max value as is
				this.landPMax = pMaxTmp;
			            
			//check difference between min/max and pref. min/max
			if (this.landPMin - this.landMin < 1) {
			    double minTmp = landPMin - 1;
			    if (minTmp > EnvCostants.landlower) this.landMin = minTmp;
			    else this.landMin = EnvCostants.landlower;
			}
			            
			if (landMax - landPMax < 1) {
			    double maxTmp = landPMax + 1;
			    if (maxTmp < EnvCostants.landUpper) this.landMax = maxTmp;
			    else this.landMax = EnvCostants.landUpper;
			}
		}
		
		species.getFieldbyName(HspenFields.landdistmin.toString()).setValue(""+this.landMin);
		species.getFieldbyName(HspenFields.landdistmax.toString()).setValue(""+this.landMax);
		species.getFieldbyName(HspenFields.landdistprefmin.toString()).setValue(""+this.landPMin);
		species.getFieldbyName(HspenFields.landdistprefmax.toString()).setValue(""+this.landPMax);
	}
}
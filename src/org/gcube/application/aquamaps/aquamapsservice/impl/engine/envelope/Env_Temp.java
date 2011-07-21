package org.gcube.application.aquamaps.aquamapsservice.impl.engine.envelope;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_DFields;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;

//###################################################################################
//This file re-computes the temperature values (Min, PrefMin, Max, PrefMax based on 
//area restriction parameters set by the user
//###################################################################################
public class Env_Temp extends EnvEngine{

	private double tempMin;
	private double tempMax;					  
	private double tempPMin;            
	private double tempPMax;
	
	public Env_Temp() throws Exception{
		super();
	}

	@Override
	public void re_computes(Species species, Set<Cell> goodCells) throws Exception {
		
		String fld;
		if (species.getFieldbyName(HspenFields.layer+"").getValue().contentEquals("b")) fld = HCAF_DFields.sbtanmean+"";		
		else fld = HCAF_DFields.sstanmean+""; //s
/*
		String strSQL="SELECT DISTINCT "+ocVar+".CsquareCode, "+ocVar+".SpeciesID, HCAF."+fld;
		strSQL+="FROM "+ocVar+" INNER JOIN HCAF ON "+ocVar+".CsquareCode = HCAF.CsquareCode ";
		strSQL+="WHERE "+ocVar+".SpeciesID = '" + hspen.getSpeciesID() + "' ";
		strSQL+="AND HCAF."+fld+" <> -9999 ";
		strSQL+="AND HCAF."+fld+" is not null ";
		strSQL+="AND HCAF.OceanArea > 0 ";
		strSQL+="AND "+ocVar+".inc = 'y' ";
		strSQL+="ORDER BY HCAF."+fld;
*/
		
		List<Cell> filterCells = new ArrayList<Cell>();
		for (Cell cell: goodCells) {
			Double fldValue=cell.getFieldbyName(fld).getValueAsDouble(defaultDoubleValue+"");
			Double oceanArea=cell.getFieldbyName(HCAF_SFields.oceanarea+"").getValueAsDouble(defaultDoubleValue+"");
			if (fldValue != null &&	fldValue != -9999 && oceanArea> 0) {
				filterCells.add(cell);
			}
		}
		
		
		
		this.fillData(filterCells, fld);
		
		this.tempMin = this.getMin();
		this.tempMax = this.getMax();					  
		this.tempPMin = this.getpMin();            
		this.tempPMax = this.getpMax();
		
		if (this.getParaAdjMax() < EnvCostants.tempUpper && 
				this.getParaAdjMax() > this.tempMax) 
					this.tempMax = this.getParaAdjMax();
		
		
		if (this.getParaAdjMin() > EnvCostants.tempLower && 
				this.getParaAdjMin() < this.tempMin) 
					this.tempMin = this.getParaAdjMin();
		            
		
		double spreadVal;
		if (this.tempMax <= 5) //then polar and deepwater species
			spreadVal = 0.25;
		else 
			spreadVal = 1; 


		if (this.tempPMax - this.tempPMin < spreadVal) {				
		    double paraMid = (tempPMin + tempPMax) / 2;                				
			double pMinTmp = paraMid - (spreadVal / 2);				
			double pMaxTmp = paraMid + (spreadVal / 2);

			//enforce a minimum preferred range as long as it doesn't extrapolate outer limits
		    if (pMinTmp >= tempMin) tempPMin = pMinTmp;      
			if (pMaxTmp <= tempMax) tempPMax = pMaxTmp;
		}               

		//check difference between min/max and pref. min/max
		if (tempPMin - tempMin < 0.5) {     
			double minTmp = tempPMin - 0.5;
		    if (minTmp > EnvCostants.tempLower){tempMin = minTmp;}
		    else {tempMin = EnvCostants.tempLower;}
		}          

		if (tempMax - tempPMax < 0.5) {     
			double maxTmp = tempPMax + 0.5;
		    if (maxTmp < EnvCostants.tempUpper){tempMax = maxTmp;}
		    else {tempMax = EnvCostants.tempUpper;}
		}

					
		//check if envelope is as broad as pre-defined minimum
		if (tempPMax >= 25)	tempMax = tempPMax + 4.2;
		
		species.getFieldbyName(HspenFields.tempmin+"").setValue(""+this.tempMin);
		species.getFieldbyName(HspenFields.tempmax+"").setValue(""+this.tempMax);
		species.getFieldbyName(HspenFields.tempprefmin+"").setValue(""+this.tempPMin);
		species.getFieldbyName(HspenFields.tempprefmax+"").setValue(""+this.tempPMax);
	}
}

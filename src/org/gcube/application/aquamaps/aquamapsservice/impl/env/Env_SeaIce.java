package org.gcube.application.aquamaps.aquamapsservice.impl.env;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_DFields;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;

//###################################################################################
//This file re-computes the sea ice concentration values (Min, PrefMin, Max, PrefMax 
//based on area restriction parameters set by the user
//###################################################################################
public class Env_SeaIce extends EnvEngine {

	private double iceMin;
	private double iceMax;					
	private double icePMin;
	private double icePMax;
	
	public Env_SeaIce() {
		super();
	}

	@Override
	public void re_computes(Species species, Set<Cell> goodCells) throws Exception {
/*
		String strSQL="SELECT DISTINCT "+ocVar+".CsquareCode, "+ocVar+".SpeciesID, HCAF.IceConAnn ";
		strSQL+="FROM "+ocVar+" INNER JOIN HCAF ON "+ocVar+".CsquareCode = HCAF.CsquareCode ";
		strSQL+="WHERE "+ocVar+".SpeciesID = '" + hspen.getSpeciesID() + "' ";
		strSQL+="AND HCAF.IceConAnn is not null ";
		strSQL+="AND HCAF.OceanArea > 0 ";
		strSQL+="AND "+ocVar+".inc = 'y' ";
		strSQL+="ORDER BY HCAF.IceConAnn";
*/
		List<Cell> filterCells = new ArrayList<Cell>();
		for (Cell cell: goodCells) {
			
			if (cell.getFieldbyName(HCAF_DFields.iceconann+"").getValue() != null &&
					Double.parseDouble(cell.getFieldbyName(HCAF_SFields.oceanarea+"").getValue()) > 0) {
				filterCells.add(cell);
			}
		}
		
		this.fillData(filterCells, HCAF_DFields.iceconann+"");
		
		this.iceMin = this.getMin();
		this.iceMax = this.getMax();					
		this.icePMin = this.getpMin();
		this.icePMax = this.getpMax();
						
		//per KK and JR: extend IceMin -  avoid exclusion of species from all non-ice covered areas
		int adjVal = 0; double sumIce = 0; double meanIce = 0;			
		adjVal = -1; 		//fix to -1 per KK (Me!AdjustIce value taken from form input)

		if (this.iceMin == 0) {
			
			sumIce = 0;

			for (Cell cell: filterCells) {
		         sumIce = sumIce + Double.parseDouble(cell.getFieldbyName(HCAF_DFields.iceconann+"").getValue());
		    }
			int reccount = filterCells.size();
			if(reccount != 0)	{meanIce = sumIce / reccount;}
			else				{meanIce = 0;}
		             
			this.iceMin = adjVal + meanIce;
		}
		
		species.getFieldbyName(HspenFields.iceconmin+"").setValue(""+this.iceMin);
		species.getFieldbyName(HspenFields.iceconmax+"").setValue(""+this.iceMax);
		species.getFieldbyName(HspenFields.iceconprefmin+"").setValue(""+this.icePMin);
		species.getFieldbyName(HspenFields.iceconprefmax+"").setValue(""+this.icePMax);
	}
}

package org.gcube.application.aquamaps.aquamapsservice.impl.env;

import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Species;

public class SpEnvelope {

	private Env_Salinity env_salinity;
	private Env_LandDist env_landDist;
	private Env_SeaIce env_seaIce;
	private Env_Temp env_temp;
	private Env_PrimProd env_primProd;
	
	public SpEnvelope() {
		super();
		
		try {
			this.env_salinity =  new Env_Salinity();
			this.env_landDist =  new Env_LandDist();
			this.env_seaIce =  new Env_SeaIce();
			this.env_temp =  new Env_Temp();
			this.env_primProd =  new Env_PrimProd();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reCalculate(Species species, List<Cell> goodCells) throws Exception {
		
		int cellCount = goodCells.size();
		
		if (cellCount > 0) {
			//then 'proceed with envelope
		    if (cellCount < 10) {
		    	//then 'to accommodate l. chalumnae
		        if (species.getId() != "2063") {
					throw(new Exception(cellCount + " Envelope creation failed (cells < 10). "+
										"Not enough number of good cells to draw data from."));					
		        }
		    }
		}
		
		
		this.env_salinity.re_computes(species, goodCells);
		this.env_landDist.re_computes(species, goodCells);
		this.env_temp.re_computes(species, goodCells);
		this.env_primProd.re_computes(species, goodCells);
		this.env_seaIce.re_computes(species, goodCells);
		
		/*
		//start depth
		String qry="select DepthMin,DepthPrefMin,DepthPrefMax,DepthMax from hspen where SpeciesID = '$SpecID'";
		getdepth = $conn->query($qry);
		$drow = $getdepth->fetch_row();			
		$DepthMin 		= $drow[0];
		$DepthPrefMin 	= $drow[1];
		$DepthPrefMax 	= $drow[2];
		$DepthMax 		= $drow[3];
		$getdepth->close();

		if($DepthMin 	 == "")	{$s21 = "DepthMin=NULL";}		else		{$s21="DepthMin=" . $DepthMin;}
		if($DepthPrefMin == "")	{$s22 = "DepthPrefMin=NULL";}	else		{$s22="DepthPrefMin=" . $DepthPrefMin;}
		if($DepthPrefMax == "")	{$s23 = "DepthPrefMax=NULL";}	else		{$s23="DepthPrefMax=" . $DepthPrefMax;}
		if($DepthMax 	 == "")	{$s24 = "DepthMax=NULL";}		else		{$s24="DepthMax=" . $DepthMax;}
		//end depth

		$qry = "UPDATE $hspen_var SET $s1,$s2,$s3,$s4,$s5,$s6,$s7,$s8,$s9,$s10,$s11,$s12,$s13,$s14,$s15,$s16,$s21,$s22,$s23,$s24
		WHERE $hspen_var.SpeciesID = '" . $SpecID . "'";
		$qry = $qry . " and $hspen_var.session_id = $user_session ";
		$update = $conn->query($qry);
		*/
	}

	//similar to MSAccess round()
	public static long eli_round(double value) { 
		
		String nstr = String.valueOf(value);
		int dec_pos = nstr.indexOf(".");
		
		String left_char = nstr.substring(dec_pos-1, dec_pos);
		String right_char = nstr.substring(dec_pos+1, dec_pos+2);
		long result = 0;
		
		if (right_char == "5") {
			if 	(
					left_char == "0" ||
					left_char == "2" ||
					left_char == "4" ||
					left_char == "6" ||
					left_char == "8" 				
				) result = Math.round(value) -1;
			else result = Math.round(value);
		} else {
			result = Math.round(value);
		}
		
		return (result); 
	} 
}

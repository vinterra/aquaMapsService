package org.gcube.application.aquamaps.aquamapsservice.impl.env;

import java.util.List;
import org.gcube.application.aquamaps.dataModel.Cell;
import org.gcube.application.aquamaps.dataModel.Species;

public abstract class EnvEngine {
	
	private long rec25;
	private long rec75;
	private long rec10;
	private long rec90;
	
	private double para25;					
	private double para75;
	private double min;
	private double max;
	private double pMin;
	private double pMax;
	
	private double paraAdjMax;
	private double paraAdjMin;

	public EnvEngine() {
		super();
	}
	
	public abstract void re_computes(Species species, List<Cell> goodCells) throws Exception;
	
	protected void fillData(List<Cell> goodCells, String fld) throws Exception {
		
		int reccount = goodCells.size();

		//compute positions of percentiles: 25th, 75th, 10th and 90th
		this.rec25 = SpEnvelope.eli_round(25 * (reccount + 1) / 100) - 1; //'25
		this.rec75 = SpEnvelope.eli_round(75 * (reccount + 1) / 100) - 1; //'75
		
		if (reccount >= 10 && reccount <= 13) {
			this.rec10 = SpEnvelope.eli_round(10 * (reccount + 1) / 100);
			this.rec90 = SpEnvelope.eli_round(90 * (reccount + 1) / 100) - 2;			
		} else {
			this.rec10 = SpEnvelope.eli_round(10 * (reccount + 1) / 100) - 1;
			this.rec90 = SpEnvelope.eli_round(90 * (reccount + 1) / 100) - 1;
		}
		
		//get percentiles
		this.min = Double.parseDouble(goodCells.get(0).attributes.get(fld).getValue());
		this.max = Double.parseDouble(goodCells.get(reccount-1).attributes.get(fld).getValue());
		this.para25 = Double.parseDouble(goodCells.get((int) this.rec25).attributes.get(fld).getValue());
		this.para75 = Double.parseDouble(goodCells.get((int) rec75).attributes.get(fld).getValue());
		this.pMin = Double.parseDouble(goodCells.get((int) rec10).attributes.get(fld).getValue());
		this.pMax = Double.parseDouble(goodCells.get((int) rec90).attributes.get(fld).getValue());

		//interquartile adjusting
		double interQuartile = Math.abs(this.para25 - this.para75);
		this.paraAdjMax = this.para75 + 1.5 * interQuartile;
		this.paraAdjMin = this.para25 - 1.5 * interQuartile;
		
	}

	protected double getMin() {
		return min;
	}

	protected void setMin(double min) {
		this.min = min;
	}

	protected double getMax() {
		return max;
	}

	protected void setMax(double max) {
		this.max = max;
	}

	protected double getpMin() {
		return pMin;
	}

	protected void setpMin(double pMin) {
		this.pMin = pMin;
	}

	protected double getpMax() {
		return pMax;
	}

	protected void setpMax(double pMax) {
		this.pMax = pMax;
	}
	
	protected double getParaAdjMax() {
		return paraAdjMax;
	}

	protected void setParaAdjMax(double paraAdjMax) {
		this.paraAdjMax = paraAdjMax;
	}

	protected double getParaAdjMin() {
		return paraAdjMin;
	}

	protected void setParaAdjMin(double paraAdjMin) {
		this.paraAdjMin = paraAdjMin;
	}
}

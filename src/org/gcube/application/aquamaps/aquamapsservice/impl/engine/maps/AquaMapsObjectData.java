package org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps;

public class AquaMapsObjectData {

	
	//********* PERL
	private StringBuilder[] csq_str;
	private int min;
	private int max;
	
	//********* GIS
	private String csvFile;

	public StringBuilder[] getCsq_str() {
		return csq_str;
	}

	public void setCsq_str(StringBuilder[] csq_str) {
		this.csq_str = csq_str;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public String getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}
	
	
	
	
}

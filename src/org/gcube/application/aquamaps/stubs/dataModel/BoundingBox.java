package org.gcube.application.aquamaps.stubs.dataModel;


public class BoundingBox {
	float N=90;
	float S=-90;
	float W=180;
	float E=-180;
	public BoundingBox() {		
	}
	
	public float getN() {
		return N;
	}
	public void setN(float n) {
		N = n;
	}
	public float getS() {
		return S;
	}
	public void setS(float s) {
		S = s;
	}
	public float getW() {
		return W;
	}
	public void setW(float w) {
		W = w;
	}
	public float getE() {
		return E;
	}
	public void setE(float e) {
		E = e;
	}
	public String toString(){
		return String.valueOf(N)+","+
		String.valueOf(S)+","+
		String.valueOf(W)+","+
		String.valueOf(E);
	}
	
	/**
	 * Sets comma separated coordinates  
	 * 
	 * @param str coordinates order : N , S , W , E 
	 */
	
	public void parse(String str){			
		String[] values= str.split(",");
		N=Float.parseFloat(values[0]);
		S=Float.parseFloat(values[1]);
		W=Float.parseFloat(values[2]);
		E=Float.parseFloat(values[3]);		
	}
}

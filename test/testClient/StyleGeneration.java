package testClient;

import java.awt.Color;

import it.cnr.isti.geoserverInteraction.engine.MakeStyle;

public class StyleGeneration {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, Exception {
		
		
		
		
		System.out.println(MakeStyle.createStyle("AppStyle", "attribute", 5, Color.YELLOW, Color.RED, Integer.class, Integer.parseInt("14"), Integer.parseInt("1")));

	}

}

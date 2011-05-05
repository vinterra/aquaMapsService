package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions.utils;

import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Envelope;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_DFields;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hcaf;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Coordinates;

public class ModelTranslation {

	public static final String maxCLat="maxCLat";
	public static final String minCLat="minCLat";
	
	
	public static Hspen species2HSPEN(Species s){
		Hspen toReturn=new Hspen();
		toReturn.setSpeciesID(s.getId());
		Envelope speciesEnvelope=s.extractEnvelope();
		toReturn.setDepth(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.Depth)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.Depth)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.Depth)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.Depth)+""));
		toReturn.setTemperature(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.Temperature)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.Temperature)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.Temperature)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.Temperature)+""));
		toReturn.setIceConcentration(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.IceConcentration)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.IceConcentration)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.IceConcentration)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.IceConcentration)+""));
		toReturn.setLandDistance(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.LandDistance)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.LandDistance)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.LandDistance)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.LandDistance)+""));
		toReturn.setPrimaryProduction(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.PrimaryProduction)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.PrimaryProduction)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.PrimaryProduction)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.PrimaryProduction)+""));
		toReturn.setSalinity(
				new org.gcube.application.aquamaps.ecomodelling.generators.connectors.subconnectors.Envelope(
						speciesEnvelope.getMinValue(EnvelopeFields.Salinity)+"",
						speciesEnvelope.getPrefMinValue(EnvelopeFields.Salinity)+"",
						speciesEnvelope.getPrefMaxValue(EnvelopeFields.Salinity)+"",
						speciesEnvelope.getMaxValue(EnvelopeFields.Salinity)+""));
		//TODO coordinates
		toReturn.setCoordinates( new Coordinates(
				s.getFieldbyName(HspenFields.nmostlat+"").getValue(),
				s.getFieldbyName(HspenFields.smostlat+"").getValue(),
				s.getFieldbyName(HspenFields.wmostlong+"").getValue(),
				s.getFieldbyName(HspenFields.emostlong+"").getValue(),
				s.getFieldbyName(maxCLat).getValue(), 
				s.getFieldbyName(minCLat).getValue()));
		
		toReturn.setLayer(s.getFieldbyName(HspenFields.layer+"").getValue());
		
		toReturn.setMeanDepth(s.getFieldbyName(HspenFields.meandepth+"").getValue());
		toReturn.setPelagic(speciesEnvelope.isPelagic());
		toReturn.setLandDistanceYN(s.getFieldbyName(HspenFields.landdistyn+"").getValueAsBoolean());
		toReturn.setFaoAreas(speciesEnvelope.getFaoAreas());
		return toReturn;
	}
	
	public static Hcaf Cell2Hcaf(Cell c){
		Hcaf toReturn= new Hcaf();
		toReturn.setCsquareCode(c.getCode());
		toReturn.setCenterlat(c.getFieldbyName(HCAF_SFields.centerlat+"").getValue());
		toReturn.setCenterlong(c.getFieldbyName(HCAF_SFields.centerlong+"").getValue());
		toReturn.setDepthmax(c.getFieldbyName(HCAF_DFields.depthmax+"").getValue());
		toReturn.setDepthmean(c.getFieldbyName(HCAF_DFields.depthmean+"").getValue());
		toReturn.setDepthmin(c.getFieldbyName(HCAF_DFields.depthmin+"").getValue());
		toReturn.setFaoaream(c.getFieldbyName(HCAF_SFields.faoaream+"").getValue());
		toReturn.setIceconann(c.getFieldbyName(HCAF_DFields.iceconann+"").getValue());
		toReturn.setLanddist(c.getFieldbyName(HCAF_SFields.landdist+"").getValue());
		toReturn.setOceanarea(c.getFieldbyName(HCAF_SFields.oceanarea+"").getValue());
		toReturn.setPrimprodmean(c.getFieldbyName(HCAF_DFields.primprodmean+"").getValue());
		toReturn.setSalinitybmean(c.getFieldbyName(HCAF_DFields.salinitybmean+"").getValue());
		toReturn.setSalinitymean(c.getFieldbyName(HCAF_DFields.salinitymean+"").getValue());
		toReturn.setSbtanmean(c.getFieldbyName(HCAF_DFields.sbtanmean+"").getValue());
		toReturn.setSstanmean(c.getFieldbyName(HCAF_DFields.sstanmean+"").getValue());
		return toReturn;
	}
	
	
	
}

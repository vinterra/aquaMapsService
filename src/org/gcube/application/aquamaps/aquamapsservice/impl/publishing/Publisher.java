package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.File;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.LayerInfoType;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.WMSContextInfoType;

public interface Publisher {

	public int publishJob(Job toPublish)throws Exception;
	public Job getJobById(int id)throws Exception;
	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception;
	public int publishAquaMapsObject(AquaMapsObject toPublish) throws Exception;
	public void removeJob(int Id)throws Exception;
	public void removeAquaMapsObject(int id)throws Exception;
	
	public List<File> publishImages(int mapId,Set<String> speciesCoverage,Map<String,String> toPublishList,GCUBEScope scope,boolean hasCustomizations)throws Exception;
	
	public LayerInfoType getExistingLayer(
			Set<String> speciesCoverage, 
			int hcafId, 
			int hspenId, 
			Map<String,Map<String,Perturbation>> envelopeCustomization,
			Map<String,Map<EnvelopeFields,Field>> envelopeWeights,
			Set<Area> selectedAreas, BoundingBox bb,float threshold)throws Exception;
	
	public LayerInfoType publishNewLayer(Set<String> speciesCoverage,
			int hcafId, int hspenId,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			Set<Area> selectedAreas, BoundingBox bb,float threshold, String mapName,
			ArrayList<String> generatedStyles, int i) throws Exception;
	
	
	public WMSContextInfoType getExistingWMSContext(List<LayerInfoType> layers)throws Exception;
	
	public WMSContextInfoType publishNewWMSContext(String groupName, List<LayerInfoType> layers)throws Exception;
	
	public LayerInfoType getStandardSpeciesLayer(LayerType type)throws Exception;
	
	
//	public int publishWMSContext()throws Exception;
//	public int publishLayer()throws Exception;
//	public LayerInfoType getLayerById()throws Exception;
//	public WMSContextInfoType() throws Exception;
	
}

package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.LayerInfoType;
import org.gcube.application.aquamaps.dataModel.enhanced.*;
import org.gcube.application.aquamaps.dataModel.Types.*;
import org.gcube.application.aquamaps.dataModel.fields.*;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube_system.namespaces.application.aquamaps.aquamapspublisher.WMSContextInfoType;

public interface Publisher {

	//***************STORE - UPDATE
	
	public int publishJob(Job toPublish)throws Exception;
	public int publishAquaMapsObject(AquaMapsObject toPublish) throws Exception;
	public List<File> publishImages(int mapId,Set<String> speciesCoverage,Map<String,String> toPublishList,GCUBEScope scope,boolean hasCustomizations)throws Exception;
	public LayerInfoType publishBiodiversityLayer(Set<String> speciesCoverage,
			int hcafId,int hspenId, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,float threshold, List<String> styles, int defaultStyleIndex)throws Exception;
	public LayerInfoType publishDistributionLayer(String speciesId,
			int hcafId,int hspenId, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,List<String> styles, int defaultStyleIndex, boolean isNativeRange)throws Exception;
	public LayerInfoType publishOccurrenceLayer(String speciesId,List<String> styles, int defaultStyleIndex)throws Exception;	
	public LayerInfoType publishEnvironmentalLayer(EnvelopeFields parameter,List<String> styles, int defaultStyleIndex)throws Exception;
	public WMSContextInfoType publishWMSContext(String groupName, List<LayerInfoType> layers)throws Exception;	
	
	
	//********* GET
	
	public Job getJobById(int id)throws Exception;
	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception;
	
	public LayerInfoType getLayerById(int id)throws Exception;
	public LayerInfoType getBiodiversityLayer(Set<String> speciesCoverage,
			int hcafId,int hspenId, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,float threshold)throws Exception;
	public LayerInfoType getDistributionLayer(String speciesId,
			int hcafId,int hspenId, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,boolean getNative)throws Exception;
	public LayerInfoType getOccurrenceLayer(String speciesId)throws Exception;
	public LayerInfoType getEnvironmentalLayer(EnvelopeFields parameter,int hcafId)throws Exception;
	
	public WMSContextInfoType getWMSContextInfoTypeById(int id) throws Exception;
	public WMSContextInfoType getWMSContextInfoTypeById(List<Integer> layers)throws Exception;
	
	//********* REMOVE
	
	
	public void removeJob(int Id)throws Exception;
	public void removeAquaMapsObject(int id)throws Exception;
	
//	public void removeLayerInfoType(int id)throws Exception;
//	public void removeWMSContext(int id)throws Exception;
}

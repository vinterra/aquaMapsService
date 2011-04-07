package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.File;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;
import org.gcube.common.gis.dataModel.enhanced.WMSContextInfo;
import org.gcube.common.gis.dataModel.types.LayersType;

public interface Publisher {

	//***************STORE - UPDATE
	
	public int publishJob(Job toPublish)throws Exception;
	public boolean publishAquaMapsObject(AquaMapsObject toPublish) throws Exception;
	public boolean publishImages(int objectId,Map<String,String> toPublishfiles)throws Exception;
	
	
	public String publishLayer(int objId,LayersType type,
			List<String> list, int defaultStyleIndex, String title, String table)throws Exception;
	
	
	public String publishPointMapLayer(String speciesId,
			List<String> list, int defaultStyleIndex, String title, String table)throws Exception;
	
	public String publishEnvironmentalLayer(Resource hcaf,EnvelopeFields parameter,
			List<String> styles, int defaultStyleIndex, String title, String table)throws Exception;
	
	public String publishWMSContext(String groupName, List<LayerInfo> layers)throws Exception;	
	
	
	//********* GET
	
	public Job getJobById(int id)throws Exception;
	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception;
	
	public LayerInfo getLayerByIdAndType(String id,LayersType type)throws Exception;
	public LayerInfo getBiodiversityLayer(Set<String> speciesCoverage,
			Resource hcaf,Resource hspen, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,float threshold)throws Exception;
	public LayerInfo getDistributionLayer(String speciesId,
			Resource hcaf,Resource hspen, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds,boolean getNative)throws Exception;
	public LayerInfo getPointMapLayer(String speciesId)throws Exception;
	public LayerInfo getEnvironmentalLayer(EnvelopeFields parameter,Resource hcaf)throws Exception;
	
	public WMSContextInfo getWMSContextById(String id) throws Exception;
	
	//********* REMOVE
	
	
	public void removeJob(int Id)throws Exception;
	public void removeAquaMapsObject(int id)throws Exception;
	
	public void removeLayer(String id)throws Exception;
	public void removeWMSContext(String id)throws Exception;
}

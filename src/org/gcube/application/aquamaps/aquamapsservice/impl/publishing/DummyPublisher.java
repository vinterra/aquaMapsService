package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Perturbation;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.fields.EnvelopeFields;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;
import org.gcube.common.gis.dataModel.enhanced.WMSContextInfo;
import org.gcube.common.gis.dataModel.types.LayersType;

public class DummyPublisher implements Publisher {

	private static GCUBELog logger= new GCUBELog(ConnectedPublisher.class);
	
	
	@Override
	public Job publishJob(Job toPublish) throws Exception {
		logger.trace("publishJob : return job ");
		toPublish.setStatus(SubmittedStatus.Pending);
		for(AquaMapsObject obj : toPublish.getAquaMapsObjectList())
			obj.setStatus(SubmittedStatus.Pending);
		return toPublish;
	}

	@Override
	public boolean publishAquaMapsObject(AquaMapsObject toPublish)
			throws Exception {
		logger.trace("publish Obj : return true");
		return true;
	}

	@Override
	public boolean publishImages(int objectId,
			Map<String, String> toPublishfiles) throws Exception {
		logger.trace("publishImages return true");
		return true;
	}

	@Override
	public String publishLayer(int objId, LayersType type, List<String> list,
			int defaultStyleIndex, String title, String table) throws Exception {
		logger.trace("publish layer return ...");
		return "...";
	}

	@Override
	public String publishPointMapLayer(String speciesId, List<String> list,
			int defaultStyleIndex, String title, String table) throws Exception {
		logger.trace("publish point map return ...");
		return "....";
	}

	@Override
	public String publishEnvironmentalLayer(Resource hcaf,
			EnvelopeFields parameter, List<String> styles,
			int defaultStyleIndex, String title, String table) throws Exception {
		logger.trace("publish environmental return ...");
		return "....";
	}

	@Override
	public String publishWMSContext(String groupName, List<LayerInfo> layers)
			throws Exception {
		logger.trace("publish wms return  ...");
		return "....";
	}

	@Override
	public Job getJobById(int id) throws Exception {
		logger.trace("getJobById return  null");
		return null;
	}

	@Override
	public AquaMapsObject getAquaMapsObjectById(int id) throws Exception {
		logger.trace("getObjectById return  null");
		return null;
	}

	@Override
	public LayerInfo getLayerByIdAndType(String id, LayersType type)
			throws Exception {
		logger.trace("getLayerById return  null");
		return null;
	}

	@Override
	public LayerInfo getBiodiversityLayer(Set<String> speciesCoverage,
			Resource hcaf, Resource hspen, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds, float threshold) throws Exception {
		logger.trace("getBiodivLayerById return  null");
		return null;
	}

	@Override
	public LayerInfo getDistributionLayer(String speciesId, Resource hcaf,
			Resource hspen, Set<Area> areaSelection,
			Map<String, Map<String, Perturbation>> envelopeCustomization,
			Map<String, Map<EnvelopeFields, Field>> envelopeWeights,
			BoundingBox bounds, AlgorithmType algorithm) throws Exception {
		logger.trace("getDistrLayerById return  null");
		return null;
	}

	@Override
	public LayerInfo getPointMapLayer(String speciesId) throws Exception {
		logger.trace("getPoinMapById return  null");
		return null;
	}

	@Override
	public LayerInfo getEnvironmentalLayer(EnvelopeFields parameter,
			Resource hcaf) throws Exception {
		logger.trace("getEnvironById return  null");
		return null;
	}

	@Override
	public WMSContextInfo getWMSContextById(String id) throws Exception {
		logger.trace("getWMSById return  null");
		return null;
	}

	@Override
	public void removeJob(int Id) throws Exception {
		logger.trace("removeJob..");
	}

	@Override
	public void removeAquaMapsObject(int id) throws Exception {
		logger.trace("removeObj..");
	}

	@Override
	public void removeLayer(String id) throws Exception {
		logger.trace("removeLayer..");
	}

	@Override
	public void removeWMSContext(String id) throws Exception {
		logger.trace("removeWMS..");
	}

	@Override
	public LayerInfo getLayerById(String id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

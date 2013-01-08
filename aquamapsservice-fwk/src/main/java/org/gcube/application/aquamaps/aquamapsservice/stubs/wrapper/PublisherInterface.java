package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMap;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.gcube.common.gis.datamodel.enhanced.LayerInfo;

public interface PublisherInterface {

//	public Species getStaticInfo(String speciesId)throws Exception;
//	public List<EnvelopeDescriptor> getEnvelopes(EnvelopeFilter filter)throws Exception;
//	public List<Submitted> getSubmittedObjects(SubmittedFilter filter)throws Exception;
//	public List<OccurrenceDescriptor> getOccurrenceReferences(OccurrenceFilter filter)throws Exception;
//	public List<File> getFiles(FileFilter filter)throws Exception;
//	public List<Layer> getLayers(LayerFilter filter)throws Exception;
	
	
	public List<AquaMap> getMapsBySpecies(String[] speciesId,boolean includeGis, boolean includeCustom, List<Resource> resources)throws Exception;
	public String getJsonSubmittedByFilters(List<Field> filters, PagedRequestSettings settings)throws Exception;
	public List<File> getFileSetById(String fileSetId)throws Exception;
	public LayerInfo getLayerById(String layerId)throws Exception;
	public List<LayerInfo> getLayersByCoverage(Resource source,String parameters)throws Exception;
	public List<File> getFileSetsByCoverage(Resource source,String parameters)throws Exception;
}

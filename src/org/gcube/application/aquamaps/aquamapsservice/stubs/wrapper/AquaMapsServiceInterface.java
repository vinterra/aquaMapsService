package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Area;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Filter;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;

public interface AquaMapsServiceInterface {

	
	public Envelope calculateEnvelope(BoundingBox bb,List<Area> areas,String speciesId,boolean useBottom, boolean useBounding, boolean useFAO) throws Exception;

	public Envelope calculateEnvelopeFromCellSelection(List<String> cellIds,String speciesId)throws Exception;

	public int deleteSubmitted(List<Integer> ids)throws Exception;

	public String getJSONSubmitted(String userName,boolean showObjects,String date,Integer jobId,SubmittedStatus status,ObjectType objType, PagedRequestSettings settings)throws Exception;

	public String getJSONOccurrenceCells(String speciesId, PagedRequestSettings settings)throws Exception;

	public String getJSONPhilogeny(SpeciesOccursumFields level, ArrayList<Field> filters, PagedRequestSettings settings)throws Exception;

	/**wraps getProfile
	 * 
	 * @return
	 * @throws Exception
	 */
	public AquaMapsObject loadObject(int objectId)throws Exception;

	public Resource loadResource(int resId)throws Exception;

	public String getJSONResources(PagedRequestSettings settings, List<Field> filter)throws Exception;

	public String getJSONSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception;

	public File getCSVSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes)throws Exception;
	
	public Species loadEnvelope(String speciesId, int hspenId)throws Exception;

	public void markSaved(List<Integer> submittedIds)throws Exception;

	public void submitJob(Job toSubmit) throws Exception;

	public Submitted loadSubmittedById(int id)throws Exception;
}

	
	

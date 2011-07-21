package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Envelope;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Filter;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;

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

	public Resource loadResource(int resId,ResourceType type)throws Exception;

	public String getJSONResources(PagedRequestSettings settings, ResourceType type)throws Exception;

	public String getJSONSpecies(int hspenId, List<Field> characteristcs, List<Filter> names, List<Filter> codes, PagedRequestSettings settings)throws Exception;

	public Species loadEnvelope(String speciesId, int hspenId)throws Exception;

	public void markSaved(List<Integer> submittedIds)throws Exception;

	public void submitJob(Job toSubmit) throws Exception;

	public Submitted loadSubmittedById(int id)throws Exception;
}

	
	

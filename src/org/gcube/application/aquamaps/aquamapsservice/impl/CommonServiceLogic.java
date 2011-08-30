package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.common.core.utils.logging.GCUBELog;

public class CommonServiceLogic {

	private static final GCUBELog logger=new GCUBELog(CommonServiceLogic.class);
	
	
	public static int generateMaps_Logic(int hspecId,List<Field> speciesFilter, String author, boolean enableGIS)throws Exception{
		logger.trace("Gnerating job for maps generation :");
		logger.trace("HSPEC id :" +hspecId);
		
		if(speciesFilter.size()==0){
			Submitted existing=getAlreadySubmitted(hspecId, author, enableGIS);
			if(existing!=null) {
				logger.trace("Found existing job "+existing.getSearchId()+", submitted by "+existing.getAuthor());
				return existing.getSearchId();
			}
		}
		
		Job job=new Job();
		Resource hspec=SourceManager.getById(hspecId);
		job.setSourceHSPEC(hspec);
		job.setSourceHCAF(SourceManager.getById(hspec.getSourceHCAFId()));
		job.setSourceHSPEN(SourceManager.getById(hspec.getSourceHSPENId()));
		job.addSpecies(SpeciesManager.getList(speciesFilter,job.getSourceHSPEN()));
		if(job.getSelectedSpecies().size()==0) throw new Exception("NO SPECIES SELECTED");
		
		
		logger.trace("loaded "+job.getSelectedSpecies().size()+" species..");
		job.setAuthor(author);
		logger.debug("HSPEC is "+hspec.toXML());
		job.setIsGis(true);
		job.setName(hspec.getTitle()+"_All Maps");
		for(Species s: job.getSelectedSpecies()){
			AquaMapsObject object=new AquaMapsObject(s.getFieldbyName(SpeciesOccursumFields.scientific_name+"").getValue(), 0, ObjectType.SpeciesDistribution);
			if(object.getName()==null||object.getName().equals(Field.VOID))
				object.setName(s.getFieldbyName(SpeciesOccursumFields.genus+"").getValue()+"_"+s.getFieldbyName(SpeciesOccursumFields.species+"").getValue());
			
			object.setAuthor(job.getAuthor());
			object.getSelectedSpecies().add(s);
			object.setGis(enableGIS);
			job.getAquaMapsObjectList().add(object);
		}
		
		
		logger.trace("Submiting job "+job.getName());
		
		return JobExecutionManager.insertJobExecutionRequest(job,true);
		
	}

	
	private static Submitted getAlreadySubmitted(int hspecId, String author, boolean GIS)throws Exception{
		logger.trace("Looking for submitted job for hspecID : "+hspecId+", GIS :"+GIS);
		ArrayList<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SubmittedFields.sourcehspec+"",hspecId+"",FieldType.INTEGER));
		filter.add(new Field(SubmittedFields.postponepublishing+"",true+"",FieldType.BOOLEAN));
		filter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));		
		if(GIS) filter.add(new Field(SubmittedFields.gisenabled+"",true+"",FieldType.BOOLEAN));
		List<Submitted> existing=SubmittedManager.getList(filter);
		for(Submitted job:existing)
			if(!job.getStatus().equals(SubmittedStatus.Error))return existing.get(0);
		return null;
	}	
}

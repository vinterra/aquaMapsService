package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.maps.JobExecutionManager;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.common.core.utils.logging.GCUBELog;

public class CommonServiceLogic {

	private static final GCUBELog logger=new GCUBELog(CommonServiceLogic.class);
	
	
	public static int generateMaps_Logic(int hspecId,List<Field> speciesFilter, String author, boolean enableGIS, boolean forceRegeneration)throws Exception{
		logger.trace("Gnerating job for maps generation :");
		logger.trace("HSPEC id :" +hspecId);
		Job job=new Job();
		Resource hspec=SourceManager.getById(hspecId);
		job.setSourceHSPEC(hspec);
		job.setSourceHCAF(SourceManager.getById(hspec.getSourceHCAFIds().get(0)));
		job.setSourceHSPEN(SourceManager.getById(hspec.getSourceHSPENIds().get(0)));
		job.addSpecies(SpeciesManager.getList(speciesFilter,job.getSourceHSPEN()));
		
		if(speciesFilter.size()==0&&!forceRegeneration){
			Submitted existing=getAlreadySubmitted(hspecId, enableGIS,job.getCompressedCoverage());
			if(existing!=null) {
				logger.trace("Found existing job "+existing.getSearchId()+", submitted by "+existing.getAuthor());
				return existing.getSearchId();
			}
		}
		
		if(job.getSelectedSpecies().size()==0) throw new Exception("NO SPECIES SELECTED");
		
		
		logger.trace("loaded "+job.getSelectedSpecies().size()+" species..");
		job.setAuthor(author);
		logger.debug("HSPEC is "+hspec.toXML());
		job.setIsGis(false);
		job.setName(hspec.getTitle()+"_All Maps");
		for(Species s: job.getSelectedSpecies()){
			AquaMapsObject object=new AquaMapsObject(s.getFieldbyName(SpeciesOccursumFields.scientific_name+"").getValue(), 0, ObjectType.SpeciesDistribution);
			if(object.getName()==null||object.getName().equals(Field.VOID))
				object.setName(s.getFieldbyName(SpeciesOccursumFields.genus+"").getValue()+"_"+s.getFieldbyName(SpeciesOccursumFields.species+"").getValue());
			
			object.setAuthor(job.getAuthor());
			object.getSelectedSpecies().add(s);
			object.setGis(enableGIS);
			object.setAlgorithmType(job.getSourceHSPEC().getAlgorithm());
			job.getAquaMapsObjectList().add(object);
		}
		
		
		logger.trace("Submiting job "+job.getName());
		
		return JobExecutionManager.insertJobExecutionRequest(job,forceRegeneration);
		
	}

	
	private static Submitted getAlreadySubmitted(int hspecId, boolean GIS,String speciesCoverage)throws Exception{
		logger.trace("Looking for submitted job for hspecID : "+hspecId+", GIS :"+GIS);
		ArrayList<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SubmittedFields.sourcehspec+"",hspecId+"",FieldType.INTEGER));		
		filter.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));		
		if(GIS) filter.add(new Field(SubmittedFields.gisenabled+"",GIS+"",FieldType.BOOLEAN));
		filter.add(new Field(SubmittedFields.iscustomized+"",false+"",FieldType.BOOLEAN));
		filter.add(new Field(SubmittedFields.speciescoverage+"",speciesCoverage,FieldType.STRING));
		List<Submitted> existing=SubmittedManager.getList(filter);
		for(Submitted job:existing)
			if(!job.getStatus().equals(SubmittedStatus.Error))return job;
		return null;
	}	
}

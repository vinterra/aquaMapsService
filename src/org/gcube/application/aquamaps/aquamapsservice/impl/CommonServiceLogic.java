package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SourceManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SpeciesManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobSubmissionThread;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Resource;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.core.utils.logging.GCUBELog;

public class CommonServiceLogic {

	private static final GCUBELog logger=new GCUBELog(CommonServiceLogic.class);
	
	
	public static int generateMaps_Logic(int hspecId,List<Field> speciesFilter, String author, boolean enableGIS)throws Exception{
		logger.trace("Gnerating job for maps generation :");
		logger.trace("HSPEC id :" +hspecId);
		
		Job job=new Job();
		Resource hspec=SourceManager.getById(ResourceType.HSPEC, hspecId);
		job.setSourceHSPEC(hspec);
		job.setSourceHCAF(SourceManager.getById(ResourceType.HCAF, hspec.getSourceHCAFId()));
		job.setSourceHSPEN(SourceManager.getById(ResourceType.HSPEN, hspec.getSourceHSPENId()));
		job.addSpecies(SpeciesManager.getList(speciesFilter,job.getSourceHSPEN()));
		logger.trace("loaded "+job.getSelectedSpecies().size()+" species..");
		job.setAuthor(author);
		logger.trace("HSPEC is "+AquaMapsXStream.getXMLInstance().toXML(hspec));
		job.setIsGis(false);
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
		JobSubmissionThread thread=new JobSubmissionThread(job);
		ServiceContext.getContext().setScope(thread,ServiceContext.getContext().getStartScopes());
		ThreadManager.getExecutor().execute(thread);
		return (int) thread.getId();
	}

}

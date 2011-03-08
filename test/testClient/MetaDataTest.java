package testClient;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.File;
import org.gcube.application.aquamaps.stubs.dataModel.Filter;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Perturbation;
import org.gcube.application.aquamaps.stubs.dataModel.Resource;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FileType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FilterType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.PerturbationType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.EnvelopeFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HspenFields;
import org.gcube.application.aquamaps.stubs.dataModel.util.MetaDataHandler;

public class MetaDataTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		Species spec = new Species("WBD-Eup-85");
//		System.out.println(MetaDataHandler.get().toXML(spec));
//		Area area=new Area(AreaType.FAO,"34");
//		area.setName("DummyArea");
//		System.out.println(MetaDataHandler.get().toXML(area));
//		System.out.println(MetaDataHandler.get().toXML(new BoundingBox()));
//		System.out.println(MetaDataHandler.get().toXML(new Cell("100:1000:221487")));
//		Field f= new Field(HspenFields.DepthMax+"", "10", FieldType.DOUBLE);
//		System.out.println(MetaDataHandler.get().toXML(new Filter(FilterType.begins, f)));
//		System.out.println(MetaDataHandler.get().toXML(new Perturbation(PerturbationType.ASSIGN, "12")));
//		System.out.println(MetaDataHandler.get().toXML(new Resource(ResourceType.HCAF, 1)));
//		AquaMapsObject distr=new AquaMapsObject("MetaTest", 21, ObjectType.SpeciesDistribution);
//		distr.getSelectedSpecies().add(spec);
//		distr.setAuthor("Tester");
//		distr.setDate("12-21-27");
//		distr.getRelatedResources().add(new File(FileType.ExternalMeta, "here", "Meta"));
//		System.out.println(MetaDataHandler.get().toXML(distr));
//		AquaMapsObject biodiv=new AquaMapsObject("MetaBiodivTest", 22, ObjectType.Biodiversity);
//		biodiv.getSelectedSpecies().add(spec);
//		biodiv.getSelectedSpecies().add(new Species("FWBD-Eup-82"));
//		biodiv.setAuthor("Tester");
//		biodiv.setDate("12-21-27");
//		biodiv.getRelatedResources().add(new File(FileType.ExternalMeta, "here", "Meta"));
//		Job job = new Job();
//		job.getAquaMapsObjectList().add(biodiv);
//		job.getAquaMapsObjectList().add(distr);
//		job.setAuthor("Tester");
//		job.setDate("12-21-27");
//		job.addSpecies(biodiv.getSelectedSpecies());
//		job.getSelectedAreas().add(area);
//		job.setCustomization(spec, f, new Perturbation(PerturbationType.ASSIGN, "12"));
//		job.setId(20);
//		job.setName("TestJob");
//		job.getRelated().add(new File(FileType.ExternalMeta, "here", "Meta"));
//		job.setSourceHCAF(new Resource(ResourceType.HCAF, 1));
//		job.setSourceHSPEN(new Resource(ResourceType.HSPEN, 1));
//		job.setSourceHSPEC(new Resource(ResourceType.HSPEC, 1));
//		List<Field> weights=new ArrayList<Field>(); 
//		for(EnvelopeFields envF:EnvelopeFields.values())
//			weights.add(new Field(envF+"", "true", FieldType.BOOLEAN));
//		job.setWeights(spec,weights);
//		
//		
		
		
	}

	
	
}

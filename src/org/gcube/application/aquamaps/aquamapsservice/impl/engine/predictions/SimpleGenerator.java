package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions.utils.ModelTranslation;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.dataModel.fields.HSPECFields;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.BoundingBoxInformation;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.DistributionGeneratorInterface;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.GenerationModel;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hcaf;
import org.gcube.application.aquamaps.ecomodelling.generators.connectors.Hspen;

public class SimpleGenerator implements SimpleGeneratorI {

	private DistributionGeneratorInterface generator;
	
	
	public SimpleGenerator(String path) {
		generator= new DistributionGeneratorInterface(GenerationModel.AQUAMAPS, path);
	}
	
	
	
	@Override
	public List<Field> getProbability(Species species, Cell cell, Boolean useBoundingBox, Boolean useFao)
			throws Exception {
		
		Hcaf hcaf=ModelTranslation.Cell2Hcaf(cell);
		Hspen hspen=ModelTranslation.species2HSPEN(species);
		BoundingBoxInformation bb=generator.getBoudingBox(hcaf, hspen, false);
		List<Field> row=null;
		if((useBoundingBox==bb.isInBoundingBox())&&(useFao==bb.isInFaoArea())){
			row=new ArrayList<Field>();
			row.add(new Field(SpeciesOccursumFields.speciesid+"",species.getId(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.csquarecode+"",cell.getCode(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.faoaream+"",cell.getFieldbyName(HCAF_SFields.faoaream+"").getValue(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.eezall+"",cell.getFieldbyName(HCAF_SFields.eezall+"").getValue(),FieldType.STRING));
			row.add(new Field(HCAF_SFields.lme+"",cell.getFieldbyName(HCAF_SFields.lme+"").getValue(),FieldType.STRING));
			row.add(new Field(HSPECFields.boundboxyn+"",bb.isInBoundingBox()+"",FieldType.BOOLEAN));
			row.add(new Field(HSPECFields.faoareayn+"",bb.isInFaoArea()+"",FieldType.BOOLEAN));
			row.add(new Field(HSPECFields.probability+"",generator.computeProbability(hcaf, hspen)+"",FieldType.DOUBLE));
		}
		return row;
	}

	@Override
	public List<Field> isAreaConstraints(BoundingBox bb, List<Area> areas)
			throws Exception {
		// TODO Auto-generated method stub
		throw new Exception ("NOT YET IMPLEMENTED");
	}

	
	
	
}

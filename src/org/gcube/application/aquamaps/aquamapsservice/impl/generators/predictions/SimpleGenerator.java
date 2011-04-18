package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;

public class SimpleGenerator implements SimpleGeneratorI {

	
	public SimpleGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public List<Double> getProbabilities(Set<Species> species, Set<Cell> cells)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Field> isAreaConstraints(BoundingBox bb, List<Area> areas)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

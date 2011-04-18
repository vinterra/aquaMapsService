package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;

public interface SimpleGeneratorI {
		
	public List<Double> getProbabilities(Set<Species> species,Set<Cell> cells)throws Exception;
 	public List<Field>  isAreaConstraints(BoundingBox bb,List<Area> areas)throws Exception; 	
	
 	
 	
}

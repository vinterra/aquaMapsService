package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import java.util.List;

import org.gcube.application.aquamaps.dataModel.enhanced.Area;
import org.gcube.application.aquamaps.dataModel.enhanced.BoundingBox;
import org.gcube.application.aquamaps.dataModel.enhanced.Cell;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;

public interface SimpleGeneratorI {
		
	
	/**
	 * return a list of fields representing an HSPEC row
	 * @param species
	 * @param cells
	 * @return
	 * @throws Exception
	 */
	
	public List<Field> getProbability(Species species,Cell cells,Boolean useBoundingBox, Boolean useFao)throws Exception;
 	public List<Field>  isAreaConstraints(BoundingBox bb,List<Area> areas)throws Exception; 	
	
 	
 	
}

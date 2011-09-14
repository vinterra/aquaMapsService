package org.gcube.application.aquamaps.aquamapsservice.impl.engine.predictions;

import java.util.List;
import java.util.Set;

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
	
 	public List<Field> getEnvelope(Species species,Set<Cell> cells)throws Exception;
 	
}

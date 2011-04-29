package org.gcube.application.aquamaps.aquamapsservice.impl.generators.predictions;

import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;

public interface BatchGeneratorI {

	public String generateHSPECTable(String hcaf, String hspen, AlgorithmType type, boolean is2050)throws Exception;
	
	
}

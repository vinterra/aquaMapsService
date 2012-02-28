package org.gcube.application.aquamaps.aquamapsservice.impl.engine.analysis;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AnalysisType;

public class AnalysisRequest {

	private AnalysisType type;
	private String[] hcafTables;
	private String[] hspecTables;
	public AnalysisRequest(AnalysisType type, String[] hcafTables,
			String[] hspecTables) {
		super();
		this.type = type;
		this.hcafTables = hcafTables;
		this.hspecTables = hspecTables;
	}
	
	public String[] getHcafTables() {
		return hcafTables;
	}
	public String[] getHspecTables() {
		return hspecTables;
	}
	public AnalysisType getType() {
		return type;
	}
}

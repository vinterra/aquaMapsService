package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;

class Execution{
	private AlgorithmType algorithm;
	private Integer hcafId;
	private Integer hspenId;
	private Integer occurrenceCellsId;
	private LogicType logic;
	
	public Integer getOccurrenceCellsId() {
		return occurrenceCellsId;
	}
	public LogicType getLogic() {
		return logic;
	}
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	public Integer getHcafId() {
		return hcafId;
	}
	public Integer getHspenId() {
		return hspenId;
	}
	public Execution(AlgorithmType algorithm, Integer hcafId, Integer hspenId,
			Integer occurrenceCellsId, LogicType logic) {
		super();
		this.algorithm = algorithm;
		this.hcafId = hcafId;
		this.hspenId = hspenId;
		this.occurrenceCellsId = occurrenceCellsId;
		this.logic = logic;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((hcafId == null) ? 0 : hcafId.hashCode());
		result = prime * result + ((hspenId == null) ? 0 : hspenId.hashCode());
		result = prime * result + ((logic == null) ? 0 : logic.hashCode());
		result = prime
				* result
				+ ((occurrenceCellsId == null) ? 0 : occurrenceCellsId
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Execution other = (Execution) obj;
		if (algorithm != other.algorithm)
			return false;
		if (hcafId == null) {
			if (other.hcafId != null)
				return false;
		} else if (!hcafId.equals(other.hcafId))
			return false;
		if (hspenId == null) {
			if (other.hspenId != null)
				return false;
		} else if (!hspenId.equals(other.hspenId))
			return false;
		if (logic != other.logic)
			return false;
		if (occurrenceCellsId == null) {
			if (other.occurrenceCellsId != null)
				return false;
		} else if (!occurrenceCellsId.equals(other.occurrenceCellsId))
			return false;
		return true;
	}
	
	
}

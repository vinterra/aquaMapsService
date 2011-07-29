package org.gcube.application.aquamaps.aquamapsservice.impl.engine.tables;

import org.gcube.application.aquamaps.dataModel.Types.AlgorithmType;

class Execution{
	private AlgorithmType algorithm;
	private Integer hcafId;
	private Integer hspenId;
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	public Integer getHcafId() {
		return hcafId;
	}
	public Integer getHspenId() {
		return hspenId;
	}
	public Execution(AlgorithmType algorithm, Integer hcafId,
			Integer hspenId) {
		super();
		this.algorithm = algorithm;
		this.hcafId = hcafId;
		this.hspenId = hspenId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((hcafId == null) ? 0 : hcafId.hashCode());
		result = prime * result + ((hspenId == null) ? 0 : hspenId.hashCode());
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
		return true;
	}
	
}

package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

public class PagedRequestSettings {

	private int limit;
	private int offset;
	private String orderColumn;
	private String orderDirection;
	
	public PagedRequestSettings(int limit,int offset, String orderColumn, String orderDirection){
		this.limit=limit;
		this.offset=offset;
		this.orderColumn=orderColumn;
		this.orderDirection=orderDirection;
	}
	
	
	
	public int getLimit() {
		return limit;
	}
	public int getOffset() {
		return offset;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public String getOrderDirection() {
		return orderDirection;
	}
	
	
	
}

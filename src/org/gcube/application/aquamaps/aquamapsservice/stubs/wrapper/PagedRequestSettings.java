package org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper;

public class PagedRequestSettings {

	public enum OrderDirection{
		ASC,DESC
	}
	
	
	private int pageSize;
	private int offset;
	private String orderColumn;
	private OrderDirection orderDirection;
	
	public PagedRequestSettings(int pageSize,int offset, String orderColumn, OrderDirection orderDirection){
		this.pageSize=pageSize;
		this.offset=offset;
		this.orderColumn=orderColumn;
		this.orderDirection=orderDirection;
	}
	
	
	/**
	 * return row limit as offset+pageSize 
	 * 
	 * @return
	 */
	public int getLimit() {
		return pageSize+offset;
	}
	public int getOffset() {
		return offset;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public OrderDirection getOrderDirection() {
		return orderDirection;
	}
	public int getPageSize(){return pageSize;}
	
	
}

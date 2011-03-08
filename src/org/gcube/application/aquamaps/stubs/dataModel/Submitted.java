package org.gcube.application.aquamaps.stubs.dataModel;

import java.sql.ResultSet;
import java.util.ArrayList;

import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;

public class Submitted {

	
	private Integer searchId;
	private String title;
	private String author;
	private Integer jobId;
	private String selectionCriteria;
	private String date;
	private SubmittedStatus status=SubmittedStatus.Pending;
	private ObjectType type;
	private Boolean isAquaMap;
	private Boolean saved;
	private Integer sourceHCAF;
	private Integer sourceHSPEN;
	private Integer sourceHSPEC;
	private String gis;
	private Integer mapId;
	
	@Deprecated
	public Submitted(Integer searchId){
		this.setSearchId(searchId);
	}
	
	public Integer getSearchId() {
		return searchId;
	}
	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public String getSelectionCriteria() {
		return selectionCriteria;
	}
	public void setSelectionCriteria(String selectionCriteria) {
		this.selectionCriteria = selectionCriteria;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public SubmittedStatus getStatus() {
		return status;
	}
	public void setStatus(SubmittedStatus status) {
		this.status = status;
	}
	public ObjectType getType() {
		return type;
	}
	public void setType(ObjectType type) {
		this.type = type;
	}
	public Boolean getIsAquaMap() {
		return isAquaMap;
	}
	public void setIsAquaMap(Boolean isAquaMap) {
		this.isAquaMap = isAquaMap;
	}
	public Boolean getSaved() {
		return saved;
	}
	public void setSaved(Boolean saved) {
		this.saved = saved;
	}
	public Integer getSourceHCAF() {
		return sourceHCAF;
	}
	public void setSourceHCAF(Integer sourceHCAF) {
		this.sourceHCAF = sourceHCAF;
	}
	public Integer getSourceHSPEN() {
		return sourceHSPEN;
	}
	public void setSourceHSPEN(Integer sourceHSPEN) {
		this.sourceHSPEN = sourceHSPEN;
	}
	public Integer getSourceHSPEC() {
		return sourceHSPEC;
	}
	public void setSourceHSPEC(Integer sourceHSPEC) {
		this.sourceHSPEC = sourceHSPEC;
	}
	public String getGis() {
		return gis;
	}
	public void setGis(String gis) {
		this.gis = gis;
	}
	public Integer getMapId() {
		return mapId;
	}
	public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}
	
	public static ArrayList<Submitted> loadResultSet(ResultSet rs)throws Exception{
		ArrayList<Submitted> toReturn=new ArrayList<Submitted>();
		while(rs.next()){
			Submitted obj=new Submitted();
			obj.setAuthor(rs.getString(SubmittedFields.author.toString()));
			obj.setDate(rs.getString(SubmittedFields.date.toString()));
			obj.setGis(rs.getString(SubmittedFields.gis.toString()));
			obj.setIsAquaMap(rs.getBoolean(SubmittedFields.isAquaMap.toString()));
			obj.setJobId(rs.getInt(SubmittedFields.jobid.toString()));
			obj.setMapId(rs.getInt(SubmittedFields.mapId.toString()));
			obj.setSaved(rs.getBoolean(SubmittedFields.saved.toString()));
			obj.setSearchId(rs.getInt(SubmittedFields.searchId.toString()));
			obj.setSelectionCriteria(rs.getString(SubmittedFields.selectionCriteria.toString()));
			obj.setSourceHCAF(rs.getInt(SubmittedFields.sourceHCAF.toString()));
			obj.setSourceHSPEC(rs.getInt(SubmittedFields.sourceHSPEC.toString()));
			obj.setSourceHSPEN(rs.getInt(SubmittedFields.sourceHCAF.toString()));
			obj.setStatus(SubmittedStatus.valueOf(rs.getString(SubmittedFields.status.toString())));
			obj.setTitle(rs.getString(SubmittedFields.title.toString()));
			if(obj.isAquaMap)
				obj.setType(ObjectType.valueOf(rs.getString(SubmittedFields.type.toString())));
			toReturn.add(obj);
		}
		return toReturn;
	}
	
	
	public Submitted (org.gcube.application.aquamaps.stubs.Submitted toLoad){
		super();
		this.author=toLoad.getAuthor();
		this.date=toLoad.getDate();
		this.gis=toLoad.getGis();
		this.isAquaMap=toLoad.isIsAquaMap();
		this.jobId=toLoad.getJobId();
		this.mapId=toLoad.getMapId();
		this.saved=toLoad.isSaved();
		this.searchId=toLoad.getSearchId();
		this.selectionCriteria=toLoad.getSelectionCriteria();
		this.sourceHCAF=toLoad.getSourceHCAF();
		this.sourceHSPEC=toLoad.getSourceHSPEC();
		this.sourceHSPEN=toLoad.getSourceHSPEN();
		this.status=SubmittedStatus.valueOf(toLoad.getStatus());
		this.title=toLoad.getTitle();
		this.type=((toLoad.getType()!=null)&&(!toLoad.getType().equalsIgnoreCase("null")))?ObjectType.valueOf(toLoad.getType()):null;
	}
	
	public org.gcube.application.aquamaps.stubs.Submitted toStubsVersion(){
		org.gcube.application.aquamaps.stubs.Submitted toReturn=new org.gcube.application.aquamaps.stubs.Submitted();
		toReturn.setAuthor(author);
		toReturn.setDate(date);
		toReturn.setGis(gis);
		toReturn.setIsAquaMap(isAquaMap);
		toReturn.setJobId(jobId);
		toReturn.setMapId(mapId);
		toReturn.setSaved(saved);
		toReturn.setSearchId(searchId);
		toReturn.setSelectionCriteria(selectionCriteria);
		toReturn.setSourceHCAF(sourceHCAF);
		toReturn.setSourceHSPEC(sourceHSPEC);
		toReturn.setSourceHSPEN(sourceHSPEN);
		toReturn.setStatus(status+"");
		toReturn.setTitle(title);
		toReturn.setType(type+"");
		return toReturn;		
	}
	
	private Submitted(){};
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((searchId == null) ? 0 : searchId.hashCode());
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
		Submitted other = (Submitted) obj;
		if (searchId == null) {
			if (other.searchId != null)
				return false;
		} else if (!searchId.equals(other.searchId))
			return false;
		return true;
	}
	
	
}

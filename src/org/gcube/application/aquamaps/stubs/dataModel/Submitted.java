package org.gcube.application.aquamaps.stubs.dataModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;
import org.gcube.application.aquamaps.stubs.wrapper.WrapperUtils;

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
	private Boolean gisEnabled;
	private List<String> gisPublishedId=new ArrayList<String>();
	private List<String> gisReferences=new ArrayList<String>();
	
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
	
	public static ArrayList<Submitted> loadResultSet(ResultSet rs)throws Exception{
		ArrayList<Submitted> toReturn=new ArrayList<Submitted>();
		while(rs.next()){
			Submitted obj=new Submitted();
			obj.setAuthor(rs.getString(SubmittedFields.author.toString()));
			obj.setDate(rs.getString(SubmittedFields.date.toString()));
			obj.setGisEnabled((rs.getInt(SubmittedFields.gisenabled+"")==1));
			obj.setIsAquaMap(rs.getBoolean(SubmittedFields.isaquamap.toString()));
			obj.setJobId(rs.getInt(SubmittedFields.jobid.toString()));
			obj.setSaved(rs.getBoolean(SubmittedFields.saved.toString()));
			obj.setSearchId(rs.getInt(SubmittedFields.searchid.toString()));
			obj.setSelectionCriteria(rs.getString(SubmittedFields.selectioncriteria.toString()));
			obj.setSourceHCAF(rs.getInt(SubmittedFields.sourcehcaf.toString()));
			obj.setSourceHSPEC(rs.getInt(SubmittedFields.sourcehspec.toString()));
			obj.setSourceHSPEN(rs.getInt(SubmittedFields.sourcehcaf.toString()));
			obj.setStatus(SubmittedStatus.valueOf(rs.getString(SubmittedFields.status.toString())));
			obj.setTitle(rs.getString(SubmittedFields.title.toString()));
			obj.setType(ObjectType.valueOf(rs.getString(SubmittedFields.type.toString())));
			obj.setGisPublishedId(WrapperUtils.CSVToList(rs.getString(SubmittedFields.gispublishedid+"")));
			obj.setGisReferences(WrapperUtils.CSVToList(rs.getString(SubmittedFields.geoserverreference+"")));
			toReturn.add(obj);
		}
		return toReturn;
	}
	
	
	public Submitted (org.gcube.application.aquamaps.stubs.Submitted toLoad){
		super();
		this.author=toLoad.getAuthor();
		this.date=toLoad.getDate();
		//FIXME
//		this.gis=toLoad.getGis();
		this.isAquaMap=toLoad.isIsAquaMap();
		this.jobId=toLoad.getJobId();
//		this.mapId=toLoad.getMapId();
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
		//FIXME
//		toReturn.setGis(gis);
		toReturn.setIsAquaMap(isAquaMap);
		toReturn.setJobId(jobId);
//		toReturn.setMapId(mapId);
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

	public void setGisEnabled(Boolean gisEnabled) {
		this.gisEnabled = gisEnabled;
	}

	public Boolean getGisEnabled() {
		return gisEnabled;
	}

	public void setGisPublishedId(List<String> gisPublishedId) {
		this.gisPublishedId = gisPublishedId;
	}

	public List<String> getGisPublishedId() {
		return gisPublishedId;
	}

	public void setGisReferences(List<String> gisReferences) {
		this.gisReferences = gisReferences;
	}

	public List<String> getGisReferences() {
		return gisReferences;
	}
	
	
}

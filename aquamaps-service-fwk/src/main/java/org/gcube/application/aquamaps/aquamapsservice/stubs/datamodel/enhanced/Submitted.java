package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ObjectType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;

public class Submitted extends DataModel{


	private Integer searchId;
	private String title;
	private String author;
	private Integer jobId;
	private String selectionCriteria;
	private SubmittedStatus status=SubmittedStatus.Pending;
	private ObjectType type;
	private Boolean isAquaMap;
	private Boolean saved;
	private Integer sourceHCAF;
	private Integer sourceHSPEN;
	private Integer sourceHSPEC;
	private Boolean gisEnabled=false;
	private String gisPublishedId;	
	private String serializedRequest;
	private Long startTime=0l;
	private Long endTime=0l;
	private Long submissionTime=0l;
	private Boolean isCustomized=false;
	private String speciesCoverage;
	private String fileSetId;
	private Boolean toDelete;
	private String serializedObject;
	
	private Boolean forceRegeneration=false;
	
	
	public String getSerializedObject() {
		return serializedObject;
	}
	public void setSerializedObject(String serializedObject) {
		this.serializedObject = serializedObject;
	}
	public Boolean isToDelete() {
		return toDelete;
	}
	public void setToDelete(Boolean toDelete) {
		this.toDelete = toDelete;
	}
	
	public String getFileSetId() {
		return fileSetId;
	}
	
	public void setFileSetId(String fileSetId) {
		this.fileSetId = fileSetId;
	}
	public Boolean getIsCustomized() {
		return isCustomized;
	}


	public void setIsCustomized(Boolean isCustomized) {
		this.isCustomized = isCustomized;
	}


	public String getSpeciesCoverage() {
		return speciesCoverage;
	}


	public void setSpeciesCoverage(String speciesCoverage) {
		this.speciesCoverage = speciesCoverage;
	}


	public Long getStartTime() {
		return startTime;
	}


	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}


	public Long getEndTime() {
		return endTime;
	}


	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}


	public Long getSubmissionTime() {
		return submissionTime;
	}


	public void setSubmissionTime(Long submissionTime) {
		this.submissionTime = submissionTime;
	}




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
			toReturn.add(new Submitted(Field.loadRow(rs)));
		}
		return toReturn;
	}


	public Submitted (org.gcube_system.namespaces.application.aquamaps.types.Submitted toLoad){
		super();
		this.author=toLoad.getAuthor();
		setSubmissionTime(toLoad.getSubmissionTime());
		setEndTime(toLoad.getEndTime());
		setStartTime(toLoad.getStartTime());
		this.isAquaMap=toLoad.isIsAquaMap();
		this.jobId=toLoad.getJobId();
		this.saved=toLoad.isSaved();
		this.searchId=toLoad.getSearchId();
		this.selectionCriteria=toLoad.getSelectionCriteria();
		this.sourceHCAF=toLoad.getSourceHCAF();
		this.sourceHSPEC=toLoad.getSourceHSPEC();
		this.sourceHSPEN=toLoad.getSourceHSPEN();
		this.status=SubmittedStatus.valueOf(toLoad.getStatus());
		this.title=toLoad.getTitle();
		this.type=((toLoad.getType()!=null)&&(!toLoad.getType().equalsIgnoreCase("null")))?ObjectType.valueOf(toLoad.getType()):null;		
		this.gisPublishedId=toLoad.getPublishedIds();
		this.setGisEnabled(toLoad.isGisEnabled());
		this.setSpeciesCoverage(toLoad.getSpeciesCoverage());
		this.setFileSetId(toLoad.getFileSetId());
		this.setIsCustomized(toLoad.isCustomized());
		this.setForceRegeneration(toLoad.isForceRegeneration());
	}

	public org.gcube_system.namespaces.application.aquamaps.types.Submitted toStubsVersion(){
		org.gcube_system.namespaces.application.aquamaps.types.Submitted toReturn=new org.gcube_system.namespaces.application.aquamaps.types.Submitted();
		toReturn.setAuthor(author);
		toReturn.setSubmissionTime(submissionTime);
		toReturn.setEndTime(endTime);
		toReturn.setStartTime(startTime);
		toReturn.setGisEnabled(gisEnabled);
		toReturn.setIsAquaMap(isAquaMap);
		toReturn.setJobId(jobId);
		toReturn.setSaved(saved);
		toReturn.setSearchId(searchId);
		toReturn.setSelectionCriteria(selectionCriteria);
		toReturn.setSourceHCAF(sourceHCAF);
		toReturn.setSourceHSPEC(sourceHSPEC);
		toReturn.setSourceHSPEN(sourceHSPEN);
		toReturn.setStatus(status+"");
		toReturn.setTitle(title);
		toReturn.setType(type+"");	
		toReturn.setPublishedIds(gisPublishedId);
		toReturn.setSpeciesCoverage(speciesCoverage);
		toReturn.setFileSetId(fileSetId);
		toReturn.setCustomized(isCustomized);
		toReturn.setForceRegeneration(isForceRegeneration());
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

	public String getGisPublishedId() {
		return gisPublishedId;
	}
	public void setGisPublishedId(String gisPublishedId) {
		this.gisPublishedId = gisPublishedId;
	}


	public void setSerializedRequest(String serializedPath) {
		this.serializedRequest = serializedPath;
	}


	public String getSerializedRequest() {
		return serializedRequest;
	}

	public Boolean isForceRegeneration() {
		return forceRegeneration;
	}
	
	public void setForceRegeneration(Boolean forceRegeneration) {
		this.forceRegeneration = forceRegeneration;
	}

	public Submitted(ResultSet rs)throws Exception{
		this(Field.loadRow(rs));
	}
	public Submitted(List<Field> row){
		for(Field f: row)	
			try{
				this.setField(f);
			}catch(Exception e){
				//skips wrong fields
			}
	}

	public boolean setField(Field f){
		switch(SubmittedFields.valueOf(f.getName().toLowerCase())){
		case searchid: this.setSearchId(f.getValueAsInteger());
		break;
		case title: this.setTitle(f.getValue());
		break;
		case author: this.setAuthor(f.getValue());
		break;
		case endtime : this.setEndTime(f.getValueAsLong());
		break;		
		case gisenabled : this.setGisEnabled(f.getValueAsBoolean());
		break;
		case gispublishedid : this.setGisPublishedId(f.getValue());
		break;
		case isaquamap : this.setIsAquaMap(f.getValueAsBoolean());
		break;
		case jobid : this.setJobId(f.getValueAsInteger());
		break;
		case saved : this.setSaved(f.getValueAsBoolean());
		break;
		case selectioncriteria : this.setSelectionCriteria(f.getValue());
		break;
		case serializedrequest : this.setSerializedRequest(f.getValue());
		break;
		
		case sourcehcaf : this.setSourceHCAF(f.getValueAsInteger());
		break;
		case sourcehspen: this.setSourceHSPEN(f.getValueAsInteger());
		break;
		case sourcehspec: this.setSourceHSPEC(f.getValueAsInteger());
		break;
		case status : this.setStatus(SubmittedStatus.valueOf(f.getValue()));
		break;
		case type:this.setType(ObjectType.valueOf(f.getValue()));
		break;
		case starttime : this.setStartTime(f.getValueAsLong());
		break;
		case submissiontime : this.setSubmissionTime(f.getValueAsLong());
		break;
		case speciescoverage : this.setSpeciesCoverage(f.getValue());
		break;
		case iscustomized : this.setIsCustomized(f.getValueAsBoolean());
		break;
		case filesetid: this.setFileSetId(f.getValue());
		break;
		case todelete: this.setToDelete(f.getValueAsBoolean());
		break;
		case serializedobject: this.setSerializedObject(f.getValue());
		break;
		case forceregeneration: this.setForceRegeneration(f.getValueAsBoolean());
		break;
		default : return false;
		}
		return true;
	}

	public Field getField(SubmittedFields fieldName){
		switch(fieldName){
		case searchid: return new Field(fieldName+"",getSearchId()+"",FieldType.INTEGER);
		case title: return new Field(fieldName+"",getTitle(),FieldType.STRING);
		case author : 	return new Field(fieldName+"",getAuthor(),FieldType.STRING);
		case endtime : return new Field(fieldName+"",getEndTime()+"",FieldType.LONG);
		case starttime: return new Field(fieldName+"",getStartTime()+"",FieldType.LONG);
		case submissiontime : return new Field(fieldName+"",getSubmissionTime()+"",FieldType.LONG);
		case gisenabled : return new Field(fieldName+"",getGisEnabled()+"",FieldType.BOOLEAN);
		case gispublishedid: return new Field(fieldName+"",getGisPublishedId(),FieldType.STRING);
		case isaquamap: return new Field(fieldName+"",getIsAquaMap()+"",FieldType.BOOLEAN);
		case jobid: return new Field(fieldName+"",getJobId()+"",FieldType.INTEGER);
		case saved: return new Field(fieldName+"",getSaved()+"",FieldType.BOOLEAN);
		case selectioncriteria: return new Field(fieldName+"",getSelectionCriteria()+"",FieldType.STRING);
		case serializedrequest: return new Field(fieldName+"",getSerializedRequest(),FieldType.STRING);
		
		case sourcehcaf : return new Field(fieldName+"",getSourceHCAF()+"",FieldType.INTEGER);
		case sourcehspen: return new Field(fieldName+"",getSourceHSPEN()+"",FieldType.INTEGER);
		case sourcehspec: return new Field(fieldName+"",getSourceHSPEC()+"",FieldType.INTEGER);
		case status : return new Field(fieldName+"",getStatus()+"",FieldType.STRING);
		case type: return new Field(fieldName+"",getType()+"",FieldType.STRING);
		case filesetid: return new Field(fieldName+"",getFileSetId(),FieldType.STRING);
		case iscustomized: return new Field(fieldName+"",getIsCustomized()+"",FieldType.BOOLEAN);
		case speciescoverage:return new Field(fieldName+"",getSpeciesCoverage(),FieldType.STRING);
		case todelete:return new Field(fieldName+"",isToDelete()+"",FieldType.BOOLEAN);		
		case serializedobject:return new Field(fieldName+"",getSerializedObject(),FieldType.STRING);
		case forceregeneration:return new Field(fieldName+"",isForceRegeneration()+"",FieldType.BOOLEAN);
		default : return null;
		}
	}

	public List<Field> toRow(){
		List<Field> toReturn= new ArrayList<Field>();
		for(SubmittedFields f : SubmittedFields.values())
			toReturn.add(getField(f));
		return toReturn;
	}

	@Override
	public String toString() {
		return "Submitted [searchId=" + searchId + ", title=" + title
				+ ", author=" + author + ", jobId=" + jobId
				+ ", selectionCriteria=" + selectionCriteria + ", status="
				+ status + ", type=" + type + ", isAquaMap=" + isAquaMap
				+ ", saved=" + saved + ", sourceHCAF=" + sourceHCAF
				+ ", sourceHSPEN=" + sourceHSPEN + ", sourceHSPEC="
				+ sourceHSPEC + ", gisEnabled=" + gisEnabled
				+ ", gisPublishedId=" + gisPublishedId +  ", serializedPath=" + serializedRequest
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", submissionTime=" + submissionTime + ", isCustomized="
				+ isCustomized + ", speciesCoverage=" + speciesCoverage
				+ ", fileSetId=" + fileSetId + "]";
	}
	
	
}

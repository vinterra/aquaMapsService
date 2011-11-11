package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.HspecGroupGenerationRequestType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.DataModel;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.LogicType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;


public class SourceGenerationRequest extends DataModel{

	private String author;
	private String generationname;
	private String id;
	private String description;
	private SourceGenerationPhase phase=SourceGenerationPhase.pending;

	private Long submissiontime=0l;
	private Long endtime=0l;
	private Long starttime=0l;
	private Double currentphasepercent=0d;


	private Integer hcafId=0;
	private Integer hspenId=0;
	private Integer occurrenceCellId=0;
	
	

	private ArrayList<Integer> generatedSources=new ArrayList<Integer>();
	private Integer reportID=0;
	private ArrayList<Integer> jobIds=new ArrayList<Integer>();

	private String submissionBackend;
	private String executionEnvironment;
	private String backendURL;
	private HashMap<String, String> environmentConfiguration=new HashMap<String, String>();
	private LogicType logic;
	private Integer numPartitions=0;
	private ArrayList<String> algorithms=new ArrayList<String>();
	private Boolean enablelayergeneration=false;
	private Boolean enableimagegeneration=false;



	
	public Integer getHcafId() {
		return hcafId;
	}
	public void setHcafId(Integer hcafId) {
		this.hcafId = hcafId;
	}
	public Integer getHspenId() {
		return hspenId;
	}
	public void setHspenId(Integer hspenId) {
		this.hspenId = hspenId;
	}
	public Integer getOccurrenceCellId() {
		return occurrenceCellId;
	}
	public void setOccurrenceCellId(Integer occurrenceCellId) {
		this.occurrenceCellId = occurrenceCellId;
	}
	public ArrayList<Integer> getGeneratedSources() {
		return generatedSources;
	}
	public void setGeneratedSources(ArrayList<Integer> generatedSources) {
		this.generatedSources = generatedSources;
	}
	public Long getEndtime() {
		return endtime;
	}
	public void setEndtime(Long endtime) {
		this.endtime = endtime;
	}
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getGenerationname() {
		return generationname;
	}
	public void setGenerationname(String generationname) {
		this.generationname = generationname;
	}
	public ArrayList<String> getAlgorithms() {
		return algorithms;
	}
	public void setAlgorithms(ArrayList<String> algorithms) {
		this.algorithms = algorithms;
	}
	public Boolean getEnablelayergeneration() {
		return enablelayergeneration;
	}
	public void setEnablelayergeneration(Boolean enablelayergeneration) {
		this.enablelayergeneration = enablelayergeneration;
	}
	public Boolean getEnableimagegeneration() {
		return enableimagegeneration;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public SourceGenerationPhase getPhase() {
		return phase;
	}
	public void setPhase(SourceGenerationPhase phase) {
		this.phase = phase;
	}
	public Long getSubmissiontime() {
		return submissiontime;
	}
	public void setSubmissiontime(Long submissiontime) {
		this.submissiontime = submissiontime;
	}
	public Double getCurrentphasepercent() {
		return currentphasepercent;
	}
	public void setCurrentphasepercent(Double currentphasepercent) {
		this.currentphasepercent = currentphasepercent;
	}



	public static ArrayList<SourceGenerationRequest> loadResultSet(ResultSet rs)throws Exception{
		ArrayList<SourceGenerationRequest> toReturn= new ArrayList<SourceGenerationRequest>();
		while(rs.next()){
			toReturn.add(new SourceGenerationRequest(Field.loadRow(rs)));
		}
		return toReturn;
	}
	public String getSubmissionBackend() {
		return submissionBackend;
	}
	public void setSubmissionBackend(String submissionBackend) {
		this.submissionBackend = submissionBackend;
	}
	public LogicType getLogic() {
		return logic;
	}
	public void setLogic(LogicType logic) {
		this.logic = logic;
	}
	public void setReportID(Integer reportID) {
		this.reportID = reportID;
	}
	public Integer getReportID() {
		return reportID;
	}
	public void setJobIds(ArrayList<Integer> jobIds) {
		this.jobIds = jobIds;
	}
	public ArrayList<Integer> getJobIds() {
		return jobIds;
	}
	public Integer getNumPartitions() {
		return numPartitions;
	}
	public void setNumPartitions(Integer numPartitions) {
		this.numPartitions = numPartitions;
	}
	public String getExecutionEnvironment() {
		return executionEnvironment;
	}
	public void setExecutionEnvironment(String executionEnvironment) {
		this.executionEnvironment = executionEnvironment;
	}
	public String getBackendURL() {
		return backendURL;
	}
	public void setBackendURL(String backendURL) {
		this.backendURL = backendURL;
	}
	public HashMap<String, String> getEnvironmentConfiguration() {
		return environmentConfiguration;
	}
	public void setEnvironmentConfiguration(
			HashMap<String, String> environmentConfiguration) {
		this.environmentConfiguration = environmentConfiguration;
	}
	public void setEnableimagegeneration(Boolean enableimagegeneration) {
		this.enableimagegeneration = enableimagegeneration;
	}

	public SourceGenerationRequest(ResultSet rs)throws Exception{
		this(Field.loadRow(rs));
	}
	public SourceGenerationRequest(List<Field> row){
		for(Field f: row)	
			try{
				this.setField(f);
			}catch(Exception e){
				//skips wrong fields
			}
	}

	public boolean setField(Field f){
		switch(SourceGenerationRequestFields.valueOf(f.getName().toLowerCase())){
		case algorithms:this.setAlgorithms(CSVUtils.CSVToList(f.getValue()));
		break;
		case author:this.setAuthor(f.getValue());
		break;
		case backendurl:this.setBackendURL(f.getValue());
		break;
		case currentphasepercent:this.setCurrentphasepercent(f.getValueAsDouble());
		break;
		case description:this.setDescription(f.getValue());
		break;
		case enableimagegeneration:this.setEnableimagegeneration(f.getValueAsBoolean());
		break;
		case enablelayergeneration:this.setEnablelayergeneration(f.getValueAsBoolean());
		break;
		case endtime:this.setEndtime(f.getValueAsLong());
		break;
		case environmentconfiguration:this.setEnvironmentConfiguration((HashMap<String, String>) AquaMapsXStream.getXMLInstance().fromXML(f.getValue()));
		break;
		case executionenvironment:this.setExecutionEnvironment(f.getValue());
		break;
		case generatedsourcesid:
			for(String id:CSVUtils.CSVToList(f.getValue()))this.getGeneratedSources().add(Integer.parseInt(id));
		break;
		case generationname:this.setGenerationname(f.getValue());
		break;
		
		case jobids:for(String id:CSVUtils.CSVToList(f.getValue()))this.getJobIds().add(Integer.parseInt(id));
		break;
		case logic:this.setLogic(LogicType.valueOf(f.getValue()));
		break;
		case numpartitions:this.setNumPartitions(f.getValueAsInteger());
		break;
		case phase:this.setPhase(SourceGenerationPhase.valueOf(f.getValue()));
		break;
		case reportid:this.setReportID(f.getValueAsInteger());
		break;
		case starttime:this.setStarttime(f.getValueAsLong());
		break;
		case submissionbackend:this.setSubmissionBackend(f.getValue());
		break;
		case submissiontime:this.setSubmissiontime(f.getValueAsLong());
		break;
		case sourcehcafid : setHcafId(f.getValueAsInteger());
		break;
		case sourcehspenid : setHspenId(f.getValueAsInteger());
		break;
		case sourceoccurrencecellsid : setOccurrenceCellId(f.getValueAsInteger());
		break;
		case id: setId(f.getValue());
		default : return false;
		}
		return true;
	}

	public Field getField(SourceGenerationRequestFields fieldName){
		switch(fieldName){
		case algorithms:return new Field(fieldName+"",CSVUtils.listToCSV(getAlgorithms()),FieldType.STRING);
		case author:return new Field(fieldName+"",getAuthor(),FieldType.STRING);
		case backendurl:return new Field(fieldName+"",getBackendURL(),FieldType.STRING);
		case currentphasepercent:return new Field(fieldName+"",getCurrentphasepercent()+"",FieldType.DOUBLE);
		case description:return new Field(fieldName+"",getDescription(),FieldType.STRING);
		case enableimagegeneration:return new Field(fieldName+"",getEnableimagegeneration()+"",FieldType.BOOLEAN);
		case enablelayergeneration:return new Field(fieldName+"",getEnablelayergeneration()+"",FieldType.BOOLEAN);
		case endtime:return new Field(fieldName+"",getEndtime()+"",FieldType.INTEGER);
		case environmentconfiguration:return new Field(fieldName+"",AquaMapsXStream.getXMLInstance().toXML(getEnvironmentConfiguration()),FieldType.STRING);
		case executionenvironment:return new Field(fieldName+"",getExecutionEnvironment(),FieldType.STRING);
		case generatedsourcesid:ArrayList<String> hspecIds=new ArrayList<String>();
								for(Integer id:getGeneratedSources())hspecIds.add(id+"");
								return new Field(fieldName+"",CSVUtils.listToCSV(hspecIds),FieldType.STRING);
		case generationname:return new Field(fieldName+"",getGenerationname(),FieldType.STRING);
		
		case id:return new Field(fieldName+"",getId(),FieldType.STRING);
		case jobids:ArrayList<String> jobIds=new ArrayList<String>();
								for(Integer id:getJobIds())jobIds.add(id+"");
								return new Field(fieldName+"",CSVUtils.listToCSV(jobIds),FieldType.STRING);
		case logic:return new Field(fieldName+"",getLogic()+"",FieldType.STRING);
		case numpartitions:return new Field(fieldName+"",getNumPartitions()+"",FieldType.INTEGER);
		case phase:return new Field(fieldName+"",getPhase()+"",FieldType.STRING);
		case reportid:return new Field(fieldName+"",getReportID()+"",FieldType.INTEGER);
		case starttime:return new Field(fieldName+"",getStarttime()+"",FieldType.LONG);
		case submissionbackend:return new Field(fieldName+"",getSubmissionBackend(),FieldType.STRING);
		case submissiontime:return new Field(fieldName+"",getSubmissiontime()+"",FieldType.LONG);
		case sourcehcafid:return new Field(fieldName+"",getHcafId()+"",FieldType.INTEGER);
		case sourcehspenid: return new Field(fieldName+"",getHspenId()+"",FieldType.INTEGER);
		case sourceoccurrencecellsid: return new Field(fieldName+"",getOccurrenceCellId()+"",FieldType.INTEGER);
		default : return null;

		}
	}

	public List<Field> toRow(){
		List<Field> toReturn= new ArrayList<Field>();
		for(SourceGenerationRequestFields f : SourceGenerationRequestFields.values())
			toReturn.add(getField(f));
		return toReturn;
	}

	@Deprecated
	public SourceGenerationRequest() {
		// TODO Auto-generated constructor stub
	}

	public SourceGenerationRequest(HspecGroupGenerationRequestType request){
		setAlgorithms(CSVUtils.CSVToList(request.getAlgorithms()));
		setAuthor(request.getAuthor());
		setBackendURL(request.getBackendUrl());
		setDescription(request.getDescription());
		setEnableimagegeneration(request.isEnableImageGeneration());
		setEnablelayergeneration(request.isEnableLayerGeneration());
		setEnvironmentConfiguration((HashMap<String, String>) AquaMapsXStream.getXMLInstance().fromXML(request.getEnvironmentConfiguration()));
		setExecutionEnvironment(request.getExecutionEnvironment());
		this.setGenerationname(request.getGenerationName());
		setHcafId(request.getHcafId());
		setHspenId(request.getHspenId());
		setOccurrenceCellId(request.getOccurrenceCellsId());
		this.setLogic(LogicType.valueOf(request.getLogic()));
		this.setNumPartitions(request.getNumPartitions());
		this.setSubmissionBackend(request.getSubmissionBackend());
	}
	
	public HspecGroupGenerationRequestType toStubsVersion(){
//		return new HspecGroupGenerationRequestType(CSVUtils.listToCSV(algorithms), author, backendURL, 
//				description, enableimagegeneration, enablelayergeneration,
//				AquaMapsXStream.getXMLInstance().toXML(environmentConfiguration), executionEnvironment, 
//				generationname, logic+"", numPartitions, submissionBackend);
		
		return new HspecGroupGenerationRequestType(CSVUtils.listToCSV(algorithms), author, backendURL,
				description, enableimagegeneration, enablelayergeneration,
				AquaMapsXStream.getXMLInstance().toXML(environmentConfiguration), executionEnvironment,
				generationname, hcafId, hspenId, logic+"", numPartitions, occurrenceCellId, submissionBackend);
		
	}
}

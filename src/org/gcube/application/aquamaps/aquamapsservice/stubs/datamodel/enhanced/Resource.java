package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.fields.MetaSourceFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.AlgorithmType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.ResourceType;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("Resource")
public class Resource extends DataModel{

	private ResourceType type=ResourceType.HCAF;
	
	private static final AlgorithmType DEFAULT_ALGORITHM_TYPE=AlgorithmType.NativeRange;
	
	
	private int searchId=1;

	private String title;
	private String tableName;
	private String description;
	private String author;
	private String disclaimer;
	private String provenance;
	private Long generationTime=0l;
	private Integer sourceHCAFId=0;
	private Integer sourceHSPENId=0;
	private Integer sourceHSPECId=0;
	private Integer sourceOccurrenceCellsId=0;
	private String parameters;
	private ResourceStatus status=ResourceStatus.Completed;
	private String sourceHSPECTable;
	private String sourceHSPENTable;
	private String sourceHCAFTable;
	private String sourceOccurrenceCellsTable;
	private AlgorithmType algorithm=DEFAULT_ALGORITHM_TYPE;
	private Boolean defaultSource=false;
	private Long rowCount=0l;

	public Long getRowCount() {
		return rowCount;
	}

	public void setRowCount(Long rowCount) {
		this.rowCount = rowCount;
	}

	public Integer getSourceOccurrenceCellsId() {
		return sourceOccurrenceCellsId;
	}
	public void setSourceOccurrenceCellsId(Integer sourceOccurrenceCellsId) {
		this.sourceOccurrenceCellsId = sourceOccurrenceCellsId;
	}
	public String getSourceOccurrenceCellsTable() {
		return sourceOccurrenceCellsTable;
	}
	public void setSourceOccurrenceCellsTable(String sourceOccurrenceCellsTable) {
		this.sourceOccurrenceCellsTable = sourceOccurrenceCellsTable;
	}
	public Boolean getDefaultSource() {
		return defaultSource;
	}
	public void setDefaultSource(Boolean defaultSource) {
		this.defaultSource = defaultSource;
	}
	public Integer getSourceHCAFId() {
		return sourceHCAFId;
	}
	public void setSourceHCAFId(Integer sourceHCAFId) {
		this.sourceHCAFId = sourceHCAFId;
	}
	public Integer getSourceHSPENId() {
		return sourceHSPENId;
	}
	public void setSourceHSPENId(Integer sourceHSPENId) {
		this.sourceHSPENId = sourceHSPENId;
	}
	public Integer getSourceHSPECId() {
		return sourceHSPECId;
	}
	public void setSourceHSPECId(Integer sourceHSPECId) {
		this.sourceHSPECId = sourceHSPECId;
	}
	public String getSourceHSPECTable() {
		return sourceHSPECTable;
	}
	public void setSourceHSPECTable(String sourceHSPECTable) {
		this.sourceHSPECTable = sourceHSPECTable;
	}
	public String getSourceHSPENTable() {
		return sourceHSPENTable;
	}
	public void setSourceHSPENTable(String sourceHSPENTable) {
		this.sourceHSPENTable = sourceHSPENTable;
	}
	public String getSourceHCAFTable() {
		return sourceHCAFTable;
	}
	public void setSourceHCAFTable(String sourceHCAFTable) {
		this.sourceHCAFTable = sourceHCAFTable;
	}
	public AlgorithmType getAlgorithm() {
		return algorithm;
	}
	public void setAlgorithm(AlgorithmType algorithm) {
		this.algorithm = algorithm;
	}
	public String getProvenance() {
		return provenance;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDisclaimer() {
		return disclaimer;
	}
	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}
	public String getProvenience() {
		return provenance;
	}
	public void setProvenance(String provenience) {
		this.provenance = provenience;
	}
	public Long getGenerationTime() {
		return generationTime;
	}
	public void setGenerationTime(Long generationTime) {
		this.generationTime = generationTime;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public ResourceStatus getStatus() {
		return status;
	}
	public void setStatus(ResourceStatus status) {
		this.status = status;
	}

	


	public void setType(ResourceType type) {
		this.type = type;
	}
	public ResourceType getType() {
		return type;
	}

	public Resource(ResourceType type,int searchId) {		
		this.type=type;
		this.searchId=searchId;
	}



	public Resource (org.gcube.application.aquamaps.datamodel.Resource toLoad){
		try{this.setAlgorithm(AlgorithmType.valueOf(toLoad.getAlgorithm()));}
		catch(Exception e){this.setAlgorithm(DEFAULT_ALGORITHM_TYPE);}
		this.setAuthor(toLoad.getAuthor());
		this.setGenerationTime(toLoad.getDate());
		this.setDescription(toLoad.getDescription());
		this.setDisclaimer(toLoad.getDisclaimer());
		this.setParameters(toLoad.getParameters());
		this.setProvenance(toLoad.getProvenance());
		this.setSearchId(toLoad.getSearchId());
		this.setSourceHCAFId(toLoad.getSourceHCAFId());
		this.setSourceHSPENId(toLoad.getSourceHSPENId());
		this.setSourceHSPECId(toLoad.getSourceHSPECId());
		this.setSourceHSPENTable(toLoad.getSourceHSPENTable());
		this.setSourceHCAFTable(toLoad.getSourceHCAFTable());
		this.setSourceHSPECTable(toLoad.getSourceHSPECTable());
		this.setStatus(ResourceStatus.valueOf(toLoad.getStatus()));
		this.setTableName(toLoad.getTableName());
		this.setTitle(toLoad.getTitle());
		this.setType(ResourceType.valueOf(toLoad.getType()));
		this.setDefaultSource(toLoad.isDefaultSource());
		setSourceOccurrenceCellsId(toLoad.getSourceOccurrenceCellsId());
		setSourceOccurrenceCellsTable(toLoad.getSourceOccurrenceCellsTable());
		setRowCount(toLoad.getPercent());
	}

	public org.gcube.application.aquamaps.datamodel.Resource toStubsVersion(){
		org.gcube.application.aquamaps.datamodel.Resource toReturn=new org.gcube.application.aquamaps.datamodel.Resource();
		
		toReturn.setAlgorithm(getAlgorithm()+"");
		toReturn.setAuthor(getAuthor());
		toReturn.setDate(generationTime!=null?generationTime:0);
		toReturn.setDescription(getDescription());
		toReturn.setDisclaimer(getDisclaimer());
		toReturn.setParameters(getParameters());
		toReturn.setProvenance(getProvenience());
		toReturn.setSearchId(getSearchId());
		toReturn.setSourceHCAFId((getSourceHCAFId()==null)?0:getSourceHCAFId());
		toReturn.setSourceHSPENId((getSourceHSPENId()==null)?0:getSourceHSPENId());
		toReturn.setSourceHSPECId((getSourceHSPECId()==null)?0:getSourceHSPECId());
		toReturn.setSourceHSPENTable(this.getSourceHSPENTable());
		toReturn.setSourceHCAFTable(this.getSourceHCAFTable());
		toReturn.setSourceHSPECTable(this.getSourceHSPECTable());
		toReturn.setStatus(this.getStatus()+"");
		toReturn.setTableName(this.getTableName());
		toReturn.setTitle(this.getTitle());
		toReturn.setType(this.getType().toString());
		toReturn.setDefaultSource(this.getDefaultSource());
		toReturn.setSourceOccurrenceCellsId(getSourceOccurrenceCellsId());
		toReturn.setSourceOccurrenceCellsTable(getSourceOccurrenceCellsTable());
		toReturn.setPercent(getRowCount());
		return toReturn;
	}


	public static List<Resource> load(org.gcube.application.aquamaps.datamodel.ResourceArray toLoad){
		List<Resource> toReturn=new ArrayList<Resource>();
		if((toLoad!=null)&&(toLoad.getResourceList()!=null))
			for(org.gcube.application.aquamaps.datamodel.Resource f: toLoad.getResourceList())toReturn.add(new Resource(f));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.datamodel.ResourceArray toStubsVersion(List<Resource> toConvert){
		List<org.gcube.application.aquamaps.datamodel.Resource> list=new ArrayList<org.gcube.application.aquamaps.datamodel.Resource>();
		if(toConvert!=null)
			for(Resource obj:toConvert)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.datamodel.ResourceArray(list.toArray(new org.gcube.application.aquamaps.datamodel.Resource[list.size()]));
	}
	
	


	
	public Resource(ResultSet rs)throws Exception{
		for(Field f: Field.loadRow(rs))	
			try{
			this.setField(f);
			}catch(Exception e){
				//skips wrong fields
			}
	}
	
	public boolean setField(Field f){
		switch(MetaSourceFields.valueOf(f.getName().toLowerCase())){
		case searchid: this.setSearchId(f.getValueAsInteger());
						break;
		case title: this.setTitle(f.getValue());
					break;
		case tablename: this.setTableName(f.getValue());
						break;
		case description : this.setDescription(f.getValue());
						break;
		case author : 	this.setAuthor(f.getValue());
						break;
		case disclaimer  : this.setDisclaimer(f.getValue());
							break;
		case provenience : this.setProvenance(f.getValue());
							break;
		case generationtime : this.setGenerationTime(Long.valueOf(f.getValueAsInteger()));
					break;
		case sourcehcaf : this.setSourceHCAFId(f.getValueAsInteger());
							break;
		case sourcehspen: this.setSourceHSPENId(f.getValueAsInteger());
							break;
		case sourcehspec: this.setSourceHSPECId(f.getValueAsInteger());
							break;
		case parameters : this.setParameters(f.getValue());
							break;
		case status : this.setStatus(ResourceStatus.valueOf(f.getValue()));
							break;
		case sourcehcaftable: this.setSourceHCAFTable(f.getValue());
								break;
		case sourcehspentable: this.setSourceHSPENTable(f.getValue());
								break;
		case sourcehspectable: this.setSourceHSPECTable(f.getValue());
								break;
		case type: this.setType(ResourceType.valueOf(f.getValue()));
							break;
		case algorithm : try{this.setAlgorithm(AlgorithmType.valueOf(f.getValue()));}catch(Exception e){}
						break;
		case defaultsource : this.setDefaultSource(f.getValueAsBoolean());
			break;
		case rowcount : this.setRowCount(f.getValueAsLong());
		break;
		case sourceoccurrencecells: this.setSourceOccurrenceCellsId(f.getValueAsInteger());
		break;
		case sourceoccurrencecellstable: this.setSourceOccurrenceCellsTable(f.getValue());
		break;
		default : return false;
		
	}
		return true;
	}
	
	public Field getField(MetaSourceFields fieldName){
		switch(fieldName){
		case searchid: return new Field(fieldName+"",getSearchId()+"",FieldType.INTEGER);
		case title: return new Field(fieldName+"",getTitle(),FieldType.STRING);
		case tablename: return new Field(fieldName+"",getTableName(),FieldType.STRING);
		case description : return new Field(fieldName+"",getDescription(),FieldType.STRING);
		case author : 	return new Field(fieldName+"",getAuthor(),FieldType.STRING);
		case disclaimer  : return new Field(fieldName+"",getDisclaimer(),FieldType.STRING);
		case provenience : return new Field(fieldName+"",getProvenance(),FieldType.STRING);
		case generationtime : return new Field(fieldName+"",getGenerationTime()+"",FieldType.LONG);
		case sourcehcaf : return new Field(fieldName+"",getSourceHCAFId()+"",FieldType.INTEGER);
		case sourcehspen: return new Field(fieldName+"",getSourceHSPENId()+"",FieldType.INTEGER);
		case sourcehspec: return new Field(fieldName+"",getSourceHSPECId()+"",FieldType.INTEGER);
		case parameters : return new Field(fieldName+"",getParameters(),FieldType.STRING);
		case status : return new Field(fieldName+"",getStatus()+"",FieldType.STRING);
		case sourcehcaftable: return new Field(fieldName+"",getSourceHCAFTable(),FieldType.STRING);
		case sourcehspentable: return new Field(fieldName+"",getSourceHSPENTable(),FieldType.STRING);
		case sourcehspectable: return new Field(fieldName+"",getSourceHSPECTable(),FieldType.STRING);
		case type: return new Field(fieldName+"",getType()+"",FieldType.STRING);
		case algorithm : return new Field(fieldName+"",getAlgorithm()+"",FieldType.STRING);
		case defaultsource : return new Field(fieldName+"",getDefaultSource()+"",FieldType.BOOLEAN);
		case sourceoccurrencecells : return new Field(fieldName+"",getSourceOccurrenceCellsId()+"",FieldType.INTEGER);
		case sourceoccurrencecellstable : return new Field(fieldName+"",getSourceOccurrenceCellsTable(),FieldType.STRING);
		case rowcount: return new Field(fieldName+"",getRowCount()+"",FieldType.LONG);
		default : return null;
		
	}
	}
	
	
	public List<Field> toRow(){
		List<Field> toReturn= new ArrayList<Field>();
		for(MetaSourceFields f : MetaSourceFields.values())
				toReturn.add(getField(f));
		return toReturn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result
				+ ((defaultSource == null) ? 0 : defaultSource.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((disclaimer == null) ? 0 : disclaimer.hashCode());
		result = prime * result
				+ ((generationTime == null) ? 0 : generationTime.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result
				+ ((provenance == null) ? 0 : provenance.hashCode());
		result = prime * result
				+ ((rowCount == null) ? 0 : rowCount.hashCode());
		result = prime * result + searchId;
		result = prime * result
				+ ((sourceHCAFId == null) ? 0 : sourceHCAFId.hashCode());
		result = prime * result
				+ ((sourceHCAFTable == null) ? 0 : sourceHCAFTable.hashCode());
		result = prime * result
				+ ((sourceHSPECId == null) ? 0 : sourceHSPECId.hashCode());
		result = prime
				* result
				+ ((sourceHSPECTable == null) ? 0 : sourceHSPECTable.hashCode());
		result = prime * result
				+ ((sourceHSPENId == null) ? 0 : sourceHSPENId.hashCode());
		result = prime
				* result
				+ ((sourceHSPENTable == null) ? 0 : sourceHSPENTable.hashCode());
		result = prime
				* result
				+ ((sourceOccurrenceCellsId == null) ? 0
						: sourceOccurrenceCellsId.hashCode());
		result = prime
				* result
				+ ((sourceOccurrenceCellsTable == null) ? 0
						: sourceOccurrenceCellsTable.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Resource other = (Resource) obj;
		if (algorithm != other.algorithm)
			return false;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (defaultSource == null) {
			if (other.defaultSource != null)
				return false;
		} else if (!defaultSource.equals(other.defaultSource))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (disclaimer == null) {
			if (other.disclaimer != null)
				return false;
		} else if (!disclaimer.equals(other.disclaimer))
			return false;
		if (generationTime == null) {
			if (other.generationTime != null)
				return false;
		} else if (!generationTime.equals(other.generationTime))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (provenance == null) {
			if (other.provenance != null)
				return false;
		} else if (!provenance.equals(other.provenance))
			return false;
		if (rowCount == null) {
			if (other.rowCount != null)
				return false;
		} else if (!rowCount.equals(other.rowCount))
			return false;
		if (searchId != other.searchId)
			return false;
		if (sourceHCAFId == null) {
			if (other.sourceHCAFId != null)
				return false;
		} else if (!sourceHCAFId.equals(other.sourceHCAFId))
			return false;
		if (sourceHCAFTable == null) {
			if (other.sourceHCAFTable != null)
				return false;
		} else if (!sourceHCAFTable.equals(other.sourceHCAFTable))
			return false;
		if (sourceHSPECId == null) {
			if (other.sourceHSPECId != null)
				return false;
		} else if (!sourceHSPECId.equals(other.sourceHSPECId))
			return false;
		if (sourceHSPECTable == null) {
			if (other.sourceHSPECTable != null)
				return false;
		} else if (!sourceHSPECTable.equals(other.sourceHSPECTable))
			return false;
		if (sourceHSPENId == null) {
			if (other.sourceHSPENId != null)
				return false;
		} else if (!sourceHSPENId.equals(other.sourceHSPENId))
			return false;
		if (sourceHSPENTable == null) {
			if (other.sourceHSPENTable != null)
				return false;
		} else if (!sourceHSPENTable.equals(other.sourceHSPENTable))
			return false;
		if (sourceOccurrenceCellsId == null) {
			if (other.sourceOccurrenceCellsId != null)
				return false;
		} else if (!sourceOccurrenceCellsId
				.equals(other.sourceOccurrenceCellsId))
			return false;
		if (sourceOccurrenceCellsTable == null) {
			if (other.sourceOccurrenceCellsTable != null)
				return false;
		} else if (!sourceOccurrenceCellsTable
				.equals(other.sourceOccurrenceCellsTable))
			return false;
		if (status != other.status)
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Resource [type=" + type + ", searchId=" + searchId + ", title="
				+ title + ", tableName=" + tableName + ", description="
				+ description + ", author=" + author + ", disclaimer="
				+ disclaimer + ", provenance=" + provenance
				+ ", generationTime=" + generationTime + ", sourceHCAFId="
				+ sourceHCAFId + ", sourceHSPENId=" + sourceHSPENId
				+ ", sourceHSPECId=" + sourceHSPECId
				+ ", sourceOccurrenceCellsId=" + sourceOccurrenceCellsId
				+ ", parameters=" + parameters + ", status=" + status
				+ ", sourceHSPECTable=" + sourceHSPECTable
				+ ", sourceHSPENTable=" + sourceHSPENTable
				+ ", sourceHCAFTable=" + sourceHCAFTable
				+ ", sourceOccurrenceCellsTable=" + sourceOccurrenceCellsTable
				+ ", algorithm=" + algorithm + ", defaultSource="
				+ defaultSource + ", rowCount=" + rowCount + "]";
	}
	
	
	
	
	
}

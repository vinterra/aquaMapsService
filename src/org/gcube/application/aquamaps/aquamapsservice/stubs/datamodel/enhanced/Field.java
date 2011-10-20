package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.json.JSONException;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.json.JSONObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.FieldType;
import org.gcube.application.aquamaps.datamodel.FieldArray;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Field")
public class Field extends DataModel{

	public static final String VOID="VOID";
	
	
	
	@XStreamAsAttribute
	private FieldType type=FieldType.STRING;
	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private String value;

	public FieldType getType() {
		return type;
	}

	public Field() {
		type=FieldType.STRING;
		name="DefaultFieldName";
		value="DefaultValue";
	}

	public Field(String name,String value) {
		this();
		this.name=name;
		this.value=value;
	}
	public Field(String name,String value,FieldType type){
		this(name,value);
		this.setType(type);
	}


	public void setType(FieldType type) {
		this.type = type;		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public static List<Field> load(FieldArray toLoad){
		List<Field> toReturn=new ArrayList<Field>();
		if((toLoad!=null)&&(toLoad.getFields()!=null))
			for(org.gcube.application.aquamaps.datamodel.Field f:toLoad.getFields())toReturn.add(new Field(f));
		return toReturn;
	}

	public static org.gcube.application.aquamaps.datamodel.FieldArray toStubsVersion(Collection<Field> collection){
		List<org.gcube.application.aquamaps.datamodel.Field> list=new ArrayList<org.gcube.application.aquamaps.datamodel.Field>();
		if(collection!=null)
			for(Field obj:collection)
				list.add(obj.toStubsVersion());
		return new org.gcube.application.aquamaps.datamodel.FieldArray(list.toArray(new org.gcube.application.aquamaps.datamodel.Field[list.size()]));
	}


	public Field(org.gcube.application.aquamaps.datamodel.Field toLoad){
		super();
		this.setName(toLoad.getName());
		this.setType(FieldType.valueOf(toLoad.getType()));
		this.setValue(toLoad.getValue());
	}
	public org.gcube.application.aquamaps.datamodel.Field toStubsVersion(){
		org.gcube.application.aquamaps.datamodel.Field toReturn=new org.gcube.application.aquamaps.datamodel.Field();
		toReturn.setName(this.getName());
		toReturn.setType(this.getType().toString());
		toReturn.setValue(this.getValue());
		return toReturn;		
	}


	public String getOperator(){
		if(name.contains("min")) return ">=";
		else if(name.contains("max")) return "<=";
		else return "=";
	}



	
	public static List<Field> loadRow(ResultSet rs)throws Exception{
		ArrayList<Field> toReturn=new ArrayList<Field>();
		ResultSetMetaData rsMeta=rs.getMetaData();
		int colCount=rsMeta.getColumnCount();
		for(int i=1;i<=colCount;i++)
			toReturn.add(new Field(rsMeta.getColumnName(i),rs.getString(i),getType(rsMeta.getColumnType(i))));
		return toReturn;
	}
	
	public static FieldType getType(int SQLType){
		if(SQLType==Types.BIGINT||SQLType==Types.TINYINT||SQLType==Types.SMALLINT||
				SQLType==Types.INTEGER||SQLType==Types.BIT||SQLType==Types.TIMESTAMP) return FieldType.INTEGER;
		if(SQLType==Types.FLOAT||SQLType==Types.DOUBLE||SQLType==Types.REAL||SQLType==Types.DECIMAL||SQLType==Types.NUMERIC) return FieldType.DOUBLE;
		if(SQLType==Types.BOOLEAN) return FieldType.BOOLEAN;
		return FieldType.STRING;
	}
	
	
	public static List<List<Field>> loadResultSet(ResultSet rs)throws SQLException{
		List<List<Field>> toReturn=new ArrayList<List<Field>>();
		ResultSetMetaData rsMeta=rs.getMetaData();
		int colCount=rsMeta.getColumnCount();
		while(rs.next()){
			List<Field> row=new ArrayList<Field>();
			for(int i=1;i<=colCount;i++)
				row.add(new Field(rsMeta.getColumnName(i),rs.getString(i),getType(rsMeta.getColumnType(i))));
			toReturn.add(row);
		}
		return toReturn;
	}
	
	
	//**************** VALUE PARSING
	
	public Double getValueAsDouble(){
		try{
			return Double.parseDouble(getValue());			
		}catch(Exception e){return null;}
	}
	
	public Integer getValueAsInteger(){
		try{
			return Integer.parseInt(getValue());
		}catch(Exception e) {return null;}
	}
	public Long getValueAsLong(){
		try{
			return Long.parseLong(getValue());
		}catch(Exception e) {return null;}
	}
	public Boolean getValueAsBoolean(){
			if(getValue().equalsIgnoreCase(false+"")||getValue().equalsIgnoreCase(true+""))
				return Boolean.parseBoolean(getValue());
			else {
				Integer i=getValueAsInteger();
				if(i!=null)	return Boolean.valueOf(i.equals(Integer.valueOf(1)));
				else return null;
			}
	}

	
	//************** USE DEFAULT
	public Double getValueAsDouble(String defaultValue){
		try{
			return Double.parseDouble(getValue());			
		}catch(Exception e){return Double.parseDouble(defaultValue);}
	}
	
	public Integer getValueAsInteger(String defaultValue){
		try{
			return Integer.parseInt(getValue());
		}catch(Exception e) {return Integer.parseInt(defaultValue);}
	}
	public Long getValueAsLong(String defaultValue){
		try{
			return Long.parseLong(getValue());
		}catch(Exception e) {return Long.parseLong(defaultValue);}
	}
	public Boolean getValueAsBoolean(String defaultValue){
		try{
		 return Boolean.parseBoolean(getValue());
		}catch(Exception e){
			Integer i=getValueAsInteger();
			if(i!=null)
			return  Boolean.valueOf(i==1);
			else return Boolean.parseBoolean(defaultValue);
		}
	}
	
	public JSONObject toJSONObject() throws JSONException{
		JSONObject toReturn=new JSONObject();
		toReturn.put("name", name);
		toReturn.put("value", value);
		toReturn.put("type", type);
		return toReturn;
	}
	
	public Field(JSONObject obj)throws JSONException{
		this.setName(obj.getString("name"));
		this.setType(FieldType.valueOf(obj.getString("type")));
		this.setValue(obj.getString("value"));
	}
}

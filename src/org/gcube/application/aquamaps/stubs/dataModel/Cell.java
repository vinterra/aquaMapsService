package org.gcube.application.aquamaps.stubs.dataModel;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;

public class Cell {
	
	
 	private String cSquareCode;
 	public List<Field> attributesList=new ArrayList<Field>();
 	
 	public Cell(String code){this.cSquareCode=code;}
 	public void setCode(String code){this.cSquareCode=code;}
 	public String getCode(){return cSquareCode;}
 	
 	
	public List<Field> getAttributesList() {
		return attributesList;
	}
	public void setAttributesList(List<Field> attributesList) {
		this.attributesList = attributesList;
	}
	public Field getFieldbyName(String fieldName){
		for(Field field:attributesList){
			if(field.getName().equals(fieldName)) return field;
		}
		return null;		
	}
	
	public void addField(Field toAddField){
		attributesList.add(toAddField);
	}
	
	public Cell(List<Field> initFields){
		this("DUMMYCODE");
		attributesList.addAll(initFields);
		this.setCode(this.getFieldbyName(HCAF_SFields.CSquareCode+"").getValue());
	}
	
	
	
	public String toXML(){
		StringBuilder toReturn=new StringBuilder();
		toReturn.append("<Cell>");
		toReturn.append("<"+HCAF_SFields.CSquareCode+">"+cSquareCode+"</"+HCAF_SFields.CSquareCode+">");
		toReturn.append("<Attributes>");
		for(Field field:attributesList) toReturn.append(field.toXML());
		toReturn.append("</Attributes>");
		toReturn.append("</Cell>");
		return toReturn.toString();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cSquareCode == null) ? 0 : cSquareCode.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Cell))
			return false;
		Cell other = (Cell) obj;
		if (cSquareCode == null) {
			if (other.cSquareCode != null)
				return false;
		} else if (!cSquareCode.equals(other.cSquareCode))
			return false;
		return true;
	}
	
	
}

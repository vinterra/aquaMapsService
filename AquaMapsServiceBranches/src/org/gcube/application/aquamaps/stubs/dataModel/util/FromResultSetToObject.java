package org.gcube.application.aquamaps.stubs.dataModel.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Species;

public class FromResultSetToObject {

	public static List<Species> getSpecies(ResultSet rs) throws SQLException{
		ArrayList<Species> toReturn=new ArrayList<Species>();
		rs.beforeFirst();
		ResultSetMetaData meta=rs.getMetaData();
		while(rs.next()){
			Species sp = new Species();
			sp.setId(rs.getString(Species.Tags.ID));			
			for(int i=0;i<meta.getColumnCount();i++){
				Field field=new Field();
				field.setName(meta.getColumnName(i+1));
				//field.setType(Field.Type.valueOf(meta.getColumnTypeName(i+1)));
				field.setValue(rs.getString(i+1));
				sp.addField(field);
			}
			toReturn.add(sp);
		}
		return toReturn;
	}
	
	public static List<Cell> getCell(ResultSet rs) throws SQLException{
		ArrayList<Cell> toReturn=new ArrayList<Cell>();
		rs.beforeFirst();
		ResultSetMetaData meta=rs.getMetaData();
		while(rs.next()){
			Cell cell = new Cell();
			cell.setCode(rs.getString(Cell.Tags.ID));			
			for(int i=0;i<meta.getColumnCount();i++){
				Field field=new Field();
				field.setName(meta.getColumnName(i+1));
				//field.setType(Field.Type.valueOf(meta.getColumnTypeName(i+1)));
				field.setValue(rs.getString(i+1));
				cell.attributes.put(field.getName(), field);
			}
			toReturn.add(cell);
		}
		return toReturn;
	}
	
}

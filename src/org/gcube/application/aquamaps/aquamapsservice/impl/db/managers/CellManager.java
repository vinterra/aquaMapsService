package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager.DBType;
import org.gcube.application.aquamaps.stubs.dataModel.Area;
import org.gcube.application.aquamaps.stubs.dataModel.BoundingBox;
import org.gcube.application.aquamaps.stubs.dataModel.Cell;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Types.AreaType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HCAF_SFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.HSPECFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.OccurrenceCellsFields;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SpeciesOccursumFields;

public class CellManager {

	private static String occurrenceCells="occurrenceCells";
	private static String HCAF_S="HCAF_S";
	
	public static Set<Cell> getCells(List<Field> filters, boolean fetchGoodCells, String speciesID, boolean fetchEnvironment, int HCAFId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			Set<Cell> toReturn=loadRS(session.executeFilteredQuery(filters, HCAF_S,null,null));
			
			if(fetchEnvironment)
				toReturn=loadEnvironmentData(HCAFId, toReturn);
			
			if(fetchGoodCells)				
				toReturn=loadGoodCellsData(speciesID, toReturn);				

			return toReturn;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	public static Set<Cell> getCellsByIds(boolean fetchGoodCells,String speciesID, boolean fetchEnvironment,int HcafId, String[] items) throws Exception{
		Set<Cell> toReturn= loadCells(items);
		if(fetchEnvironment) toReturn=loadEnvironmentData(HcafId, toReturn);
		if(fetchGoodCells) toReturn=loadGoodCellsData(speciesID, toReturn);
		return toReturn;
	}
	
	public static String getJSONCells(String orderBy, String orderDir, int limit, int offset) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
		return DBUtils.toJSon(session.executeFilteredQuery(new ArrayList<Field>(), HCAF_S, orderBy, orderDir), offset, offset+limit);
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	public static String getJSONOccurrenceCells(String speciesId, String orderBy, String orderDir, int limit, int offset) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SpeciesOccursumFields.SpeciesID+"",speciesId,FieldType.STRING));
		return DBUtils.toJSon(session.executeFilteredQuery(filter, occurrenceCells, orderBy, orderDir), offset, offset+limit);
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
//	public static String getJSONCellsByIds(boolean fetchStatic, boolean fetchGoodCells, boolean fetchEnvironment,String[] items,String orderBy, String orderDir, int limit, int offset) throws Exception{
//		throw new Exception("not implemented");
//	}
	
	public static String filterCellByFaoAreas(String newName,String sourceTable){
	return "INSERT IGNORE INTO "+newName+" ( Select "+sourceTable+".* from "+sourceTable+
		" where "+sourceTable+"."+HSPECFields.FAOAreaM+" = ? ) ";
	}	
	
	public static String filterCellByLMEAreas(String newName,String sourceTable){
		return "INSERT IGNORE INTO "+newName+" ( Select "+sourceTable+".* from "+sourceTable+
			" where "+sourceTable+"."+HSPECFields.LME+" = ? ) ";
		}
	public static String filterCellByEEZAreas(String newName,String sourceTable){
		return "INSERT IGNORE INTO "+newName+" ( Select "+sourceTable+".* from "+sourceTable+
			" where find_in_set( ? , "+sourceTable+"."+HSPECFields.EEZAll+")) ";
		}
	
	private static Set<Cell> loadRS(ResultSet rs) throws SQLException{
		HashSet<Cell> toReturn=new HashSet<Cell>();
		List<List<Field>> rows=DBUtils.toFields(rs);
		for(List<Field> row:rows){
			toReturn.add(new Cell(row));
		}
		return toReturn;
	}
	
//	private static Set<Cell> loadRS(ResultSet rs, Set<Cell> toUpdate)throws SQLException{
//		if(toUpdate.size()==0) return loadRS(rs);		
//		List<List<Field>> rows=DBUtils.toFields(rs);
//		for(List<Field> row:rows){
//			Cell app= new Cell(row);
//			for(Cell c: toUpdate)
//				if(c.getCode().equals(app.getCode()))
//					c.attributesList.addAll(app.attributesList);
//		}
//		return toUpdate;
//	}
	
	private static Set<Cell> loadEnvironmentData(int HCAFId, Set<Cell> toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			String HCAFName=SourceManager.getSourceName(ResourceType.HCAF, HCAFId);
			PreparedStatement ps=session.preparedStatement("Select * from "+HCAFName+" where "+HCAF_SFields.CSquareCode+" = ?");
			for(Cell c: toUpdate){
				ps.setString(1,c.getCode());
				c.attributesList.addAll(DBUtils.toFields(ps.executeQuery()).get(0));
			}
			return toUpdate;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	private static Set<Cell> loadGoodCellsData(String SpeciesID, Set<Cell> toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select * from "+occurrenceCells+" where "+HCAF_SFields.CSquareCode+" = ? AND "+SpeciesOccursumFields.SpeciesID+" =?");
			ps.setString(2,SpeciesID);
			for(Cell c: toUpdate){
				ps.setString(1,c.getCode());
				c.attributesList.addAll(DBUtils.toFields(ps.executeQuery()).get(0));
			}
			return toUpdate;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	public static Set<Cell> getGoodCells(BoundingBox bb, List<Area> areas, String speciesID, int hcafId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			List<Field> cellFilter=new ArrayList<Field>();
			cellFilter.add(new Field(SpeciesOccursumFields.SpeciesID+"",speciesID,FieldType.STRING));
			Set<Cell> toReturn = loadRS(session.executeFilteredQuery(cellFilter, occurrenceCells, HCAF_SFields.CSquareCode+"", "ASC"));
			
			for(Cell c : toReturn){
				//Cehcking BB
				float latitude=Float.parseFloat(c.getFieldbyName(OccurrenceCellsFields.CenterLat+"").getValue());
				float longitude=Float.parseFloat(c.getFieldbyName(OccurrenceCellsFields.CenterLong+"").getValue());
				if((latitude-0.25<bb.getS())||(latitude+0.25>bb.getN())||(longitude-0.25<bb.getE())||(longitude+0.25>bb.getW()))
						toReturn.remove(c);
				//Checking A
				Area areaM=new Area(AreaType.FAO,c.getFieldbyName(OccurrenceCellsFields.FAOAreaM+"").getValue());
				if((areas.size()>0)&&(!areas.contains(areaM))) toReturn.remove(c);
			}
			
			
			toReturn=loadEnvironmentData(hcafId, toReturn);
			return toReturn;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
	
	private static Set<Cell> loadCells(String[] ids)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select * from "+HCAF_S+" where "+HCAF_SFields.CSquareCode+" = ?");
			Set<Cell> toReturn=new HashSet<Cell>();
			for(String code:ids){
				ps.setString(1,code);
				toReturn.add(new Cell(DBUtils.toFields(ps.executeQuery()).get(0)));
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{session.close();}
	}
	
}

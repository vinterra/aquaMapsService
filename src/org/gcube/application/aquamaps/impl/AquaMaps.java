package org.gcube.application.aquamaps.impl;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.gcube.application.aquamaps.impl.threads.JobSubmissionThread;
import org.gcube.application.aquamaps.impl.util.DBCostants;
import org.gcube.application.aquamaps.impl.util.DBUtils;
import org.gcube.application.aquamaps.impl.util.DataTranslation;
import org.gcube.application.aquamaps.stubs.*;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.common.core.types.VOID;

public class AquaMaps extends GCUBEPortType {


	protected GCUBEServiceContext getServiceContext() {		
		return ServiceContext.getContext();
	}

	public String getPhylogeny(GetPhylogenyRequestType req) throws GCUBEFault{
		//TODO Implement Method
		return null;
	}

	public String submitJob(Job req)throws GCUBEFault{
		
		return "";//TODO return created job Id
	}
	
	public FieldArray getSpeciesEnvelop(String speciesId)throws GCUBEFault{
		ArrayList<Field> array=new ArrayList<Field>();		
		logger.trace("Serving getSpeciesEnvelop for speciesID : "+speciesId);
		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			PreparedStatement ps=conn.prepareStatement(DBCostants.speciesEnvelop);
			ps.setString(1, speciesId);
			ResultSet rs = ps.executeQuery();
			if(rs.first())			
				array=DataTranslation.resultSetToFields(rs,rs.getMetaData());
			rs.close();
			ps.close();
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getSpeciesEnvelop");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}
		return new FieldArray(array.toArray(new Field[array.size()]));
	}

	public FieldArray getCellEnvironment(String code)throws GCUBEFault{
		ArrayList<Field> array=new ArrayList<Field>();
		logger.trace("Serving getCellEnvironment for cellCode : "+code);
		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			PreparedStatement ps=conn.prepareStatement(DBCostants.cellEnvironment);
			ps.setString(1, code);
			ResultSet rs = ps.executeQuery();
			if(rs.first())			
				array=DataTranslation.resultSetToFields(rs,rs.getMetaData());
			rs.close();
			ps.close();
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getCellEnvironment");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}
		return new FieldArray(array.toArray(new Field[array.size()]));
	}

	public String getSelectedCells(GetSelectedCellsRequestType req)throws GCUBEFault{				
		String toReturn="";
		int limit=req.getLimit();
		int offset=req.getOffset();
		AreasArray areas=req.getAreas();
		logger.trace("Serving getSelectedCells ");
		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			Area[] selection=areas.getAreasList();
			String[] queries=DBCostants.cellFiltering(selection, DBCostants.HCAF_S);
			logger.trace("Gonna use query : "+queries[0]);
			PreparedStatement ps=conn.prepareStatement(queries[0]+" LIMIT "+limit+" OFFSET "+offset);
			PreparedStatement psCount=conn.prepareStatement(queries[1]);
			if((selection!=null)&&(selection.length>0))			
				for(int i=0;i<selection.length;i++){
					ps.setString(i+1, selection[i].getCode());
					psCount.setString(i+1,selection[i].getCode());
				}
			ResultSet rs=ps.executeQuery();
			ResultSet rsCount=psCount.executeQuery();
			rsCount.first();
			toReturn=DBUtils.toJSon(rs, rsCount.getInt(1));
			rs.close();
			rsCount.close();
			ps.close();
			psCount.close();
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getSelectedCells");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}
		return toReturn;
	}

	public String getSpecies(GetSpeciesRequestType req)throws GCUBEFault{
		String toReturn="";
		logger.debug("entro in getSpecies");
		Field[] L = req.getFieldList().getFields();		
		String myQuery = "Select speciesoccursum.* from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ";
		if(L!=null){
			for(int i = 0; i < L.length;i++){
				Field myField = (Field) L[i];
				myQuery+= (String)(DataTranslation.completeFieldNamesMap.get(myField.getName()))+myField.getValue()+" AND ";				

			}
		}
		int index = myQuery.lastIndexOf("AND");
		myQuery = myQuery.substring(0, index-1);
		//myQuery+=" LIMIT "+l+" OFFSET "+o;
		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(myQuery);
			toReturn= DBUtils.toJSon(rs,req.getOffset(),req.getLimit());
			rs.close();
			stmt.close();
			conn.close();			
		}catch(Exception e){
			logger.error("Errors while performing operation",e);
		}	
		logger.trace("Served");
		return toReturn;
	}

	public String getSpeciesByFilters(GetSpeciesByFiltersRequestType req) throws GCUBEFault{
		//TODO Implement Method
		return null;
	}

	public String searchBy2Filters(SearchBy2FiltersRequestType req) throws GCUBEFault{
		logger.debug("entro in searchBy2Filters");
		String toReturn="";


		StringBuilder myQueryCount = new StringBuilder("Select count(speciesoccursum.SPECIESID) As conta from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");
		StringBuilder myQuery = new StringBuilder("Select speciesoccursum.SPECIESID, speciesoccursum.Species, speciesoccursum.Genus, speciesoccursum.FBNAME, speciesoccursum.Scientific_Name, speciesoccursum.English_Name, speciesoccursum.French_Name, speciesoccursum.Spanish_Name from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");

		myQueryCount.append(" "+DataTranslation.filterToString(req.getFilter1())+" AND "+DataTranslation.filterToString(req.getFilter2()));
		myQuery.append(" "+DataTranslation.filterToString(req.getFilter1())+" AND "+DataTranslation.filterToString(req.getFilter2()));
		myQuery.append(" LIMIT "+req.getLimit()+" OFFSET "+req.getOffset());


		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(myQuery.toString());						
			ResultSet rsC = stmt.executeQuery(myQueryCount.toString());
			rsC.next();
			toReturn=DBUtils.toJSon(rs, rsC.getInt(1));			
			rsC.close();
			rs.close();
			stmt.close();
			conn.close();

		}catch(Exception e){
			logger.error("Errors while performing operation",e);
		}
		logger.debug("esco da searchBy2Filters");
		return toReturn;
	}

	public String searchByFilter(SearchByFilterRequestType req) throws GCUBEFault{
		logger.debug("entro in searchBy2Filters");
		String toReturn="";


		StringBuilder myQueryCount = new StringBuilder("Select count(speciesoccursum.SPECIESID) As conta from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");
		StringBuilder myQuery = new StringBuilder("Select speciesoccursum.SPECIESID, speciesoccursum.Species, speciesoccursum.Genus, speciesoccursum.FBNAME, speciesoccursum.Scientific_Name, speciesoccursum.English_Name, speciesoccursum.French_Name, speciesoccursum.Spanish_Name from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");

		myQueryCount.append(" "+DataTranslation.filterToString(req.getFilter()));
		myQuery.append(" "+DataTranslation.filterToString(req.getFilter()));
		myQuery.append(" LIMIT "+req.getLimit()+" OFFSET "+req.getOffset());


		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(myQuery.toString());						
			ResultSet rsC = stmt.executeQuery(myQueryCount.toString());
			rsC.next();
			toReturn=DBUtils.toJSon(rs, rsC.getInt(1));			
			rsC.close();
			rs.close();
			stmt.close();
			conn.close();

		}catch(Exception e){
			logger.error("Errors while performing operation",e);
		}
		logger.debug("esco da searchBy2Filters");
		return toReturn;
	}


	public Resource getResourceInfo(Resource myResource) throws GCUBEFault{
		logger.debug("entro in getResourceInfo");		
		Resource toReturn=myResource;		
		//Cambiare id in SearchId
		//String myQuery ="SELECT * FROM "+to_do.getType()+ " WHERE searchId = "+to_do.getId();
		String myQuery = "";
		if(myResource.getType().equalsIgnoreCase("JOBS")){

			myQuery = DataTranslation.completeResourceListQuery.get(myResource.getType())+ " AND JOBS.searchId ="+myResource.getId();
		}else{
			if(myResource.getType().equalsIgnoreCase("Meta_HSPEC")){
				myQuery = DataTranslation.completeResourceListQuery.get(myResource.getType())+ " AND Meta_HSPEC.searchId ="+myResource.getId();
			}else{
				myQuery ="SELECT * FROM "+myResource.getType()+ " WHERE searchId = "+myResource.getId();
			}
		}

		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(myQuery);
			rs.next();
			toReturn=DataTranslation.getResourceFromResultSet(rs,rs.getMetaData(),myResource.getType());


			//bisogna controllare se siamo in JOBS per via del campo addizionale
			if(myResource.getType().equalsIgnoreCase("JOBS")){
				Field relatedField =new Field();
				relatedField.setName("related");
				relatedField.setType("String");
				relatedField.setValue(getJobMaps(myResource.getId(),conn));
				Field[] fields=toReturn.getAdditionalField().getFields();
				fields[fields.length-1]=relatedField;
			}			
			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			logger.error("Errors while performing operation",e);	
		}
		logger.debug("esco da getresourceInfo");
		return toReturn;
	}

	private String getJobMaps(String jobId,Connection  c)throws SQLException{
		Statement stmt=c.createStatement();
		String query = "Select Path, nameHuman from Maps Where Jobs="+jobId;
		ResultSet rsJ = stmt.executeQuery(query);
		return DBUtils.toJSon(rsJ);
	}



	public ResourceArray getResourceList(GetResourceListRequestType req) throws GCUBEFault{
		logger.debug("entroin getResourceList");
		ArrayList<Resource> resources = new ArrayList<Resource>();
		String query = DataTranslation.completeResourceListQuery.get(req.getType());

		try{
			Class.forName(DBCostants.JDBCClassName).newInstance();
			Connection conn = DriverManager.getConnection(DBCostants.mySQLServerUri);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			ResultSetMetaData metaData=rs.getMetaData();
			while(rs.next()){
				Resource app=DataTranslation.getResourceFromResultSet(rs, metaData,req.getType());
				if(req.getType().equalsIgnoreCase("JOBS")){
					Field relatedField =new Field();
					relatedField.setName("related");
					relatedField.setType("String");
					relatedField.setValue(getJobMaps(app.getId(),conn));
					Field[] fields=app.getAdditionalField().getFields();
					fields[fields.length-1]=relatedField;
				}
				resources.add(app);
			}

			rs.close();
			stmt.close();
			conn.close();
		}catch(Exception e){
			logger.error("Errors while performing getResourceList operation",e);
		}

		logger.debug("esco da getResourceList");
		return new ResourceArray(resources.toArray(new Resource[resources.size()]));
	}
	public VOID generateMap(GenerateMapRequestType req) throws GCUBEFault{
		throw new GCUBEFault();
	}

	public VOID generateMapFromFile(String arg0) throws RemoteException,
	GCUBEFault {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpeciesByArea(GetSpeciesByAreaRequestType arg0)
	throws RemoteException, GCUBEFault {
		// TODO Auto-generated method stub
		return null;
	}


}

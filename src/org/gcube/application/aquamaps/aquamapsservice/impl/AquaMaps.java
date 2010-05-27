package org.gcube.application.aquamaps.aquamapsservice.impl;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.env.SpEnvelope;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobSubmissionThread;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBCostants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.DataTranslation;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.Area;
import org.gcube.application.aquamaps.stubs.AreasArray;
import org.gcube.application.aquamaps.stubs.CalculateEnvelopeRequestType;
import org.gcube.application.aquamaps.stubs.CalculateEnvelopefromCellSelectionRequestType;
import org.gcube.application.aquamaps.stubs.Cell;
import org.gcube.application.aquamaps.stubs.Field;
import org.gcube.application.aquamaps.stubs.FieldArray;
import org.gcube.application.aquamaps.stubs.FileArray;
import org.gcube.application.aquamaps.stubs.GenerateMapRequestType;
import org.gcube.application.aquamaps.stubs.GetAquaMapsPerUserRequestType;
import org.gcube.application.aquamaps.stubs.GetOccurrenceCellsRequestType;
import org.gcube.application.aquamaps.stubs.GetPhylogenyRequestType;
import org.gcube.application.aquamaps.stubs.GetResourceListRequestType;
import org.gcube.application.aquamaps.stubs.GetSelectedCellsRequestType;
import org.gcube.application.aquamaps.stubs.GetSpeciesByAreaRequestType;
import org.gcube.application.aquamaps.stubs.GetSpeciesByFiltersRequestType;
import org.gcube.application.aquamaps.stubs.GetSpeciesRequestType;
import org.gcube.application.aquamaps.stubs.Job;
import org.gcube.application.aquamaps.stubs.Resource;
import org.gcube.application.aquamaps.stubs.ResourceArray;
import org.gcube.application.aquamaps.stubs.SearchBy2FiltersRequestType;
import org.gcube.application.aquamaps.stubs.SearchByFilterRequestType;
import org.gcube.application.aquamaps.stubs.StringArray;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.util.FromResultSetToObject;
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

	public int deleteSubmitted(StringArray submittedIds)throws GCUBEFault{
		int toReturn=0;
		DBSession session = null;
		if((submittedIds!=null)&&(submittedIds.getStringList()!=null)&&(submittedIds.getStringList().length>0))
			try{
				session=DBSession.openSession(PoolManager.DBType.mySql);
				PreparedStatement ps1=session.preparedStatement(DBCostants.submittedRetrieval);				
				for(String submittedId:submittedIds.getStringList()){
					logger.trace("Deleting submitted : "+submittedId);
					ps1.setInt(1, Integer.parseInt(submittedId));
					ResultSet rs1=ps1.executeQuery();
					
					if(rs1.first()){
						logger.trace("found submitted ..");
						PreparedStatement psFiles=session.preparedStatement(DBCostants.fileRetrievalByOwner);
						PreparedStatement psDeleteFiles=session.preparedStatement(DBCostants.fileDeletingByOwner);
						PreparedStatement psDeleteSubmitted=session.preparedStatement(DBCostants.deleteSubmittedById);
						if(!rs1.getBoolean("isAquaMap")){
							logger.trace("going to delete AquaMaps for the selected job..");
							PreparedStatement psAquaMaps=session.preparedStatement(DBCostants.AquaMapsListPerJob);
							psAquaMaps.setInt(1,Integer.parseInt(submittedId));
							ResultSet rsAquaMaps=psAquaMaps.executeQuery();				
							while(rsAquaMaps.next()){
								String aquamapsId=rsAquaMaps.getString("searchId");					
								String path=rsAquaMaps.getString("resourcePath");
								if(path!=null){
									logger.trace("deleting files for aquamaps from path : "+path);
									psFiles.setInt(1, Integer.parseInt(aquamapsId));
									ResultSet rsFiles=psFiles.executeQuery();
									while(rsFiles.next()){
										String[] urlParts=rsFiles.getString("Path").split("/");
										ServiceUtils.deleteFile(path+File.pathSeparator+urlParts[urlParts.length-1]);
										logger.trace("deleted file "+rsFiles.getString("nameHuman"));
									}						
								}
								psDeleteFiles.setInt(1,Integer.parseInt(aquamapsId));
								psDeleteFiles.executeUpdate();
								psDeleteSubmitted.setInt(1,Integer.parseInt(aquamapsId));					
								toReturn+=psDeleteSubmitted.executeUpdate();
							}
							logger.trace("deleted "+toReturn+" AquaMap Objects");				
						}
						String toDeletePath=rs1.getString("resourcePath");
						if(toDeletePath!=null){
							logger.trace("deleting files for selected from path : "+toDeletePath);
							psFiles.setInt(1, Integer.parseInt(submittedId));
							ResultSet rsFiles=psFiles.executeQuery();
							while(rsFiles.next()){
								String[] urlParts=rsFiles.getString("Path").split("/");
								ServiceUtils.deleteFile(toDeletePath+File.pathSeparator+urlParts[urlParts.length-1]);
								logger.trace("deleted file "+rsFiles.getString("nameHuman"));
							}						
						}
						psDeleteFiles.setInt(1,Integer.parseInt(submittedId));
						psDeleteFiles.executeUpdate();
						psDeleteSubmitted.setInt(1,Integer.parseInt(submittedId));
						toReturn+=psDeleteSubmitted.executeUpdate();
					}
				}
//				session.close();
				logger.trace("Total deleted submitted count : "+toReturn);
				
			}catch(SQLException e){
				logger.error("SQLException, unable to serve getjobList");
				logger.trace("Raised Exception", e);
				throw new GCUBEFault();
			} catch (Exception e){
				logger.error("General Exception, unable to contact DB");
				logger.trace("Raised Exception", e);
				throw new GCUBEFault();
			}finally{
				try {
					session.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return toReturn;
	}




	public FieldArray calculateEnvelope(CalculateEnvelopeRequestType req)throws GCUBEFault{
		logger.trace("Serving calculateEnvelope");
		DBSession session = null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			String query =DBCostants.calculateGoodCells(req.isUseFAO(), req.isUseBounding(), req.getFaoAreas(), req.getBoundingNorth(),	 req.getBoundingSouth(), req.getBoundingWest(), req.getBoundingEast());
			logger.trace("submitting query "+query);
			PreparedStatement ps = session.preparedStatement(query);
			ps.setString(1, req.getSpeciesID());
			ResultSet rs=ps.executeQuery();			
			List<org.gcube.application.aquamaps.stubs.dataModel.Cell> foundCells =FromResultSetToObject.getCell(rs);
			logger.trace("found "+foundCells.size()+" cells");

			ps=session.preparedStatement(DBCostants.completeCellById);
			List<org.gcube.application.aquamaps.stubs.dataModel.Cell> toAnalyze=new ArrayList<org.gcube.application.aquamaps.stubs.dataModel.Cell>(); 
			for(org.gcube.application.aquamaps.stubs.dataModel.Cell cell: foundCells){
				ps.setString(1, cell.getCode());
				rs=ps.executeQuery();
				toAnalyze.addAll(FromResultSetToObject.getCell(rs));
			}			
			//String returnValue=DBUtils.toJSon(rs);
			/*String fileName=(new GregorianCalendar()).getTimeInMillis()+".json";
			logger.trace("saving to "+fileName);
			File dir=new File(ServiceContext.getContext().getPersistenceRoot()+File.separator+"ResultSetFiles");
			dir.mkdirs();			
			File file=new File(dir.getAbsolutePath(),fileName);
			//file.mkdirs();
			FileWriter writer=new FileWriter(file);
			writer.write(returnValue);
			writer.close();
			logger.trace("done");*/
			if(toAnalyze.size()<10) return null;
			ps = session.preparedStatement(DBCostants.completeSpeciesById);			
			ps.setString(1, req.getSpeciesID());
			rs = ps.executeQuery();
			Species spec=FromResultSetToObject.getSpecies(rs).get(0);
			if(req.isUseBottomSeaTempAndSalinity())
				spec.getFieldbyName(Species.Tags.Layer).setValue("b");
			else spec.getFieldbyName(Species.Tags.Layer).setValue("u");
			SpEnvelope envelope=new SpEnvelope();
			envelope.reCalculate(spec, toAnalyze);
			ArrayList<Field> array=new ArrayList<Field>();
			for(org.gcube.application.aquamaps.stubs.dataModel.Field f:spec.attributesList){
				Field toAdd=new Field();
				toAdd.setName(f.getName());
				toAdd.setType(f.getType().toString());
				toAdd.setValue(f.getValue());
				array.add(toAdd);
			}
//			session.close();		
			logger.trace("re-calculation complete");
			return new FieldArray(array.toArray(new Field[array.size()]));
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getjobList");
			logger.trace("Raised Exception", e);
			throw new GCUBEFault();
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
			throw new GCUBEFault();
		}finally{
			try {
				session.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public FieldArray calculateEnvelopefromCellSelection(CalculateEnvelopefromCellSelectionRequestType request)throws GCUBEFault{
		ArrayList<Field> array=new ArrayList<Field>();		
		logger.trace("Serving calculateEnvelopefromCellSelection for speciesID : "+request.getSpeciesID());
		DBSession conn=null;
		try{

			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps = conn.preparedStatement(DBCostants.completeSpeciesById);			
			ps.setString(1, request.getSpeciesID());
			ResultSet rs = ps.executeQuery();
			Species spec=FromResultSetToObject.getSpecies(rs).get(0);
			/*if(request.isUseBottomSeaTempAndSalinity())
				spec.getFieldbyName(Species.Tags.Layer).setValue("b");
			else spec.getFieldbyName(Species.Tags.Layer).setValue("u");*/

			ps=conn.preparedStatement(DBCostants.completeCellById);
			List<org.gcube.application.aquamaps.stubs.dataModel.Cell> toAnalyze=new ArrayList<org.gcube.application.aquamaps.stubs.dataModel.Cell>(); 
			for(Cell cell: request.getCells().getCellList()){
				ps.setString(1, cell.getCode());
				rs=ps.executeQuery();
				toAnalyze.addAll(FromResultSetToObject.getCell(rs));
			}
			SpEnvelope envelope=new SpEnvelope();
			envelope.reCalculate(spec, toAnalyze);
			for(org.gcube.application.aquamaps.stubs.dataModel.Field f:spec.attributesList){
				Field toAdd=new Field();
				toAdd.setName(f.getName());
				toAdd.setType(f.getType().toString());
				toAdd.setValue(f.getValue());
				array.add(toAdd);
			}
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve calculateEnvelopefromCellSelection");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new FieldArray(array.toArray(new Field[array.size()]));
	}

	public String getProfile(String id)throws GCUBEFault{
		logger.trace("getting profile for owner id : "+id);
		String toReturn="";
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=conn.preparedStatement(DBCostants.profileRetrieval);
			ps.setInt(1, Integer.parseInt(id));		
			ResultSet rs=ps.executeQuery();
			if(rs.first()){
				String path=rs.getString(1);
				String publicBasePath=ServiceContext.getContext().getWebServiceURL();
				String realPath=ServiceContext.getContext().getPersistenceRoot()+File.separator+
				ServiceContext.getContext().getHttpServerBasePath()+File.separator+path.substring(publicBasePath.length());
				toReturn=ServiceUtils.fileToString(realPath);
			}
			rs.close();
			ps.close();
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getjobList");
			logger.trace("Raised Exception", e);			
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	public String getOccurrenceCells(GetOccurrenceCellsRequestType request)throws GCUBEFault{
		String speciesId=request.getSpeciesID();
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			ResultSet rs=session.executeQuery("select occurrenceCells.* , HCAF_D.DepthMean, HCAF_D.SSTAnMean, HCAF_D.SBTAnMean, HCAF_D.SalinityBMean, HCAF_D.SalinityMean, HCAF_D.PrimProdMean, HCAF_D.IceConAnn  from HCAF_D inner join occurrenceCells on HCAF_D.CsquareCode = occurrenceCells.CsquareCode where occurrenceCells.SpeciesID = '"+speciesId+"'");		
			String toReturn= DBUtils.toJSon(rs,request.getOffset(),request.getOffset()+request.getLimit());
			session.close();
			return toReturn;
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getjobList");
			logger.trace("Raised Exception", e);			
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				session.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public String submitJob(Job req)throws GCUBEFault{
		try{
			logger.trace("Serving submit job "+req.getName());
		JobSubmissionThread thread=new JobSubmissionThread(req);
		ThreadManager.getExecutor().execute(thread);
		return String.valueOf(thread.getId());
		}catch(Exception e){
			logger.error("Unable to execute Job "+req.getName(), e);
			throw new GCUBEFault("Unable to execute, please try again later");
		}
	}

	public String getJobList(String author)throws GCUBEFault{
		logger.trace("Serving get JobList for author : "+author);
		String toReturn="";
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=conn.preparedStatement(DBCostants.AquaMapsListPerAuthor);
			ps.setString(1, author);	
			ps.setBoolean(2, false);
			ResultSet rs=ps.executeQuery();
			toReturn=DBUtils.toJSon(rs);
			rs.close();
			ps.close();
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getjobList");
			logger.trace("Raised Exception", e);			
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	public FileArray getRelatedFiles(String owner)throws GCUBEFault{
		logger.trace("getting file List for owner id : "+owner);
		FileArray toReturn=null;
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);			
			ResultSet rs=conn.executeQuery("Select * from Files where owner = "+owner);
			ArrayList<org.gcube.application.aquamaps.stubs.File> files=new ArrayList<org.gcube.application.aquamaps.stubs.File>();
			while(rs.next()){
				org.gcube.application.aquamaps.stubs.File f=new org.gcube.application.aquamaps.stubs.File();
				f.setName(rs.getString(4));
				f.setType(rs.getString(5));
				f.setUrl(rs.getString(3));
				files.add(f);			
			}
			toReturn=new FileArray(files.toArray(new org.gcube.application.aquamaps.stubs.File[files.size()]));
			rs.close();			
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getjobList");
			logger.trace("Raised Exception", e);			
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}



	public String getAquaMapsList(String jobId)throws GCUBEFault{
		logger.trace("Serving getAquaMapsList for job Id : "+jobId);
		String toReturn="";
		DBSession conn = null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=conn.preparedStatement(DBCostants.AquaMapsListPerJob);		
			ps.setInt(1, Integer.parseInt(jobId));
			ResultSet rs=ps.executeQuery();
			toReturn=DBUtils.toJSon(rs);
			rs.close();
			ps.close();
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getAquaMapsList");
			logger.trace("Raised Exception", e);
		}catch (NumberFormatException e){
			logger.error("Invalid jobId");
			logger.trace("Raised Exception",e);		
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	public FieldArray getSpeciesEnvelop(String speciesId)throws GCUBEFault{
		ArrayList<Field> array=new ArrayList<Field>();		
		logger.trace("Serving getSpeciesEnvelop for speciesID : "+speciesId);
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=conn.preparedStatement(DBCostants.speciesEnvelop);
			ps.setString(1, speciesId);
			ResultSet rs = ps.executeQuery();
			if(rs.first())			
				array=DataTranslation.resultSetToFields(rs,rs.getMetaData());
			rs.close();
			ps.close();
			//conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getSpeciesEnvelop");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new FieldArray(array.toArray(new Field[array.size()]));
	}

	public FieldArray getCellEnvironment(String code)throws GCUBEFault{
		ArrayList<Field> array=new ArrayList<Field>();
		logger.trace("Serving getCellEnvironment for cellCode : "+code);
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=conn.preparedStatement(DBCostants.cellEnvironment);
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
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new FieldArray(array.toArray(new Field[array.size()]));
	}

	public String getGoodCells(String speciesId)throws GCUBEFault{
		logger.trace("Serving getGoodCell for species ID : " +speciesId);
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=conn.preparedStatement(DBCostants.cellEnvironment);
			conn.close();
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getCellEnvironment");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}	finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return "";
	}


	public String getSelectedCells(GetSelectedCellsRequestType req)throws GCUBEFault{				
		String toReturn="";
		int limit=req.getLimit();
		int offset=req.getOffset();
		String sortColumn=req.getSortColumn();
		String sortDirection=req.getSortDirection();
		AreasArray areas=req.getAreas();
		logger.trace("Serving getSelectedCells ");
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			Area[] selection=areas.getAreasList();
			String[] queries=DBCostants.cellFiltering(selection, DBCostants.HCAF_S);
			logger.trace("Gonna use query : "+queries[0]);
			PreparedStatement ps=conn.preparedStatement(queries[0]+((sortColumn!=null)?" order by "+sortColumn+" "+sortDirection:"")+" LIMIT "+limit+" OFFSET "+offset);
			PreparedStatement psCount=conn.preparedStatement(queries[1]);
			if((selection!=null)&&(selection.length>0))			
				for(int i=0;i<selection.length;i++){
					ps.setInt(i+1, Integer.parseInt(selection[i].getCode()));
					psCount.setInt(i+1,Integer.parseInt(selection[i].getCode()));
				}
			ResultSet rs=ps.executeQuery();
			ResultSet rsCount=psCount.executeQuery();
			rsCount.first();
			toReturn=DBUtils.toJSon(rs, rsCount.getInt(1));
			rs.close();
			rsCount.close();
			ps.close();
			psCount.close();
			
		}catch(SQLException e){
			logger.error("SQLException, unable to serve getSelectedCells");
			logger.trace("Raised Exception", e);
		} catch (Exception e){
			logger.error("General Exception, unable to contact DB");
			logger.trace("Raised Exception", e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	/**
	 * return a complete list of available species (within limit and offset specified) ordered by a specified column in a specified order
	 * 
	 * @param req
	 * @return
	 * @throws GCUBEFault
	 */


	public String getSpecies(GetSpeciesRequestType req)throws GCUBEFault{
		String toReturn="";
		logger.debug("entro in getSpecies");
		/*Field[] L = req.getFieldList().getFields();		
		String myQuery = "Select speciesoccursum.* from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ";
		if(L!=null){
			for(int i = 0; i < L.length;i++){
				Field myField = (Field) L[i];
				myQuery+= (String)(DataTranslation.completeFieldNamesMap.get(myField.getName()))+myField.getValue()+" AND ";				

			}
		}*/
		String sortColumn=req.getSortColumn();
		String sortDirection=req.getSortDirection();
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);			
			ResultSet rs = conn.executeQuery("Select * from "+DBCostants.speciesOccurSum+
					((sortColumn!=null)?" order by "+DBCostants.speciesOccurSum+"."+sortColumn+" "+sortDirection:"")+" LIMIT "+req.getLimit()+" OFFSET "+req.getOffset());

			ResultSet rsCount=conn.executeQuery("Select count("+DBCostants.SpeciesID+") from "+DBCostants.speciesOccurSum);
			rsCount.first();
			int count=rsCount.getInt(1);
			toReturn= DBUtils.toJSon(rs,count);
			rsCount.close();

			rs.close();

//			conn.close();			
		}catch(Exception e){
			logger.error("Errors while performing operation",e);
		}	finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.trace("Served");
		return toReturn;
	}

	/**
	 * return a list of species filtered by 3 groups of filters (species characteristics OR species names OR species codes) 
	 * 
	 * @param req
	 * @return
	 * @throws GCUBEFault
	 */



	public String getSpeciesByFilters(GetSpeciesByFiltersRequestType req) throws GCUBEFault{
		logger.trace("Serving getSpecies by filters");
		String toReturn="";
		DBSession conn=null;
		try{
			String[] queries=DBCostants.filterSpecies(req);
			logger.trace("Gonna use query :"+queries[0]);
			String sortColumn=req.getSortColumn();
			String sortDirection=req.getSortDirection();
			conn = DBSession.openSession(PoolManager.DBType.mySql);

			ResultSet rs = conn.executeQuery(queries[0]+
					((sortColumn!=null)?" order by "+DBCostants.speciesOccurSum+"."+sortColumn+" "+sortDirection:"")+" LIMIT "+req.getLimit()+" OFFSET "+req.getOffset());

			ResultSet rsCount=conn.executeQuery(queries[1]);
			rsCount.first();
			int count=rsCount.getInt(1);
			toReturn= DBUtils.toJSon(rs,count);
			rsCount.close();

			rs.close();

//			conn.close();		
		}catch(Exception e){
			logger.error("Exception occurred : "+e.getMessage());
			logger.trace("Errors while performing operation",e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	public String searchBy2Filters(SearchBy2FiltersRequestType req) throws GCUBEFault{
		logger.debug("entro in searchBy2Filters");
		String toReturn="";
		/*	String sortColumn=req.getSortColumn();
		String sortDirection=req.getSortDirection();

		StringBuilder myQueryCount = new StringBuilder("Select count(speciesoccursum.SPECIESID) As conta from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");
		StringBuilder myQuery = new StringBuilder("Select speciesoccursum.SPECIESID, speciesoccursum.Species, speciesoccursum.Genus, speciesoccursum.FBNAME, speciesoccursum.Scientific_Name, speciesoccursum.English_Name, speciesoccursum.French_Name, speciesoccursum.Spanish_Name from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");

		myQueryCount.append(" "+DataTranslation.filterToString(req.getFilter1())+" AND "+DataTranslation.filterToString(req.getFilter2()));
		myQuery.append(" "+DataTranslation.filterToString(req.getFilter1())+" AND "+DataTranslation.filterToString(req.getFilter2()));
		myQuery.append(((sortColumn!=null)?" order by "+sortColumn+" "+sortDirection:"")+" LIMIT "+req.getLimit()+" OFFSET "+req.getOffset());


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
		logger.debug("esco da searchBy2Filters");*/
		return toReturn;
	}

	public String searchByFilter(SearchByFilterRequestType req) throws GCUBEFault{
		/*		logger.debug("entro in searchBy2Filters");
		String toReturn="";
		String sortColumn=req.getSortColumn();
		String sortDirection=req.getSortDirection();

		StringBuilder myQueryCount = new StringBuilder("Select count(speciesoccursum.SPECIESID) As conta from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");
		StringBuilder myQuery = new StringBuilder("Select speciesoccursum.SPECIESID, speciesoccursum.Species, speciesoccursum.Genus, speciesoccursum.FBNAME, speciesoccursum.Scientific_Name, speciesoccursum.English_Name, speciesoccursum.French_Name, speciesoccursum.Spanish_Name from hspen, speciesoccursum WHERE hspen.SpeciesID = speciesoccursum.SPECIESID AND ");

		myQueryCount.append(" "+DataTranslation.filterToString(req.getFilter()));
		myQuery.append(" "+DataTranslation.filterToString(req.getFilter()));
		myQuery.append(((sortColumn!=null)?" order by "+sortColumn+" "+sortDirection:"")+" LIMIT "+req.getLimit()+" OFFSET "+req.getOffset());


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
		return toReturn;*/
		return null;
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
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			ResultSet rs = conn.executeQuery(myQuery);
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
//			conn.close();
		}catch(Exception e){
			logger.error("Errors while performing operation",e);	
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.debug("esco da getresourceInfo");
		return toReturn;
	}

	private String getJobMaps(String jobId,DBSession  c)throws Exception{

		String query = "Select Path, nameHuman from Maps Where Jobs="+jobId;
		ResultSet rsJ = c.executeQuery(query);
		return DBUtils.toJSon(rsJ);
	}



	public ResourceArray getResourceList(GetResourceListRequestType req) throws GCUBEFault{
		logger.debug("entroin getResourceList");
		ArrayList<Resource> resources = new ArrayList<Resource>();
		String query = DataTranslation.completeResourceListQuery.get(req.getType());
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			ResultSet rs = conn.executeQuery(query);

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

			conn.close();
		}catch(Exception e){
			logger.error("Errors while performing getResourceList operation",e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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


	public String getAquaMapsPerUser(GetAquaMapsPerUserRequestType arg0)throws GCUBEFault{

		String sortColumn=arg0.getSortColumn();
		String sortDir=arg0.getSortDirection();
		int offset=arg0.getOffset();
		int limit=arg0.getLimit();
		String user=arg0.getUserID();
		String toReturn="{\"data\":[],\"totalcount\":0}";
		boolean isAquaMaps=arg0.isAquamaps();
		String jobId=arg0.getJobId();
		DBSession conn=null;
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			String jobFilter=((jobId!=null)&&isAquaMaps)?" AND jobId=? ":"";
			/*
			 * query parameters 
			 * 			author
			 * 			isAquaMaps
			 * 			jobId (facoltativo) 
			 */
			String query=DBCostants.AquaMapsListPerAuthor+jobFilter+
				((sortColumn!=null)?" order by "+sortColumn+" "+sortDir:"")+" LIMIT "+limit+" OFFSET "+offset;
			String countQuery="Select count(*) from "+DBCostants.JOB_Table+" where author = ? AND isAquaMap = ? "+jobFilter;
			PreparedStatement ps=conn.preparedStatement(query);
			PreparedStatement psCount=conn.preparedStatement(countQuery);
			logger.debug("Request isAquamap="+isAquaMaps+" query : "+query+"; countQuery : "+countQuery);
			ps.setString(1, user);
			psCount.setString(1, user);
			ps.setBoolean(2,isAquaMaps);
			psCount.setBoolean(2, isAquaMaps);
			
			if((jobId!=null)&&isAquaMaps){
				ps.setString(3, jobId);
				psCount.setString(3, jobId);
			}
			ResultSet rsCount=psCount.executeQuery();
			if(rsCount.next()){
				int totalCount=rsCount.getInt(1);
				if(totalCount>0){
					ResultSet rs=ps.executeQuery();
					toReturn=DBUtils.toJSon(rs,totalCount);
					rs.close();
					ps.close();					
				}
			}
			rsCount.close();
			psCount.close();
//			conn.close();			
		}catch(Exception e ){
			logger.error("Exception while trying to serve -getAquaMapsPerUser : user = "+user+" sort ("+sortColumn+","+sortDir+") "+offset+"-"+limit,e);
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return toReturn;
	}

	public VOID markSaved(StringArray ids)throws GCUBEFault{
		DBSession conn=null;
		if((ids!=null)&&(ids.getStringList()!=null)&&(ids.getStringList().length>0))			
		try{
			conn = DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= conn.preparedStatement(DBCostants.markSaved);
			for(String id:ids.getStringList()){
				try{
				ps.setString(1,id);
				ps.execute();
				}catch(Exception e1){
					logger.error("Unable to mark "+id+" as saved",e1);
				}
			}
//			conn.close();
		}catch(Exception e){
			logger.error(e);
			throw new GCUBEFault();
		}finally{
			try {
				conn.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}

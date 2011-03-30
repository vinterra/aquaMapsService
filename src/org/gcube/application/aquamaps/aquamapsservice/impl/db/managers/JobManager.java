package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.PublisherImpl;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.dataModel.enhanced.*;
import org.gcube.application.aquamaps.dataModel.Types.*;
import org.gcube.application.aquamaps.dataModel.fields.*;

public class JobManager extends SubmittedManager{

	public static final String toDropTables="temptables";
	public static final String toDropTablesJobId=SubmittedFields.jobid+"";
	public static final String toDropTablesTableName="tablename";
	
	public static final String tempFolders="tempfolders";
	public static final String tempFoldersJobId=SubmittedFields.jobid+"";
	public static final String tempFoldersFolderName="foldername";
	
	public static final String selectedSpecies="selectedspecies";
	public static final String selectedSpeciesStatus=SubmittedFields.status+"";
	public static final String selectedSpeciesJobId=SubmittedFields.jobid+"";
	public static final String selectedSpeciesSpeciesID=SpeciesOccursumFields.speciesid+"";
	public static final String selectedSpeciesIsCustomized="iscustomized";
	//******************************************* working tables management ***************************************

	protected static final String workingTables="workingtables";
	protected static final String tableField="tablename";
	protected static final String tableTypeField="tabletype";
	
	
	
	protected static void setWorkingTable(int submittedId,String tableType,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			row.add(new Field(tableTypeField,tableType,FieldType.STRING));
			row.add(new Field(tableField,tableName,FieldType.STRING));
			rows.add(row);			
			session.insertOperation(workingTables, rows);
			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	protected static String getWorkingTable (int submittedId,String tableType)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			filter.add(new Field(tableTypeField,tableType,FieldType.STRING));
			ResultSet rs= session.executeFilteredQuery(filter, workingTables, tableField, "ASC");
			if(rs.next())
			return rs.getString(tableField);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	
	public static void setWorkingHCAF(int submittedId,String tableName)throws Exception{
		setWorkingTable(submittedId,ResourceType.HCAF.toString(),tableName);
	}
	public static void setWorkingHSPEN(int submittedId,String tableName)throws Exception{
		setWorkingTable(submittedId,ResourceType.HSPEN.toString(),tableName);
	}
	public static void setWorkingHSPEC(int submittedId,String tableName)throws Exception{
		setWorkingTable(submittedId,ResourceType.HSPEC.toString(),tableName);
	}
	public static String getWorkingHCAF(int submittedId)throws Exception{
		return getWorkingTable(submittedId,ResourceType.HCAF.toString());
	}
	public static String getWorkingHSPEN(int submittedId)throws Exception{
		return getWorkingTable(submittedId,ResourceType.HSPEN.toString());
	}
	public static String getWorkingHSPEC(int submittedId)throws Exception{
		return getWorkingTable(submittedId,ResourceType.HSPEC.toString());
	}

	public static void addToDropTableList(int jobId,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(toDropTablesJobId,jobId+"",FieldType.INTEGER));
			row.add(new Field(toDropTablesTableName,tableName,FieldType.STRING));
			rows.add(row);			
			session.insertOperation(toDropTables, rows);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void addToDeleteTempFolder(int jobId,String folderName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(tempFoldersJobId,jobId+"",FieldType.INTEGER));
			row.add(new Field(tempFoldersFolderName,folderName,FieldType.STRING));
			rows.add(row);			
			session.insertOperation(tempFolders, rows);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static void updateSpeciesStatus(int jobId,String speciesId[],SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> values= new ArrayList<List<Field>>();
			List<List<Field>> keys= new ArrayList<List<Field>>();
			for(String id:speciesId){
				List<Field> value=new ArrayList<Field>();
				value.add(new Field(selectedSpeciesStatus,status+"",FieldType.STRING));			
				values.add(value);

				List<Field> key=new ArrayList<Field>();
				key.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
				key.add(new Field(selectedSpeciesSpeciesID,id,FieldType.STRING));
				keys.add(key);
			}
			
			session.updateOperation(selectedSpecies, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			
			List<Field> filters= new ArrayList<Field>();
			filters.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
				if(status!=null) filters.add(new Field(selectedSpeciesStatus,status+"",FieldType.STRING));
			ResultSet rs = session.executeFilteredQuery(filters, selectedSpecies, selectedSpeciesStatus, "ASC");
			ArrayList<String> toReturn=new ArrayList<String>(); 
			while(rs.next()){
				toReturn.add(rs.getString(selectedSpeciesSpeciesID));
			}
			return toReturn.toArray(new String[toReturn.size()]);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static boolean isJobComplete(int jobId) throws Exception{
		DBSession session=null;
		try{
			logger.debug("Checking if "+jobId+" is completed..");
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.jobid+"",jobId+"",FieldType.INTEGER));
			long count=session.getCount(submittedTable, filter);
			logger.debug("Found "+count+" aquamaps object for jobId ");
			Field statusField=new Field(SubmittedFields.status+"",SubmittedStatus.Error+"",FieldType.STRING);
			filter.add(statusField);
			long errorCount=session.getCount(submittedTable, filter);
			logger.debug("Found "+count+" ERROR aquamaps object for jobId ");
			statusField.setValue(SubmittedStatus.Completed+"");
			long completedCount=session.getCount(submittedTable, filter);
			logger.debug("Found "+count+" COMPLETED aquamaps object for jobId ");
			return (completedCount+errorCount-count==0);
			
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void cleanTemp(int jobId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			logger.debug("cleaning tables for : "+jobId);
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(toDropTablesJobId,jobId+"",FieldType.INTEGER));
			ResultSet rs=session.executeFilteredQuery(filter, toDropTables, toDropTablesTableName, "ASC");
			while(rs.next()){
				String table=rs.getString(toDropTablesTableName);
				session.dropTable(table);
			}
			session.deleteOperation(toDropTables, filter);

			logger.debug("cleaning folders for : "+jobId);
			rs=session.executeFilteredQuery(filter, tempFolders, tempFoldersFolderName, "ASC");
			
			while(rs.next()){
				String folder=rs.getString(tempFoldersFolderName);
				try{					
					File tempDir=new File(folder);
					if(tempDir.exists()){
						FileUtils.cleanDirectory(tempDir);
						FileUtils.deleteDirectory(tempDir);
					}else logger.warn("Wrong file name "+folder);

				}catch(Exception e1){
					logger.debug("unable to delete temp Folder : "+folder,e1);
				}
			}
			session.deleteOperation(tempFolders, filter);

			logger.debug("cleaning speceisSelection for : "+jobId);
			session.deleteOperation(selectedSpecies, filter);
			logger.debug("cleaning references to working tables for : "+jobId);
			session.deleteOperation(workingTables, filter);
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static boolean isSpeciesListReady(int jobId,Set<String> toCheck)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
			Field idField=new Field(selectedSpeciesSpeciesID,"",FieldType.STRING);
			filter.add(idField);
			for(String id : toCheck){
				idField.setValue(id);
				ResultSet rs= session.executeFilteredQuery(filter, selectedSpecies, selectedSpeciesSpeciesID, "ASC");
				if(rs.next()){
					if(!rs.getString(selectedSpeciesStatus).equalsIgnoreCase(SpeciesStatus.Ready+""))
							return false;
				}else throw new Exception("SpeciesID "+id+" not found in jobId "+jobId+" selection");
			}
			return true;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}



	@Deprecated
	public static void createGroup (int jobId)throws Exception{
		throw new Exception("NOT YET IMPLEMENTED");
		
		/* Gather information for required group
			GeoServer layer references are in DB as submitted.gis
				needed layer-style map
			LayersInfoType information may be available at generation time
			
		*/
		
		
		
		
		
		
//		try{
//			logger.trace("Starting job Id : "+jobId+" layers group creation ..");
//			logger.trace("Looking for generated layers");
//			ArrayList<String> layers=new ArrayList<String>();
//			for(Submitted obj:getObjects(jobId)){				
//				if((obj.getGis()!=null)&&(!obj.getGis().equalsIgnoreCase("null")))
//					layers.add(obj.getGis());
//			}
//			if(layers.size()>0){
//				logger.trace("found "+layers.size()+" generated layer(s), looking for related style(s)");
//				GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
//				GroupGenerationRequest req=new GroupGenerationRequest();
//				req.setLayers(layers);
//				for(String layerId:layers){
//					LayerRest lRest=caller.getLayer(layerId);
//					req.getStyles().put(layerId, lRest.getDefaultStyle());
//				}								
//				req.setName(String.valueOf(jobId));				
//				req.setSubmittedId(jobId);
//				if(GeneratorManager.requestGeneration(req))logger.trace("Generation of jobId "+jobId+" layers group complete");
//				else throw new Exception("Unable to generate Group");
//			}else logger.trace("No generated layers found for job Id "+jobId);
//		}catch(Exception e){
//			logger.error("Unable to complete group "+jobId+" generation",e);
//			throw e;
//		}
		
		
	}




	public static boolean isSpeciesSetCustomized(int submittedId,Set<String> ids)throws Exception{
		DBSession session=null;		
		try{
			logger.trace("Checking species customizations flag..");
			session=DBSession.getInternalDBSession();
			
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(selectedSpeciesJobId,submittedId+"",FieldType.INTEGER));
			Field idField=new Field(selectedSpeciesSpeciesID,"",FieldType.STRING);
			filter.add(idField);
			for(String id : ids){
				idField.setValue(id);
				ResultSet rs= session.executeFilteredQuery(filter, selectedSpecies, selectedSpeciesSpeciesID, "ASC");
				if(rs.next()){
					if(rs.getInt(selectedSpeciesIsCustomized)==0)
							return false;
				}else throw new Exception("SpeciesID "+id+" not found in jobId "+submittedId+" selection");
			}
			
			return true;
		}catch(Exception e){
			logger.error("unable to check species customization flag",e);
			throw e;
		}finally{
			if(!session.getConnection().isClosed())session.close();
		}
	}





	/**Creates a new entry in Job table and AquaMap table (for every aquamap object in job)
	 * 
	 * @return new job id
	 */
	public static int insertNewJob(Job toPerform) throws Exception{
		logger.trace("Creating new pending Job");
		String myData = ServiceUtils.getDate();
		DBSession session=null;
//		String submittedInsertion="INSERT INTO "+submittedTable+" ("+SubmittedFields.title+","+SubmittedFields.author+","+SubmittedFields.date+","+SubmittedFields.status+","+
//		SubmittedFields.isAquaMap+","+SubmittedFields.type+","+SubmittedFields.sourceHCAF+","+SubmittedFields.sourceHSPEC+","+
//		SubmittedFields.sourceHSPEN+","+SubmittedFields.jobid+") values (?,?,?,?,?,?,?,?,?,?)";
		
		////*************** Send to publisher
		try{
		
			session=DBSession.getInternalDBSession();
		Publisher publisher= PublisherImpl.getPublisher();
		
		int jobId=publisher.publishJob(toPerform);
		
		toPerform=publisher.getJobById(jobId);
		
		//***************** Now both job and objects has updated references
		//***************** Store in internal DB
		
		Field author=new Field(SubmittedFields.author+"",toPerform.getAuthor(),FieldType.STRING);
		Field date= new Field (SubmittedFields.date+"",myData,FieldType.STRING);
		Field isAquaMaps= new Field(SubmittedFields.isaquamap+"","true",FieldType.BOOLEAN);
		Field sourceHCAF=new Field(SubmittedFields.sourcehcaf+"",toPerform.getSourceHCAF().getSearchId()+"",FieldType.INTEGER);
		Field sourceHSPEN=new Field(SubmittedFields.sourcehspen+"",toPerform.getSourceHSPEN().getSearchId()+"",FieldType.INTEGER);
		Field sourceHSPEC=new Field(SubmittedFields.sourcehspec+"",toPerform.getSourceHSPEC().getSearchId()+"",FieldType.INTEGER);
		Field jobIdField=new Field(SubmittedFields.jobid+"",toPerform.getId()+"",FieldType.INTEGER);
		
		
		List<List<Field>> aquamapsList=new ArrayList<List<Field>>();
		for(AquaMapsObject obj: toPerform.getAquaMapsObjectList()){
			List<Field> objRow= new ArrayList<Field>();
			objRow.add(new Field(SubmittedFields.title+"",toPerform.getName(),FieldType.STRING));
			objRow.add(author);
			objRow.add(date);
			
			objRow.add(new Field(SubmittedFields.status+"",obj.getStatus()+"",FieldType.STRING));
			
			objRow.add(isAquaMaps);
			objRow.add(sourceHCAF);
			objRow.add(sourceHSPEN);
			objRow.add(sourceHSPEC);
			objRow.add(jobIdField);
			//FIXME Comment
//			objRow.add(new Field(SubmittedFields.gisenabled+"",obj.()+"",FieldType.STRING));			
			objRow.add(new Field(SubmittedFields.searchid+"",obj.getId()+"",FieldType.STRING));
			aquamapsList.add(objRow);
		}
		session.insertOperation(submittedTable, aquamapsList);
		
		
		List<List<Field>> jobList=new ArrayList<List<Field>>();
		List<Field> jobRow= new ArrayList<Field>();
		jobRow.add(new Field(SubmittedFields.title+"",toPerform.getName(),FieldType.STRING));
		jobRow.add(author);
		jobRow.add(date);
		
		jobRow.add(new Field(SubmittedFields.status+"",toPerform.getStatus()+"",FieldType.STRING));
		
		jobRow.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
		jobRow.add(sourceHCAF);
		jobRow.add(sourceHSPEN);
		jobRow.add(sourceHSPEC);
		jobRow.add(new Field(SubmittedFields.searchid+"",toPerform.getId()+"",FieldType.INTEGER));
		//FIXME Comment
//		jobRow.add(new Field(SubmittedFields.gis+"",toPerform.getWmsContextId(),FieldType.STRING));
		jobList.add(jobRow);
		session.insertOperation(submittedTable, jobList);
		
		
		if(!toPerform.getStatus().equals(SubmittedStatus.Completed)){
			//Initialize working variables 
			if((toPerform.getSelectedSpecies().size()>0)){
			
							boolean hasPerturbation=false;
							if((toPerform.getEnvelopeCustomization().size()>0)) hasPerturbation=true;
			
							boolean hasWeight=false;
							if((toPerform.getEnvelopeWeights().size()>0)) hasWeight=true;
			
							
							List<Field> fields=new ArrayList<Field>();
							fields.add(new Field(selectedSpeciesJobId,"",FieldType.INTEGER));
							fields.add(new Field(selectedSpeciesSpeciesID,"",FieldType.STRING));
							fields.add(new Field(selectedSpeciesStatus,"",FieldType.STRING));
							fields.add(new Field(selectedSpeciesIsCustomized,"",FieldType.BOOLEAN));
							
							PreparedStatement psSpecies=session.getPreparedStatementForInsert(fields, selectedSpecies);
							fields.get(0).setValue(jobId+"");
							for(Species s:toPerform.getSelectedSpecies()){
								String status=SpeciesStatus.Ready.toString();
								if((hasWeight)&&(toPerform.getEnvelopeWeights().containsKey(s.getId())))status=SpeciesStatus.toGenerate.toString();
								if((hasPerturbation)&&(toPerform.getEnvelopeCustomization().containsKey(s.getId())))status=SpeciesStatus.toCustomize.toString();
								fields.get(1).setValue(s.getId());
								fields.get(2).setValue(status);
								fields.get(3).setValue((hasWeight||hasPerturbation)+"");
								psSpecies=session.fillParameters(fields, psSpecies);
								psSpecies.executeUpdate();
							}
						}else throw new Exception("Invalid job, no species found");
						
						
						//Setting selected sources as working tables
						
						setWorkingHCAF(jobId, SourceManager.getSourceName(ResourceType.HCAF, toPerform.getSourceHCAF().getSearchId()));
						setWorkingHSPEC(jobId, SourceManager.getSourceName(ResourceType.HSPEC, toPerform.getSourceHSPEC().getSearchId()));
						setWorkingHSPEN(jobId, SourceManager.getSourceName(ResourceType.HSPEN, toPerform.getSourceHSPEN().getSearchId()));
			
			
		}
		return jobId;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	protected static int deleteJob(int submittedId)throws Exception{
		logger.trace("Deleting job "+submittedId);
		List<Submitted> objects=getObjects(submittedId);
		logger.trace("Found "+objects.size()+" object(s) to delete..");
		int count=0;
		for(Submitted obj:objects) {
			try{
				count+=deleteFromTables(obj.getSearchId());
			}catch(Exception e){
				logger.error("Unable to delete object "+obj);
			}
		}
		
		Publisher pub=PublisherImpl.getPublisher();
		pub.removeJob(submittedId);		
		count+=deleteFromTables(submittedId);
		return count;
	}

	public static List<Submitted> getObjects (int jobId)throws Exception{
		List<Field> filters=new ArrayList<Field>();
		Field jobIdField=new Field();
		jobIdField.setValue(jobId+"");
		jobIdField.setType(FieldType.INTEGER);
		jobIdField.setName(SubmittedFields.jobid+"");
		filters.add(jobIdField);
		return getList(filters);
	}
}

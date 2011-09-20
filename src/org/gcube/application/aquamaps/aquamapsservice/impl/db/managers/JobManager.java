package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.engine.gis.GroupGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.wrapper.PagedRequestSettings.OrderDirection;
import org.gcube.application.aquamaps.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.dataModel.Types.ObjectType;
import org.gcube.application.aquamaps.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.dataModel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.dataModel.enhanced.Field;
import org.gcube.application.aquamaps.dataModel.enhanced.Job;
import org.gcube.application.aquamaps.dataModel.enhanced.Species;
import org.gcube.application.aquamaps.dataModel.enhanced.Submitted;
import org.gcube.application.aquamaps.dataModel.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.dataModel.fields.SubmittedFields;
import org.gcube.application.aquamaps.dataModel.utils.CSVUtils;
import org.gcube.application.aquamaps.dataModel.xstream.AquaMapsXStream;
import org.gcube.common.gis.dataModel.enhanced.LayerInfo;


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
			logger.trace("inserting working table reference "+submittedId+", "+tableType+" : "+tableName);
			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			row.add(new Field(tableTypeField,tableType,FieldType.STRING));
			row.add(new Field(tableField,tableName,FieldType.STRING));
			rows.add(row);	
			try{
				session.insertOperation(workingTables, rows);
			}catch(Exception e1){
				logger.trace("trying toupdate working table reference "+submittedId+", "+tableType+" : "+tableName);
				List<List<Field>> values= new ArrayList<List<Field>>();
				List<Field> value=new ArrayList<Field>();
				value.add(new Field(tableField,tableName,FieldType.STRING));
				values.add(row);
				List<List<Field>> keys= new ArrayList<List<Field>>();
				List<Field> key=new ArrayList<Field>();
				key.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
				key.add(new Field(tableTypeField,tableType,FieldType.STRING));
				keys.add(key);
				session.updateOperation(workingTables, keys, values);
			}
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	protected static String getWorkingTable (int submittedId,String tableType)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			filter.add(new Field(tableTypeField,tableType,FieldType.STRING));
			ResultSet rs= session.executeFilteredQuery(filter, workingTables, tableField, OrderDirection.ASC);
			if(rs.next())
				return rs.getString(tableField);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
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
			if(session!=null) session.close();
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
			try{
				session.insertOperation(tempFolders, rows);
			}catch(Exception e ){
				logger.error("checking already inserted temp folders..");
				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(tempFoldersJobId,jobId+"",FieldType.INTEGER));
				boolean found= false;
				for(List<Field> f:Field.loadResultSet(session.executeFilteredQuery(filter, tempFolders, tempFoldersJobId, OrderDirection.ASC))){
					for(Field g:f)
						if(g.getName().equals(tempFoldersFolderName)&&g.getValue().equals(folderName))
							found=true;
				}
				if(!found)logger.warn("Unable to register temp folder "+folderName);
			}
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
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
			if(values.size()>0)
				session.updateOperation(selectedSpecies, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();

			List<Field> filters= new ArrayList<Field>();
			filters.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
			if(status!=null) filters.add(new Field(selectedSpeciesStatus,status+"",FieldType.STRING));
			ResultSet rs = session.executeFilteredQuery(filters, selectedSpecies, selectedSpeciesStatus, OrderDirection.ASC);
			ArrayList<String> toReturn=new ArrayList<String>(); 
			while(rs.next()){
				toReturn.add(rs.getString(selectedSpeciesSpeciesID));
			}
			return toReturn.toArray(new String[toReturn.size()]);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
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
			logger.debug("Found "+errorCount+" ERROR aquamaps object for jobId ");
			statusField.setValue(SubmittedStatus.Completed+"");
			long completedCount=session.getCount(submittedTable, filter);
			logger.debug("Found "+completedCount+" COMPLETED aquamaps object for jobId ");
			return (completedCount+errorCount-count==0);

		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static void cleanTemp(int jobId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			logger.debug("cleaning tables for : "+jobId);
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(toDropTablesJobId,jobId+"",FieldType.INTEGER));
			ResultSet rs=session.executeFilteredQuery(filter, toDropTables, toDropTablesTableName, OrderDirection.ASC);
			while(rs.next()){
				String table=rs.getString(toDropTablesTableName);
				session.dropTable(table);
			}
			session.deleteOperation(toDropTables, filter);

			logger.debug("cleaning folders for : "+jobId);
			rs=session.executeFilteredQuery(filter, tempFolders, tempFoldersFolderName, OrderDirection.ASC);

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

			logger.debug("Cleaning serialized requests...");
			for(Submitted obj:getObjects(jobId))
				try{
					if(obj.getSerializedPath()!=null)ServiceUtils.deleteFile(obj.getSerializedPath());
				}catch(Exception e){
					logger.warn("Unable to delete file "+obj.getSerializedPath(), e);
				}


				logger.debug("cleaning speceisSelection for : "+jobId);
				session.deleteOperation(selectedSpecies, filter);
				logger.debug("cleaning references to working tables for : "+jobId);

				filter=new ArrayList<Field>();
				filter.add(new Field(SubmittedFields.searchid+"",jobId+"",FieldType.INTEGER));
				session.deleteOperation(workingTables, filter);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
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
				ResultSet rs= session.executeFilteredQuery(filter, selectedSpecies, selectedSpeciesSpeciesID, OrderDirection.ASC);
				if(rs.next()){
					if(!rs.getString(selectedSpeciesStatus).equalsIgnoreCase(SpeciesStatus.Ready+""))
						return false;
				}else throw new Exception("SpeciesID "+id+" not found in jobId "+jobId+" selection");
			}
			return true;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}



	@Deprecated
	public static void createGroup (int jobId)throws Exception{
		logger.trace("Creating group for "+jobId);
		Submitted job= SubmittedManager.getSubmittedById(jobId);
		Map<String,ObjectType> layers=new HashMap<String, ObjectType>(); 
		for(Submitted obj:getObjects(jobId)){
			if(obj.getGisEnabled()&&obj.getStatus().equals(SubmittedStatus.Completed))
				for(String id:obj.getGisPublishedId())
					layers.put(id, obj.getType());
		}
		if(layers.isEmpty()){
			logger.trace("No layer found, skipping group generation for job id : "+jobId);
		}else{
			GroupGenerationRequest request=new GroupGenerationRequest();
			request.setToGenerateGroupName(ServiceUtils.generateId("WMS_"+job.getTitle(), ""));
			request.setLayers(layers);

			logger.trace("Sending request to generator, "+request.getLayers().size()+" layers to add to group "+request.getToGenerateGroupName());
			if(GeneratorManager.requestGeneration(request))
				updateField(jobId, SubmittedFields.geoserverreference, FieldType.STRING, request.getToGenerateGroupName());
			else throw new Exception ("Group Generation Failed "+request.getToGenerateGroupName());
		}

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
				ResultSet rs= session.executeFilteredQuery(filter, selectedSpecies, selectedSpeciesSpeciesID, OrderDirection.ASC);
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
			if(session!=null) session.close();
		}
	}





	/**Creates a new entry in Job table and AquaMap table (for every aquamap object in job)
	 * 
	 * @return new job id
	 */
	public static Job insertNewJob(Job toPerform,boolean skipPublishing) throws Exception{
		//		logger.trace("Creating new pending Job");
		DBSession session=null;

		////*************** Send to publisher
		try{

			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();
			Publisher publisher= ServiceContext.getContext().getPublisher();

			////*************** Insert references into local DB
			logger.trace("Inserting references into internal DB...");


			//Uncomment here to insert job references

			//			List<Field> row=new ArrayList<Field>();
			//			row.add(new Field(SubmittedFields.title+"",toPerform.getName(),FieldType.STRING));
			//			row.add(new Field(SubmittedFields.isaquamap+"",false+"",FieldType.BOOLEAN));
			//			row.add(new Field(SubmittedFields.jobid+"",0+"",FieldType.INTEGER));
			//			PreparedStatement ps=session.getPreparedStatementForInsert(row, submittedTable);
			//			session.fillParameters(row,0, ps).executeUpdate();
			//			ResultSet rs=ps.getGeneratedKeys();
			//			rs.next();
			//			toPerform.setId(rs.getInt(SubmittedFields.searchid+""));
			//
			//			ps=null;

			PreparedStatement ps=null;
			List<Field> row=null;
			ResultSet rs=null;
			Submitted submittedJob=getSubmittedById(toPerform.getId());
			logger.debug("Submitted Job is "+submittedJob.toXML());
			
			for(AquaMapsObject obj : toPerform.getAquaMapsObjectList()){
				//				row.add(new Field(SubmittedFields.title+"",obj.getName(),FieldType.STRING));
				//				row.add(new Field(SubmittedFields.isaquamap+"",true+"",FieldType.BOOLEAN));
				//				row.add(new Field(SubmittedFields.jobid+"",toPerform.getId()+"",FieldType.INTEGER));
				//				row.add(new Field)


				row=new ArrayList<Field>();
				row.add(submittedJob.getField(SubmittedFields.author));
				row.add(new Field(SubmittedFields.gisenabled+"",obj.getGis()+"",FieldType.BOOLEAN));
				row.add(new Field(SubmittedFields.isaquamap+"",true+"",FieldType.BOOLEAN));
				row.add(new Field(SubmittedFields.jobid+"",submittedJob.getSearchId()+"",FieldType.INTEGER));
				row.add(new Field(SubmittedFields.saved+"",false+"",FieldType.BOOLEAN));
				row.add(submittedJob.getField(SubmittedFields.sourcehcaf));
				row.add(submittedJob.getField(SubmittedFields.sourcehspec));
				row.add(submittedJob.getField(SubmittedFields.sourcehspen));
				row.add(new Field(SubmittedFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
				row.add(submittedJob.getField(SubmittedFields.submissiontime));
				row.add(new Field(SubmittedFields.title+"",obj.getName(),FieldType.STRING));
				row.add(new Field(SubmittedFields.type+"",obj.getType()+"",FieldType.STRING));
				row.add(submittedJob.getField(SubmittedFields.postponepublishing));
				if(ps==null)ps=session.getPreparedStatementForInsert(row, submittedTable);
				session.fillParameters(row,0, ps).executeUpdate();
				rs=ps.getGeneratedKeys();
				rs.next();

				obj.setId(rs.getInt(SubmittedFields.searchid+""));
			}

			logger.trace("Preparing taxonomy for species selections");
			for(Species s:toPerform.getSelectedSpecies()){
				Species updated=SpeciesManager.getSpeciesById(true, false, s.getId(), toPerform.getSourceHSPEN().getSearchId());
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.kingdom+""));
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.ordercolumn+""));
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.phylum+""));
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.classcolumn+""));
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.familycolumn+""));
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.species+""));
				s.addField(updated.getFieldbyName(SpeciesOccursumFields.genus+""));
				for(AquaMapsObject obj:toPerform.getAquaMapsObjectList())
					if(obj.getSelectedSpecies().contains(s)){ 
						obj.getSelectedSpecies().remove(s);
						obj.getSelectedSpecies().add(updated);
					}
			}
			session.commit();
			logger.trace("Sending job to publisher..");
			
			////*************** Send to publisher
			if(!skipPublishing){
				toPerform=publisher.publishJob(toPerform);		

				////*************** update references in local DB


				PreparedStatement psUpdateObjects=null;
				ArrayList<Field> objRow=null;
				ArrayList<Field> objKey=null;

				for(AquaMapsObject obj: toPerform.getAquaMapsObjectList()){
					objRow= new ArrayList<Field>();
					objKey= new ArrayList<Field>();

					objRow.add(new Field(SubmittedFields.status+"",obj.getStatus()+"",FieldType.STRING));
					ArrayList<String> layersId=new ArrayList<String>();
					ArrayList<String> layersUri=new ArrayList<String>();
					for(LayerInfo info: obj.getLayers()){
						layersId.add(info.getId());
						layersUri.add(info.getUrl()+"/"+info.getName());
					}
					objRow.add(new Field(SubmittedFields.gispublishedid+"",CSVUtils.listToCSV(layersId),FieldType.STRING));
					objRow.add(new Field(SubmittedFields.geoserverreference+"",CSVUtils.listToCSV(layersUri),FieldType.STRING));

					objKey.add(new Field(SubmittedFields.searchid+"",obj.getId()+"",FieldType.INTEGER));

					if(psUpdateObjects==null) psUpdateObjects=session.getPreparedStatementForUpdate(objRow, objKey, submittedTable);

					//fill values
					psUpdateObjects=session.fillParameters(objRow, 0, psUpdateObjects);
					//fill keys
					psUpdateObjects=session.fillParameters(objKey,objRow.size(),psUpdateObjects);

					psUpdateObjects.executeUpdate();
				}


				ArrayList<Field> jobRow= new ArrayList<Field>();
				ArrayList<Field> jobKey=new ArrayList<Field>();

				//************** toPerform seems to have pending status when returned from publisher, this would trigger duplicate execution
				if(toPerform.getStatus().equals(SubmittedStatus.Completed))
					jobRow.add(new Field(SubmittedFields.status+"",toPerform.getStatus()+"",FieldType.STRING));


				jobRow.add(new Field(SubmittedFields.gispublishedid+"",toPerform.getWmsContextId(),FieldType.STRING));

				jobKey.add(new Field(SubmittedFields.searchid+"",toPerform.getId()+"",FieldType.INTEGER));


				PreparedStatement psJobUpdate=session.getPreparedStatementForUpdate(jobRow, jobKey, submittedTable);

				psJobUpdate=session.fillParameters(jobRow, 0, psJobUpdate);
				psJobUpdate=session.fillParameters(jobKey, jobRow.size(), psJobUpdate);
				psJobUpdate.executeUpdate();

			}
			
			
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
					fields.get(0).setValue(toPerform.getId()+"");
					for(Species s:toPerform.getSelectedSpecies()){
						String status=SpeciesStatus.Ready.toString();
						if((hasWeight)&&(toPerform.getEnvelopeWeights().containsKey(s.getId())))status=SpeciesStatus.toGenerate.toString();
						if((hasPerturbation)&&(toPerform.getEnvelopeCustomization().containsKey(s.getId())))status=SpeciesStatus.toCustomize.toString();
						fields.get(1).setValue(s.getId());
						fields.get(2).setValue(status);
						fields.get(3).setValue((hasWeight||hasPerturbation)+"");
						psSpecies=session.fillParameters(fields,0, psSpecies);
						psSpecies.executeUpdate();
					}
				}else throw new Exception("Invalid job, no species found");


				//Setting selected sources as working tables

				setWorkingHCAF(toPerform.getId(), SourceManager.getSourceName(toPerform.getSourceHCAF().getSearchId()));
				setWorkingHSPEC(toPerform.getId(), SourceManager.getSourceName(toPerform.getSourceHSPEC().getSearchId()));
				setWorkingHSPEN(toPerform.getId(), SourceManager.getSourceName(toPerform.getSourceHSPEN().getSearchId()));

				AquaMapsXStream.serialize(submittedJob.getSerializedPath(), toPerform);
			}
			session.commit();
			return toPerform;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	protected static int deleteJob(int submittedId)throws Exception{
		logger.trace("Deleting job "+submittedId);
		List<Submitted> objects=getObjects(submittedId);
		logger.trace("Found "+objects.size()+" object(s) to delete..");

		int count=0;
		for(Submitted obj:objects) {
			try{
				count+=AquaMapsManager.deleteObject(obj.getSearchId());
			}catch(Exception e){
				logger.error("Unable to delete object "+obj);
			}
		}

		Publisher pub=ServiceContext.getContext().getPublisher();
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

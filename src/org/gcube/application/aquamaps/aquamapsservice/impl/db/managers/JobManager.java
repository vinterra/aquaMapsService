package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import it.cnr.isti.geoserverInteraction.GeoserverCaller;
import it.cnr.isti.geoserverInteraction.bean.LayerRest;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.GeneratorManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis.GroupGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.stubs.dataModel.AquaMapsObject;
import org.gcube.application.aquamaps.stubs.dataModel.Field;
import org.gcube.application.aquamaps.stubs.dataModel.Job;
import org.gcube.application.aquamaps.stubs.dataModel.Species;
import org.gcube.application.aquamaps.stubs.dataModel.Submitted;
import org.gcube.application.aquamaps.stubs.dataModel.Types.FieldType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.ResourceType;
import org.gcube.application.aquamaps.stubs.dataModel.Types.SubmittedStatus;
import org.gcube.application.aquamaps.stubs.dataModel.fields.SubmittedFields;

public class JobManager extends SubmittedManager{

	public static final String toDropTables="tempTables";
	public static final String tempFolders="tempFolders";
	public static final String selectedSpecies="selectedSpecies";
	
	//******************************************* working tables management ***************************************

	protected static final String workingTables="workingTables";
	protected static final String tableField="tableName";
	protected static final String tableTypeField="tableType";
	
	
	
	protected static void setWorkingTable(int submittedId,String tableType,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);			
			PreparedStatement ps= session.preparedStatement("INSERT into "+workingTables+" SET "+SubmittedFields.searchId+"=? , "+tableTypeField+"=? ,"+tableField+"=? ON DUPLICATE KEY UPDATE "+tableField+"=?");
			ps.setInt(1, submittedId);
			ps.setString(2, tableType);
			ps.setString(3,tableName);
			ps.setString(4,tableName);
			ps.executeUpdate();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	protected static String getWorkingTable (int submittedId,String tableType)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);			
			PreparedStatement ps= session.preparedStatement("Select * from "+workingTables+" where "+SubmittedFields.searchId+"=? AND "+tableTypeField+"=?");
			ps.setInt(1, submittedId);
			ps.setString(2, tableType);
			ResultSet rs= ps.executeQuery();
			rs.next();
			return rs.getString(tableField);
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
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Insert into "+toDropTables+" (jobId,tableName) VALUE(?,?)");
			ps.setInt(1, jobId);
			ps.setString(2, tableName);
			ps.executeUpdate();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void addToDeleteTempFolder(int jobId,String folderName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Insert into "+tempFolders+" (jobId,folderName) VALUE(?,?)");
			ps.setInt(1, jobId);
			ps.setString(2, folderName);
			ps.executeUpdate();
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}

	public static void updateSpeciesStatus(int jobId,String speciesId[],SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Update "+selectedSpecies+" set status = ? where jobId=? AND speciesId=?");
			ps.setString(1, status.toString());
			ps.setInt(2, jobId);
			for(String specId:speciesId){
				ps.setString(3,specId);
				ps.execute();
			}
			//		session.close();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=null;
			if(status!=null){
				ps=session.preparedStatement("Select speciesId from "+selectedSpecies+" where jobId=? AND status=?");
				ps.setInt(1, jobId);
				ps.setString(2,status.toString());
			}else{
				ps=session.preparedStatement("Select speciesId from "+selectedSpecies+" where jobId=?");
				ps.setInt(1, jobId);
			}
			ResultSet rs=ps.executeQuery();
			ArrayList<String> toReturn=new ArrayList<String>(); 
			while(rs.next()){
				toReturn.add(rs.getString("speciesId"));
			}
			//		session.close();
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
			session=DBSession.openSession(PoolManager.DBType.mySql);		
			PreparedStatement ps =session.preparedStatement("Select count(*) from submitted where jobId=? AND status!=? AND status!=?");
			ps.setInt(1, jobId);
			ps.setString(2, SubmittedStatus.Error.toString());
			ps.setString(3, SubmittedStatus.Completed.toString());
			ResultSet rs=ps.executeQuery();
			rs.first();
			boolean toReturn=rs.getInt(1)==0;
			//		session.close();
			return toReturn;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static void cleanTemp(int jobId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			logger.debug("cleaning tables for : "+jobId);
			PreparedStatement ps=session.preparedStatement("Select tableName from "+toDropTables+" where jobId=?");
			ps.setInt(1, jobId);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				String table=rs.getString(1);
				session.executeUpdate("DROP TABLE IF EXISTS "+table);
			}
			ps=session.preparedStatement("Delete from "+toDropTables+" where jobId=?");
			ps.setInt(1, jobId);
			ps.executeUpdate();			
			logger.debug("cleaning folders for : "+jobId);
			ps=session.preparedStatement("Select folderName from "+tempFolders+" where jobId=?");
			ps.setInt(1, jobId);
			rs=ps.executeQuery();
			while(rs.next()){
				String folder=rs.getString(1);
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
			ps=session.preparedStatement("Delete from "+tempFolders+" where jobId=?");
			ps.setInt(1, jobId);
			ps.executeUpdate();
			logger.debug("cleaning speceisSelection for : "+jobId);
			ps=session.preparedStatement("Delete from "+selectedSpecies+" where jobId=?");
			ps.setInt(1, jobId);
			ps.executeUpdate();
			logger.debug("cleaning references to working tables for : "+jobId);
			ps=session.preparedStatement("Delete from "+workingTables+" where "+SubmittedFields.searchId+" = ?");
			ps.setInt(1,jobId);
			ps.executeUpdate();
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}
	public static boolean isSpeciesListReady(int jobId,Set<String> toCheck)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps=session.preparedStatement("Select status from "+selectedSpecies+" where jobId =? and speciesId=?");
			ps.setInt(1, jobId);
			for(String id:toCheck){
				ps.setString(2,id);
				ResultSet rs =ps.executeQuery();
				rs.first();
				if(!(rs.getString("status").equalsIgnoreCase(SpeciesStatus.Ready.toString())))
					return false;
			}
			return true;
		}catch (Exception e){
			throw e;
		}finally {
			session.close();
		}
	}




	public static void createGroup (int jobId)throws Exception{
		try{
			logger.trace("Starting job Id : "+jobId+" layers group creation ..");
			logger.trace("Looking for generated layers");
			ArrayList<String> layers=new ArrayList<String>();
			for(Submitted obj:getObjects(jobId)){				
				if((obj.getGis()!=null)&&(!obj.getGis().equalsIgnoreCase("null")))
					layers.add(obj.getGis());
			}
			if(layers.size()>0){
				logger.trace("found "+layers.size()+" generated layer(s), looking for related style(s)");
				GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
				GroupGenerationRequest req=new GroupGenerationRequest();
				req.setLayers(layers);
				for(String layerId:layers){
					LayerRest lRest=caller.getLayer(layerId);
					req.getStyles().put(layerId, lRest.getDefaultStyle());
				}								
				req.setName(String.valueOf(jobId));				
				req.setSubmittedId(jobId);
				if(GeneratorManager.requestGeneration(req))logger.trace("Generation of jobId "+jobId+" layers group complete");
				else throw new Exception("Unable to generate Group");
			}else logger.trace("No generated layers found for job Id "+jobId);
		}catch(Exception e){
			logger.error("Unable to complete group "+jobId+" generation",e);
			throw e;
		}
	}




	public static boolean isSpeciesSetCustomized(int submittedId,Set<String> ids)throws Exception{
		DBSession session=null;		
		try{
			logger.trace("Checking species customizations flag..");
			session=DBSession.openSession(PoolManager.DBType.mySql);
			PreparedStatement ps= session.preparedStatement("Select isCustomized from "+selectedSpecies+" where jobId=? AND speciesId=?");
			ps.setInt(1, submittedId);
			for(String id:ids){
				ps.setString(2, id);
				ResultSet rs= ps.executeQuery();
				if(rs.next()){
					if(!rs.getBoolean(1)) return false;
				}else throw new Exception("customized flag not found for species "+id+" under "+submittedId+" selection");
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
		Integer jobId=null;
		String myData = ServiceUtils.getDate();
		String submittedInsertion="INSERT INTO "+submittedTable+" ("+SubmittedFields.title+","+SubmittedFields.author+","+SubmittedFields.date+","+SubmittedFields.status+","+
		SubmittedFields.isAquaMap+","+SubmittedFields.type+","+SubmittedFields.sourceHCAF+","+SubmittedFields.sourceHSPEC+","+
		SubmittedFields.sourceHSPEN+","+SubmittedFields.jobid+") values (?,?,?,?,?,?,?,?,?,?)";
		DBSession session=null;
		try{
			session=DBSession.openSession(PoolManager.DBType.mySql);
			session.disableAutoCommit();
			PreparedStatement ps =session.getConnection().prepareStatement(submittedInsertion,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, toPerform.getName());
			ps.setString(2, toPerform.getAuthor());
			ps.setString(3, myData);
			ps.setString(4,SubmittedStatus.Pending.toString());
			ps.setBoolean(5, false);
			ps.setObject(6,null);
			int HCAFId=toPerform.getSourceHCAF().getSearchId();
			int HSPENId=toPerform.getSourceHSPEN().getSearchId();
			int HSPECId=toPerform.getSourceHSPEC().getSearchId();
			ps.setInt(7, HCAFId);
			ps.setInt(8,HSPECId);
			ps.setInt(9, HSPENId);
			ps.setObject(10,null);
			ps.executeUpdate();
			ResultSet rs=ps.getGeneratedKeys();
			rs.first();
			jobId=rs.getInt(1);
			toPerform.setId(jobId);			
			//TODO make jobProfile

			ps.setBoolean(5, true);
			ps.setInt(10,jobId);
			logger.trace("inserting associated aquamapsObj(s)");

			for(AquaMapsObject aquaMapObj:toPerform.getAquaMapsObjectList()){

				ps.setString(1, aquaMapObj.getName());
				ps.setString(6,aquaMapObj.getType().toString());
				ps.executeUpdate();
				rs=ps.getGeneratedKeys();
				rs.first();
				aquaMapObj.setId(rs.getInt(1));

				updateProfile(aquaMapObj.getName(), aquaMapObj.getId(),
						aquaMapObj.toXML(),
						SourceManager.getSourceName(ResourceType.HSPEC,HSPECId), String.valueOf(jobId));
			}



			if((toPerform.getSelectedSpecies().size()>0)){

				boolean hasPerturbation=false;
				if((toPerform.getEnvelopeCustomization().size()>0)) hasPerturbation=true;

				boolean hasWeight=false;
				if((toPerform.getEnvelopeWeights().size()>0)) hasWeight=true;

				PreparedStatement psSpecies=session.preparedStatement("Insert into "+selectedSpecies+" (jobId,speciesId,status,isCustomized) value(?,?,?,?)");
				psSpecies.setInt(1, jobId);
				for(Species s:toPerform.getSelectedSpecies()){
					String status=SpeciesStatus.Ready.toString();
					if((hasWeight)&&(toPerform.getEnvelopeWeights().containsKey(s.getId())))status=SpeciesStatus.toGenerate.toString();
					if((hasPerturbation)&&(toPerform.getEnvelopeCustomization().containsKey(s.getId())))status=SpeciesStatus.toCustomize.toString();
					psSpecies.setString(2, s.getId());
					psSpecies.setString(3, status);
					psSpecies.setBoolean(4, (hasWeight||hasPerturbation));
					psSpecies.executeUpdate();
				}
			}else throw new Exception("Invalid job, no species found");
			
			
			//Setting selected sources as working tables
			
			setWorkingHCAF(jobId, SourceManager.getSourceName(ResourceType.HCAF, HCAFId));
			setWorkingHSPEC(jobId, SourceManager.getSourceName(ResourceType.HSPEC, HSPECId));
			setWorkingHSPEN(jobId, SourceManager.getSourceName(ResourceType.HSPEN, HSPENId));
			
			session.commit();
			logger.trace("New Job created with Id "+jobId);
			
			
			
			
			
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
			AquaMapsManager.deleteObject(obj.getSearchId());
			count++;
			}catch(Exception e){
				logger.error("Unable to delete object "+obj);
			}
		}
		try{
		deletelocalFiles(submittedId);
		}catch(Exception e){
			logger.error("Unable to delete files for job"+submittedId,e);
		}
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

package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import it.cnr.isti.geoserverInteraction.GeoserverCaller;
import it.cnr.isti.geoserverInteraction.bean.BoundsRest;
import it.cnr.isti.geoserverInteraction.bean.FeatureTypeRest;
import it.cnr.isti.geoserverInteraction.bean.GroupRest;
import it.cnr.isti.geoserverInteraction.engine.MakeStyle;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.generators.BadRequestException;
import org.gcube.application.aquamaps.aquamapsservice.impl.threads.JobGenerationDetails;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.json.JSONException;




public class GISGenerator {
	private static GCUBELog logger= new GCUBELog(GISGenerator.class);
	public static char delimiter=',';
	public static boolean hasHeader=false;
	
	//DateFormat.getDateInstance(DateFormat.DEFAULT,  Locale.getDefault());
	
	
//	private static String[] columnsAndConstraintDefinition = new String[]{
//		"CsquareCode varchar(10)",
//		"feature float(3,2)"
//	};
	
	private static final String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
    "AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
  "AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";
	
	
	long count=0;
	long lines=0;
	

	
	private static String cSquareCode="CsquareCode";
	private static String cSquareCodeDefinition="varchar(10)";
	
	
	
	private DBSession session;
	
	private String importLayerData(LayerGenerationRequest request)throws Exception{		
		final String fileName=request.getCsvFile();
		String featureLabel=request.getFeatureLabel();
		String featureDefinition=request.getFeatureDefinition();
		CSVReaderProcessor processor= new CSVReaderProcessor();
		processor.setDelimiter(delimiter);
		processor.setHasHeader(hasHeader);
//		final ArrayList<Object[]> data = new ArrayList<Object[]>();
		logger.trace("Reading from file "+fileName); 
		String tableName=ServiceUtils.generateId("app", "");//"app"+(uuidGen.nextUUID()).replaceAll("-", "_");
		
//		session.createTable(tableName, columnsAndConstraintDefinition, DBSession.ENGINE.InnoDB);
		session.executeUpdate("Create table "+tableName+" ( "+cSquareCode+" "+cSquareCodeDefinition+" , "+featureLabel+" "+featureDefinition+")");
		
		final PreparedStatement ps = session.getPreparedStatementForInsert(2, tableName);
		count=0;
		lines=0;
		Reader reader= new InputStreamReader(new FileInputStream(fileName), Charset.defaultCharset());
		processor.processStream(reader , new CSVLineProcessor(){
			public boolean continueProcessing() {return true;}
			public void processDataLine(int arg0, List<String> arg1) {
				lines++;
				try{
					ps.setString(1, arg1.get(0));				
					ps.setFloat(2, Float.parseFloat(arg1.get(1)));
					count+=ps.executeUpdate();
				}catch(Exception e){
					logger.warn("Unable to complete insertion from file "+fileName, e);
					e.printStackTrace();
				}
			}
			public void processHeaderLine(int arg0, List<String> arg1) {}});
		if(lines>0){
			if(count>0)
				logger.trace("Inserted "+count+" entries of "+lines+" lines");			 
		}else logger.warn("No records found"); 
		return tableName;
	}
	
	private String createLayerTable(String appTableName,LayerGenerationRequest request)throws Exception{
//		String featureTable="f"+(uuidGen.nextUUID()).replaceAll("-", "_");
		
		String featureTable=request.getLayerName()+ServiceUtils.getTimeStamp();
		featureTable=(featureTable.toLowerCase()).replaceAll("-", "_");
		logger.trace("Creating table "+featureTable);
		session.executeUpdate("Create table "+featureTable+" AS (Select "+ServiceContext.getContext().getWorldTable()+".*, app."+request.getFeatureLabel()+
				" FROM "+appTableName+" AS app inner join "+ServiceContext.getContext().getWorldTable()+" ON app."+cSquareCode+"="+ServiceContext.getContext().getWorldTable()+"."+cSquareCode+")");
		logger.trace(featureTable+" created");
		logger.trace("going do drop appTable "+appTableName);
		session.dropTable(appTableName);
		return featureTable;
	}
	
	private boolean createLayer(String featureTable,LayerGenerationRequest request) throws JSONException{		
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
		FeatureTypeRest featureTypeRest=new FeatureTypeRest();
		featureTypeRest.setDatastore("aquamapsdb");
        featureTypeRest.setEnabled(true);
        featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
        featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
        featureTypeRest.setName(featureTable);
        featureTypeRest.setNativeName(featureTable);
        featureTypeRest.setProjectionPolicy("FORCE_DECLARED");
        featureTypeRest.setSrs("EPSG:4326");
        featureTypeRest.setNativeCRS(crs);
        featureTypeRest.setTitle(request.getLayerName());
        featureTypeRest.setWorkspace("aquamaps");   	
		if (caller.addFeatureType(featureTypeRest)){
			try {
				Thread.sleep(6*1000);
			} catch (InterruptedException e) {}			
			return caller.setLayer(featureTypeRest, request.getDefaultStyle(), request.getStyles());			
		}else return false;
	}
	
	private boolean createGroup(List<String> layers,Map<String,String> styles, String groupName)throws Exception{	 
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());		
		GroupRest g=caller.getLayerGroup(ServiceContext.getContext().getTemplateGroup());
//		g.setBounds(new BoundsRest(-180.0,180.0,-90.0,90.0,"EPSG:4326"));
//        g.setLayers(list);
//        g.setStyles(styles);
		
		for(String l:layers){
			g.addLayer(l);
			g.addStyle(l, styles.get(l));
		}		
        g.setName(groupName);
        return caller.addLayersGroup(g);
	}
	
	
	public boolean createGroup(GroupGenerationRequest request)throws Exception{
		logger.trace("Creating group "+request.getName());
		boolean result=this.createGroup(request.getLayers(), request.getStyles(), request.getName());
		if(result) JobGenerationDetails.updateGISData(request.getSubmittedId(), request.getName());		
		return result;
	}
	
	public boolean createLayer(LayerGenerationRequest request)throws Exception{
		logger.trace("Creating layer "+request.getLayerName());
		try{
		session = DBSession.openSession(PoolManager.DBType.postGIS);
		session.disableAutoCommit();
		String appTableName=this.importLayerData(request);
		String featureTable=this.createLayerTable(appTableName, request);
		session.commit();
		try {
			Thread.sleep(4*1000);
		} catch (InterruptedException e) {}
		boolean result= this.createLayer(featureTable, request);
		if(result) JobGenerationDetails.updateGISData(request.getSubmittedId(), featureTable);		
		return result;
		}catch(Exception e){			
			logger.error("Unable to complete Gis generation for layer "+request.getLayerName());
			throw e;
		}finally{
			try{
				session.close();
				}catch(Exception e){
					logger.error("Unexpected Error, unable to close session");
				}
			}		
	}
	
	public boolean copyLayers(String srcName,String destName)throws Exception{
		logger.trace("Copying layers from  "+srcName+" to "+destName);
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
		GroupRest src=caller.getLayerGroup(srcName);
		GroupRest dest=caller.getLayerGroup(destName);
		dest.getLayers().addAll(0, src.getLayers());
		return caller.addLayersGroup(dest);
	}
	
	
	public boolean generateStyle(StyleGenerationRequest req)throws Exception{
		logger.trace("Generating style "+req.getNameStyle()+" attribute :"+req.getAttributeName()+" min "+req.getMin()+" max "+req.getMax()+" N classes "+req.getNClasses());
		GeoserverCaller caller= new GeoserverCaller(ServiceContext.getContext().getGeoServerUrl(),ServiceContext.getContext().getGeoServerUser(),ServiceContext.getContext().getGeoServerPwd());
		String style;
		if(req.getTypeValue()==Integer.class)
			style=MakeStyle.createStyle(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Integer.parseInt(req.getMax()), Integer.parseInt(req.getMin()));
		else if(req.getTypeValue()==Float.class)
			style=MakeStyle.createStyle(req.getNameStyle(), req.getAttributeName().toLowerCase(), req.getNClasses(), req.getC1(), req.getC2(), req.getTypeValue(), Float.parseFloat(req.getMax()), Float.parseFloat(req.getMin()));
		else throw new BadRequestException();
		logger.trace("Submitting style "+req.getNameStyle());
		boolean toReturn=false;
		toReturn=caller.sendStyleSDL(style);
		logger.trace("Submitting style result : "+toReturn);
		return toReturn;
	}
	
	
	
}

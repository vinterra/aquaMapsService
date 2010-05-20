package org.gcube.application.aquamaps.aquamapsservice.impl.generators.gis;

import it.cnr.isti.geoserverInteraction.GeoserverCaller;
import it.cnr.isti.geoserverInteraction.bean.BoundsRest;
import it.cnr.isti.geoserverInteraction.bean.FeatureTypeRest;
import it.cnr.isti.geoserverInteraction.bean.GroupRest;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVReaderProcessor;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.PoolManager;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.json.JSONException;




public class GISGenerator {
	private static GCUBELog logger= new GCUBELog(GISGenerator.class);
	public static char delimiter=',';
	public static boolean hasHeader=false;
	private static final UUIDGen uuidGen = UUIDGenFactory.getUUIDGen();
	
	private static String[] columnsAndConstraintDefinition = new String[]{
		"CsquareCode varchar(10)",
		"feature float(3,2)"
	};
	
	private static final String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
    "AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
  "AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";
	
	
	long count=0;
	long lines=0;
	private static String worldTable="world";
	private static String geoServerUrl="http://geoserver.d4science-ii.research-infrastructures.eu:8080/geoserver";
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
		String tableName="app"+(uuidGen.nextUUID()).replaceAll("-", "_");
		
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
					if(ps.execute()) count++;
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
		String featureTable="f"+(uuidGen.nextUUID()).replaceAll("-", "_");
		logger.trace("Creating table "+featureTable);
		session.executeUpdate("Create table "+featureTable+" AS (Select "+worldTable+".*, app."+request.getFeatureLabel()+
				" FROM "+appTableName+" AS app inner join "+worldTable+" ON app."+cSquareCode+"="+worldTable+"."+cSquareCode+")");
		logger.trace(featureTable+" created");
		logger.trace("going do drop appTable "+appTableName);
		session.dropTable(appTableName);
		return featureTable;
	}
	
	private boolean createLayer(String featureTable,String humanName) throws JSONException{		
		GeoserverCaller caller= new GeoserverCaller(geoServerUrl);
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
        featureTypeRest.setTitle(humanName);
        featureTypeRest.setWorkspace("aquamaps");   	
		return caller.addFeatureType(featureTypeRest);
	}
	
	private boolean createGroup(ArrayList<String> list, ArrayList<String> styles, String groupName)throws Exception{	 
		GeoserverCaller caller=new GeoserverCaller(geoServerUrl);
		GroupRest g=new GroupRest();
		g.setBounds(new BoundsRest(-180.0,180.0,-90.0,90.0,"EPSG:4326"));
        g.setLayers(list);
        g.setStyles(styles);
        g.setName(groupName);
        return caller.addLayerGroup(g);
	}
	
	
	
	public boolean createLayer(LayerGenerationRequest request)throws Exception{
		try{
		session = DBSession.openSession(PoolManager.DBType.postGIS);
		session.disableAutoCommit();
		String appTableName=this.importLayerData(request);
		String featureTable=this.createLayerTable(appTableName, request);
		session.commit();
		Thread.sleep(4*1000);
		return this.createLayer(featureTable, request.getLayerName());		
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
	
}

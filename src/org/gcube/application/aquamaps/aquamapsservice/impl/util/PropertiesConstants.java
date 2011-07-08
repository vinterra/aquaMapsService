package org.gcube.application.aquamaps.aquamapsservice.impl.util;

public class PropertiesConstants {
	
	//********************** PROPERTIES
	//*********	GENERAL
	public static final String GIS_MODE="GIS_MODE";
	public static final String STANDALONE_MODE="STANDALONE_MODE";
	public static final String USE_DUMMY_PUBLISHER="USE_DUMMY_PUBLISHER";
	public static final String USE_ENVIRONMENT_MODELLING_LIB="USE_ENVIRONMENT_MODELLING_LIB";
	public static final String ENABLE_SCRIPT_LOGGING="ENABLE_SCRIPT_LOGGING";
	public static final String POSTPONE_SUBMISSION="POSTPONE_SUBMISSION";
	public static final String DEFAULT_PUBLISHER_URL="DEFAULT_PUBLISHER_URL";
	
	
	
	//*********	ENVIRONMENT LIBRARY
	
	public static final String BATCH_POOL_SIZE="BATCH_POOL_SIZE";
	
	//*********	INTERNAL DB ACCESS
	
	public static final String INTERNAL_DB_USERNAME="INTERNAL_DB_USERNAME";
	public static final String INTERNAL_DB_PASSWORD="INTERNAL_DB_PASSWORD";
	public static final String INTERNAL_DB_TYPE="INTERNAL_DB_TYPE";
	public static final String INTERNAL_DB_NAME="INTERNAL_DB_NAME";
	public static final String INTERNAL_DB_HOST="INTERNAL_DB_HOST";
	public static final String INTERNAL_DB_PORT="INTERNAL_DB_PORT";
	
	//********	INTERNAL WEB SERVER
	
	public static final String HTTP_SERVER_BASE_PORT="HTTP_SERVER_BASE_PORT";
	public static final String HTTP_SERVER_BASE_PATH="HTTP_SERVER_BASE_PATH";
	
	//******** 	RESOURCE MONITOR
	
	public static final String MONITOR_INTERVAL="MONITOR_INTERVAL";
	public static final String MONITOR_FREESPACE_THRESHOLD="MONITOR_FREESPACE_THRESHOLD";
	
	//********	GEOSERVER
	
	public static final String GEOSERVER_DB_USERNAME="GEOSERVER_DB_USERNAME";
	public static final String GEOSERVER_DB_PASSWORD="GEOSERVER_DB_PASSWORD";
	public static final String GEOSERVER_DB_TYPE="GEOSERVER_DB_TYPE";
	public static final String GEOSERVER_DB_NAME="GEOSERVER_DB_NAME";
	public static final String GEOSERVER_DB_HOST="GEOSERVER_DB_HOST";
	public static final String GEOSERVER_DB_PORT="GEOSERVER_DB_PORT";
	
	public static final String GEOSERVER_WORKSPACE="GEOSERVER_WORKSPACE";
	public static final String GEOSERVER_WORLD_TABLE="GEOSERVER_WORLD_TABLE";
	public static final String GEOSERVER_URL="GEOSERVER_URL";
	public static final String GEOSERVER_USER="GEOSERVER_USER";
	public static final String GEOSERVER_PASSWORD="GEOSERVER_PASSWORD";
	public static final String GEOSERVER_TEMPLATE_GROUP="GEOSERVER_TEMPLATE_GROUP";
	public static final String GEOSERVER_DEFAULT_DISTRIBUTION_STYLE="GEOSERVER_DEFAULT_DISTRIBUTION_STYLE";
	
	
	//********* WORKERS CONFIGURATION
	
	public static final String JOB_MAX_WORKERS="JOB_MAX_WORKERS";
	public static final String JOB_MIN_WORKERS="JOB_MIN_WORKERS";
	public static final String JOB_INTERVAL_TIME="JOB_INTERVAL";
	
	public static final String AQUAMAPS_OBJECT_MAX_WORKERS="AQUAMAPS_OBJECT_MAX_WORKERS";
	public static final String AQUAMAPS_OBJECT_MIN_WORKERS="AQUAMAPS_OBJECT_MIN_WORKERS";
	
	//********* SOURCES
	
	public static final String DEFAULT_HSPEC_ID="DEFAULT_HSPEC_ID";
	public static final String DEFAULT_HCAF_ID="DEFAULT_HCAF_ID";
	public static final String DEFAULT_HSPEN_ID="DEFAULT_HSPEN_ID";
	
	//********* DEFAULT DB VALUES
	
	public static final String INTEGER_DEFAULT_VALUE="INTEGER_DEFAULT_VALUE";
	public static final String DOUBLE_DEFAULT_VALUE="DOUBLE_DEFAULT_VALUE";
	public static final String BOOLEAN_DEFAULT_VALUE="BOOLEAN_DEFAULT_VALUE";
	
	
	//********* Execution params
	public static final String LOCAL_BACKEND="LOCAL_BACKEND";
}

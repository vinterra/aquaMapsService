package org.gcube.application.aquamaps.aquamapsservice.impl.util;

public class PropertiesConstants {
	
	//********************** PROPERTIES
	//*********	GENERAL

	
	public static final String ENABLE_SCRIPT_LOGGING="ENABLE_SCRIPT_LOGGING";
//	public static final String POSTPONE_SUBMISSION="POSTPONE_SUBMISSION";
	public static final String DEFAULT_PUBLISHER_URL="DEFAULT_PUBLISHER_URL";
	public static final String PURGE_PENDING_OBJECTS="PURGE_PENDING_OBJECTS";
	public static final String PURGE_PENDING_HSPEC_REQUESTS="PURGE_PENDING_HSPEC_REQUESTS";
	
	
	//*********	ENVIRONMENT LIBRARY
	
	public static final String BATCH_POOL_SIZE="BATCH_POOL_SIZE";
	public static final String PROGRESS_MONITOR_INTERVAL_SEC="PROGRESS_MONITOR_INTERVAL_SEC";
	
	
	//*********	INTERNAL DB ACCESS
	
	public static final String INTERNAL_DB_USERNAME="INTERNAL_DB_USERNAME";
	public static final String INTERNAL_DB_PASSWORD="INTERNAL_DB_PASSWORD";
	public static final String INTERNAL_DB_TYPE="INTERNAL_DB_TYPE";
	public static final String INTERNAL_DB_NAME="INTERNAL_DB_NAME";
	public static final String INTERNAL_DB_HOST="INTERNAL_DB_HOST";
	public static final String INTERNAL_DB_PORT="INTERNAL_DB_PORT";
	
	public static final String INTERNAL_DB_MAX_CONNECTION="INTERNAL_DB_MAX_CONNECTION"; 

	
	//********** PUBLISHER DB ACCESS
	
	public static final String PUBLISHER_DB_USERNAME="PUBLISHER_DB_USERNAME";
	public static final String PUBLISHER_DB_PASSWORD="PUBLISHER_DB_PASSWORD";
//	public static final String PUBLISHER_DB_TYPE="PUBLISHER_DB_TYPE";
	public static final String PUBLISHER_DB_NAME="PUBLISHER_DB_NAME";
	public static final String PUBLISHER_DB_HOST="PUBLISHER_DB_HOST";
	public static final String PUBLISHER_DB_PORT="PUBLISHER_DB_PORT";
	
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
	
	public static final String GEOSERVER_DB_MAX_CONNECTION="GEOSERVER_DB_MAX_CONNECTION"; 
//	public static final String GEOSERVER_DB_MAX_IDLE="GEOSERVER_DB_MAX_IDLE";
	
	public static final String GEOSERVER_DATASTORE="GEOSERVER_DATASTORE";
	
	
	
	public static final String GEOSERVER_WORKSPACE="GEOSERVER_WORKSPACE";
	public static final String GEOSERVER_WORLD_TABLE="GEOSERVER_WORLD_TABLE";
	public static final String GEOSERVER_URL="GEOSERVER_URL";
	public static final String GEOSERVER_USER="GEOSERVER_USER";
	public static final String GEOSERVER_PASSWORD="GEOSERVER_PASSWORD";
	public static final String GEOSERVER_TEMPLATE_GROUP="GEOSERVER_TEMPLATE_GROUP";
	public static final String GEOSERVER_DEFAULT_DISTRIBUTION_STYLE="GEOSERVER_DEFAULT_DISTRIBUTION_STYLE";
	public static final String GEOSERVER_WAIT_FOR_DB_MS="GEOSERVER_WAIT_FOR_DB_MS";
	public static final String GEOSERVER_WAIT_FOR_FT="GEOSERVER_WAIT_FOR_FT";
	
	
	public static final String GEONETWORK_URL="GEONETWORK_URL";
	public static final String GEONETWORK_USERNAME="GEONETWORK_USERNAME";
	public static final String GEONETWORK_PASSWORD="GEONETWORK_PASSWORD";
	
	//********* WORKERS CONFIGURATION
	
	public static final String JOB_MAX_WORKERS="JOB_MAX_WORKERS";
	public static final String JOB_MIN_WORKERS="JOB_MIN_WORKERS";
	public static final String JOB_INTERVAL_TIME="JOB_INTERVAL_TIME";
	public static final String JOB_PRIORITY="JOB_PRIORITY";
	
	public static final String AQUAMAPS_OBJECT_MAX_WORKERS="AQUAMAPS_OBJECT_MAX_WORKERS";
	public static final String AQUAMAPS_OBJECT_MIN_WORKERS="AQUAMAPS_OBJECT_MIN_WORKERS";
	public static final String AQUAMAPS_OBJECT_INTERVAL_TIME="AQUAMAPS_OBJECT_INTERVAL_TIME";
	public static final String AQUAMAPS_OBJECT_PRIORITY="AQUAMAPS_OBJECT_PRIORITY";
	
	public static final String HSPEC_GROUP_MAX_WORKERS="HSPEC_GROUP_MAX_WORKERS";
	public static final String HSPEC_GROUP_MIN_WORKERS="HSPEC_GROUP_MIN_WORKERS";
	public static final String HSPEC_GROUP_INTERVAL_TIME="HSPEC_GROUP_INTERVAL_TIME";
	public static final String HSPEC_GROUP_PRIORITY="HSPEC_GROUP_PRIORITY";
	
	//********* SOURCES

	
	//********* DEFAULT DB VALUES
	
	public static final String INTEGER_DEFAULT_VALUE="INTEGER_DEFAULT_VALUE";
	public static final String DOUBLE_DEFAULT_VALUE="DOUBLE_DEFAULT_VALUE";
	public static final String BOOLEAN_DEFAULT_VALUE="BOOLEAN_DEFAULT_VALUE";
	
	
}

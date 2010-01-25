#! /bin/sh

TSDBUSERNAME=$(sed '/^\#/d' $GLOBUS_LOCATION/etc/org.gcube.contentmanagement.timeseriesservice/dbprop.properties | grep 'dbusername'  | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//')
TSDBPASSWORD=$(sed '/^\#/d' $GLOBUS_LOCATION/etc/org.gcube.contentmanagement.timeseriesservice/dbprop.properties | grep 'dbpassword'  | tail -n 1 | sed 's/^.*=//;s/^[[:space:]]*//;s/[[:space:]]*$//')

echo $TSDBUSERNAME
echo $TSDBPASSWORD

USERINSTRUCTION=""

if [ "x$TSDBUSERNAME" != "x" ]; then 
	USERINSTRUCTION=" -u $TSDBUSERNAME" 
fi 

PASSWORDINSTRUCTION=""

if [ "x$TSDBPASSWORD" != "x" ]; then 
	PASSWORDINSTRUCTION=" -p$TSDBPASSWORD" 
fi

echo $USERINSTRUCTION
echo $PASSWORDINSTRUCTION

mysql $USERINSTRUCTION $PASSWORDINSTRUCTION < $GLOBUS_LOCATION/etc/org.gcube.application.aquamaps/aquamapsDump.sql

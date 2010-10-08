#!/bin/sh

# 
# This script makes it easier to launch various cloudscape tools
# Usage: "sh cs.sh <command>"
#     where <command> is one of "start, stop, ij, look"
#

# where the Cloudscape libraries reside
CSLIB=/Volumes/Stuff/Java/Cloudscape_10.0/lib

# where the DBs live
DBROOT=/tmp/cloudscape

# name of the default DB
DBNAME=perfDB

# the Java VM
JAVA=/usr/bin/java

NSC="$JAVA -Dcloudscape.system.home=$DBROOT com.ihost.cs.drda.NetworkServerControl"

export CLASSPATH="${CSLIB}/cs.jar:${CSLIB}/cstools.jar:${CSLIB}/csnet.jar:${CLASSPATH}"

case $1 in
	start )
		$NSC start -h 0.0.0.0
		break;;
		
	stop )
		$NSC shutdown
		break;;

	ij )
		$JAVA -Dij.protocol=jdbc:cloudscape: -Dij.database=$DBROOT/$DBNAME com.ihost.cs.tools.ij
		break;;
	
	look )
		$JAVA com.ihost.cs.tools.cslook -d jdbc:cloudscape:$DBROOT/$DBNAME
		break;;
			
	* )
		echo "unknown command $1"
		break ;;
esac

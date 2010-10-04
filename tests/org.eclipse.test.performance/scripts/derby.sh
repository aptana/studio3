#!/bin/sh

# 
# This script makes it easier to launch various Derby tools
# Usage: "sh derby.sh <command>"
#     where <command> is one of "start, stop, ij, look"
#

# where the Derby libraries reside
CSLIB=/Volumes/Stuff/Java/IBM/Cloudscape_10.0/lib

# where the DBs live
DBROOT=/tmp/derby

# name of the default DB
DBNAME=perfDB

# the Java VM
JAVA=/usr/bin/java

#MacOS: NSC="-Dderby.system.home=$DBROOT -Dderby.storage.fileSyncTransactionLog=true org.apache.derby.drda.NetworkServerControl"
NSC="-Dderby.system.home=$DBROOT org.apache.derby.drda.NetworkServerControl"

export CLASSPATH="${CSLIB}/derby.jar:${CSLIB}/derbytools.jar:${CSLIB}/derbynet.jar:${CLASSPATH}"

case $1 in
	start )
		$JAVA -Xms256M -Xmx256M $NSC start -h 0.0.0.0
		break;;
		
	stop )
		$JAVA $NSC shutdown
		break;;

	ij )
		$JAVA -Dij.protocol=jdbc:derby: -Dij.database=$DBROOT/$DBNAME com.ihost.cs.tools.ij
		break;;
	
	look )
		$JAVA com.ihost.cs.tools.cslook -d jdbc:derby:$DBROOT/$DBNAME
		break;;
			
	* )
		echo "unknown command $1"
		break ;;
esac

/**
   File Name:          test_multi_statements.js
   Description:

	  Test the support of multi-statement executions

   Author:             Jane Tudor
   Date:               22 Feb 2007
*/

//Define global variables
var SECTION = "MySQL";
var VERSION = "5.0";
var TITLE   = "test_multi_statements";

var conn, res, rc, count;
var CLIENT_MULTI_STATEMENTS = 65536;


// Database connection variables
var dbServer = "curt_9100";
var dbDatabase = "testdb";
var dbUser = "jane";
var dbPass = "jaxer";
var dbPort = 0;

var rows = new Array(0, 0, 2, 1, 3, 2, 2, 1, 1, 0, 0, 1, 0); 
var rowElements = 13;
var query= "\
DROP TABLE IF EXISTS test_multi_tab;\
CREATE TABLE test_multi_tab(id int, name char(20));\
INSERT INTO test_multi_tab(id) VALUES(10), (20);\
INSERT INTO test_multi_tab VALUES(20, 'insert;comma');\
SELECT * FROM test_multi_tab;\
UPDATE test_multi_tab SET name='new;name' WHERE id=20;\
DELETE FROM test_multi_tab WHERE name='new;name';\
SELECT * FROM test_multi_tab;\
DELETE FROM test_multi_tab WHERE id=10;\
SELECT * FROM test_multi_tab;\
DROP TABLE test_multi_tab;\
select 1;\
DROP TABLE IF EXISTS test_multi_tab";

startTest();  // Leave this alone

// Prepare for database connection
conn = new MySQL50();

// Establish connection to database server
res = conn.connect(dbServer, dbUser, dbPass, dbDatabase);

// First test that we get an error for multi statements, because a default connection is
// not opened with CLIENT_MULTI_STATEMENTS
res = conn.query(query);
if(!res.errno()) {
	throw ("Multi statement query should have failed.  Error: " + res.error());
}

rc = conn.nextResult();
if (!rc == -1) {
	throw ("Expect conn.nextResult() to return -1.  Received: " + rc);
}

res = conn.connect(dbServer, dbUser, dbPass, dbDatabase, 0, conn.CLIENT_MULTI_STATEMENTS);
if(!res) {
	throw ("CLIENT_MULTI_STATEMENTS connect failed.  Error: " + conn.error());
}

// Now test that we do not get an error for multi statements, since the current connection has
// been opened with CLIENT_MULTI_STATEMENTS
res = conn.query(query);

if(res.errno()) {
	throw ("Multi statement query failed, but was NOT expected to fail.  Errno: " + res.error());
}

for (count= 0 ; count < rows.length ; count++)
  {
    exp_value= res.rowCount();
    if (rows[count] !=  exp_value)
    {
      throw ("element #: "+ count + " had affected rows = " + exp_value + " and should have been " +  rows[count]);
    }
	
	// Free existing result set
	rc = res.close();
	
    if (count != rows.length -1)
    {
	  // Read next result set
	  res = conn.nextResult();
	  if (res.errno()) {
	  	// Prepare to fall out of this for loop, since we have complete processing multi results statements
	  	exp_value= res.errno();
      }
    }
    else
    {
	  // Read next result set
	  res = conn.nextResult();
	  if (res.errno()) {
	  	throw ("Expected valid result set from conn.nextResult(). Error :" + res.error());
      }
    }
  }

// Check that errors statements accordingly, in a multi statement query

res = conn.query("select 1+1+a;select 1+1"); 
if(!res.errno()) { 
     throw ("'select 1+1+a;select 1+1' should have failed.  Error: " + res.error()); 
} 
 
// Free existing result set 
rc = res.close(); 
      
res = conn.nextResult(); 
if (res.errno()) { 
       throw ("conn.nextResult() after 'select 1+1+a;select 1+1' failed with error: " + res.error()); 
} 
 
res = conn.query("select 1+1;select 1+1+a;select 1"); 
if (res.errno()) { 
     throw ("'select 1+1;select 1+1+a;select 1' should have failed.  Error: " + res.error()); 
} 
 
// Free the results 
rc = res.close(); 
 
res = conn.nextResult(); 
if (!res.errno()) { 
       throw ("conn.nextResult() after 'SELECT 1+1;select 1+1+a;select 1' failed with Error: " + res.error()); 
} 
 
// Free the results 
rc = res.close(); 
 
res = conn.nextResult(); 
if (res.errno()) { 
       throw ("conn.nextResult() after 'select 1+1;SELECT 1+1+a;select 1' failed with Error: " + res.error()); 
} 

// Free the results
rc = res.close();

res = conn.nextResult(); 
if (res.errno()) { 
       throw ("conn.nextResult() after 'select 1+1;select 1+1+a;SELECT 1' failed with Error: " + res.error()); 
} 

//  Ensure that we can now do a simple query (this checks that the server is
//  not trying to send us the results for the last 'select 1'

res = conn.query("select 1+1+1");
if(res.errno()) {
	throw ("Simple query should not fail here.  Error: " + res.error());
}

 // Drop table again if necessary
res = conn.query("DROP TABLE IF EXISTS t1, t2");
if(res.error()) {
	throw ("Drop existing table failed");
}
	
// Disconnect from database server
res = conn.close(); 

test();  // Leave this alone




/**
   File Name:          test_prepare_insert_update.js
   Description:

	Test logical sequnece of insert,update prepare statements, validating results.  

   Author:             Jane Tudor
   Date:               17 March 2007
*/

//Define global variables
var SECTION = "MySQL";
var VERSION = "5.0";
var TITLE   = "test_prepare_insert_update";

var conn, res, stmt;
var iquery, uquery, dquery, squery, pcnt;

// Database connection variables
var dbServer = "curt_9100";
var dbDatabase = "testdb";
var dbUser = "jane";
var dbPass = "jaxer";
var dbPort = 0;


startTest();  // Leave this alone

// Prepare for database connection
conn = new MySQL50();

 // Establish connection to database server
res = conn.connect(dbServer, dbUser, dbPass, dbDatabase);

 // Clean up if necessary
res = conn.query("DROP TABLE IF EXISTS t1");
if(res.error()) {
	throw ("Drop existing table failed");
}

 // Create table via prepare statement
stmt = conn.prepare("CREATE TABLE t1 (a INT, b INT, c INT, UNIQUE (A), UNIQUE(B))");
if(!stmt) {
	throw ("Create table prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

 // Insert initial data into table via prepare statement
stmt = conn.prepare("INSERT t1 VALUES (1,2,10), (3,4,20)");
if(!stmt) {
	throw ("Insert #1 prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

 // Insert additional row of data into table via prepare statement
stmt = conn.prepare("INSERT t1 VALUES (5,6,30), (7,4,40), (8,9,60) ON DUPLICATE KEY UPDATE c=c+100");
if(!stmt) {
	throw ("Insert #2 prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

 // Select * from table
stmt = conn.prepare("SELECT * FROM t1");
if(!stmt) {
	throw ("Create table prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

 // Insert additional row of data into table via prepare statement
stmt = conn.prepare("INSERT t1 SET a=5 ON DUPLICATE KEY UPDATE b=0");
if(!stmt) {
	throw ("Insert #3 prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

 // Select * from table
stmt = conn.prepare("SELECT * FROM t1");
if(!stmt) {
	throw ("Create table prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

 // Insert final row of data into table via prepare statement
stmt = conn.prepare("INSERT t1 VALUES (2,1,11), (7,4,40) ON DUPLICATE KEY UPDATE c=c+VALUES(a)");
if(!stmt) {
	throw ("Insert #4 prepare statement failed.  Error: " + res.error());
}

// Verify that the param count is as expected.
pcnt = stmt.paramCount();
 if (!pcnt == 0){
	throw ("Param count incorrect. Expected 0.  Received: " + pcnt);
}

rc = stmt.execute();
if (rc){
	throw ("Statement execute failed. Error: " + rc);
}

// Execute final query several times
for (i=0; i < 3;i++) {
	rc = stmt.execute();
	if (rc){
		throw ("Statement execute failed. Error: " + rc);
	}
}

stmt.close();

// Commit the results
res = conn.commit();
if(conn.error()) {
	throw ("Commit failed");
}
	
// Disconnect from database server
res = conn.close(); 

test();  // Leave this alone

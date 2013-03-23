/**
 * See http://developer.mozilla.org/en/docs/Storage for a bit of inspiration
 * 
 * @author sarid
 */

var connection = new DB.MySQLDataAdapter.Connection("<connection string>");

// Alternatively, for some adapters:
var connection = new DB.SqlLiteDataAdapter.Connection();
connection.server = "127.0.0.1";
connection.port = "4224";
connection.database = "demo_db";
connection.user = "root";
connection.password = "mypassword";

// Optional: will happen automatically once you use the connection
connection.open();

// Execute SQL with no parameters and no return results:
connection.execute("CREATE TABLE Foo (a INTEGER)");

// Execute SQL with no parameters and with return results:
var resultset = connection.execute("SELECT * FROM Foo, Bar WHERE Bar.foo_id = Foo.id");

// Execute SQL with parameters and with return results:
var resultset = connection.execute("SELECT * FROM foo WHERE a = ? AND b > ?", [aValue, bValue]);
// or -- We may or may not want the following syntax:
var statement = connection.createStatement("SELECT * FROM foo WHERE a = ? AND b > ?");
// Come up with various bind() methods...
var resultset = statement.execute();

// Using a resultset:
var iHobby = resultset.indexOfColumn("hobby");
for (var i=0; i<resultset.rows.length; i++)
{
	var row = resultset.rows[i]; // Object: columnName => columnValue
	alert(row.email);
	var hobby = row[iHobby];
}

//Execute SQL one row at a time:
var resultset = connection.executeStep("CREATE TABLE Foo (a INTEGER)");
// or
var resultset = statement.executeStep();
// then
while (!resultset.isAtEnd)
{
	var row = resultset.currentRow;
	var colA = row[0];
	var colB = row["email"];
	resultset.nextRow();
}
// or
var row;
while (row = resultset.nextRow())
{
	//...
}

connection.close(); // Necessary only if you use executeSqlStep() or executeStep()


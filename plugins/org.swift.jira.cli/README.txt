Command line interface client for JIRA

This is a command line interface (CLI) for remotely accessing JIRA.
This is a working command line client that can be used directly with your installation.
It uses JIRA's SOAP remote API, REST API, and other techniques.
This is part of a family of CLI clients supporting Atlassian products.
Also available as part of the Atlassian CLI distribution.

Installation
- Unzip the distribution package and put the enclosed directory in a convenient location
- Ensure Remote API is enabled in your JIRA installation
  -- You need to be a JIRA administrator
  -- Go to General Administration under Remote API
  -- Setting should be YES

Java Requirements
- Requires a JRE version 1.6 or higher on the client
- Run java -version from a command line
  - ensure it shows 1.6 or higher


Usage
- On a command line, cd to the directory where you installed the client
- On Windows
-- Run jira
- On Linux, Mac, or Unix
-- Run ./jira.sh
- On any system supporting Java
-- Run java -jar release/jira-cli-x.x.x.jar
- This will show help text for the command line interface client
- The client defaults to use a user of automation. Either add this user with all the authorities required to do the actions you want or specify a different user parameter
- It is recommended that you open the confluene.bat or jira.sh file with an editor and customize it for your environment by adding server, user, and password parameters.
  Follow the example in the comments and make sure you do not remove the %* at the end of the line.

License
- The software provided for this tool has a BSD style license
- The distribution ships binaries with various licenses (BSD, LGPL, and Apache)
- Look in the license directory for detailed license information
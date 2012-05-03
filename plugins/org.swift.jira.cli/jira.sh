#!/bin/bash

# Comments
# - Customize for your installation, for instance you might want to add default parameters like the following:
# java -jar `dirname $0`/lib/jira-cli-2.5.0.jar --server http://my-server --user automation --password automation "$@"

java -jar lib/jira-cli-2.5.0.jar --server https://jira.appcelerator.org "$@"

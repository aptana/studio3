# xcode-project-parser

## Latest Version
0.0.3

## Description

A maven plugin that will be used to build/test/deploy/pull in iOS frameworks.

## Pom.xml information

In order to use this plugin, add the following to your pom.xml in the dependencies section

```
<dependency>
	<groupId>ca.mestevens.ios</groupId>
	<artifactId>xcode-project-parser</artifactId>
	<version>${xcode.project.parser.version}</version>
</dependency>
```

where `${xcode.project.parser.version}` is the version of the plugin you want to use.

## Usage

The easiest way to use this project parser is to create a new `XCodeProject` object.

```
XCodeProject project = new XCodeProject("path/to/xcodeproj");
```
You can then use the XCodeProject methods to create/modify sections of the xcode project.

When you are done with the project, you need to write it back to a file (which at the moment is NOT built into the api). The string that is created from `project.toString()` is the appropriate string to write to a file.

## Release Notes:

* 0.0.3
	* Bug fixes
* 0.0.2
	* Bug fixes
* 0.0.1
	* Initial release
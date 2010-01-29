Freebase Power Tools
====================

Freebase Power Tools are a collection of command line tools targeted at Freebase 
power users for the purpose of creating, manipulating and writing large sets of
data to Freebase through the public API.

Installation
------------

- Make sure that you have Java 1.5 or higher installed
- Make sure that the JAVA_HOME environment variable is setup properly
- Extract the contents of the zip file to a convenient location on your hard drive
- Add the bin directory to your Path environment variable

Usage
-----

To get a detailed listing of the available arguments for any of the tools just use 
the --help flag. For example:

	freebase-update --help

There is a user.properties file in the conf directory that can be used to record common 
properties like username, password or format so that they don't have to be typed in 
each time. For example:

	username=johnsmith
	password=abc123
	format=TSV	

Building from source
--------------------

Compile the project from the root directory

	mvn package
	
The fully assembled application will be created in target/appassembler

Copyright (c) 2010 Shawn Simister. See LICENSE for details.
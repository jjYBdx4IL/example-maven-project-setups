## launch4j example

Creates a windows executable to start your java application.
Builds a zip file that contains:
 * Main jar with mainClass and classpath in its MANIFEST.
 * dependency/ folder containing all dependent jars.
 * The executable.
 * Win 64 OpenJDK JRE.
Read more: http://launch4j.sourceforge.net/

## TODO

The build phases are messed up and the project should probably
be split into two, ie. a separate platform package project.

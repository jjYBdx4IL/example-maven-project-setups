## launch4j example

Creates a windows executable to start your java application.
Builds a zip file that contains:
 * Main jar with mainClass and classpath in its MANIFEST.
 * dependency/ folder containing all dependent jars.
 * The executable.
 * Win 64 OpenJDK JRE.
Read more: http://launch4j.sourceforge.net/

## Other target platforms

There seems to be no single nice and open-source way of creating an executable wrapper
for the most popular platforms. For Mac, app bundler would probably be a good option.
For Linux, a simple shell script would suffice.


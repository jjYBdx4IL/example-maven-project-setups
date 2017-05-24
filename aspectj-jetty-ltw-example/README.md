# aspectj-jetty-ltw-example

## Usage

Build:

```
mvn install
```

Start the jetty webapp container with automatic reload and AspectJ weaving enabled:

```
mvn dependency:properties exex:exec 
```

This command might not properly stop the jetty instance when hitting CTRL-C.

Go to http://localhost:8080/ to check the result for a JSP using a woven object, to http://localhost:8080/content to
check the result for weaving servlet methods.

Finally, shut down the jetty instance with:

```
mvn jetty:stop
```

## Status

All working, ie.

* weaving stuff that is used in JSPs,
* weaving servlets,
* jetty webapp gets restarted on Netbeans compile-on-save, ie. when you save your Java code,
* JSPs get refreshed with a simple browser reload.

## Issues

There are currently only known issues with:

* the dependency:properties goal gets executed but the results aren't available to jetty:run if one only invokes "mvn
jetty:run" instead of "mvn install jetty:run" or "mvn dependency:properties jetty:run".
* LTW (load-time-weaving) needs the AspectJ Weaver jar file specified as -javaagent on JVM start. This requires to run
tests and the jetty plugin to run in forked mode. However, the jetty plugin does not stupped classpath scanning and
corresponding auto-reload in forked mode ("run-forked"). So you are left with either explicitly adding the -javaagent
command line option to the MAVEN_OPTS environment variable, or use the exec:exec goal set up with this project.

## TODO

This JVM startup business can potentially be done in a better way, ie. a way that does not require jetty:stop, by means
of using settings stored in $basedir/.mvn.
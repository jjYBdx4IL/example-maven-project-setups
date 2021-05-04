# gwt-example-2

A multi-module GWT example based on the new TBroyer's gwt-maven-plugin.

## Features

This example is based on gwt-maven-plugin's webapp archetype. I added:

* GwtTestCases to demonstrate what can be done using HtmlUnit and how.
* Selenium test unit (integration test)

## Usage

For dev mode (fast in-browser reloads while modifying the source code)
start the following two commands in the given order:

```
mvn gwt:codeserver
# and
mvn jetty:run -Denv=dev
# or: mvn tomcat7:run -Denv=dev (no auto-reload of server classes)
```

Point your browser at http://localhost:8080 and start hacking.

The jetty server is configured to automatically restart after changing any of the 
server classes. Updating the CSS sometimes requires to do a shift-reload
(force reload) in your browser (or simply disable caching using your
browser's dev tools). The gwt:codeserver is known to not shut down properly
under Windows when using SIGINT/ctrl-c. Use task manager for that.
You'll likely discover a few locked files in that case.

For release do:

```
mvn clean install
```

To check out the produced WAR (web archive), run one of the following
two commands and go to the same web address again to verify the final
result with generated, static JavaScript:

```
mvn tomcat7:run-war-only
# or
mvn jetty:run-war
```

##

Start WildFly server (JavaEE 8):

```
mvn -f example-server wildfly:start@start-server wildfly:add-resource@add-datasource wildfly:deploy@deploy-war
```

Administrative frontend is at localhost:9990, user/pwd is admin/admin.

Shutdown:

```
mvn -f example-server wildfly:shutdown
```

Caveats: currently runs against the packaged war file, therefore this mode is currently not suitable for server-side
development. There is kind of a workaround for this though:

    https://github.com/jjYBdx4IL/snippets/blob/master/java/jee_autodeploy.sh

scans for changes on disk, and
Wildfire allows to deploy ecploded WARs and add external classpaths to them:
https://developer.jboss.org/wiki/HowToPutAnExternalFileInTheClasspath. That should make it possible to include
the build paths of other workspace modules in order to avoid having to rebuild and deploy them.



# aspectj-jetty-ltw-example

## Usage

Build and configure Maven to use AspectJ Weaver (.mvn/jvm.config):

```
mvn install
```

Start the jetty webapp container with automatic reload and AspectJ weaving enabled:

```
mvn jetty:run
```

Go to http://localhost:8080/ to check the result for a JSP using a woven object, to http://localhost:8080/content to
check the result for weaving servlet methods.

## Status

All working, ie.

* weaving stuff that is used in JSPs,
* weaving servlets,
* jetty webapp gets restarted on Netbeans compile-on-save, ie. when you save your Java code,
* JSPs get refreshed with a simple browser reload.

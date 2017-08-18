# GWT Example

## devel

	mvn clean install
	mvn gwt:run
	mvn gwt:debug

Updating the java source file and hitting "reload" in your browser
is all it takes to reload the java source code changes!

Since GWT 2.7 gwt:debug is only for debugging the server side implementation.
For the (javascript) client, use the development tools built into the chrome browser.
With the source code mappings you can debug the original java sources from
within the chrome javascript debugger.

## prod

To build the final javascript code, use the 'prod' profile:

	mvn clean install -Pprod

## chromedriver

Make sure you have the latest chromedriver installed and it is available on
your path.

## fast compilation

The 'fast' profile is intended to limit javascript compilation to one browser
and one language.

	mvn clean install -Pprod,fast

## find updates

	mvn versions:display-dependency-updates
	mvn versions:display-plugin-updates

## session handling

sandbox.jsp forces the existence of a session, ie. the session (id) itself is
decoupled from the authorization state.

## debugging

GWT uses source maps for debugging client code directly inside your browser.
These source maps do not support reverse mappings to the compiled javascript
code (for whatever stupid reason), watches generally don't work, and browsers
currently do not allow to look up variables. This makes debugging static
variables using the browser's development tools almost impossible or at least
extremely tedious. Solution: don't
use static variables at all or buffer them locally in non-static variables so they
can be inspected via the local scope. If you need global singleton variables,
like the event bus, provide them via Gin instead of accessing them statically.

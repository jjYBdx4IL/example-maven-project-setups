# development setup testing

This module contains the main (selenium) tests and runs them against GWT DevMode.

Use -Psurefire to disable starting/stopping of development mode servers. This assumes
you have started them manually and should be used for developing tests. For example,
you most likely want to enable this profile in your IDE, ie Netbeans.

Use -DskipITs to do only compilation.

The same test classes are also used in the integration-test phase of the dist module
to check the final distribution package.

Use mvn antrun:run@start (or @stop) to start (stop) the servers so you can run
and develop single unit tests. (you need a reasonably recent maven version for the
@-part to work)

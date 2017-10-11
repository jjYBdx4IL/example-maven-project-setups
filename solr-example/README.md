# solr Example

Run the example test using `mvn clean install`. It will automatically install, start,
create a table, execute test units against it, and then stop the
solr application at the default port 8983.

To run the test units manually, start/stop the solr server (after having run `mvn install`
once) using `./target/solr-$version/bin/solr start|stop`.

## running single-node example test units

    mvn clean install

## running test units against a full-blown cluster
  
    mvn clean install -Dcluster

## getting a clean solr installation (just unpacking the distro)

    mvn clean download:wget@solr-dist-unpack

## setting up and starting/stopping the cluster for manual test execution against it
   
    mvn clean install -Dcluster
    mvn antrun:run@start-pre-integration-test -Dcluster # start
    mvn antrun:run@stop-post-integration-test -Dcluster # stop

You can use these commands to manually run the test units against the cluster setup.

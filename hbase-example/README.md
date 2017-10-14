# HBase Examples

The maven build downloads the binary HBase distribution and installs it below target/hbase-$version.

The default single-node setup will be start before integration-tests are being run if there is
nothing listening on port 2181.

You may start/stop the server gracefully by running 

    mvn antrun:run@start
    mvn antrun:run@stop

A management frontend is accessible via:

    localhost:16010

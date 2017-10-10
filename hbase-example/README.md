# HBase Examples

The maven build downloads the binary HBase distribution and installs it below target/hbase-$version.

The default single-node setup will be start before integration-tests are being run if there is
nothing listening on port 2181.

You may stop the server gracefully by running `./target/hbase-1.2.6/bin/stop-hbase.sh`.

It won't be stopped automatically.

A management frontend is accessible via localhost:16010.

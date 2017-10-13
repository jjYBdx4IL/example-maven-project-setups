# solr Example

Run the example test using `mvn clean install`. It will automatically install, start,
create a table, execute test units against it, and then stop the
solr application at the default port 8983.

To run the test units manually, start/stop the solr server (after having run `mvn install`
once) using `./target/solr-$version/bin/solr start|stop`.

## running test units against single-node example setup

    mvn clean install
    ./target/solr-*/bin/solr start
    ./target/solr-*/bin/solr stop

## running test units against a full-blown cluster
  
    mvn clean install -Dcluster

## getting a clean solr installation (just unpacking the distro)

    mvn clean download:wget@solr-dist-unpack

## setting up and starting/stopping the cluster for manual test execution against it
   
    mvn clean install -Dcluster
    mvn antrun:run@start-pre-integration-test -Dcluster # start
    mvn antrun:run@stop-post-integration-test -Dcluster # stop

You can use these commands to manually run the test units against the cluster setup.

## replacing a failed solr node

First, run SpamMain for some seconds to generate some data on the cluster.

Forced fail:

    kill -KILL `netstat -tlpen | grep "^tcp.*:8985.*java" | sed -e 's:.*\b\([0-9][0-9]*\)/java\b:\1:'`
    rm -rf target/solr-*/node3/*

Verify that the data is gone on the 3rd node:

    du -sm target/solr-*/node*
    
Replace:

    ./target/solr-*/bin/solr start -c -z localhost:2181,localhost:2182,localhost:2183 \
        -s `pwd`/target/solr-*/node3 -h localhost -p 8985

Now point your web browser to localhost:8983, go to Collections -> gettingstarted and replace
(remove and add new) the dead shard replicas there. After that go to Cloud -> Graph to verify
everything is green again. Beware! There is nothing special preventing you from deleting the
last functioning replica of a shard. This whole process is cumbersome.

Verify that the data has recovered on the 3rd node:

    du -sm target/solr-*/node*

## Some ZooKeeper commands

    # download configuration stored in zookeeper:
    ./target/solr-*/bin/solr zk cp -r zk:/ target/cfg-dl -z localhost:2181
    # recursive directory listing
    ./target/solr-*/bin/solr zk ls -r zk:/ -z localhost:2181

## Migration of non-cloud solr instance to hdfs cluster

The following commands show how the data directory of the 'gettingstarted' collection can be moved to
a hdfs cluster 'mycluster'. Note that we won't switch to SolrCloud and that the collection config gets
copied to the hdfs cluster only for backup purposes. Everything except the '../data' directory will
still be read from the local disk after the migration.

    # setup single-node solr instance
    mvn clean install
    
    # start the single-node instance:
    ./target/solr-*/bin/solr start
    
    # open SpamMain.java and run it from your IDE (ie. eclipse) to feed the index with randomly generated documents
    ...

    # now set up and start a full hdfs cluster:
    cd ../hdfs-example
    mvn clean integration-test
    export HADOOP_HOME=`pwd`/target/dfsnode1
    
    # migrate the data: (make sure there are no writes happening by disabling your index feeder task etc.)
    ./hdfs.sh 1 dfs -mkdir -p /solr
    ./hdfs.sh 1 dfs -copyFromLocal ../solr-example/target/solr-*/server/solr/gettingstarted /solr/gettingstarted
    ./hdfs.sh 1 dfs -rm /solr/gettingstarted/data/index/write.lock
    ./hdfs.sh 1 dfs -ls -R /
    
    # restart solr against cluster:
    cd ../solr-example
    ./target/solr-*/bin/solr stop
    rm -rf target/solr-*/server/solr/gettingstarted/data
    ./target/solr-*/bin/solr start -Dsolr.directoryFactory=HdfsDirectoryFactory -Dsolr.lock.type=hdfs \
        -Dsolr.data.dir=hdfs://mycluster/solr/gettingstarted/data -Dsolr.hdfs.confdir=$HADOOP_HOME/etc/hadoop \
        -Djava.library.path=$HADOOP_HOME/lib/native
        
    # done.
    # Now go to http://localhost:8983/solr/#/gettingstarted/query , click on Execute Query and verify
    # that your data is still there.
    
    # clean up:
    mvn clean
    mvn clean -f ../hdfs-example

Sadly, I didn't find a way to make a non-cloud solr instance store its collection configuration on hdfs, though
the cloud-version of solr would use ZooKeeper's shared storage for storing configuration files.




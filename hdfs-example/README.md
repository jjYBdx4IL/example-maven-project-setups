# HDFS Example

Full cluster HDFS example using QJM.

The QJM allows use to have redundancy for the name nodes without
using external redundancy providers like SAN/NFS/DRBD etc.

## Requirements

* Linux/BASH
* Java 1.8
* Maven
* common linux tools like screen, netstat, ...

## WWW Frontends

    http://127.0.0.1:50070 (name node 1 (active))
    http://127.0.0.1:50071 (name node 2 (passive))
    http://127.0.0.1:9164 (data node 1)
    http://127.0.0.1:9264 (data node 2)
    http://127.0.0.1:9364 (data node 3)
    http://127.0.0.1:8180 (journal node 1)
    http://127.0.0.1:8280 (journal node 2)
    http://127.0.0.1:8380 (journal node 3)
    
    http://localhost:50070/jmx
    http://localhost:50070/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem
    http://localhost:9164/blockScannerReport

name, data and journal nodes with the same number share the same installation directory:

    ./target/dfsnode<node-number>

## copy stuff to HDFS

    ./target/dfsnode1/bin/hdfs --config `pwd`/target/dfsnode1/etc/hadoop dfs -copyFromLocal src /
    
## manual start/stop of the cluster

    mvn integration-test # install and start cluster, run tests, keep running
    mvn antrun:run@stop  # stop cluster
    mvn clean            # stop and clean
    bash src/test/resources/setup.sh `pwd` start|stop # start/stop cluster
    ./hdfs.sh <nodeid=1-3> [cmd-args] 

## Some commands

    # dump blocks (remove cursor file to force block scan on startup of data node)
    find . -name 'blk_??????????' -printf '%p\n' -exec bash -c 'cat {}; echo' \; ; find . -name scanner.cursor
    # list hdfs contents
    ./hdfs.sh 1 dfs -ls -R /
    
    ./hdfs.sh 1 dfs -mkdir /solr
    ./hdfs.sh 1 dfs -copyFromLocal ../solr-example/target/solr-7.0.1/server/solr/gettingstarted /solr/gettingstarted
    ./hdfs.sh 1 dfs -rm -R /solr
    

## Notes

* it seems that it's not possible to run multiple data nodes on the same machine as long
as they share the same ports: they will be identified by 127.0.0.1:<port> by the name node
even if one tells them to bind to different local ip addresses.

## MAYDO

* HA using ZooKeeper and fence script?

## Conclusion

* append-only, one writer at a time only
* corrupt blocks don't get fixed automatically. Not even the redundancy level is kept up
because corrupt blocks count towards the number of active/good replicas.
To avoid data loss manual intervention and monitoring of CorruptedBlocks is necessary.
This runs counter to the promise of expecting frequent hardware failures. HDFS is thus
best run on RAID system which again runs counter to the HDFS-level replication.
https://stackoverflow.com/questions/19205057/how-to-fix-corrupt-hdfs-files



# HDFS Example

## Requirements

* Linux/BASH
* Java 1.8
* Maven
* common linux tools like screen, netstat, ...

## WWW Frontends

    http://127.0.0.1:9870 (name node)
    http://127.0.0.1:9164 (data node 1)
    http://127.0.0.1:9264 (data node 2)
    http://127.0.0.1:9364 (data node 3)

## copy stuff to HDFS

    ./target/dfsnode1/bin/hdfs --config `pwd`/target/dfsnode1/etc/hadoop dfs -copyFromLocal src /

    
## manual start/stop of the cluster

    mvn clean download:wget@hadoop-dist-unpack
    bash src/test/resources/setup.sh `pwd` start|stop

## Notes

* it seems that it's not possible to run multiple data nodes on the same machine as long
as they share the same ports: they will be identified by 127.0.0.1:<port> by the name node
even if one tells them to bind to different local ip addresses.

## TODO

* namenode replication/HA?

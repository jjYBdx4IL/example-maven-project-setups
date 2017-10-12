#!/bin/bash

set -Eex

export HADOOP_HOME=$(pwd)/target/dfsnode$1
shift
test -d $HADOOP_HOME


$HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop "$@"


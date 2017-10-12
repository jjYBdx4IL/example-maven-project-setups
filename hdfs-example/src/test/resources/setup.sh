#!/bin/bash

set -Eex
set -o pipefail

basedir=$1
shift
cmd=$1
shift || :
test -d $basedir
hadoopdist=$(ls -d $basedir/target/hadoop-*) || exit 0

stopAddr() {
    kill -HUP `netstat -tlpen | grep "^tcp.* $1 .*java" | sed -e 's:.*\b\([0-9][0-9]*\)/java\b:\1:'` || :
}

which screen
which netstat

for i in 1 2 3; do
    export HADOOP_HOME=$basedir/target/dfsnode$i
    install -d $HADOOP_HOME/name
    install -d $HADOOP_HOME/data
    install -d $HADOOP_HOME/logs
    find $hadoopdist -mindepth 1 -maxdepth 1 -type d -printf '%P\n' | while read d; do
        if test -e $HADOOP_HOME/$d; then continue; fi
        ln -s $hadoopdist/$d $HADOOP_HOME/$d
    done
    rm -rf $HADOOP_HOME/etc
    cp -r $hadoopdist/etc $HADOOP_HOME
    cp -rfv $basedir/src/test/configs/etc $HADOOP_HOME/.
    sed -i -e "s:@DFSNODEHOME@:$HADOOP_HOME:" $HADOOP_HOME/etc/hadoop/hdfs-site.xml
    sed -i -e "s:@NODENUM@:$i:" $HADOOP_HOME/etc/hadoop/hdfs-site.xml
done

export HADOOP_HOME=$basedir/target/dfsnode1

if ! test -e $HADOOP_HOME/name/current/VERSION; then
    $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop namenode -format
fi

if [[  "$cmd" == "start" ]]; then
    rm -fv $basedir/target/*node*.log
    screen -dmS namenode -L $basedir/target/namenode.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop namenode
    screen -dmS datanode1 -L $basedir/target/datanode1.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop datanode
    export HADOOP_HOME=$basedir/target/dfsnode2
    screen -dmS datanode2 -L $basedir/target/datanode2.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop datanode
    export HADOOP_HOME=$basedir/target/dfsnode3
    screen -dmS datanode3 -L $basedir/target/datanode3.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop datanode
fi

if [[  "$cmd" == "stop" ]]; then
    stopAddr 127.0.0.1:9870 # namenode
    for i in 1 2 3; do
        addr=127.0.0.1:9"$i"67
        export HADOOP_HOME=$basedir/target/dfsnode$i
        if netstat -tlpen | grep $addr; then
            $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop dfsadmin -shutdownDatanode $addr
        fi
    done
fi

exit 0

#!/bin/bash

set -Ee
set -o pipefail

if [[ -n "$DEBUG" ]]; then
    set -x
fi

basedir=$1
shift
cmd=$1
shift || :
test -d $basedir

hadoopdist=$basedir/target/hadoop

if tarfile=$(ls -d $basedir/target/hadoop-*.tar.gz 2>/dev/null); then
    if ! test -e $hadoopdist; then
        install -d $hadoopdist
        compr="-z"
        if which pigz >&/dev/null; then compr="-I pigz"; fi
        tar -C $hadoopdist --exclude="**/share/doc/***" -xf $tarfile $compr --strip-components=1
    fi
fi

if ! test -e $hadoopdist; then exit 0; fi

stopAddr() {
    local pids=`netstat -tlpen 2>/dev/null | grep "^tcp.* $1 .*java" | sed -e 's:.*\b\([0-9][0-9]*\)/java\b.*:\1:'`
    if [[ -n "$pids" ]]; then
        echo "sending SIGHUP to $1 (pid $pids)"
        kill -HUP $pids || :
        waitAddrDown $1
    fi
}

waitAddr() {
    echo "waiting for $1 ($2)"
    while ! isAddrUp $1; do
        sleep 1
    done
}

waitAddrDown() {
    while isAddrUp $1; do
        sleep 1
    done
}

isAddrUp() {
    if netstat -tlpen 2>/dev/null | grep "^tcp.* $1 .*java" >&/dev/null; then
        return 0
    else
        return 1
    fi
}

which screen >/dev/null
which netstat >/dev/null

for i in 1 2 3; do
    export HADOOP_HOME=$basedir/target/dfsnode$i
    install -d $HADOOP_HOME/name
    install -d $HADOOP_HOME/data
    install -d $HADOOP_HOME/logs
    install -d $HADOOP_HOME/journaldata
    find $hadoopdist -mindepth 1 -maxdepth 1 -type d -printf '%P\n' | while read d; do
        if test -e $HADOOP_HOME/$d; then continue; fi
        ln -s $hadoopdist/$d $HADOOP_HOME/$d
    done
    rm -rf $HADOOP_HOME/etc
    cp -r $hadoopdist/etc $HADOOP_HOME
    cp -rf $basedir/src/test/configs/etc $HADOOP_HOME/.
    sed -i -e "s:@DFSNODEHOME@:$HADOOP_HOME:" $HADOOP_HOME/etc/hadoop/hdfs-site.xml
    sed -i -e "s:@NODENUM@:$i:" $HADOOP_HOME/etc/hadoop/hdfs-site.xml
    sed -i -e "s:@BASEDIR@:$basedir:" $HADOOP_HOME/etc/hadoop/hdfs-site.xml
done


if [[  "$cmd" == "start" ]]; then
    for i in 1 2 3; do
        if isAddrUp 127.0.0.1:8"$i"80 ; then continue; fi
        export HADOOP_HOME=$basedir/target/dfsnode$i
        screen -dmS journalnode$i -L -Logfile $basedir/target/journalnode$i.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop journalnode
    done
    for i in 1 2 3; do
        waitAddr 127.0.0.1:8"$i"80 journalnode$i
    done
    
    if ! isAddrUp 127.0.0.1:8020 ; then
	    export HADOOP_HOME=$basedir/target/dfsnode1
	    if ! test -e $HADOOP_HOME/name/current/VERSION; then
	        $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop namenode -format -force
	    fi
	    screen -dmS namenode1 -L -Logfile $basedir/target/namenode1.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop namenode
	    waitAddr 127.0.0.1:8020 namenode1
    fi
    
    if ! isAddrUp 127.0.0.1:8021 ; then
	    export HADOOP_HOME=$basedir/target/dfsnode2
	    if ! test -e $HADOOP_HOME/name/current/VERSION; then
	        $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop namenode -bootstrapStandby -force
	    fi
	    screen -dmS namenode2 -L -Logfile $basedir/target/namenode2.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop namenode
	    waitAddr 127.0.0.1:8021 namenode2
	fi
    
    for i in 1 2 3; do
        if isAddrUp 127.0.0.1:9"$i"67 ; then continue; fi
        export HADOOP_HOME=$basedir/target/dfsnode$i
        screen -dmS datanode$i -L -Logfile $basedir/target/datanode$i.log $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop datanode
    done
    for i in 1 2 3; do
        waitAddr 127.0.0.1:9"$i"67 datanode$i
    done
    
    export HADOOP_HOME=$basedir/target/dfsnode1
    while [[ "$($HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop haadmin -getServiceState nn1)" != "active" ]]; do
        $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop haadmin -failover nn2 nn1 || :
        sleep 3
    done
fi

if [[  "$cmd" == "stop" ]]; then
    stopAddr 127.0.0.1:8021 namenode2
    stopAddr 127.0.0.1:8020 namenode1
    for i in 1 2 3; do
        addr=127.0.0.1:9"$i"67
        export HADOOP_HOME=$basedir/target/dfsnode$i
        if netstat -tlpen 2>/dev/null | grep $addr >&/dev/null; then
            $HADOOP_HOME/bin/hdfs --config $HADOOP_HOME/etc/hadoop dfsadmin -shutdownDatanode $addr
        fi
        stopAddr 127.0.0.1:8"$i"80 journalnode$i
    done
fi

exit 0

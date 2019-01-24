#!/bin/bash

# vim:set sw=4 et ts=4:

set -Ee
set -o pipefail

if [[ -n $DEBUG ]]; then
    set -x
fi

# this is used to verify that a process is really OUR process before
# terminating it:
PROCIDTAG=$1
shift
port=$1
shift
cmd=$1
shift

export LC_ALL=C
export LANG=C
export TZ=UTC

isWin=0
if which taskkill; then
    isWin=1
    which tasklist
    which netstat
fi

function check_started() {
    if [[ -n $(get_win_pids) ]]; then return 0; fi
    (
        set +o pipefail
        grep -l "PROCIDTAG=$PROCIDTAG" /proc/*/environ 2>/dev/null | grep proc >&/dev/null
    )
}

function get_pids() {
    local f
    for f in `grep -l "PROCIDTAG=$PROCIDTAG" /proc/*/environ 2>/dev/null || :`; do
        if [[ $f =~ /proc/(.+)/environ ]]; then
            echo " ${BASH_REMATCH[1]}"
        fi
    done
}

function get_win_pids() {
    local f
    if ! ((isWin)); then return 0; fi
    if [[ $PROCIDTAG =~ :gwt$ ]]; then
        for f in `tasklist /v /fo csv /fi "imagename eq java.exe" /fi "windowtitle eq GWT Development Mode" /nh | grep '^"' | cut -d , -f 2 | cut -d '"' -f 2`; do
            echo " $f"
        done
    fi
    local l
    netstat -a -o -n -p TCP | LC_ALL=C grep "^\s*TCP\s*[:\.0-9]*:$port\s" | while read l; do
        if [[ $l =~ [[:space:]]([0-9]*)[[:space:]]*$ ]]; then
            if [[ ${BASH_REMATCH[1]} -ne 0 ]]; then 
                echo " ${BASH_REMATCH[1]}"
            fi
        fi
    done
}

function stop() {
    local sig=$1
    for pid in `get_pids`; do
        kill -$sig $pid
    done
    for pid in `get_win_pids`; do
        if [[ $sig -eq KILL ]] || [[ $sig -eq kill ]]; then
            taskkill /F /PID $pid
        else 
            taskkill /PID $pid
        fi
    done
    return 0
}

function shutdown() {
    local count=0
    local delay=3
    local maxCount=10
    while check_started && (( count < maxCount )); do
        if (( count == 0 )); then
            stop HUP
        else
            stop TERM
        fi
        sleep $delay
        count=$((count + 1))
    done
    if check_started; then
        stop KILL
    fi
    ! check_started
}

if [[ $cmd == "start" ]]; then
    if check_started; then
        echo "already running" >&2
        exit 0
    fi

    export PROCIDTAG
    pwd
    echo "$@ >& log"
    (
        "$@" || :
    ) >& log &
    echo "started" >&2
elif [[ $cmd == "stop" ]]; then
    if ! check_started; then
        echo "not running" >&2
        exit 0
    fi
    if ! shutdown; then
        echo "shutdown failed" >&2
        exit 1
    fi
    echo "shutdown ok" >&2
else
    echo "unknown command: $cmd" >&2
    exit 1
fi

exit 0

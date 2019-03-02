#!/bin/bash
# vim:set sw=4 ts=4 et ai smartindent fileformat=unix fileencoding=utf-8 syntax=sh:

#
# Starts and kills gwt dev mode servers while merging their outputs to the current
# console. Needs a recent SCREEN version.
#
# For slow (proper) mode, do: `SLOW=1 ./startDev.sh`
#
# Tested with:
#   Screen version 4.06.02 (GNU) 23-Oct-17
# on
#   Ubuntu 18.04.1 LTS (bionic)
#   Linux ubuntu 4.15.0-45-lowlatency #48-Ubuntu SMP PREEMPT Tue Jan 29 17:45:27 UTC 2019 x86_64 x86_64 x86_64 GNU/Linux
#

set -Eex ; set -o pipefail
scriptdir=$(readlink -f "$(dirname "$0")")
cd $scriptdir

which screen
install -d target
touch target/{gwtcodeserver,webserver}.log
echo "Hit ENTER to STOP, go to http://localhost:8080 for development."
tail -n0 -F target/*.log &
tailpid=$!
trap "kill $tailpid" EXIT
screen -c /dev/null -dmS gwtcodeserver -L -Logfile target/gwtcodeserver.log mvn gwt:codeserver
# haven't had problems with the two maven processes starting up in parallel yet, but
# it should be problematic at least in principle.
# Also, in a clean environment, the codeserver seems to create directories that make jetty
# fail if they are not there yet - that's a potential race I know of.
# So, for a proper startup, we should wait for codeserver to finish its startup first.
if [[ -n "$SLOW" ]]; then
    while ! curl -s -o /dev/null http://localhost:9876; do sleep 1; done
fi
screen -c /dev/null -dmS webserver -L -Logfile target/webserver.log mvn jetty:run -Denv=dev
read
kill `screen -ls | perl -ne '/^\s+(\d+)\.(webserver|gwtcodeserver)\s+.*$/&&print $1.$/'`



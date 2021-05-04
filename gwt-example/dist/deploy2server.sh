#!/bin/bash

set -Eex
set -o pipefail

which curl
which ssh
which rsync
which unzip

fn=$1
artifactId=$2
srcArtifactId=$3
acct=$4
if [[ -z "$acct" ]]; then
    echo "please define deployment SSH account, ie -Ddeployment.sshacct=user@host" >&2
    exit 1
fi
host=${acct##*@}
chktgt=http://$host:80/
chktgt2=https://$host:443/
unpd=target/deploy.zip.unpack
distname=$artifactId
relexec=./$distname/autostart.sh
tmpdest=$distname.deploy.tmpdest
sshopts='-o PasswordAuthentication=no -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o ConnectTimeout=10 -o TCPKeepAlive=yes -o BatchMode=yes -o ServerAliveInterval=3 -o ServerAliveCountMax=4 -o IdentitiesOnly=yes'

uptest() {
    if curl -k --connect-timeout 3 -s -I "$chktgt" | grep "^Content-Type:" >/dev/null; then
        return 0
    fi
    if curl -k --connect-timeout 3 -s -I "$chktgt2" | grep "^Content-Type:" >/dev/null; then
	return 0
    fi	
    return 1
}

rm -rf $unpd
unzip -d $unpd $fn
rsync -azi --partial -e "ssh $sshopts" --del --copy-dest="../$distname/" $unpd/$distname/ $acct:$tmpdest
if ssh $sshopts $acct test -e $relexec; then
    ssh $sshopts $acct $relexec stop
fi
uptest && exit 1
ssh $sshopts $acct rsync -ai --del --exclude="/data/*" ./$tmpdest/ "./$distname"
ssh $sshopts $acct $relexec start
ssh $sshopts $acct rm -rf $tmpdest

# wait for server to come up
endtime=$(( $(date +%s) + 180 ))
while ! uptest && (( $(date +%s) < endtime )); do
    sleep 3
done

uptest

curl -k -s "$chktgt2" | grep sandbox.html >/dev/null


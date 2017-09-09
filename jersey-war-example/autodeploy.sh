#!/bin/bash

# tested with WildFly 11 and Glassfish 4.x

set -x

webappdir=`pwd`/target/jersey-war-example-1.0-SNAPSHOT
iswildfly=0

if curl http://localhost:9990/ ; then
    iswildfly=1
fi

if (( iswildfly )); then
	curl --digest -L -u admin:admin -D - http://localhost:9990/management --header "Content-Type: application/json" \
                -d '{"operation" : "composite", "address" : [], "steps" : [{"operation" : "undeploy", "address" : {"deployment" : "ROOT.war"}},{"operation" : "remove", "address" : {"deployment" : "ROOT.war"}}],"json.pretty":1}'
	curl --digest -L -u admin:admin -D - http://localhost:9990/management --header "Content-Type: application/json" \
		-d '{"operation" : "composite", "address" : [], "steps" : [{"operation" : "add", "address" : {"deployment" : "ROOT.war"}, "content" : [{"path" : "'$webappdir'", "archive":"false"}]},{"operation" : "deploy", "address" : {"deployment" : "ROOT.war"}}],"json.pretty":1}'
fi

while inotifywait -e close_write -r $webappdir; do
    if ! (( iswildfly )); then
    	curl -v -H 'Accept: application/json' \
		-X POST \
		-H 'X-Requested-By: loadr' \
		-F force=true \
		-F id=$webappdir \
		-F isredeploy=true \
		-F virtualservers=server \
		-F contextRoot=/ \
		-F name=jersey-war-example \
		http://localhost:4848/management/domain/applications/application
    else
        curl --digest -L -u admin:admin -D - http://localhost:9990/management --header "Content-Type: application/json" \
        -d '{"operation" : "composite", "address" : [], "steps" : [{"operation" : "redeploy", "address" : {"deployment" : "ROOT.war"}}],"json.pretty":1}'
    fi
done



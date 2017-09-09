#!/bin/bash

webappdir=`pwd`/target/jersey-war-example-1.0-SNAPSHOT

while inotifywait -e close_write -r $webappdir; do
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
done



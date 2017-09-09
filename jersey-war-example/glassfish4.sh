	# deploy and redeploy local directory to local instance's root context ("/")
	curl -v -H 'Accept: application/json' \
		-X POST \
		-H 'X-Requested-By: loadr' \
		-F force=true \
		-F id=`pwd`/target/jersey-war-example-1.0-SNAPSHOT \
		-F isredeploy=true \
		-F virtualservers=server \
		-F contextRoot=/ \
		-F name=jersey-war-example \
		http://localhost:4848/management/domain/applications/application
	# undeploy
	curl -v -H 'Accept: application/json' \
		-X DELETE \
		-H 'X-Requested-By: loadr' \
		http://localhost:4848/management/domain/applications/application/jersey-war-example



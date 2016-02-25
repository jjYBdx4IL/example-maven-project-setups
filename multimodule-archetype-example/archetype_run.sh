#!/bin/bash

rm -rf test-artifact-from-archetype
pwd=$(pwd)
tmpd=$(mktemp -d)
trap "rm -rf $tmpd" EXIT
# avoid modification of pom.xml in curretn dir:
pushd $tmpd
mvn archetype:generate                                \
  -DarchetypeGroupId=org.bytedeco.javacpp-presets                \
  -DarchetypeArtifactId=javacpp-archetype          \
  -DarchetypeVersion=1.0               \
  -DgroupId=org.bytedeco.javacpp-presets                                \
  -DjavacppVersion=1.1 \
  -DartifactId=test-artifact-from-archetype \
  -Dversion=3.0.0
mv test-artifact-from-archetype "$pwd"
popd
ls -lR test-artifact-from-archetype


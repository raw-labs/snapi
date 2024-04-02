#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21-graalce || true
sdk use java 21-graalce

cd "$SCRIPT_HOME"
# On CI we will only manage versioning when publishing
# For local builds and testing we want to dynamically set the version to avoid re-using an artifact from local cache
[ "$CI" == "true" ] && { export MAVEN_CLI_OPTS="-B"; } || ./set-version.sh
mvn clean install

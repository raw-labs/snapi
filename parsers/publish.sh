#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export MAVEN_CLI_OPTS="-B";}
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce

cd "$SCRIPT_HOME"

VERSION=$(git describe --tags | sed 's/^v//;s/-\([0-9]*\)-g/+\1-/')
mvn clean deploy -Pdev,deploy -Drevision=$VERSION
echo "${VERSION}-SNAPSHOT" > version

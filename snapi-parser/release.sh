#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export MAVEN_CLI_OPTS="-B";}
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21-graalce || true
sdk use java 21-graalce

cd "$SCRIPT_HOME"

VERSION=$(git describe --tags | sed 's/^v//;s/-\([0-9]*\)-g/+\1-/')
export MAVEN_OPTS="--add-opens=java.base/java.util=ALL-UNNAMED"
mvn clean deploy -Pdev,deploy -Drevision=$VERSION -Dchangelist=
mvn nexus-staging:release -Pdev,deploy -Drevision=$VERSION -Dchangelist=
echo "${VERSION}" > version

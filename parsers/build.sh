#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; export MAVEN_CLI_OPTS="-B";}
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce

cd "$SCRIPT_HOME"

VERSION=$(git describe --tags | sed 's/^v//;s/-\([0-9]*\)-g/+\1-/')
MVN_OPTS="-Drevision=$VERSION"
VERSION_SUFFIX="-SNAPSHOT"
for arg in "$@"
do
  if [[ $arg == "--release" ]]; then
    MVN_OPTS="$MVN_OPTS -Dchangelist="
    VERSION_SUFFIX=""
    break
  fi
done
mvn clean install $MVN_OPTS
echo "${VERSION}${VERSION_SUFFIX}" > version

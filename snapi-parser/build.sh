#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21-graalce || true
sdk use java 21-graalce

cd "$SCRIPT_HOME"
NEW_VERSION=$(git describe --tags | sed 's/^v//;s/-\([0-9]*\)-g/+\1-/')
mvn versions:set -DnewVersion=$NEW_VERSION
mvn clean install

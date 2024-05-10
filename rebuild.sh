#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

find . -type d -name "target" -exec rm -r {} \; || true

cd "${SCRIPT_HOME}/deps"
./build.sh

export COURSIER_PROGRESS=false
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 22.0.1-graalce || true
sdk use java 22.0.1-graalce

cd "${SCRIPT_HOME}"
sbt clean compile

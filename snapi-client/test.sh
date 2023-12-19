#!/bin/bash -ex
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce
# snapi-frontend

cd "${SCRIPT_HOME}"

rm -rfv test-results
mkdir -p test-results

sbt test

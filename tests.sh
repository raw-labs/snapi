#!/bin/bash -ex
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce

rm -rfv test-results
mkdir -p test-results

# snapi-frontend
sbt snapiFrontend/test

# snapi-client
sbt snapiClient/test

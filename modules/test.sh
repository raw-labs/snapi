#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce

cd "${SCRIPT_HOME}"

rm -rfv test-results
mkdir -p test-results

set -x
sbt snapiTruffle/runJavaAnnotationProcessor test

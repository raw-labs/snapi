#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh
# reset shell env for later stages
[ "$JAVA_OPTS" == "-XX:+UseG1GC" ] && { unset JAVA_OPTS; }

yes | sdk install java 17.0.5-amzn || true
sdk use java 17.0.5-amzn

cd "$SCRIPT_HOME"
sbt clean "project core" publishLocal


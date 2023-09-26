#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

. ~/.sdkman/bin/sdkman-init.sh
# reset shell env for later stages
[ "$CI" == "true" ] && { INITAL_PATH=$PATH; unset JAVA_OPTS; }
sdk install java 17.0.5-amzn
sdk use java 17.0.5-amzn

cd "$SCRIPT_HOME"
sbt "project core" publishLocal
[ "$CI" == "true" ] && { PATH=$INITAL_PATH; unset JAVA_HOME; }
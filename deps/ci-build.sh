#!/bin/bash

SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

# assuming sdkman is installed
. ~/.sdkman/bin/sdkman-init.sh
# save initial path
INITAL_PATH=$PATH
# avoid GC conflict since graalvm-community is setting JAVA_OPTS to -XX:+UseG1GC
unset JAVA_OPTS
# install java 17.0.5-amzn and use it
sdk install java 17.0.5-amzn
sdk use java 17.0.5-amzn

# build kiama
cd "${SCRIPT_HOME}/deps/kiama"
sbt "project core" publishLocal

# build scala-logging
cd "${SCRIPT_HOME}/deps/scala-logging"
sbt "project core" publishLocal

# reset sdkman stuff
PATH=$INITAL_PATH
unset JAVA_HOME

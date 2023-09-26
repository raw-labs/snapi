#!/bin/bash
. ~/.sdkman/bin/sdkman-init.sh
# reset shell env for later stages
[ "$CI" == "true" ] && {INITAL_PATH=$PATH; unset JAVA_OPTS; }
sdk install java 17.0.5-amzn
sdk use java 17.0.5-amzn
sbt publishLocal
[ "$CI" == "true" ] && {PATH=$INITAL_PATH; unset JAVA_HOME; }
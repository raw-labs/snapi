#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

. ~/.sdkman/bin/sdkman-init.sh
sdk install java 21-graalce
sdk use java 21-graalce

cd "$SCRIPT_HOME"
sbt publishLocal


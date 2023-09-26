#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

export COURSIER_PROGRESS=false

cd "${SCRIPT_HOME}/deps"
./ci-build.sh


export JAVA_OPTS="-XX:+UseG1GC"

cd "${SCRIPT_HOME}/language"

sbt publishLocal

cd "${SCRIPT_HOME}/extensions"

sbt publishLocal

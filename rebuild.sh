#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

export COURSIER_PROGRESS=false

cd "${SCRIPT_HOME}"
find . -type d -name "target" -exec rm -r {} \; || true
sbt clean Test/clean cleanFiles compile Test/compile

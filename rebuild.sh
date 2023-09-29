#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

export COURSIER_PROGRESS=false

find . -type d -name "target" -exec rm -r {} \; || true

cd "${SCRIPT_HOME}/deps/kiama"
./build.sh

cd "${SCRIPT_HOME}/deps/scala-logging"
./build.sh

cd "${SCRIPT_HOME}/language"
./build.sh

cd "${SCRIPT_HOME}/extensions"
./build.sh

cd "${SCRIPT_HOME}/launcher"
./build.sh
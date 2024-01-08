#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

export COURSIER_PROGRESS=false

find . -type d -name "target" -exec rm -r {} \; || true

cd "${SCRIPT_HOME}/deps"
./build.sh

cd "${SCRIPT_HOME}/utils"
./build.sh

cd "${SCRIPT_HOME}/client"
./build.sh

cd "${SCRIPT_HOME}/snapi-frontend"
./build.sh

cd "${SCRIPT_HOME}/snapi-truffle"
./build.sh

cd "${SCRIPT_HOME}/snapi-client"
./build.sh

cd "${SCRIPT_HOME}/sql-client"
./build.sh

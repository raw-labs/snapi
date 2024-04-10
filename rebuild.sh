#!/bin/bash -ex
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

export COURSIER_PROGRESS=false

find . -type d -name "target" -exec rm -r {} \; || true

cd "${SCRIPT_HOME}/deps"
./build.sh

cd "${SCRIPT_HOME}/utils"
./build.sh

cd "${SCRIPT_HOME}/client"
./build.sh

cd "${SCRIPT_HOME}/parsers"
if [ "$1" == "--release" ]; then
    cd "${SCRIPT_HOME}/parsers"
    ./build.sh --release
else
    cd "${SCRIPT_HOME}/parsers"
    ./build.sh
fi

cd "${SCRIPT_HOME}/modules"
./build.sh

cd "${SCRIPT_HOME}/sql-client"
./build.sh

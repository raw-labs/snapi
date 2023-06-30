#!/bin/bash -eu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

export COURSIER_PROGRESS=false

cd "${SCRIPT_HOME}"
git fetch
sbt "show version" 2>&1 | grep -A 1 'info.*version' | tail -n 1 | awk '{print $2}'

#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

cd "${SCRIPT_HOME}"/snapi-client

rm -rfv test-results
mkdir -p test-results

sbt test

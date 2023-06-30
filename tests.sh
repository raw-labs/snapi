#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
cd "${SCRIPT_HOME}"

rm -rfv test-results
mkdir -p test-results

sbt "testOnly * -- -n raw.testing.tags.TruffleTests"

#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

# snapi-frontend

cd "${SCRIPT_HOME}"/snapi-frontend

rm -rfv test-results
mkdir -p test-results

sbt test

# snapi-client

cd "${SCRIPT_HOME}"/snapi-client

rm -rfv test-results
mkdir -p test-results

sbt test

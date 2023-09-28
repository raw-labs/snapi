#!/bin/bash -exu
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

cd "${SCRIPT_HOME}"/language

rm -rfv test-results
mkdir -p test-results

sbt test

cd "${SCRIPT_HOME}"/extensions

rm -rfv test-results
mkdir -p test-results

sbt test
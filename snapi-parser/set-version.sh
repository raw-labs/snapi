#!/bin/bash
[ "$CI" == "true" ] && { export MAVEN_CLI_OPTS="-B"; }
NEW_VERSION=$(git describe --tags | sed 's/^v//;s/-\([0-9]*\)-g/+\1-/')
mvn versions:set -DnewVersion=$NEW_VERSION

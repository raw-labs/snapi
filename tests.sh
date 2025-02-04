#!/bin/bash -ex
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21.0.2-graalce || true
sdk use java 21.0.2-graalce

# compiler-snapi-frontend
sbt compilerSnapiFrontend/test

# compiler-snapi
sbt compilerSnapi/test

# compiler-sql
sbt compilerSql/test

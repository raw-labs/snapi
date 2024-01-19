#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

set -e

yes | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce

utils_version=$(cat $SCRIPT_HOME/../utils/version)
snapi_frontend_version=$(cat $SCRIPT_HOME/../snapi-frontend/version)

echo $utils_version
echo $snapi_frontend_version

mvn versions:set -DnewVersion="1.0-SNAPSHOT" -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=raw-utils.version -DnewVersion=${utils_version} -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=raw-snapi-frontend.version -DnewVersion=${snapi_frontend_version} -B -DgenerateBackupPoms=false
mvn clean install

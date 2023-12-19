#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

set -e

yes | sdk install java 21.0.1-graalce || true
sdk use java 21-graalce

client_version=$(cat $SCRIPT_HOME/../client/version)
snapi_client_version=$(cat $SCRIPT_HOME/../snapi-client/version)
python_client_version=$(cat $SCRIPT_HOME/../python-client/version)

# we correlate raw cli version with snapi client version
rawcli_version=$snapi_client_version

echo $client_version
echo $snapi_client_version
echo $rawcli_version

mvn versions:set -DnewVersion=${rawcli_version} -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=raw-client.version -DnewVersion=${client_version} -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=raw-snapi-client.version -DnewVersion=${snapi_client_version} -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=raw-python-client.version -DnewVersion=${python_client_version} -B -DgenerateBackupPoms=false
mvn clean install
#!/bin/bash
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes | sdk install java 21-graalce || true
sdk use java 21-graalce

language_version=$(awk '{$1=$1};1' $SCRIPT_HOME/../language/version)

# we correlate raw cli version with launcher version
rawcli_version=$language_version
extensions_version=$(awk '{$1=$1};1' $SCRIPT_HOME/../extensions/version)

echo $language_version
echo $extensions_version

mvn versions:set -DnewVersion=${rawcli_version} -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=language.version -DnewVersion=${language_version} -B -DgenerateBackupPoms=false
mvn versions:set-property -Dproperty=extensions.version -DnewVersion=${extensions_version} -B -DgenerateBackupPoms=false
mvn clean install
#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; export MAVEN_CLI_OPTS="-B";}
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21-graalce || true
sdk use java 21-graalce

cd "$SCRIPT_HOME"

# when running on CI we don't care about this intermediate component versioning
if [ "$CI" == "true" ] && [ "$GITHUB_WORKFLOW" == "CI" ]; then
  mvn clean install
# when running on publish workflow it is important to version it properly
# even if we don't publish it to maven central, it is a dependency for snapi-frontend and sql-client
else
  VERSION=$(git describe --tags | sed 's/^v//;s/-\([0-9]*\)-g/+\1-/')
  mvn clean install -Drevision=$VERSION
fi

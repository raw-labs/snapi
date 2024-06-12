#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

# Check if GITHUB_TOKEN is set
if [ -z "${GITHUB_TOKEN}" ]; then
  echo -e "\033[33mWARNING: GITHUB_TOKEN is not set. If you don't have local custom deps this build will fail\033[0m"
  read -p "Do you want to continue? (y/n): " choice
  case "$choice" in
    y|Y ) echo "Continuing...";;
    n|N ) echo "Exiting..."; exit 1;;
    * ) echo "Invalid input. Exiting..."; exit 1;;
  esac
fi

find . -type d -name "target" -exec rm -r {} \; || true

export COURSIER_PROGRESS=false
[ "$CI" == "true" ] && { export HOME=/home/sbtuser; }
. ~/.sdkman/bin/sdkman-init.sh

yes n | sdk install java 21.0.1-graalce || true
sdk use java 21.0.1-graalce

cd "${SCRIPT_HOME}"
sbt clean compile

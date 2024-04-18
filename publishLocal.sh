#!/bin/bash -e
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

./rebuild.sh

sbt publishLocal

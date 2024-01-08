#!/bin/bash
DEPS_HOME="$(cd "$(dirname "$0")"; pwd)"

cd "$DEPS_HOME"/kiama
./build.sh

cd "$DEPS_HOME"/others
./build.sh
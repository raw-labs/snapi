#!/bin/bash
DEPS_HOME="$(cd "$(dirname "$0")"; pwd)"

cd "$DEPS_HOME"/kiama
./publish.sh

cd "$DEPS_HOME"/others
./publish.sh

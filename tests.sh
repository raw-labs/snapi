#!/bin/bash -ex
SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"

# snapi-frontend

cd "${SCRIPT_HOME}"/snapi-frontend

./test.sh

# snapi-client

cd "${SCRIPT_HOME}"/snapi-client

./test.sh

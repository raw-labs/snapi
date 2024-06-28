#!/bin/bash -eux
usage() {
  echo "Usage: $0 <sbt_command>"
  echo "Allowed commands are: ${ALLOWED_COMMANDS[*]}"
  exit 1
}
ALLOWED_COMMANDS=("headerCheckAll" "scalafmtCheckAll" "javafmtCheckAll" "doc")
if [ "$#" -ne 1 ]; then
  usage
fi
if [[ ! " ${ALLOWED_COMMANDS[@]} " =~ " ${1} " ]]; then
  echo "Error: Invalid command '${1}'"
  usage
fi

SCRIPT_HOME="$(cd "$(dirname "$0")" && pwd)"
SNAPI_HOME="$(cd "${SCRIPT_HOME}/.." && pwd)"
cd "${SNAPI_HOME}"
IMAGE_NAME="$(basename "${SNAPI_HOME}")-env"

docker build \
  -f ci/Dockerfile . \
  --tag "${IMAGE_NAME}:latest"

docker run \
  -e "GITHUB_TOKEN=${GITHUB_TOKEN}" \
  "${IMAGE_NAME}:latest" \
  "sbt ${1}"

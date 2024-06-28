#!/bin/bash -exu
usage() {
  echo "Usage: $0 <snapi_component>"
  echo "Allowed components are: ${ALLOWED_COMPONENTS[*]}"
  exit 1
}

to_kebab_case() {
  echo "$1" | sed -E 's/([a-z])([A-Z])/\1-\2/g' | tr '[:upper:]' '[:lower:]'
}

ALLOWED_COMPONENTS=("snapiFrontend" "snapiClient" "sqlClient" "pythonClient")
FOLDER_COMPONENT=$(to_kebab_case $1)
if [ "$#" -ne 1 ]; then
  usage
fi
if [[ ! " ${ALLOWED_COMPONENTS[@]} " =~ " ${1} " ]]; then
  echo "Error: Invalid command '${1}'"
  usage
fi

SCRIPT_HOME="$(cd "$(dirname "$0")"; pwd)"
SNAPI_HOME="$(cd "${SCRIPT_HOME}/.."; pwd)"
cd "${SNAPI_HOME}"
IMAGE_NAME="$(basename "${SNAPI_HOME}")-env"
export REPORTS_DIR=$(mktemp -d)
export LOGS_DIR=$(mktemp -d)
chmod 777 ${REPORTS_DIR}
chmod 777 ${LOGS_DIR}

: "${CI:=false}"
if [ "$CI" == "true" ]
then
  echo "REPORTS_DIR=${REPORTS_DIR}" >> $GITHUB_ENV
  echo "LOGS_DIR=${LOGS_DIR}" >> $GITHUB_ENV
fi

docker build \
  -f ci/Dockerfile . \
  --tag ${IMAGE_NAME}:latest

docker run \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /usr/bin/docker:/usr/bin/docker \
  -v "${REPORTS_DIR}:/root/workdir/${FOLDER_COMPONENT}/target/test-reports" \
  -v "${LOGS_DIR}:/root/workdir/target/test-results" \
  -v "$(pwd)/.env:/root/workdir/.env:ro" \
  -e "GITHUB_TOKEN=${GITHUB_TOKEN}" \
  ${IMAGE_NAME}:latest \
  "sbt clean ${1}/test"

DOCKER_RUN_RESULT=$?
if [ $DOCKER_RUN_RESULT -eq 0 ]; then
    echo -e "\e[32mTests reports are available in ${REPORTS_DIR}\e[0m"
    echo -e "\e[32mTests logs are available in ${LOGS_DIR}\e[0m"
else
    echo -e "\e[31mTests failed. Check the reports in ${REPORTS_DIR}\e[0m"
    echo -e "\e[31mTests logs are available in ${LOGS_DIR}\e[0m"
fi

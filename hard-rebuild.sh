#!/bin/bash

set -e

cd utils
rm -rf target/
sbt clean publishLocal

cd ../client
rm -rf target/
sbt clean publishLocal

cd ../sql-client
rm -rf target/
sbt clean publishLocal

cd ../snapi-frontend
rm -rf target/
sbt clean publishLocal

cd ../snapi-truffle
rm -rf target/
sbt clean publishLocal

cd ../snapi-client
rm -rf target/
sbt clean publishLocal

cd ../python-client
rm -rf target/
sbt clean publishLocal

cd ../launcher
rm -rf target/
./build.sh

#!/bin/bash
. ~/.sdkman/bin/sdkman-init.sh
sdk use java 17.0.5-amzn
sbt "project" publishLocal

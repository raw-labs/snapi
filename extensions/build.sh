#!/bin/bash
. ~/.sdkman/bin/sdkman-init.sh
sdk install java 21-graalce
sdk use java 21-graalce
sbt publishLocal

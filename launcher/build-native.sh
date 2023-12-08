#!/bin/bash
CLASSPATH=`cat target/dependencies.txt`
echo "${CLASSPATH}:$1" > foo
${JAVA_HOME}/bin/native-image \
  -cp ${CLASSPATH}:$1 \
  -p ${CLASSPATH}:$1 \
  -H:+UnlockExperimentalVMOptions \
  -H:+ReportExceptionStackTraces \
  -m raw.cli/raw.cli.RawCli \
  --initialize-at-build-time=net.snowflake.client.jdbc.internal.google.api.gax.nativeimage \
  -o ./snapi-native

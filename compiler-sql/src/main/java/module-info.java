/*
 * Copyright 2023 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

import com.rawlabs.compiler.api.CompilerServiceBuilder;

module raw.sql.client {
  requires scala.library;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires raw.utils.core;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires raw.compiler.protocol;
  requires raw.client;
  requires raw.sql.parser;
  requires java.sql;
  requires com.zaxxer.hikari;

  provides CompilerServiceBuilder with
      raw.client.sql.SqlCompilerServiceBuilder;
}

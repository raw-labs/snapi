/*
 * Copyright 2024 RAW Labs S.A.
 *
 *   Use of this software is governed by the Business Source License
 *   included in the file licenses/BSL.txt.
 *
 *   As of the Change Date specified in that file, in accordance with
 *   the Business Source License, use of this software will be governed
 *   by the Apache License, Version 2.0, included in the file
 *   licenses/APL.txt.
 */

module raw.client.jinja.sql {
  requires scala.library;
  requires raw.client;
  requires raw.utils;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires org.graalvm.python.embedding;

  provides raw.client.api.CompilerServiceBuilder with
      raw.client.jinja.sql.JinjaSqlCompilerServiceBuilder;
}

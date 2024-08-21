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

module raw.snapi.client {
  requires scala.library;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires raw.utils;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.core;
  requires raw.sources;
  requires raw.protocol;
  requires raw.client;
  requires raw.snapi.frontend;

  uses raw.client.api.CompilerServiceBuilder;

  provides raw.client.api.CompilerServiceBuilder with
      raw.client.rql2.truffle.Rql2TruffleCompilerServiceBuilder;
}

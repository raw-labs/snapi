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

module raw.client {
  requires scala.library;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.scala;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires raw.utils.core;
  requires raw.protocol;

  exports raw.client.api;
  exports raw.client.writers;
  exports raw.client.utils;

  opens raw.client.api to
      com.fasterxml.jackson.databind;

  uses raw.client.api.CompilerServiceBuilder;
}

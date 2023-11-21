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

module raw.utils {
  requires scala.library;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.scala;
  requires org.apache.commons.io;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;

  exports raw.utils;

  opens raw.utils to
      com.fasterxml.jackson.databind;
}

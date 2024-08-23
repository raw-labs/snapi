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

import com.rawlabs.snapi.truffle.Rql2LanguageProvider;

module raw.snapi.truffle {
  // Direct dependencies
  requires java.base;
  requires java.logging;
  requires jdk.unsupported;
  requires org.graalvm.truffle;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires java.xml;
  requires java.sql;
  requires scala.library;
  requires com.ctc.wstx;
  requires raw.utils.core;
  requires raw.protocol.raw;
  requires raw.protocol.compiler;
  requires raw.compiler;
  requires raw.utils.sources;
  requires raw.snapi.frontend;

  // Indirect dependencies
  requires kiama;
  requires com.fasterxml.jackson.scala;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires java.net.http;
  requires com.ibm.icu;
  requires spring.core;
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires org.slf4j;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;

  provides com.oracle.truffle.api.provider.TruffleLanguageProvider with Rql2LanguageProvider;
}

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

module raw.snapi.frontend {
  requires scala.library;
  requires org.slf4j;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.csv;
  requires com.fasterxml.jackson.scala;
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires java.xml;
  requires java.sql;
  requires com.ctc.wstx;
  requires kiama;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires java.net.http;
  requires com.ibm.icu;
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;
  requires typesafe.config;
  requires typesafe.scalalogging;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;
  requires org.graalvm.polyglot;
  requires raw.utils.core;
  requires raw.compiler.protocol;
  requires raw.client;
  requires raw.utils.sources;
  requires raw.snapi.parser;

  exports com.rawlabs.compiler.snapi.base;
  exports com.rawlabs.compiler.snapi.base.errors;
  exports com.rawlabs.compiler.snapi.base.source;
  exports com.rawlabs.compiler.snapi.common;
  exports com.rawlabs.compiler.snapi.common.source;
  exports com.rawlabs.compiler.snapi.rql2;
  exports com.rawlabs.compiler.snapi.rql2.api;
  exports com.rawlabs.compiler.snapi.rql2.builtin;
  exports com.rawlabs.compiler.snapi.rql2.errors;
  exports com.rawlabs.compiler.snapi.rql2.lsp;
  exports com.rawlabs.compiler.snapi.rql2.source;
  exports com.rawlabs.compiler.snapi.rql2.antlr4;
  exports com.rawlabs.compiler.snapi.utils;
  exports com.rawlabs.compiler.snapi.inferrer.api;
  exports com.rawlabs.compiler.snapi.inferrer.local;

  opens com.rawlabs.compiler.snapi.inferrer.api to
      com.fasterxml.jackson.databind;
  opens com.rawlabs.compiler.snapi.rql2.api to
      com.fasterxml.jackson.databind;
}

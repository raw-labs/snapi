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
  requires com.fasterxml.jackson.datatype.jsr310;
  requires com.fasterxml.jackson.datatype.jdk8;
  requires org.graalvm.polyglot;
  requires raw.utils;
  requires raw.client;
  requires raw.sources;
  requires raw.snapi.parser;

  exports raw.compiler.base;
  exports raw.compiler.base.errors;
  exports raw.compiler.base.source;
  exports raw.compiler.common;
  exports raw.compiler.common.source;
  exports raw.compiler.rql2;
  exports raw.compiler.rql2.api;
  exports raw.compiler.rql2.builtin;
  exports raw.compiler.rql2.errors;
  exports raw.compiler.rql2.lsp;
  exports raw.compiler.rql2.source;
  exports raw.compiler.rql2.antlr4;
  exports raw.compiler.utils;
  exports raw.inferrer.api;
  exports raw.inferrer.local;

  opens raw.inferrer.api to
      com.fasterxml.jackson.databind;
  opens raw.compiler.rql2.api to
      com.esotericsoftware.kryo;
}

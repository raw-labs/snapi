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
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires java.sql;
  requires java.net.http;
  requires com.ibm.icu;
  requires kiama;
  //requires dropbox.core.sdk;
  requires spring.core;
  requires com.esotericsoftware.kryo;
  requires com.esotericsoftware.minlog;
  requires com.esotericsoftware.reflectasm;

  requires raw.utils;
  requires raw.client;

  exports raw.auth.api;

  exports raw.compiler;
  exports raw.compiler.base;
  exports raw.compiler.base.source;
  exports raw.compiler.base.errors;
  exports raw.compiler.common;
  exports raw.compiler.common.source;
  exports raw.compiler.rql2;
  exports raw.compiler.rql2.api;
  exports raw.compiler.rql2.builtin;
  exports raw.compiler.rql2.errors;
  exports raw.compiler.rql2.lsp;
  exports raw.compiler.rql2.source;
  exports raw.compiler.utils;

  exports raw.creds.api;
  exports raw.creds.local;

  provides raw.creds.api.CredentialsServiceBuilder with
      raw.creds.local.LocalCredentialsServiceBuilder;

  exports raw.inferrer.api;
  exports raw.inferrer.local;

  provides raw.inferrer.api.InferrerServiceBuilder with
          raw.inferrer.local.LocalInferrerServiceBuilder;

  exports raw.runtime;

  exports raw.sources.api;
  exports raw.sources.bytestream.api;
  exports raw.sources.bytestream.http;
  exports raw.sources.filesystem.api;
  exports raw.sources.jdbc.api;

}

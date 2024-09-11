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

module raw.utils.sources {
  requires scala.library;
  requires org.slf4j;
  requires java.xml;
  requires java.sql;
  requires org.apache.commons.io;
  requires org.apache.commons.lang3;
  requires org.apache.commons.text;
  requires org.apache.httpcomponents.core5.httpcore5;
  requires org.apache.httpcomponents.client5.httpclient5;
  requires java.net.http;
  requires spring.core;
  requires typesafe.config;
  requires scala.logging;
  requires ch.qos.logback.classic;
  requires com.google.common;
  requires jul.to.slf4j;
  requires software.amazon.awssdk.annotations;
  requires software.amazon.awssdk.http.apache;
  requires software.amazon.awssdk.arns;
  requires software.amazon.awssdk.auth;
  requires software.amazon.awssdk.awscore;
  requires software.amazon.awssdk.protocols.query;
  requires software.amazon.awssdk.protocols.xml;
  requires software.amazon.awssdk.crtcore;
  requires software.amazon.awssdk.endpoints;
  requires software.amazon.awssdk.http;
  requires software.amazon.awssdk.protocols.jsoncore;
  requires software.amazon.awssdk.metrics;
  requires software.amazon.awssdk.http.nio.netty;
  requires software.amazon.awssdk.profiles;
  requires software.amazon.awssdk.protocols.core;
  requires software.amazon.awssdk.regions;
  requires software.amazon.awssdk.services.s3;
  requires software.amazon.awssdk.core;
  requires software.amazon.awssdk.thirdparty.jackson.core;
  requires software.amazon.awssdk.utils;
  requires org.postgresql.jdbc;
  requires com.microsoft.sqlserver.jdbc;
  requires mysql.connector.j;
  requires ojdbc10;
  requires snowflake.jdbc;
  requires dropbox.core.sdk;
  requires raw.utils.core;

  exports com.rawlabs.utils.sources.api;
  exports com.rawlabs.utils.sources.bytestream.api;
  exports com.rawlabs.utils.sources.bytestream.github;
  exports com.rawlabs.utils.sources.bytestream.http;
  exports com.rawlabs.utils.sources.bytestream.inmemory;
  exports com.rawlabs.utils.sources.filesystem.api;
  exports com.rawlabs.utils.sources.filesystem.dropbox;
  exports com.rawlabs.utils.sources.filesystem.local;
  exports com.rawlabs.utils.sources.filesystem.mock;
  exports com.rawlabs.utils.sources.filesystem.s3;
  exports com.rawlabs.utils.sources.jdbc.api;
  exports com.rawlabs.utils.sources.jdbc.mysql;
  exports com.rawlabs.utils.sources.jdbc.pgsql;
  exports com.rawlabs.utils.sources.jdbc.snowflake;
  exports com.rawlabs.utils.sources.jdbc.sqlite;
  exports com.rawlabs.utils.sources.jdbc.sqlserver;
  exports com.rawlabs.utils.sources.jdbc.oracle;
  exports com.rawlabs.utils.sources.jdbc.teradata;
}

/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package raw.sources.jdbc.pgsql

import raw.sources.jdbc.api.{JdbcSchemaConfig, JdbcServerConfig, JdbcTableConfig}

case class PostgresqlServerConfig(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String
) extends JdbcServerConfig

case class PostgresqlSchemaConfig(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schema: String
) extends JdbcSchemaConfig

case class PostgresqlTableConfig(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schema: String,
    tableName: String
) extends JdbcTableConfig

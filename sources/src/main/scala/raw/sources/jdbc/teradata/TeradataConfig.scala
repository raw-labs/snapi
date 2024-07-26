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

package raw.sources.jdbc.teradata

import raw.sources.jdbc.api.{JdbcSchemaConfig, JdbcServerConfig, JdbcTableConfig}

case class TeradataServerConfig(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    parameters: Map[String, String]
) extends JdbcServerConfig

case class TeradataSchemaConfig(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schema: String,
    parameters: Map[String, String]
) extends JdbcSchemaConfig

case class TeradataTableConfig(
    host: String,
    port: Int,
    dbName: String,
    username: String,
    password: String,
    schema: String,
    tableName: String,
    parameters: Map[String, String]
) extends JdbcTableConfig

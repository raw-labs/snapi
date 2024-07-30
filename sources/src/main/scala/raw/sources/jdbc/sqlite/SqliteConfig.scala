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

package raw.sources.jdbc.sqlite

import raw.sources.jdbc.api.{JdbcSchemaConfig, JdbcServerConfig, JdbcTableConfig}

case class SqliteServerConfig(path: String, dbName: String) extends JdbcServerConfig

case class SqliteSchemaConfig(path: String, dbName: String) extends JdbcSchemaConfig

case class SqliteTableConfig(path: String, dbName: String, table: String) extends JdbcTableConfig
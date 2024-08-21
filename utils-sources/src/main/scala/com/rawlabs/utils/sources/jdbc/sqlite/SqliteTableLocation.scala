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

package com.rawlabs.utils.sources.jdbc.sqlite

import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings

class SqliteTableLocation(
    cli: SqliteClient,
    val table: String
) extends JdbcTableLocation(cli, None, table) {

  val path: String = cli.path

  def this(path: String, table: String)(implicit settings: RawSettings) = {
    this(new SqliteClient(path), table)
  }

}

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

import java.io.Closeable
import com.rawlabs.utils.sources.jdbc.api._
import com.rawlabs.utils.core.RawSettings

class SqliteServerLocation(val path: String)(
    implicit settings: RawSettings
) extends JdbcServerLocation(new SqliteClient(path)) {

  override def listSchemas(): Iterator[JdbcSchemaLocation] with Closeable = {
    throw new JdbcLocationException("no schemas in sqlite")
  }

}

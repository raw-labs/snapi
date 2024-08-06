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

package raw.client.sql

import raw.client.api.{ProgramEnvironment, RawValue}
import raw.utils.RawUid

final case class SqlProgramEnvironment(
    uid: RawUid,
    maybeArguments: Option[Array[(String, RawValue)]],
    scopes: Set[String],
    jdbcUrl: String,
    options: Map[String, String],
    maybeTraceId: Option[String]
) extends ProgramEnvironment

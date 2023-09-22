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

package raw.compiler

import raw.runtime.{ParamValue, ProgramEnvironment}

final case class ProgramDefinition(
    code: String,
    decl: Option[String],
    parameters: Option[Array[(String, ParamValue)]],
    environment: ProgramEnvironment
)

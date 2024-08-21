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

package com.rawlabs.compiler.api

final case class ProgramDescription(
    decls: Map[String, List[DeclDescription]],
    maybeRunnable: Option[DeclDescription],
    comment: Option[String]
)

final case class DeclDescription(
    params: Option[Vector[ParamDescription]],
    outType: Option[RawType],
    comment: Option[String]
)

final case class ParamDescription(
    idn: String,
    tipe: Option[RawType],
    defaultValue: Option[RawValue],
    comment: Option[String],
    required: Boolean
)

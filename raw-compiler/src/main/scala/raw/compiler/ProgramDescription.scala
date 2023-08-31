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

import com.fasterxml.jackson.annotation.JsonProperty

final case class ProgramDescription(
    @JsonProperty("declarations") decls: Map[String, List[DeclDescription]],
    @JsonProperty("type") maybeType: Option[String],
    comment: Option[String]
)

final case class DeclDescription(
    @JsonProperty("parameters") params: Option[Vector[ParamDescription]],
    @JsonProperty("outputType") outType: String,
    comment: Option[String]
)

final case class ParamDescription(
    @JsonProperty("identifier") idn: String,
    @JsonProperty("type") tipe: String,
    required: Boolean
)

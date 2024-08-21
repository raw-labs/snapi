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

package com.rawlabs.compiler.snapi.base

import com.rawlabs.compiler.snapi.base.source.Type

final case class TreeDescription(
    expDecls: Map[String, List[TreeDeclDescription]],
    maybeType: Option[Type],
    comment: Option[String]
)

final case class TreeDeclDescription(
    params: Option[Vector[TreeParamDescription]],
    outType: Type,
    comment: Option[String]
)

final case class TreeParamDescription(idn: String, tipe: Type, required: Boolean)

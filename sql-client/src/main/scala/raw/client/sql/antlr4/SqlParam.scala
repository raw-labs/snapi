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

package raw.client.sql.antlr4

/**
 * Represents a parameter in a SQL program
 * @param name the name of the parameter
 * @param description the description of the parameter
 * @param tipe the type of the parameter
 * @param default the default value of the parameter
 * @param nodes tree nodes where the parameter is defined, used for deduplication
 * @param occurrences tree nodes where the parameter occurs
 */
case class SqlParam(
    name: String,
    description: Option[String],
    tipe: Option[String],
    default: Option[String],
    nodes: Vector[SqlBaseNode],
    occurrences: Vector[SqlParamUseNode]
)

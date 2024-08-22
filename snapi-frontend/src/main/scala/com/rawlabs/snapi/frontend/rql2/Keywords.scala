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

package com.rawlabs.snapi.frontend.rql2

import com.rawlabs.snapi.frontend.base

trait Keywords extends base.Keywords {

  private val reservedIdns: Seq[String] = Seq(
    "let", // Let declaration.
    "in", // Let declaration.
    "rec", // Let recursive declaration.
    "type", // Type declaration.
    "if", // If-Then-Else control flow.
    "then", // If-Then-Else control flow.
    "else", // If-Then-Else control flow.
    "false", // Boolean constants.
    "true", // Boolean constants.
    "not", // Boolean operator.
    "package" // Package definition
  )

  private val reservedTypes: Seq[String] = Seq(
    "error",
    "any",
    "nothing",
    "bool",
    "string",
    "byte",
    "short",
    "int",
    "long",
    "float",
    "double",
    "decimal",
    "date",
    "time",
    "timestamp",
    "interval",
    "binary",
    "void",
    "record",
    "list",
    "generator",
    "option",
    "location",
    "collection",
    "library",
    "package",
    "try",
    "common"
  )

  override def isReserved(idn: String): Boolean = reservedIdns.contains(idn)

  override def isReservedType(idn: String): Boolean = reservedTypes.contains(idn)

}

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

package antlr4_parser;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface SnapiKeywords {

  Set<String> reservedIdns =
      Stream.of(
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
          .collect(Collectors.toCollection(HashSet::new));

  Set<String> reservedTypes =
      Stream.of(
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
              "common")
          .collect(Collectors.toCollection(HashSet::new));

  default boolean isReserved(String idn) {
    return reservedIdns.contains(idn);
  }

  default boolean isReservedType(String idn) {
    return reservedTypes.contains(idn);
  }
}

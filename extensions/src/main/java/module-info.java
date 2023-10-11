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

module raw.language.extensions {
  requires scala.library;
  requires org.slf4j;
  requires org.graalvm.polyglot;
  requires org.graalvm.truffle;
  requires raw.language;
  requires kiama;

  provides raw.compiler.api.CompilerServiceBuilder with
      raw.compiler.rql2.truffle.Rql2TruffleCompilerServiceBuilder;
  provides raw.compiler.common.CommonCompilerBuilder with
      raw.compiler.rql2.truffle.Rql2TruffleCompilerBuilder;
}

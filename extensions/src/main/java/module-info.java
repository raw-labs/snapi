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

  provides raw.compiler.rql2.api.EntryExtension with
      raw.compiler.snapi.truffle.builtin.binary.TruffleFromStringBinaryEntry,
      raw.compiler.snapi.truffle.builtin.binary.TruffleBinaryBase64Entry,
      raw.compiler.snapi.truffle.builtin.binary.TruffleBinaryReadEntry;
}

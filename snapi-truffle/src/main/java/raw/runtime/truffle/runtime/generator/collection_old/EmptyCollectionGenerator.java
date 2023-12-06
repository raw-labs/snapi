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

package raw.runtime.truffle.runtime.generator.collection_old;

import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;

@ExportLibrary(GeneratorLibrary.class)
public class EmptyCollectionGenerator {

  @ExportMessage
  boolean isGenerator() {
    return true;
  }

  @ExportMessage
  void init() {}

  @ExportMessage
  void close() {}

  @ExportMessage
  Object next() {
    throw new BreakException();
  }

  @ExportMessage
  boolean hasNext() {
    return false;
  }
}

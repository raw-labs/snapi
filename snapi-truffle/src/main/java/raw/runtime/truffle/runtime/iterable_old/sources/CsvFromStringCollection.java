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

package raw.runtime.truffle.runtime.iterable_old.sources;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.ast.io.csv.reader.parser.RawTruffleCsvParserSettings;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection_old.CollectionAbstractGenerator;
import raw.runtime.truffle.runtime.generator.collection_old.compute_next.sources.CsvReadFromStringComputeNext;
import raw.runtime.truffle.runtime.iterable_old.IterableLibrary;

@ExportLibrary(IterableLibrary.class)
@ExportLibrary(InteropLibrary.class)
public class CsvFromStringCollection implements TruffleObject {

  private final String str;
  private final RootCallTarget rowParserCallTarget;

  private final RawTruffleCsvParserSettings settings;

  public CsvFromStringCollection(
      String str, RootCallTarget rowParserCallTarget, RawTruffleCsvParserSettings settings) {
    this.str = str;
    this.rowParserCallTarget = rowParserCallTarget;
    this.settings = settings;
  }

  @ExportMessage
  boolean isIterable() {
    return true;
  }

  @ExportMessage
  Object getGenerator() {
    return new CollectionAbstractGenerator(
        new CsvReadFromStringComputeNext(str, rowParserCallTarget, settings));
  }

  // InteropLibrary: Iterable

  @ExportMessage
  boolean hasIterator() {
    return true;
  }

  private final GeneratorLibrary generatorLibrary =
      GeneratorLibrary.getFactory().createDispatched(1);

  @ExportMessage
  Object getIterator(@CachedLibrary("this") IterableLibrary iterables) {
    Object generator = iterables.getGenerator(this);
    generatorLibrary.init(generator);
    return generator;
  }
}

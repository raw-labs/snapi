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

package raw.runtime.truffle.runtime.generator.collection.compute_next.operations;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.interop.ArityException;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.interop.UnsupportedTypeException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.io.*;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodes;
import raw.runtime.truffle.ast.tryable_nullable.TryableNullableNodesFactory;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.generator.collection.compute_next.ComputeNextLibrary;
import raw.runtime.truffle.runtime.iterable.IterableLibrary;
import raw.runtime.truffle.runtime.kryo.KryoReader;
import raw.runtime.truffle.runtime.kryo.KryoReaderLibrary;
import raw.runtime.truffle.runtime.kryo.KryoWriter;
import raw.runtime.truffle.runtime.kryo.KryoWriterLibrary;
import raw.runtime.truffle.utils.IOUtils;
import raw.sources.api.SourceContext;

@ExportLibrary(ComputeNextLibrary.class)
public class JoinComputeNext {

  protected final Object leftIterable;
  protected final Object rightIterable;
  private Object leftGen = null;
  private final Object remap;
  private final Object predicate;

  private Object leftRow = null;
  private Object rightRow = null;
  Input kryoRight = null;
  private final int kryoOutputBufferSize;
  private final KryoWriter writer;
  private final KryoReader reader;
  private final Rql2TypeWithProperties rightRowType; // grouped key and value types.
  private final KryoWriterLibrary writers = KryoWriterLibrary.getUncached();
  private final KryoReaderLibrary readers = KryoReaderLibrary.getUncached();
  private int spilledRight = 0;
  private int readRight = 0;
  private final File diskRight;
  private final Boolean reshapeBeforePredicate;

  private final InteropLibrary interop = InteropLibrary.getFactory().getUncached();

  TryableNullableNodes.HandleOptionTryablePredicateNode handleOptionTriablePredicateNode =
      TryableNullableNodesFactory.HandleOptionTryablePredicateNodeGen.getUncached();

  @CompilerDirectives.TruffleBoundary // Needed because of SourceContext
  public JoinComputeNext(
      Object leftIterable,
      Object rightIterable,
      Object remap,
      Object predicate,
      Boolean reshapeBeforePredicate,
      Rql2TypeWithProperties rightRowType,
      SourceContext context,
      RawLanguage language) {
    this.leftIterable = leftIterable;
    this.rightIterable = rightIterable;
    this.remap = remap;
    this.predicate = predicate;
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.writer = new KryoWriter();
    this.reader = new KryoReader(language);
    this.rightRowType = rightRowType;
    this.reshapeBeforePredicate = reshapeBeforePredicate;
    this.diskRight = IOUtils.getScratchFile("cartesian.", ".kryo", context).toFile();
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  void init(
      @CachedLibrary(limit = "5") IterableLibrary iterables,
      @Cached.Shared("gen") @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    // initialize left
    leftGen = iterables.getGenerator(leftIterable);
    generators.init(leftGen);

    // save right to disk
    Output buffer;
    try {
      buffer = new Output(new FileOutputStream(diskRight), kryoOutputBufferSize);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
    Object rightGen = iterables.getGenerator(rightIterable);
    try {
      generators.init(rightGen);
      while (generators.hasNext(rightGen)) {
        Object row = generators.next(rightGen);
        writers.write(writer, buffer, rightRowType, row);
        spilledRight++;
      }
    } finally {
      generators.close(rightGen);
      buffer.close();
    }
  }

  @ExportMessage
  void close(@Cached.Shared("gen") @CachedLibrary(limit = "5") GeneratorLibrary generators) {
    generators.close(leftGen);
    if (kryoRight != null) kryoRight.close();
  }

  @ExportMessage
  public boolean isComputeNext() {
    return true;
  }

  @ExportMessage
  @CompilerDirectives.TruffleBoundary
  Object computeNext(@CachedLibrary(limit = "3") GeneratorLibrary generators) {
    Object row = null;

    while (row == null) {
      if (leftRow == null || rightRow == null) {
        if (leftRow == null) {
          if (generators.hasNext(leftGen)) {
            leftRow = generators.next(leftGen);
          } else {
            // end of left, nothing else to read
            throw new BreakException();
          }
        }
        if (kryoRight == null) {
          kryoRight = createInput(diskRight);
          readRight = 0;
        }
        if (rightRow == null) {
          if (readRight < spilledRight) {
            rightRow = readers.read(reader, kryoRight, rightRowType);
            row = check(leftRow, rightRow);
            readRight++;
            rightRow = null;
          } else {
            // end of right, reset currentLeft to make sure we try another round
            leftRow = null;
            kryoRight.close();
            rightRow = kryoRight = null;
          }
        }
      }
    }
    return row;
  }

  @CompilerDirectives.TruffleBoundary
  private Input createInput(File file) {
    try {
      return new Input(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }

  private Object check(Object leftRow, Object rightRow) {
    try {
      Boolean pass;
      Object row = null;
      if (reshapeBeforePredicate) {
        row = interop.execute(remap, leftRow, rightRow);
        pass = handleOptionTriablePredicateNode.execute(interop.execute(predicate, row), false);
        if (!pass) row = null;
      } else {
        pass =
            handleOptionTriablePredicateNode.execute(
                interop.execute(predicate, leftRow, rightRow), false);
        if (pass) row = interop.execute(remap, leftRow, rightRow);
      }
      return row;
    } catch (UnsupportedMessageException | UnsupportedTypeException | ArityException e) {
      throw new RawTruffleRuntimeException("failed to execute function");
    }
  }
}

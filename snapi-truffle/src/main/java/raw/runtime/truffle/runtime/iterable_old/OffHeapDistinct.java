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

package raw.runtime.truffle.runtime.iterable_old;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeInput;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.interop.InteropLibrary;
import com.oracle.truffle.api.interop.StopIterationException;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.interop.UnsupportedMessageException;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.io.*;
import java.util.*;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.kryo.KryoReader;
import raw.runtime.truffle.runtime.kryo.KryoReaderLibrary;
import raw.runtime.truffle.runtime.kryo.KryoWriter;
import raw.runtime.truffle.runtime.kryo.KryoWriterLibrary;
import raw.runtime.truffle.runtime.list.StringList;
import raw.runtime.truffle.utils.IOUtils;
import raw.runtime.truffle.utils.KryoFootPrint;
import raw.sources.api.SourceContext;

/** A map that spills to disk when it reaches a certain size. */
public class OffHeapDistinct {
  private final TreeSet<Object>
      index; // in-memory map that's used as long as the data fits in memory.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long blockSize; // maximum size of a spilled file.
  private int
      binarySize; // estimated size of currently memory held objects (when reaching blockSize,
  // spill
  // to disk).

  private final Comparator<Object> keyCompare; // grouping key compare function.

  private final KryoWriter writer; // Kryo writer used to write the spilled data.
  private final KryoWriterLibrary writers = KryoWriterLibrary.getUncached();
  private final KryoReader reader; // Kryo reader used to read the spilled data.
  private final KryoReaderLibrary readers = KryoReaderLibrary.getUncached();

  private final Rql2TypeWithProperties itemType; // grouped key and value types.
  private final int itemSize; // grouped key and value kryo binary size
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.

  private final SourceContext context;

  @CompilerDirectives.TruffleBoundary // Needed because of SourceContext
  public OffHeapDistinct(
      Comparator<Object> itemCmp,
      Rql2TypeWithProperties vType,
      RawLanguage rl,
      SourceContext context) {
    this.keyCompare = itemCmp;
    this.reader = new KryoReader(rl);
    this.index = new TreeSet<>(itemCmp);
    this.itemType = vType;
    this.itemSize = KryoFootPrint.of(vType);
    this.binarySize = 0;
    this.blockSize = context.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    this.writer = new KryoWriter();
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.kryoInputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    this.context = context;
  }

  public void put(Object item) {
    // append the value to the list of values for the key.
    boolean added = index.add(item);
    if (added) {
      binarySize += itemSize;
      if (binarySize >= blockSize) {
        flush();
      }
    }
  }

  // flushes the current in-memory map to disk.
  private void flush() {
    Output kryoOutput = new UnsafeOutput(nextFile(), kryoOutputBufferSize);
    for (Object item : index) {
      writers.write(writer, kryoOutput, itemType, item);
    }
    kryoOutput.close();
    // reset the memory map and footprint
    index.clear();
    binarySize = 0;
  }

  // creates a new file in the scratch directory.
  private FileOutputStream nextFile() throws RawTruffleRuntimeException {
    File file;
    file = IOUtils.getScratchFile("distinct.", ".kryo", context).toFile();
    spilledBuffers.add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }

  // method that returns an iterator to the GROUP BY result.
  // If nothing was spilled to disk, we return a simple iterator over the in-memory map.
  // Otherwise, we return an iterator that reads and merges the spilled data from disk.
  public Object generator() {
    if (spilledBuffers.isEmpty()) {
      return new DistinctMemoryGenerator();
    } else {
      // flush the in-memory map to disk before creating the iterator.
      flush();
      return new DistinctSpilledFilesGenerator();
    }
  }

  // A simple in-memory generator over the map content. Internally iterates through the
  // key set and returns a pair of key and list of values.
  @ExportLibrary(GeneratorLibrary.class)
  @ExportLibrary(InteropLibrary.class)
  class DistinctMemoryGenerator implements TruffleObject {

    private final Iterator<Object> items = index.iterator();

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
      return items.next();
    }

    @ExportMessage
    boolean hasNext() {
      return items.hasNext();
    }

    // InteropLibrary: Iterator

    @ExportMessage
    final boolean isIterator() {
      return true;
    }

    @ExportMessage
    final boolean hasIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
        throws UnsupportedMessageException {
      return generatorLibrary.hasNext(this);
    }

    @ExportMessage
    final Object getIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
        throws UnsupportedMessageException, StopIterationException {
      return generatorLibrary.next(this);
    }

    @ExportMessage
    final boolean hasMembers() {
      return true;
    }

    @ExportMessage
    final Object getMembers(boolean includeInternal) {
      return new StringList(new String[] {"close"});
    }

    @ExportMessage
    final boolean isMemberInvocable(String member) {
      return Objects.equals(member, "close");
    }

    @ExportMessage
    final Object invokeMember(
        String member, Object[] args, @CachedLibrary("this") GeneratorLibrary generatorLibrary) {
      assert (Objects.equals(member, "close"));
      generatorLibrary.close(this);
      return 0;
    }
  }

  // An iterator that reads the spilled data from disk and merges it with the in-memory map.
  @ExportLibrary(GeneratorLibrary.class)
  @ExportLibrary(InteropLibrary.class)
  class DistinctSpilledFilesGenerator implements TruffleObject {

    private ArrayList<Input> kryoBuffers; // list of Kryo buffers that contain the spilled data.
    private ArrayList<Object>
        headKeys; // list of keys that are currently at the head of each buffer.

    @ExportMessage
    boolean isGenerator() {
      return true;
    }

    @ExportMessage
    void init() {
      int nSpilledFiles = spilledBuffers.size();
      kryoBuffers = new ArrayList<>(nSpilledFiles);
      headKeys = new ArrayList<>(nSpilledFiles);
      spilledBuffers.forEach(
          file -> {
            try {
              kryoBuffers.add(new UnsafeInput(new FileInputStream(file), kryoInputBufferSize));
              headKeys.add(null);
            } catch (FileNotFoundException e) {
              throw new RawTruffleRuntimeException(e.getMessage());
            }
          });
    }

    @ExportMessage
    void close() {
      kryoBuffers.forEach(Input::close);
    }

    @ExportMessage
    public boolean hasNext() {
      return nextKey() != null;
    }

    // Computes the next key to return. This is the smallest key among the keys
    // that are currently at the head of each buffer.
    // If all buffers are empty, returns null.
    private Object nextKey() {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < kryoBuffers.size(); idx++) {
        Object bufferKey = headKeys.get(idx);
        if (bufferKey == null) {
          // The buffer next key hasn't been read yet.
          Input buffer = kryoBuffers.get(idx);
          try {
            bufferKey = readers.read(reader, buffer, itemType);
          } catch (KryoException e) {
            // we reached the end of that buffer
            // remove both the buffer and its key from the lists
            final Input removed = kryoBuffers.remove(idx);
            removed.close();
            headKeys.remove(idx);
            idx--;
            continue;
          }
          headKeys.set(idx, bufferKey);
        }
        if (key == null || keyCompare.compare(bufferKey, key) < 0) {
          key = bufferKey;
        }
      }
      return key;
    }

    @ExportMessage
    public Object next() {
      Object key = nextKey();
      // Walk through the buffers to consume the ones that expose the same smallest key.
      for (int idx = 0; idx < kryoBuffers.size(); idx++) {
        Object bufferKey = headKeys.get(idx);
        if (keyCompare.compare(key, bufferKey) == 0) {
          // reset the key since we read its data
          headKeys.set(idx, null);
        }
      }
      return key;
    }

    // InteropLibrary: Iterator

    @ExportMessage
    final boolean isIterator() {
      return true;
    }

    @ExportMessage
    final boolean hasIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
        throws UnsupportedMessageException {
      return generatorLibrary.hasNext(this);
    }

    @ExportMessage
    final Object getIteratorNextElement(@CachedLibrary("this") GeneratorLibrary generatorLibrary)
        throws UnsupportedMessageException, StopIterationException {
      return generatorLibrary.next(this);
    }

    @ExportMessage
    final boolean hasMembers() {
      return true;
    }

    @ExportMessage
    final Object getMembers(boolean includeInternal) {
      return new StringList(new String[] {"close"});
    }

    @ExportMessage
    final boolean isMemberInvocable(String member) {
      return Objects.equals(member, "close");
    }

    @ExportMessage
    final Object invokeMember(
        String member, Object[] args, @CachedLibrary("this") GeneratorLibrary generatorLibrary) {
      assert (Objects.equals(member, "close"));
      generatorLibrary.close(this);
      return 0;
    }
  }
}

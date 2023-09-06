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

package raw.runtime.truffle.runtime.iterable;

import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.unsafe.UnsafeInput;
import com.esotericsoftware.kryo.unsafe.UnsafeOutput;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.io.*;
import java.util.*;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.kryo.*;
import raw.runtime.truffle.utils.IOUtils;
import raw.runtime.truffle.utils.KryoFootPrint;

/** A map that spills to disk when it reaches a certain size. */
class OffHeapGroupByKey {
  private final TreeMap<Object, ArrayList<Object>>
      memMap; // in-memory map that's used as long as the data fits in memory.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long maxSize; // maximum size of a spilled file.
  private int
      size; // estimated size of currently memory held objects (when reaching blockSize, spill
  // to
  // disk).

  private final Comparator<Object> keyCompare; // grouping key comparison function.

  private final KryoWriter writer; // Kryo writer used to write the spilled data.
  private final KryoWriterLibrary writers = KryoWriterLibrary.getUncached();
  private final KryoReader reader; // Kryo reader used to read the spilled data.
  private final KryoReaderLibrary readers = KryoReaderLibrary.getUncached();
  private final RuntimeContext context;
  private final Rql2TypeWithProperties keyType, rowType; // grouping key and row types.
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.
  private final int keySize, rowSize; // grouping key and row kryo estimated binary size

  private final GroupByRecordShaper reshape;

  public OffHeapGroupByKey(
      Comparator<Object> keyCompare,
      Rql2TypeWithProperties kType,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      RuntimeContext context,
      GroupByRecordShaper reshape) {
    this.keyCompare = keyCompare;
    this.reader = new KryoReader(language);
    this.memMap = new TreeMap<>(keyCompare);
    this.keyType = kType;
    this.rowType = rowType;
    this.rowSize = KryoFootPrint.of(rowType);
    this.keySize = KryoFootPrint.of(kType);
    this.size = 0;
    this.maxSize = context.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    this.writer = new KryoWriter();
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.kryoInputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    this.context = context;
    this.reshape = reshape;
  }

  public void put(Object key, Object value) {
    // append the value to the list of values for the key.
    ArrayList<Object> list = memMap.get(key);
    if (list == null) {
      list = new ArrayList<>();
      memMap.put(key, list);
      // add the size of the key to the memory footprint. (Row size added below in main path.)
      size += keySize;
    }
    list.add(value);
    // add the size of the row to the memory footprint.
    size += rowSize;
    if (size >= maxSize) {
      flush();
    }
  }

  // flushes the current in-memory map to disk.
  private void flush() {
    Output kryoOutput = new UnsafeOutput(newDiskBuffer(), kryoOutputBufferSize);
    for (Object key : memMap.keySet()) {
      ArrayList<Object> values = memMap.get(key);
      // write key, then n, then values.
      writers.write(writer, kryoOutput, keyType, key);
      kryoOutput.writeInt(values.size());
      for (Object value : values) {
        writers.write(writer, kryoOutput, rowType, value);
      }
    }
    kryoOutput.close();
    // reset both the memory map and memory footprint.
    memMap.clear();
    size = 0;
  }

  // creates a new file in the scratch directory.
  private FileOutputStream newDiskBuffer() throws RawTruffleRuntimeException {
    File file;
    file = IOUtils.getScratchFile("groupby.", ".kryo", this.context).toFile();
    spilledBuffers.add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }

  // return a generator to the GROUP BY result. There are two implementations:
  // * if data were small and nothing was spilled to disk, return a simple in-memory one over the
  // in-memory map.
  // * otherwise, return a SpilledFilesGenerator that reads and merges the spilled data from disk.
  public Object generator() {
    if (spilledBuffers.isEmpty()) {
      return new GroupByMemoryGenerator();
    } else {
      // flush the in-memory map to disk before creating the iterator.
      flush();
      return new GroupBySpilledFilesGenerator();
    }
  }

  // A simple in-memory generator over the map content. Internally iterates through the
  // key set and returns a pair of key and list of values.
  @ExportLibrary(GeneratorLibrary.class)
  class GroupByMemoryGenerator {

    private final Iterator<Object> keys = memMap.keySet().iterator();

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
      Object key = keys.next();
      ArrayList<Object> values = memMap.get(key);
      return reshape.makeRow(key, values.toArray());
    }

    @ExportMessage
    boolean hasNext() {
      return keys.hasNext();
    }
  }

  // A generator that reads the spilled data from disk and merges it with the in-memory map.
  @ExportLibrary(GeneratorLibrary.class)
  class GroupBySpilledFilesGenerator {

    // A helper class to manage the multiple kryo buffers.
    private class InputBuffer {
      private final Input input;
      private Object key;
      public int itemsLeft;

      public InputBuffer(Input input) {
        this.input = input;
        this.key = null;
      }

      public void close() {
        input.close();
      }

      public Object headKey() {
        // read the next key (if it is null, otherwise keep the current one).
        if (key == null) {
          key = readers.read(reader, input, keyType);
          itemsLeft = input.readInt();
        }
        return key;
      }

      // extract a row from the current buffer. When reaching the end of the buffer, the key
      // is set
      // to null
      // so that it's reset in the next call to headKey().
      public Object readRow() {
        itemsLeft--;
        if (itemsLeft == 0) {
          key = null;
        }
        return readers.read(reader, input, rowType);
      }
    }

    private ArrayList<InputBuffer>
        inputBuffers; // list of Kryo buffers that contain the spilled data.

    @ExportMessage
    boolean isGenerator() {
      return true;
    }

    @ExportMessage
    void close() {
      inputBuffers.forEach(InputBuffer::close);
    }

    @ExportMessage
    void init() {
      // turn the list of spilled files into a list of Kryo buffers. Buffers will be read and
      // dropped from
      // the list as they are exhausted.
      int nSpilledFiles = spilledBuffers.size();
      inputBuffers = new ArrayList<>(nSpilledFiles);
      spilledBuffers.forEach(
          file -> {
            try {
              Input kryoBuffer = new UnsafeInput(new FileInputStream(file), kryoInputBufferSize);
              InputBuffer buffer = new InputBuffer(kryoBuffer);
              inputBuffers.add(buffer);
            } catch (FileNotFoundException e) {
              throw new RawTruffleRuntimeException(e.getMessage());
            }
          });
    }

    @ExportMessage
    public boolean hasNext() {
      return nextKey() != null;
    }

    // Computes the next key to return. This is the smallest key among the buffers headkeys.
    // If all buffers are empty, returns null.
    private Object nextKey() {
      Object key = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < inputBuffers.size(); idx++) {
        InputBuffer inputBuffer = inputBuffers.get(idx);
        try {
          Object bufferKey = inputBuffer.headKey();
          if (key == null || keyCompare.compare(bufferKey, key) < 0) {
            key = bufferKey;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          final InputBuffer removed = inputBuffers.remove(idx);
          removed.close();
          idx--;
        }
      }
      return key;
    }

    @ExportMessage
    public Object next() {
      Object key = nextKey();
      // First walk through the buffers to find the ones that expose the same smallest key.
      // Take note of the number of items stored in each in order to allocate the right amount
      // of
      // memory.
      int numberOfRows = 0;
      for (InputBuffer inputBuffer : inputBuffers) {
        Object bufferKey = inputBuffer.headKey();
        if (keyCompare.compare(key, bufferKey) == 0) {
          numberOfRows += inputBuffer.itemsLeft;
        }
      }
      // Allocate the exact amount of memory needed to store the values.
      Object[] values = new Object[numberOfRows];

      // Walk through the buffers that had the matching key and read values into the single
      // array.
      int n = 0;
      for (InputBuffer inputBuffer : inputBuffers) {
        if (keyCompare.compare(key, inputBuffer.headKey()) == 0) {
          int inThatBuffer = inputBuffer.itemsLeft;
          for (int i = 0; i < inThatBuffer; i++) {
            values[n++] = inputBuffer.readRow();
          }
        }
      }
      return reshape.makeRow(key, values);
    }
  }
}

// A helper class to reshape the GROUP BY result into the right format (when group by is used with
// collections/lists, the result is a pair of key and _collection/list_ of values).
abstract class GroupByRecordShaper {

  protected final RawLanguage language;

  public GroupByRecordShaper(RawLanguage language) {
    this.language = language;
  }

  // returns the key/group record, with the group being of the correct type (collection/list).
  public abstract Object makeRow(Object key, Object[] values);
}

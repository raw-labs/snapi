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
import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import java.io.*;
import java.util.*;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.runtime.truffle.runtime.generator.GeneratorLibrary;
import raw.runtime.truffle.runtime.kryo.KryoReader;
import raw.runtime.truffle.runtime.kryo.KryoReaderLibrary;
import raw.runtime.truffle.runtime.kryo.KryoWriter;
import raw.runtime.truffle.runtime.kryo.KryoWriterLibrary;
import raw.runtime.truffle.utils.IOUtils;
import raw.runtime.truffle.utils.KryoFootPrint;

/**
 * An ordered map with multiple keys, that spills to disk when it reaches a certain size. Similar to
 * OffHeapGroupByKey, but supports multiple keys. Also the iterator it provides isn't returning the
 * <key/value> pairs like OffHeapGroupByKey. It's an iterator over the values (to give the effect of
 * the OrderBy).
 */
public class OffHeapGroupByKeys {
  private final TreeMap<Object[], ArrayList<Object>>
      memMap; // in-memory map from arrays of keys to array of rows.
  private final ArrayList<File> spilledBuffers =
      new ArrayList<>(); // list of files that contain the spilled data.
  private final long maxSize; // maximum size of a spilled file.
  private int
      size; // estimated size of currently memory held objects (when reaching blockSize, spill
  // to
  // disk).

  private final Comparator<Object[]> keyCompare; // grouping keys compare function.

  private final KryoWriter writer; // Kryo writer used to write the spilled data.
  private final KryoWriterLibrary writers = KryoWriterLibrary.getUncached();
  private final KryoReader reader; // Kryo reader used to read the spilled data.
  private final KryoReaderLibrary readers = KryoReaderLibrary.getUncached();

  private final Rql2TypeWithProperties[] keyTypes; // grouped key and value types.
  private final Rql2TypeWithProperties rowType; // grouped key and value types.
  private final int keysSize, rowSize; // grouping keys and row kryo binary size
  private final int kryoOutputBufferSize,
      kryoInputBufferSize; // size of the kryo buffers used to write and read the data.

  private final RuntimeContext context;

  // estimate the size of the keys in memory.
  private static int keysFootPrint(Rql2TypeWithProperties[] keyType) {
    int size = 0;
    for (Rql2TypeWithProperties t : keyType) {
      size += KryoFootPrint.of(t);
    }
    return size;
  }

  public OffHeapGroupByKeys(
      Comparator<Object[]> keyCompare,
      Rql2TypeWithProperties[] kTypes,
      Rql2TypeWithProperties rowType,
      RawLanguage language,
      RuntimeContext context) {
    this.keyCompare = keyCompare;
    this.memMap = new TreeMap<>(keyCompare);
    this.keyTypes = kTypes;
    this.rowType = rowType;
    this.rowSize = KryoFootPrint.of(rowType);
    this.keysSize = keysFootPrint(kTypes);
    this.size = 0;
    this.maxSize = context.settings().getMemorySize("raw.runtime.external.disk-block-max-size");
    this.writer = new KryoWriter();
    this.reader = new KryoReader(language);
    this.kryoOutputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.output-buffer-size");
    this.kryoInputBufferSize =
        (int) context.settings().getMemorySize("raw.runtime.kryo.input-buffer-size");
    this.context = context;
  }

  public void put(Object[] keys, Object value) {
    // append the value to the list of values for the key.
    ArrayList<Object> values = memMap.get(keys);
    if (values == null) {
      values = new ArrayList<>();
      memMap.put(keys, values);
      // add the size of the key to the memory footprint.
      size += keysSize;
    }
    values.add(value);
    size += rowSize;
    if (size >= maxSize) {
      flush();
    }
  }

  // flushes the current in-memory map to disk.
  private void flush() {
    Output kryoOutput = new UnsafeOutput(nextFile(), kryoOutputBufferSize);
    for (Object[] keys : memMap.keySet()) {
      // write keys, then n, then values.
      for (int i = 0; i < keys.length; i++) {
        writers.write(writer, kryoOutput, keyTypes[i], keys[i]);
      }
      ArrayList<Object> values = memMap.get(keys);
      kryoOutput.writeInt(values.size());
      for (Object value : values) {
        writers.write(writer, kryoOutput, rowType, value);
      }
    }
    kryoOutput.close();
    // reset the memory map and footprint
    memMap.clear();
    size = 0;
  }

  // creates a new file in the scratch directory.
  private FileOutputStream nextFile() throws RawTruffleRuntimeException {
    File file;
    file = IOUtils.getScratchFile("orderby.", ".kryo", this.context).toFile();
    spilledBuffers.add(file);
    try {
      return new FileOutputStream(file);
    } catch (FileNotFoundException e) {
      throw new RawTruffleRuntimeException(e.getMessage());
    }
  }

  public Object generator() {
    if (spilledBuffers.isEmpty()) {
      return new OrderByMemoryGenerator();
    } else {
      // flush the in-memory map to disk before creating the iterator.
      flush();
      return new OrderBySpilledFilesGenerator();
    }
  }

  // A simple in-memory generator over the map. Internally iterates through the
  // key set and returns items from the values list, one by one.
  @ExportLibrary(GeneratorLibrary.class)
  class OrderByMemoryGenerator {
    private final Iterator<Object[]> keysIterator; // the iterator from the in-memory map keys.
    private Iterator<Object> values; // the current iterator over the grouped rows

    public OrderByMemoryGenerator() {
      this.keysIterator = memMap.keySet().iterator();
      this.values = null;
    }

    @ExportMessage
    boolean isGenerator() {
      return true;
    }

    @ExportMessage
    void init() {}

    @ExportMessage
    void close() {}

    @ExportMessage
    public boolean hasNext() {
      return keysIterator.hasNext() || values != null;
    }

    @ExportMessage
    @CompilerDirectives.TruffleBoundary
    public Object next() {
      Object n;
      if (values == null) {
        // no iterator over the values, create one from the next key.
        Object[] keys = keysIterator.next();
        values = Arrays.stream(memMap.get(keys).toArray()).iterator();
      }
      n = values.next();
      if (!values.hasNext()) {
        // reset values to make sure we create a new iterator on the next call to next().
        values = null;
      }
      return n;
    }
  }

  // An iterator that reads the spilled data from disk and merges it with the in-memory map.
  @ExportLibrary(GeneratorLibrary.class)
  class OrderBySpilledFilesGenerator {

    private class InputBuffer {
      private final Input input;
      private Object[] keys;
      public int itemsLeft;

      public InputBuffer(Input input) {
        this.input = input;
        this.keys = null;
      }

      public void close() {
        input.close();
      }

      public Object[] headKeys() {
        if (keys == null) {
          keys = readKeys(input);
          itemsLeft = input.readInt();
        }
        return keys;
      }

      public Object readValue() {
        itemsLeft--;
        if (itemsLeft == 0) {
          keys = null;
        }
        return readers.read(reader, input, rowType);
      }
    }

    private ArrayList<InputBuffer>
        inputBuffers; // list of Kryo buffers that contain the spilled data.
    private InputBuffer currentKryoBuffer; // the current buffer being read.

    @ExportMessage
    void init() {
      int nSpilledFiles = spilledBuffers.size();
      inputBuffers = new ArrayList<>(nSpilledFiles);
      spilledBuffers.forEach(
          file -> {
            try {
              inputBuffers.add(
                  new InputBuffer(new UnsafeInput(new FileInputStream(file), kryoInputBufferSize)));
            } catch (FileNotFoundException e) {
              throw new RawTruffleRuntimeException(e.getMessage());
            }
          });
      currentKryoBuffer = null;
    }

    @ExportMessage
    void close() {
      inputBuffers.forEach(InputBuffer::close);
    }

    @ExportMessage
    public boolean hasNext() {
      return nextKeys() != null;
    }

    private Object[] readKeys(Input buffer) {
      Object[] keys = new Object[keyTypes.length];
      for (int i = 0; i < keyTypes.length; i++) {
        keys[i] = readers.read(reader, buffer, keyTypes[i]);
      }
      return keys;
    }

    // Computes the next key to return. This is the "smallest key" (according to the compare
    // function) among the keys
    // that are currently at the head of each buffer.
    // If all buffers are empty, returns null.
    private Object[] nextKeys() {
      Object[] smallestKeys = null;
      // read missing keys and compute the smallest
      for (int idx = 0; idx < inputBuffers.size(); idx++) {
        InputBuffer inputBuffer = inputBuffers.get(idx);
        try {
          Object[] bufferKeys = inputBuffer.headKeys();
          if (smallestKeys == null || keyCompare.compare(bufferKeys, smallestKeys) < 0) {
            smallestKeys = bufferKeys;
          }
        } catch (KryoException e) {
          // we reached the end of that buffer
          InputBuffer removed = inputBuffers.remove(idx);
          removed.close();
          idx--;
        }
      }
      return smallestKeys;
    }

    @ExportMessage
    public Object next() {
      if (currentKryoBuffer == null) {
        // we need to read the next keys and prepare the new buffer to read from.
        Object[] keys = nextKeys();
        // First walk through the buffers to find the ones that expose the same smallest
        // key.
        // Take note of the number of items stored in each in order to allocate the right
        // amount of
        // memory.
        for (InputBuffer inputBuffer : inputBuffers) {
          Object[] bufferKeys = inputBuffer.keys;
          if (keyCompare.compare(keys, bufferKeys) == 0) {
            currentKryoBuffer = inputBuffer;
            break;
          }
        }
      }
      Object row = currentKryoBuffer.readValue();
      if (currentKryoBuffer.itemsLeft == 0) {
        currentKryoBuffer = null;
      }
      return row;
    }
  }
}

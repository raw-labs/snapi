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

package raw.runtime.truffle.runtime.generator.collection.abstract_generator.compute_next.sources;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import java.io.BufferedReader;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.ReadLinesRawTruffleException;
import raw.runtime.truffle.utils.TruffleCharInputStream;

public class ReadLinesComputeNext {
  private final TruffleCharInputStream stream;

  private BufferedReader reader;

  public ReadLinesComputeNext(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  @TruffleBoundary
  public void init() {
    this.reader = new BufferedReader(stream.getReader());
  }

  @TruffleBoundary
  public void close() {
    IOUtils.closeQuietly(reader);
  }

  public Object next() {
    String line = readLine();
    if (line != null) {
      return line;
    } else {
      this.close();
      throw new BreakException();
    }
  }

  @TruffleBoundary
  private String readLine() {
    try {
      return this.reader.readLine();
    } catch (IOException e) {
      throw new ReadLinesRawTruffleException(e.getMessage(), stream, e, null);
    }
  }
}

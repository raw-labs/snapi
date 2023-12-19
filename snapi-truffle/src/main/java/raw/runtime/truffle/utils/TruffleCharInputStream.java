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

package raw.runtime.truffle.utils;

import com.oracle.truffle.api.CompilerDirectives;
import java.io.Reader;

public class TruffleCharInputStream extends RawTruffleCharStream {
  TruffleInputStream stream;

  String encoding;

  public TruffleCharInputStream(TruffleInputStream stream, String encoding) {
    this.encoding = encoding;
    this.stream = stream;
  }

  @CompilerDirectives.TruffleBoundary
  public Reader getReader() {
    return stream.getReader(encoding);
  }

  @CompilerDirectives.TruffleBoundary
  public String positionDescription() {
    return "url: " + stream.getUrl();
  }
}

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

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.rawlabs.utils.core.RawSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;

public class IOUtils {

  @TruffleBoundary
  public static Path getScratchPath(RawSettings rawSettings) {
    Path p = Paths.get(rawSettings.getString("raw.runtime.scratch-path", true));
    if (!Files.exists(p)) {
      try {
        Files.createDirectories(p);
      } catch (IOException ex) {
        throw new RawTruffleRuntimeException("failed to create scratch file", ex);
      }
    }
    return p;
  }

  @TruffleBoundary
  public static Path getScratchFile(String prefix, String suffix, RawSettings rawSettings) {
    try {
      return Files.createTempFile(getScratchPath(rawSettings), prefix, suffix);
    } catch (IOException ex) {
      throw new RawTruffleRuntimeException("failed to create scratch file", ex);
    }
  }
}

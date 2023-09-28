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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.runtime.exceptions.RawTruffleRuntimeException;
import raw.sources.api.SourceContext;

public class IOUtils {

  @CompilerDirectives.TruffleBoundary
  public static Path getScratchPath(SourceContext context) {
    Path p = Paths.get(context.settings().getString("raw.runtime.scratch-path", true));
    if (!Files.exists(p)) {
      try {
        Files.createDirectories(p);
      } catch (IOException ex) {
        throw new RawTruffleRuntimeException("failed to create scratch file");
      }
    }
    return p;
  }

  @CompilerDirectives.TruffleBoundary
  public static Path getScratchFile(String prefix, String suffix, SourceContext context) {
    try {
      return Files.createTempFile(getScratchPath(context), prefix, suffix);
    } catch (IOException ex) {
      throw new RawTruffleRuntimeException("failed to create scratch file");
    }
  }
}

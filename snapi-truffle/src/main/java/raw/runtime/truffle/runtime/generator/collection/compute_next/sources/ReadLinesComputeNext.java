package raw.runtime.truffle.runtime.generator.collection.compute_next.sources;

import com.oracle.truffle.api.CompilerDirectives;
import org.apache.commons.io.IOUtils;
import raw.runtime.truffle.runtime.exceptions.BreakException;
import raw.runtime.truffle.runtime.exceptions.ReadLinesRawTruffleException;
import raw.runtime.truffle.utils.TruffleCharInputStream;

import java.io.BufferedReader;
import java.io.IOException;

public class ReadLinesComputeNext {
  private final TruffleCharInputStream stream;

  private BufferedReader reader;

  public ReadLinesComputeNext(TruffleCharInputStream stream) {
    this.stream = stream;
  }

  public void init() {
    this.reader = new BufferedReader(stream.getReader());
  }

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

  @CompilerDirectives.TruffleBoundary
  private String readLine() {
    try {
      return this.reader.readLine();
    } catch (IOException e) {
      throw new ReadLinesRawTruffleException(e.getMessage(), stream);
    }
  }
}

package raw.compiler.snapi.truffle.compiler;

import com.oracle.truffle.api.nodes.RootNode;
import raw.compiler.base.ProgramContext;
import raw.compiler.common.source.SourceProgram;
import raw.compiler.rql2.Tree;
import raw.runtime.truffle.RawLanguage;

public class TruffleEmitter {
  public TruffleEntrypoint doEmit(
      String signature,
      SourceProgram program,
      RawLanguage language,
      ProgramContext programContext) {

    Tree tree = new Tree(program, true, (raw.compiler.rql2.ProgramContext) programContext);
    return null;
  }
}

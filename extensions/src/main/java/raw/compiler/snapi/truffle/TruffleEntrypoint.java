package raw.compiler.snapi.truffle;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.nodes.RootNode;
import org.graalvm.polyglot.Context;
import raw.runtime.Entrypoint;

public class TruffleEntrypoint implements Entrypoint {

  Context context;

  RootNode rootNode;

  FrameDescriptor frameDescriptor;

  public TruffleEntrypoint(Context context, RootNode rootNode, FrameDescriptor frameDescriptor) {
    this.context = context;
    this.rootNode = rootNode;
    this.frameDescriptor = frameDescriptor;
  }

  @Override
  public RootNode target() {
    return rootNode;
  }
}

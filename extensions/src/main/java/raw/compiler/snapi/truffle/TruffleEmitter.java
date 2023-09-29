package raw.compiler.snapi.truffle;

import com.oracle.truffle.api.frame.FrameDescriptor;
import raw.compiler.common.source.Exp;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;

public interface TruffleEmitter {
  void addScope();

  FrameDescriptor dropScope();

  ExpressionNode recurseExp(Exp in);

  ExpressionNode recurseLambda(ExpressionNode exp);

  RawLanguage getLanguage();
}

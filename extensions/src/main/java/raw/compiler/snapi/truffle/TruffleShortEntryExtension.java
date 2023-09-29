package raw.compiler.snapi.truffle;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.api.Rql2Arg;
import raw.compiler.rql2.truffle.TruffleArg;
import raw.compiler.rql2.truffle.TruffleEmitter;
import raw.compiler.rql2.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.List;

public interface TruffleShortEntryExtension extends TruffleEntryExtension {

  ExpressionNode toTruffle(ExpressionNode[] args);


  @Override
  default ExpressionNode toTruffle(Type t, Seq<Rql2Arg> args, TruffleEmitter emitter) {
    return TruffleEntryExtension.super.toTruffle(t, args, emitter);
  }
}

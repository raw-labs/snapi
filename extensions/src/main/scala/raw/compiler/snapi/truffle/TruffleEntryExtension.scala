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

///*
// * Copyright 2023 RAW Labs S.A.
// *
// * Use of this software is governed by the Business Source License
// * included in the file licenses/BSL.txt.
// *
// * As of the Change Date specified in that file, in accordance with
// * the Business Source License, use of this software will be governed
// * by the Apache License, Version 2.0, included in the file
// * licenses/APL.txt.
// */
//
//package raw.compiler.snapi.truffle
//
//import java.util
//import raw.compiler.base.source.Type
//import raw.compiler.rql2.api.Rql2Arg
//import raw.runtime.truffle.ExpressionNode
//import raw.runtime.truffle.RawLanguage
//
//import scala.collection.JavaConverters
//
//trait TruffleEntryExtension { // TO DO: remove if not needed
//
//  //  ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage);
//  def toTruffle(`type`: Type, args: util.List[TruffleArg], rawLanguage: RawLanguage): ExpressionNode =
//    throw new UnsupportedOperationException("Not implemented")
//  def toTruffle(`type`: Type, args: util.List[Rql2Arg], emitter: TruffleEmitter): ExpressionNode = {
//    val args2: util.List[TruffleArg] = {
//      val items = args.stream.toArray.toList
//      JavaConverters.seqAsJavaList(items.map {
//        case a: Rql2Arg => new TruffleArg(
//            emitter.recurseExp(a.e),
//            a.t,
//            if (a.idn.isDefined) a.idn.get
//            else null
//          )
//      })
//    }
//    toTruffle(
//      `type`,
//      args2,
//      emitter.getLanguage
//    )
//  }
//}

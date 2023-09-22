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

package raw.compiler.rql2.truffle

import raw.compiler.base.source.Type
import raw.compiler.rql2.{Rql2Arg, ShortEntryExtension}
import raw.runtime.truffle.ExpressionNode

import scala.collection.mutable

trait TruffleShortEntryExtension extends TruffleEntryExtension { this: ShortEntryExtension =>

  def toTruffle(args: Seq[ExpressionNode]): ExpressionNode

  final override def toTruffle(
      t: Type,
      args: Seq[Rql2Arg],
      emitter: TruffleEmitter
  ): ExpressionNode = {
    // Arguments have to be passed in the correct order.
    // Mandatory arguments are passed as they are.
    val orderedArgs = mutable.ArrayBuffer[ExpressionNode]()
    args.collect { case Rql2Arg(e, _, None) => orderedArgs.append(emitter.recurseExp(e)) }
    // Optional arguments are passed in the order of definition in optionalParamsMap.
    optionalParamsMap.keys.foreach { name =>
      val e = args
        .collectFirst {
          case Rql2Arg(e, _, Some(idn)) if idn == name => emitter.recurseExp(e)
        }
        .getOrElse(emitter.recurseExp(optionalParamsMap(name)._2))
      orderedArgs.append(e)
    }
    toTruffle(orderedArgs)
  }

}

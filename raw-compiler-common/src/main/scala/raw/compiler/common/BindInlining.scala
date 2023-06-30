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

package raw.compiler.common

import raw.compiler.common.source._

trait BindInlining extends FreeVars {

  protected def inline(v: IdnDef, e1: Exp, e2: Exp): Exp = inl(Bind(v, e1), e2).getOrElse(e2)

  protected def inl(b: Bind, e: Exp): Option[Exp] = e match {
    case commonExp: CommonExp => inlCommonExp(b, commonExp)
    case _ => throw new AssertionError(s"inl unhandled: $e")
  }

  // TODO: Build upon this one as object extractor though
  //  protected def inl(bind: Bind, es: Exp*): Option[Seq[Exp]] = {
  //    val oes = es.map(x => inl(bind, x))
  //    if (oes.exists(_.isDefined)) Some(oes.zip(es).map{case (mv, v) => mv.getOrElse(v)}) else None
  //  }

  // TODO: for consistency rename args below to b: Bind

  protected def inl(bind: Bind, e1: Exp, e2: Exp): Option[(Exp, Exp)] = {
    val (mv1, mv2) = (inl(bind, e1), inl(bind, e2))
    if (mv1.isEmpty && mv2.isEmpty) None
    else Some((mv1.getOrElse(e1), mv2.getOrElse(e2)))
  }

  protected def inl(bind: Bind, e1: Exp, e2: Exp, e3: Exp): Option[(Exp, Exp, Exp)] = {
    val (mv1, mv2, mv3) = (inl(bind, e1), inl(bind, e2), inl(bind, e3))
    if (mv1.isEmpty && mv2.isEmpty && mv3.isEmpty) None
    else Some((mv1.getOrElse(e1), mv2.getOrElse(e2), mv3.getOrElse(e3)))
  }

  protected def inl(bind: Bind, e1: Exp, e2: Exp, e3: Exp, e4: Exp): Option[(Exp, Exp, Exp, Exp)] = {
    val (mv1, mv2, mv3, mv4) = (inl(bind, e1), inl(bind, e2), inl(bind, e3), inl(bind, e4))
    if (mv1.isEmpty && mv2.isEmpty && mv3.isEmpty && mv4.isEmpty) None
    else Some((mv1.getOrElse(e1), mv2.getOrElse(e2), mv3.getOrElse(e3), mv4.getOrElse(e4)))
  }

  protected def inl(bind: Bind, e1: Exp, e2: Exp, e3: Exp, e4: Exp, e5: Exp): Option[(Exp, Exp, Exp, Exp, Exp)] = {
    val (mv1, mv2, mv3, mv4, mv5) = (inl(bind, e1), inl(bind, e2), inl(bind, e3), inl(bind, e4), inl(bind, e5))
    if (mv1.isEmpty && mv2.isEmpty && mv3.isEmpty && mv4.isEmpty && mv5.isEmpty) None
    else Some((mv1.getOrElse(e1), mv2.getOrElse(e2), mv3.getOrElse(e3), mv4.getOrElse(e4), mv5.getOrElse(e5)))
  }

  protected def inl(
      bind: Bind,
      e1: Exp,
      e2: Exp,
      e3: Exp,
      e4: Exp,
      e5: Exp,
      e6: Exp
  ): Option[(Exp, Exp, Exp, Exp, Exp, Exp)] = {
    val (mv1, mv2, mv3, mv4, mv5, mv6) =
      (inl(bind, e1), inl(bind, e2), inl(bind, e3), inl(bind, e4), inl(bind, e5), inl(bind, e6))
    if (mv1.isEmpty && mv2.isEmpty && mv3.isEmpty && mv4.isEmpty && mv5.isEmpty && mv6.isEmpty) None
    else Some(
      (mv1.getOrElse(e1), mv2.getOrElse(e2), mv3.getOrElse(e3), mv4.getOrElse(e4), mv5.getOrElse(e5), mv6.getOrElse(e6))
    )
  }

  protected def inl(bind: Bind, es: Vector[Exp]): Option[Vector[Exp]] = {
    val oes = es.map(x => inl(bind, x))
    val nes = if (oes.exists(_.isDefined)) Some(oes.zip(es).map { case (mv, v) => mv.getOrElse(v) }) else None
    if (nes.isEmpty) None
    else Some(nes.getOrElse(es))
  }

  protected def inl(bind: Bind, e1: Exp, es2: Vector[Exp]): Option[(Exp, Vector[Exp])] = {
    val mv1 = inl(bind, e1)
    val oes2 = es2.map(x => inl(bind, x))
    val mv2 = if (oes2.exists(_.isDefined)) Some(oes2.zip(es2).map { case (mv, v) => mv.getOrElse(v) }) else None
    if (mv1.isEmpty && mv2.isEmpty) None
    else Some((mv1.getOrElse(e1), mv2.getOrElse(es2)))
  }

  protected def inl(
      bind: Bind,
      e1: Exp,
      es2: Vector[Exp],
      es3: Vector[Exp]
  ): Option[(Exp, Vector[Exp], Vector[Exp])] = {
    val mv1 = inl(bind, e1)
    val oes2 = es2.map(x => inl(bind, x))
    val oes3 = es3.map(x => inl(bind, x))
    val mv2 = if (oes2.exists(_.isDefined)) Some(oes2.zip(es2).map { case (mv, v) => mv.getOrElse(v) }) else None
    val mv3 = if (oes3.exists(_.isDefined)) Some(oes3.zip(es3).map { case (mv, v) => mv.getOrElse(v) }) else None
    if (mv1.isEmpty && mv2.isEmpty && mv3.isEmpty) None
    else Some((mv1.getOrElse(e1), mv2.getOrElse(es2), mv2.getOrElse(es3)))
  }

  private def inlCommonExp(b: Bind, n: CommonExp): Option[Exp] = n match {
    case IdnExp(IdnUse(idn)) => if (idn == b.idn.idn) Some(b.e) else None
    case _: Eval => None
    case _: ErrorExp => None
  }

}

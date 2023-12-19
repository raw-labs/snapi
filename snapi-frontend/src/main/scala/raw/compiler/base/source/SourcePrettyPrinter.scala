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

package raw.compiler.base.source

import com.typesafe.scalalogging.StrictLogging
import raw.compiler.base.{Keywords, PrettyPrinter, SyntaxAnalyzer}
import raw.utils._

import scala.language.implicitConversions

trait SourcePrettyPrinter extends PrettyPrinter with Keywords with StrictLogging {

  implicit protected def baseNodeToDoc(n: BaseNode): Doc = toDoc(n)

  protected def sepArgs(sep: Doc, elems: Doc*): Doc = parens(group(nest(lsep(elems.to, sep))))

  def args(n: Doc*): Doc = sepArgs(comma, n: _*)

  def method(name: Doc, n: Doc*): Doc = {
    if (n.nonEmpty) group(name <> args(n: _*))
    else name
  }

  protected def dquoted(s: String): Doc = dquotes(RawUtils.descape(s))

  def ident(idn: String): String = {
    if (SyntaxAnalyzer.identRegex.unapplySeq(idn).isDefined && !isReserved(idn)) {
      // If simple word (no spaces, no escaping) and not reserved, print word as-is.
      idn
    } else {
      s"`$idn`"
    }
  }

  override def toDoc(n: BaseNode): Doc = n match {
    case _: AnythingType => "anything"
    case _: NotValueType =>
      // TODO (msb): This message could be improved but kept for backward compatibility.
      "non-executable query"
    case _ => super.toDoc(n)
  }

}

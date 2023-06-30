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

package raw.compiler.base.errors

import raw.compiler.base
import raw.compiler.base.source.{BaseNode, NotValueType}
import raw.compiler.{ErrorMessage, ErrorRange}

trait ErrorsPrettyPrinter extends base.source.SourcePrettyPrinter {

  override def toDoc(n: BaseNode): Doc = n match {
    case UnexpectedValue(_, expected, actual) => "expected" <+> expected <+> "but got" <+> actual
    case UnexpectedType(_, actual, expected, hints, suggestions) =>
      handleHintsAndSuggestions("expected" <+> expected <+> "but got" <+> actual, hints, suggestions)
    case InvalidSemantic(_, reason, hints, suggestions) => handleHintsAndSuggestions(text(reason), hints, suggestions)
    case UnknownDecl(i, hints, suggestions) =>
      handleHintsAndSuggestions(text(i.idn) <+> "is not declared", hints, suggestions)
    case MultipleDecl(i) => text(i.idn) <+> "is declared more than once"
    case UnsupportedType(_, NotValueType(), _) => "non-executable query"
    case UnsupportedType(_, _, _) => "unsupported type"
    case ExternalError(_, lang, errors) => lang <+> "error: " <+> ssep(errors.map(executorErrorToDoc).to, ",")
    case _ => super.toDoc(n)
  }

  final protected def handleHintsAndSuggestions(d: Doc, hint: Option[String], suggestions: Seq[String]): Doc = {
    val finalDoc = hint match {
      case Some(h) => d <> semi <+> text(h.trim)
      case None => d
    }
    if (suggestions.nonEmpty) {
      finalDoc <>
        line(":") <> ssep(
          suggestions.flatMap(s => s.linesIterator.map(l => "*" <+> text(l.trim))).to,
          linebreak
        )
    } else {
      finalDoc
    }
  }

  private def executorErrorToDoc(err: ErrorMessage): Doc = {
    if (err.positions.nonEmpty) err.message <+> parens(ssep(err.positions.map(rangeToDoc).to, ","))
    else err.message
  }

  private def rangeToDoc(r: ErrorRange): Doc = {
    r.begin.line.toString <> ":" <> r.begin.column.toString <> "," <> r.end.line.toString <> ":" <> r.end.column.toString
  }

}

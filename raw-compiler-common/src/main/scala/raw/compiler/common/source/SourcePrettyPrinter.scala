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

package raw.compiler.common.source

import raw.compiler.base.CompilerProvider
import raw.compiler.base.source._
import raw.compiler.{base, common}
import raw.utils.RawUtils

trait SourcePrettyPrinter extends base.source.SourcePrettyPrinter with common.Keywords {

  protected def args(n: Vector[SourceNode]): Doc = sepArgs(comma, n.map(toDoc): _*)

  private def commonType(t: CommonType): Doc = t match {
    case _: ErrorType => "error"
    case _: AnyType => "any"
    case _: NothingType => "nothing"
    case _: BoolType => "common" <+> "bool"
    case _: StringType => "common" <+> "string"
    case _: ByteType => "common" <+> "byte"
    case _: ShortType => "common" <+> "short"
    case _: IntType => "common" <+> "int"
    case _: LongType => "common" <+> "long"
    case _: FloatType => "common" <+> "float"
    case _: DoubleType => "common" <+> "double"
    case _: DecimalType => "common" <+> "decimal"
    case _: DateType => "common" <+> "date"
    case _: TimeType => "common" <+> "time"
    case _: IntervalType => "common" <+> "interval"
    case _: TimestampType => "common" <+> "timestamp"
    case _: BinaryType => "common" <+> "binary"
    case _: LocationType => "common" <+> "location"
    case _: InputStreamType => "inputstream"
    case _: OutputStreamType => "outputstream"
    case _: VoidType => "void"
    case RecordType(atts) => method("common" <+> "record", atts.map(toDoc): _*)
    case IterableType(innerType) => method("common" <+> "collection", innerType)
    case ListType(innerType) => method("common" <+> "list", innerType)
    case GeneratorType(innerType) => method("common" <+> "generator", innerType)
    case RDDType(inner) => method("rdd", inner)
    case OptionType(innerType) => method("option", innerType)
    case TryType(innerType) => method("try", innerType)
    case RegexType(ngroups) => method("common" <+> "regex", ngroups.toString)
    case OrType(tipes) => method("common" <+> "or", tipes.map(toDoc): _*)
    case t: CommonTypeConstraint => commonTypeConstraint(t)
  }

  private def commonTypeConstraint(t: CommonTypeConstraint): Doc = t match {
    case OneOfType(tipes) => "either" <+> folddoc(tipes.map(toDoc), { case (x, y) => x <+> "or" <+> y })
    case ExpectedRecordType(idns) =>
      if (idns.size == 0) "record"
      else if (idns.size == 1) "record" <+> "with" <+> "field" <+> idns.head
      else s"record" <+> "with" <+> "fields" <+> ssep(idns.map(text).to, ",")
    case ExpectedRegexType() => "regular" <+> "expression"
    case ExpectedFunType() => "function"
    case CastableToType(t) => "castable" <+> "to" <+> t
  }

  private def commonExp(e: CommonExp): Doc = e match {
    case IdnExp(idn) => toDoc(idn)
    case Eval(r) => r match {
        case RawBridgeImpl(lang, p) =>
          val code = CompilerProvider.prettyPrint(lang, p)
          method("$eval", s""""$lang"""", s""""${RawUtils.descape(code)}"""")
        case RawBridgeRef(t, id) =>
          // Used exclusively when creating a program signature while templating code.
          method("$eval", t, id)
      }
    case ErrorExp() => "$error"
  }

  private def commonNode(n: CommonNode): Doc = n match {
    case t: CommonType => commonType(t)
    case e: CommonExp => commonExp(e)
    case i: CommonIdnNode => idnToDoc(i)
    case SourceProgramParam(i, t) => toDoc(i) <> ":" <+> t
    case Bind(e, i) => toDoc(i) <+> ":=" <+> toDoc(e)
    case AttrType(idn, t) => ident(idn) <> ":" <> toDoc(t)
    case i: Intrinsic => intrinsicToDoc(i)
  }

  private def intrinsicToDoc(i: Intrinsic): Doc = i.getClass.getSimpleName

  protected def idnToDoc(i: CommonIdnNode): Doc = i match {
    case IdnDef(idn) => ident(idn)
    case IdnUse(idn) => ident(idn)
  }

  override def toDoc(n: BaseNode): Doc = n match {
    case n: CommonNode => commonNode(n)
    case _ => super.toDoc(n)
  }

  protected def listOfNodes(es: Vector[BaseNode]): Doc = brackets(ssep(es.map(toDoc), comma))

  protected def listOfTuple2Nodes(es: Vector[(BaseNode, BaseNode)]): Doc =
    brackets(ssep(es.map(e => parens(e._1 <> comma <> e._2)), comma))

}

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

package raw.compiler.rql2.source

import org.bitbucket.inkytonik.kiama.output._
import raw.compiler.base
import raw.compiler.base.source.{AnythingType, BaseNode, Type}
import raw.compiler.common.source._
import raw.compiler.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.{Keywords, Rql2TypeUtils}
import raw.utils._

import scala.collection.mutable

trait SourcePrettyPrinter
    extends base.source.SourcePrettyPrinter
    with Keywords
    with Rql2TypeUtils
    with ParenPrettyPrinter {

  protected def args(n: Vector[SourceNode]): Doc = sepArgs(comma, n.map(toDoc): _*)

  protected def idnToDoc(i: CommonIdnNode): Doc = i match {
    case IdnDef(idn) => ident(idn)
    case IdnUse(idn) => ident(idn)
  }

  protected def internal: Boolean = false

  protected def rql2Type(t: Rql2Type): Doc = t match {
    case t: Rql2TypeConstraint => rql2TypeConstraints(t)
    case t: Rql2TypeWithProperties => rql2TypeWithProperties(t)
    case TypeAliasType(idn) => idn
    case ExpType(t) => "type" <+> t
    case PackageType(name) => method("package", s""""$name"""")
    case PackageEntryType(pkgName, entName) => method("package", s""""$pkgName"""", s""""$entName"""")
  }

  protected def rql2TypeWithProperties(t: Rql2TypeWithProperties): Doc = {
    val d: Doc = t match {
      case Rql2OrType(ts, props) =>
        val d = folddoc(ts.map(toDoc), _ <+> "or" <+> _)
        if (internal) {
          // Wrap in parenthesis to disambiguate the type property annotations.
          // Refer to the parser for details.
          parens(d)
        } else {
          d
        }
      case FunType(ms, os, r, props) =>
        val args = ms.map(toDoc) ++ os.map(o => o.i <> ":" <+> o.t)
        val d = parens(enclosedList(args)) <+> "->" <+> r
        if (internal && props.nonEmpty) {
          // Wrap in parenthesis to disambiguate the type property annotations.
          // Refer to the parser for details.
          parens(d)
        } else {
          d
        }
      case other => other match {
          case _: Rql2BoolType => "bool"
          case _: Rql2StringType => "string"
          case _: Rql2LocationType => "location"
          case _: Rql2BinaryType => "binary"
          case _: Rql2ByteType => "byte"
          case _: Rql2ShortType => "short"
          case _: Rql2IntType => "int"
          case _: Rql2LongType => "long"
          case _: Rql2FloatType => "float"
          case _: Rql2DoubleType => "double"
          case _: Rql2DecimalType => "decimal"
          case _: Rql2DateType => "date"
          case _: Rql2TimeType => "time"
          case _: Rql2IntervalType => "interval"
          case _: Rql2TimestampType => "timestamp"
          case Rql2RecordType(atts, _) => method("record", atts.map(att => ident(att.idn) <> ":" <+> att.tipe): _*)
          case Rql2IterableType(innerType, _) => innerType match {
              case _: AnythingType => "collection"
              case _ => method("collection", innerType)
            }
          case Rql2ListType(innerType, _) => innerType match {
              case _: AnythingType => "list"
              case _ => method("list", innerType)
            }
          case _: Rql2UndefinedType => "undefined"
        }
    }
    if (internal) {
      val isNullable = t.props.contains(Rql2IsNullableTypeProperty())
      val isTryable = t.props.contains(Rql2IsTryableTypeProperty())
      if (!isTryable && !isNullable) d
      else {
        val props: Doc =
          if (isTryable && isNullable) "@try" <+> "@null"
          else if (isTryable) "@try"
          else "@null"
        d <+> props
      }
    } else {
      d
    }

  }

  private def rql2TypeConstraints(t: Rql2TypeConstraint): Doc = t match {
    case MergeableType(t) => "compatible" <+> "with" <+> t
    case ExpectedProjType(i) =>
      "package" <> "," <+> "record" <> "," <+> "collection" <+> "or" <+> "list" <+> "with" <+> "field" <+> i
    case IsNullable() => "type nullable"
    case IsTryable() => "type tryable"
    case HasTypeProperties(props) =>
      val isNullable = props.contains(Rql2IsNullableTypeProperty())
      val isTryable = props.contains(Rql2IsTryableTypeProperty())
      if (isNullable && isTryable) {
        "type nullable and tryable"
      } else if (isNullable) {
        "type nullable"
      } else if (isTryable) {
        "type tryable"
      } else {
        throw new AssertionError("No type properties")
      }
    case DoesNotHaveTypeProperties(props) =>
      // We are doing the "DoesNotHave" type constraints.
      val isNotNullable = props.contains(Rql2IsNullableTypeProperty())
      val isNotTryable = props.contains(Rql2IsTryableTypeProperty())
      if (isNotNullable && isNotTryable) {
        "type not nullable and not tryable"
      } else if (isNotNullable) {
        "type not nullable"
      } else if (isNotTryable) {
        "type not tryable"
      } else {
        throw new AssertionError("No type properties")
      }
  }

  protected def rql2Exp(e: Rql2Exp): Doc = e match {
    case Let(stmts, e) =>
      "let" <> nest(line <> ssep(stmts.map(toDoc), comma <> line)) <> line <> "in" <> nest(line <> e)
    case TypeExp(t) => "type" <+> t
    case c: Const => c match {
        case _: NullConst => "null"
        case nc: NumberConst => nc match {
            case IntConst(v) => v
            case LongConst(v) => if (RawUtils.endsWithIgnoreCase(v, 'l')) v else s"${v}L"
            case FloatConst(v) => if (RawUtils.endsWithIgnoreCase(v, 'f')) v else s"${v}f"
            case DoubleConst(v) => if (RawUtils.endsWithIgnoreCase(v, 'd')) v else s"${v}d"
            case DecimalConst(v) => if (RawUtils.endsWithIgnoreCase(v, 'q')) v else s"${v}q"
            case ShortConst(v) => if (RawUtils.endsWithIgnoreCase(v, 's')) v else s"${v}s"
            case ByteConst(v) => if (RawUtils.endsWithIgnoreCase(v, 'b')) v else s"${v}b"
          }
        case BoolConst(v) => v.toString
        case StringConst(v) => s""""${RawUtils.descape(v)}""""
        case TripleQuotedStringConst(v) => s"""\"\"\"$v\"\"\""""
        case BinaryConst(bytes) => s"""0x${bytes.map("%02x".format(_)).mkString}"""
      }
    case IfThenElse(e1, e2, e3) => "if" <+> e1 <+> "then" <> nest(line <> e2) <@> "else" <> nest(line <> e3)
    // Here we are using the toParenDoc only for binary and unary expressions
    case exp: UnaryExp => toParenDoc(exp)
    case binExp: BinaryExp => toParenDoc(binExp)
    case fa: FunApp => fa match {
        // The following must be handled before FunApp.
        case ListPackageBuilder.Build(es) => brackets(enclosedList(es.map(toDoc).to))
        case RecordPackageBuilder.Build(as) => braces(enclosedList(as.map(a => ident(a._1) <> ":" <+> toDoc(a._2))))
        case FunApp(f, as) =>
          val fDoc: Doc =
            if (internal && f.isInstanceOf[Let] || f.isInstanceOf[IfThenElse] || f.isInstanceOf[FunAbs]) parens(f)
            else f
          method(fDoc, as.map(funAppArg): _*)
      }
    case FunAbs(FunProto(ps, mr, FunBody(e))) => mr match {
        case Some(r) => group(parens(enclosedList(ps.map(funParam))) <> ":" <+> r <+> "->" <> nest(line <> e))
        case None => group(parens(enclosedList(ps.map(funParam))) <+> "->" <> nest(line <> e))
      }
    case Proj(e, i) => e <> "." <> ident(i)
    case PackageIdnExp(name) => method("$package", s""""$name"""")
  }

  protected def enclosedList(ls: Seq[Doc], sep: Doc = comma): Doc = group(nest(lsep(ls.to, sep)) <> linebreak)

  protected def funAppArg(a: FunAppArg): Doc = {
    val FunAppArg(e, maybeIdn) = a
    maybeIdn match {
      case Some(idn) => ident(idn) <+> "=" <+> e
      case None => e
    }
  }

  protected def funParam(a: FunParam): Doc = {
    val FunParam(i, mt, me) = a
    (mt, me) match {
      case (Some(t), Some(e)) => i <> ":" <+> t <+> "=" <+> e
      case (Some(t), None) => i <> ":" <+> t
      case (None, Some(e)) => i <+> "=" <+> e
      case (None, None) => i
    }
  }

  protected def rql2LetDecl(d: LetDecl): Doc = d match {
    case LetBind(e, i, mt) => mt match {
        case Some(t) => i <> ":" <+> t <+> "=" <+> e
        case None => i <+> "=" <+> e
      }
    case LetFun(p, i) => i <> funProto(p)
    case LetFunRec(i, p) => "rec" <+> i <> funProto(p)
  }

  protected def funProto(f: FunProto): Doc = {
    val FunProto(ps, r, FunBody(e)) = f
    // prefer to break line after the '='
    r match {
      case Some(r) => group(arguments(ps, funParam) <> ":" <+> r <+> "=" <> nest(line <> e))
      case None => group(arguments(ps, funParam) <+> "=" <> nest(line <> e))
    }
  }

  protected def rql2Node(n: Rql2Node): Doc = n match {
    case e: Rql2Exp => rql2Exp(e)
    case t: Rql2Type => rql2Type(t)
    case Rql2Program(methods, me) =>
      val methodsDoc = methods.map { case Rql2Method(p, idn) => idn <> funProto(p) }
      ssep(methodsDoc ++ me.toSeq.map(toDoc), line)
    case l: LetDecl => rql2LetDecl(l)
  }

  private val integer: Set[Type] = Set(Rql2ByteType(), Rql2ShortType(), Rql2IntType(), Rql2LongType())
  private val number: Set[Type] = integer ++ Set(Rql2FloatType(), Rql2DoubleType(), Rql2DecimalType())
  private val temporal: Set[Type] = Set(Rql2DateType(), Rql2TimeType(), Rql2TimestampType(), Rql2IntervalType())

  override def toDoc(n: BaseNode): Doc = n match {
    case n: CommonNode => commonNode(n)
    case n: Rql2Node => rql2Node(n)
    case _ => super.toDoc(n)
  }

  override def method(name: Doc, n: Doc*): Doc = {
    name <> parens(enclosedList(n))
  }

  override def toParenDoc(e: PrettyExpression): Doc = e match {
    case b: BinaryExp =>
      val ld = recursiveToDoc(b, b.left, LeftAssoc)
      val rd = recursiveToDoc(b, b.right, RightAssoc)
      group(ld <+> text(b.op) <> line <> rd)
    case u: UnaryExp =>
      val ed = recursiveToDoc(u, u.exp, NonAssoc)
      // Not using the super.toParenDoc here because "not" needs a space
      u.unaryOp match {
        case Not() => text(u.op) <+> ed
        case Neg() => text(u.op) <> ed
      }
    // Putting parenthesis always in let's and if then else's.
    // We tried giving then a priority and using the recursiveToDoc function
    // but either there is a bug or we do not understand well the algorithm.
    case l: Let => parens(toDoc(l))
    case i: IfThenElse => parens(i)
    case exp: Exp => toDoc(exp)
  }

  private def commonType(t: CommonType): Doc = t match {
    case _: ErrorType => "error"
    case _: AnyType => "any"
    case _: NothingType => "nothing"
    case t: CommonTypeConstraint => commonTypeConstraint(t)
  }

  private def commonTypeConstraint(t: CommonTypeConstraint): Doc = t match {
    case ExpectedRecordType(idns) =>
      if (idns.size == 0) "record"
      else if (idns.size == 1) "record" <+> "with" <+> "field" <+> idns.head
      else s"record" <+> "with" <+> "fields" <+> ssep(idns.map(text).to, ",")
    case OneOfType(ts) =>
      val info = mutable.ArrayBuffer[Doc]()
      val cleanTs: mutable.HashSet[Type] =
        ts.map(t => removeProps(t, Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))).to
      if (number.subsetOf(cleanTs)) {
        number.foreach(cleanTs.remove)
        info += "number"
      }
      if (integer.subsetOf(cleanTs)) {
        integer.foreach(cleanTs.remove)
        info += "integer"
      }
      if (temporal.subsetOf(cleanTs)) {
        temporal.foreach(cleanTs.remove)
        info += "temporal"
      }
      cleanTs.foreach(t => info += toDoc(t))
      if (info.length == 1) info.head
      else "either" <+> folddoc(info.to, { case (x, y) => x <+> "or" <+> y })
  }

  private def commonExp(e: CommonExp): Doc = e match {
    case IdnExp(idn) => toDoc(idn)
    case ErrorExp() => "$error"
  }

  private def commonNode(n: CommonNode): Doc = n match {
    case t: CommonType => commonType(t)
    case e: CommonExp => commonExp(e)
    case i: CommonIdnNode => idnToDoc(i)
    case SourceProgramParam(i, t) => toDoc(i) <> ":" <+> t
    case Bind(e, i) => toDoc(i) <+> ":=" <+> toDoc(e)
  }

}

object SourcePrettyPrinter extends SourcePrettyPrinter

object InternalSourcePrettyPrinter extends SourcePrettyPrinter {
  final override protected def internal: Boolean = true
}

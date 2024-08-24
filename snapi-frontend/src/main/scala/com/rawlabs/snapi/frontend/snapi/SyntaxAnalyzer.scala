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

package com.rawlabs.snapi.frontend.snapi

import org.bitbucket.inkytonik.kiama.parsing._
import org.bitbucket.inkytonik.kiama.util.Positions
import com.rawlabs.snapi.frontend.base.SyntaxAnalyzer.identRegex
import com.rawlabs.snapi.frontend.base.source.{BaseProgram, Type}
import com.rawlabs.snapi.frontend.base
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.{ListPackageBuilder, RecordPackageBuilder}
import com.rawlabs.snapi.frontend.snapi.source._

import scala.util.matching.Regex

object SyntaxAnalyzerTokens {

  val tokBool = "bool\\b".r
  val tokString = "string\\b".r
  val tokLocation = "location\\b".r

  val tokBinary = "binary\\b".r

  val tokByte = "byte\\b".r
  val tokShort = "short\\b".r
  val tokInt = "int\\b".r
  val tokLong = "long\\b".r
  val tokFloat = "float\\b".r
  val tokDouble = "double\\b".r
  val tokDecimal = "decimal\\b".r

  val tokDate = "date\\b".r
  val tokTime = "time\\b".r
  val tokInterval = "interval\\b".r
  val tokTimestamp = "timestamp\\b".r
  val tokRecord = "record\\b".r
  val tokCollection = "collection\\b".r
  val tokList = "list\\b".r

  val tokAnd = "and\\b".r
  val tokOr = "or\\b".r
  val tokNot = "not\\b".r

  val tokLibrary = "library\\b".r
  val tokPackage = "package\\b".r
  val tokType = "type\\b".r
  val tokUndefined = "undefined\\b".r

  val int = "(\\d+)".r

  val tokLet = "let\\b".r
  val tokIn = "in\\b".r
  val tokRec = "rec\\b".r

  val tokIf = "if\\b".r
  val tokThen = "then\\b".r
  val tokElse = "else\\b".r

  val tokNull = "null\\b".r

  val tokTrue = "true\\b".r
  val tokFalse = "false\\b".r

  val unsignedNumericLit: Regex = """(?i)(\d+(\.\d*)?|\d*\.\d+)(e[+-]?\d+)?[fdlsbq]?""".r

  val kwError: Regex = "(?i)error\\b".r
}

/**
 * FrontendSyntaxAnalyzer - for user code.
 *
 * - Must be fast.
 * - Must not contain internal nodes.
 * - Must not contain internal types (e.g. types from common).
 */
class SyntaxAnalyzer(val positions: Positions) extends Parsers(positions) with base.SyntaxAnalyzer with Keywords {

  import SyntaxAnalyzerTokens._

  final protected lazy val idnDef: Parser[IdnDef] = identDef ^^ { i => IdnDef(i) }

  final protected lazy val identDef: Parser[String] = ident

  final protected lazy val errorType: Parser[ErrorType] = kwError ^^^ ErrorType()

  final protected lazy val idnExp: Parser[IdnExp] = idnUse ^^ { idn => IdnExp(idn) }

  final protected lazy val idnUse: Parser[IdnUse] = ident ^^ { idn => IdnUse(idn) }

  ///////////////////////////////////////////////////////////////////////////
  // Program vs Package
  ///////////////////////////////////////////////////////////////////////////

  override lazy val program: Parser[BaseProgram] = rep(snapiMethod) ~ opt(exp) ^^ {
    case ms ~ me => SnapiProgram(ms, me)
  }

  final protected lazy val snapiMethod: Parser[SnapiMethod] = idnDef ~ funProto ^^ { case i ~ p => SnapiMethod(p, i) }

  ///////////////////////////////////////////////////////////////////////////
  // Types
  ///////////////////////////////////////////////////////////////////////////

  private lazy val plainType: Parser[Type] = packageType |
    packageEntryType |
    expType |
    errorType |
    typeAliasType | "(" ~> plainType <~ ")"

  final override protected lazy val tipe: Parser[Type] = (rawType | "(" ~> tipe <~ ")") ~ opt(typeProps) ^^ {
    case t ~ mProps => mProps match {
        case Some(props) => t match {
            case pType: SnapiTypeWithProperties =>
              props.foldLeft(pType)((t, p) => t.cloneAndAddProp(p).asInstanceOf[SnapiTypeWithProperties])
            case _ => t
          }
        case None => t
      }
  }
  private lazy val rawType: Parser[Type] = orType
  private lazy val basicType: Parser[Type] = propFriendlyType | plainType

  final private lazy val propFriendlyType: Parser[SnapiTypeWithProperties] = funType | primitiveType |
    recordType | iterableType | listType |
    undefinedType | "(" ~> propFriendlyType <~ ")"

  protected lazy val orType: PackratParser[Type] = {
    tipe ~ rep1(tokOr ~> tipe) ^^ {
      case t1 ~ ts => ts.tail.foldLeft(SnapiOrType(t1, ts.head, Set.empty))((agg, t) => SnapiOrType(agg, t, Set.empty))
    } | basicType
  }

  final protected lazy val primitiveType: Parser[SnapiPrimitiveType] =
    boolType | stringType | locationType | binaryType | numberType | temporalType

  final protected lazy val boolType: Parser[SnapiBoolType] = tokBool ^^^ SnapiBoolType()

  final protected lazy val stringType: Parser[SnapiStringType] = tokString ^^^ SnapiStringType()

  final protected lazy val locationType: Parser[SnapiLocationType] = tokLocation ^^^ SnapiLocationType()

  final protected lazy val binaryType: Parser[SnapiBinaryType] = tokBinary ^^^ SnapiBinaryType()

  final protected lazy val numberType: Parser[SnapiNumberType] =
    byteType | shortType | intType | longType | floatType | doubleType | decimalType

  final protected lazy val byteType: Parser[SnapiByteType] = tokByte ^^^ SnapiByteType()

  final protected lazy val shortType: Parser[SnapiShortType] = tokShort ^^^ SnapiShortType()

  final protected lazy val intType: Parser[SnapiIntType] = tokInt ^^^ SnapiIntType()

  final protected lazy val longType: Parser[SnapiLongType] = tokLong ^^^ SnapiLongType()

  final protected lazy val floatType: Parser[SnapiFloatType] = tokFloat ^^^ SnapiFloatType()

  final protected lazy val doubleType: Parser[SnapiDoubleType] = tokDouble ^^^ SnapiDoubleType()

  final protected lazy val decimalType: Parser[SnapiDecimalType] = tokDecimal ^^^ SnapiDecimalType()

  final protected lazy val temporalType: Parser[SnapiTemporalType] = dateType | timeType | intervalType | timestampType

  final protected lazy val dateType: Parser[SnapiDateType] = tokDate ^^^ SnapiDateType()

  final protected lazy val timeType: Parser[SnapiTimeType] = tokTime ^^^ SnapiTimeType()

  final protected lazy val intervalType: Parser[SnapiIntervalType] = tokInterval ^^^ SnapiIntervalType()

  final protected lazy val timestampType: Parser[SnapiTimestampType] = tokTimestamp ^^^ SnapiTimestampType()

  final protected lazy val recordType: Parser[SnapiRecordType] =
    tokRecord ~> ("(" ~> repsep(attrType, ",") <~ opt(",") <~ ")") ^^ (attrs => SnapiRecordType(attrs))

  final protected lazy val attrType: Parser[SnapiAttrType] = (ident <~ ":") ~ tipe ^^ SnapiAttrType

  final protected lazy val iterableType: Parser[SnapiIterableType] =
    tokCollection ~> ("(" ~> tipe <~ ")") ^^ (innerType => SnapiIterableType(innerType))

  final protected lazy val listType: Parser[SnapiListType] =
    tokList ~> ("(" ~> tipe <~ ")") ^^ (innerType => SnapiListType(innerType))

  final protected lazy val funType: PackratParser[FunType] = {
    ("(" ~> repsep(funOptTypeParam | tipe, ",") <~ ")") ~ ("->" ~> tipe) ^^ {
      case ts ~ r =>
        FunType(ts.collect { case t: Type => t }, ts.collect { case p: FunOptTypeParam => p }, r, Set.empty)
    } |
      primitiveType ~ ("->" ~> tipe) ^^ { case i ~ r => FunType(Vector(i), Vector.empty, r) }
  }

  final private lazy val funOptTypeParam: Parser[FunOptTypeParam] = ident ~ (":" ~> tipe) ^^ FunOptTypeParam

  final protected lazy val packageType: Parser[PackageType] = tokPackage ~> "(" ~> stringLit <~ ")" ^^ PackageType

  final protected lazy val packageEntryType: Parser[PackageEntryType] =
    tokPackage ~> "(" ~> stringLit ~ ("," ~> stringLit <~ ")") ^^ PackageEntryType

  final protected lazy val expType: Parser[ExpType] = tokType ~> tipe ^^ ExpType

  final protected lazy val undefinedType: Parser[SnapiUndefinedType] = tokUndefined ^^^ SnapiUndefinedType()

  final protected lazy val typeAliasType: Parser[TypeAliasType] = typeIdnUse ^^ TypeAliasType

  final protected lazy val typeIdnUse: Parser[IdnUse] = typeIdent ^^ { idn => IdnUse(idn) }

  final private lazy val typeIdent: Parser[String] = escapedIdent | identRegex into { idn =>
    if (isReservedType(idn)) failure("reserved type keyword") else success(idn)
  }

  final private lazy val typeProps: Parser[Set[SnapiTypeProperty]] = {
    ("@try" ~ "@null" ^^^ Set[SnapiTypeProperty](SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())) |
      ("@null" ~ "@try" ^^^ Set[SnapiTypeProperty](SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())) |
      ("@try" ^^^ Set[SnapiTypeProperty](SnapiIsTryableTypeProperty())) |
      ("@null" ^^^ Set[SnapiTypeProperty](SnapiIsNullableTypeProperty())) |
      success(Set.empty[SnapiTypeProperty])
  }

  ///////////////////////////////////////////////////////////////////////////
  // Expressions
  ///////////////////////////////////////////////////////////////////////////

  final protected lazy val exp: PackratParser[Exp] = exp1

  final private lazy val exp1: PackratParser[Exp] = exp1 ~ orOp ~ exp2 ^^ {
    case e1 ~ op ~ e2 => BinaryExp(op, e1, e2)
  } | exp2

  final private lazy val orOp: Parser[Or] = tokOr ^^^ Or()

  final private lazy val exp2: PackratParser[Exp] = exp2 ~ andOp ~ exp3 ^^ {
    case e1 ~ op ~ e2 => BinaryExp(op, e1, e2)
  } | exp3

  final private lazy val andOp: Parser[And] = tokAnd ^^^ And()

  final private lazy val exp3: PackratParser[Exp] = notOp ~ exp3 ^^ { case op ~ e => UnaryExp(op, e) } |
    exp4

  final private lazy val notOp: Parser[Not] = tokNot ^^^ Not()

  final private lazy val exp4: PackratParser[Exp] = exp4 ~ compOp ~ exp5 ^^ {
    case e1 ~ op ~ e2 => BinaryExp(op, e1, e2)
  } | exp5

  final private lazy val compOp: Parser[ComparableOp] = "==" ^^^ Eq() |
    "!=" ^^^ Neq() |
    "<=" ^^^ Le() |
    "<" ^^^ Lt() |
    ">=" ^^^ Ge() |
    ">" ^^^ Gt()

  final private lazy val exp5: PackratParser[Exp] = exp5 ~ plusSubOp ~ exp6 ^^ {
    case e1 ~ op ~ e2 => BinaryExp(op, e1, e2)
  } | exp6

  final private lazy val plusSubOp: Parser[BinaryOp] = "+" ^^^ Plus() | "-" ^^^ Sub()

  final private lazy val exp6: PackratParser[Exp] = exp6 ~ multDivModOp ~ exp7 ^^ {
    case e1 ~ op ~ e2 => BinaryExp(op, e1, e2)
  } | exp7

  final private lazy val multDivModOp: Parser[BinaryOp] = "*" ^^^ Mult() | "/" ^^^ Div() | "%" ^^^ Mod()

  // The part '"-" ~> unsignedNumericLit' is because of issue https://raw-labs.atlassian.net/browse/RD-572
  final private lazy val exp7: PackratParser[Exp] = "+" ~> exp7 | "-" ~> unsignedNumericLit ^^ { v =>
    stringToNumberConst("-" + v)
  } |
    negOp ~ exp7 ^^ { case o ~ e => UnaryExp(o, e) } |
    exp8

  final private lazy val negOp: Parser[Neg] = "-" ^^^ Neg()

  protected def exp8: PackratParser[Exp] = exp8Attr

  final private lazy val exp8Attr: PackratParser[Exp] = exp8Attr ~ ("(" ~> repsep(funAppArg, ",") <~ ")") ^^ {
    case f ~ args => FunApp(f, args)
  } | exp8Attr ~ ("." ~> identDef) ^^ { case e ~ idn => Proj(e, idn) } | baseExp

  final protected lazy val funAppArg: Parser[FunAppArg] = opt(ident <~ "=") ~ exp ^^ { case i ~ e => FunAppArg(e, i) }

  final private lazy val packageIdnExp: Parser[PackageIdnExp] =
    "\\$package\\b".r ~> "(" ~> stringLit <~ ")" ^^ PackageIdnExp

  final private lazy val baseExp: PackratParser[Exp] = {
    packageIdnExp |
      let |
      funAbs |
      typeExp |
      ifThenElse |
      nullConst |
      boolConst |
      // Because of the way we parse strings, we need to try triple quotes first
      tripleQStringConst |
      stringConst |
      numberConst |
      lists |
      records |
      idnExp |
      "(" ~> exp <~ ")" |
      failure("expected expression")
  }

  protected def let: Parser[Let] = letAttr

  final private lazy val letAttr: Parser[Let] = (tokLet ~> rep1sep(letDecl, ",")) ~ (opt(",") ~> tokIn ~> exp) ^^ Let

  final protected lazy val letDecl: Parser[LetDecl] = letBind | letFun | letFunRec | failure("expected declaration")

  final private lazy val letBind: Parser[LetBind] = idnDef ~ opt(":" ~> tipe) ~ ("=" ~> exp) ^^ {
    case i ~ t ~ e => LetBind(e, i, t)
  }

  final private lazy val letFun: Parser[LetFun] = idnDef ~ funProto ^^ { case i ~ p => LetFun(p, i) }

  final private lazy val letFunRec: Parser[LetFunRec] = (tokRec ~> idnDef) ~ funProto ^^ {
    case i ~ p => LetFunRec(i, p)
  }

  final private lazy val funProto: Parser[FunProto] =
    // Parse the short type first (primitiveType rule), and only if that fails, try the long type (tipe rule)
    ("(" ~> repsep(funParam, ",") <~ ")") ~ opt(
      ":" ~> primitiveType
    ) ~ ("=" ~> funBody) ^^ { case ps ~ t ~ b => FunProto(ps, t, b) } |
      ("(" ~> repsep(funParam, ",") <~ ")") ~ opt(
        ":" ~> tipe
      ) ~ ("=" ~> funBody) ^^ { case ps ~ t ~ b => FunProto(ps, t, b) }

  final private lazy val funParam: Parser[FunParam] = idnDef ~ opt(":" ~> (tipe ~ opt("=" ~> exp))) ^^ {
    case i ~ mt => mt match {
        case Some(t ~ e) => FunParam(i, Some(t), e)
        case None => FunParam(i, None, None)
      }
  }

  final protected lazy val funAbs: Parser[FunAbs] = funAbsMultiParams | funAbsSingleParam

  final private lazy val funAbsMultiParams: Parser[FunAbs] = funProtoForFunAbsMultiParams ^^ FunAbs

  final private lazy val funProtoForFunAbsMultiParams: Parser[FunProto] = {
    // Parse the short type first (primitiveType rule), and only if that fails, try the long type (tipe rule)
    ("(" ~> repsep(funParam, ",") <~ ")") ~ opt(
      ":" ~> primitiveType
    ) ~ ("->" ~> funBody) ^^ { case ps ~ t ~ b => FunProto(ps, t, b) } |
      ("(" ~> repsep(funParam, ",") <~ ")") ~ opt(
        ":" ~> tipe
      ) ~ ("->" ~> funBody) ^^ {
        case ps ~ t ~ b => FunProto(ps, t, b)
      } |
      (("(" ~> repsep(funParam, ",") <~ ")") <~ opt(
        ":" <~ tipe
      ) <~ "=>" flatMap { _ => failure("use '->' instead of '=>', e.g. '(x: int) -> x + 1'") })
  }

  final private lazy val funAbsSingleParam: Parser[FunAbs] = funProtoForFunAbsSingleParam ^^ FunAbs

  final private lazy val funProtoForFunAbsSingleParam: Parser[FunProto] = funParam ~ ("->" ~> funBody) ^^ {
    case p ~ b => FunProto(Vector(p), None, b)
  }

  final private lazy val funBody: Parser[FunBody] = exp ^^ FunBody

  final protected lazy val typeExp: Parser[TypeExp] = {
    // Copes with cases such as:
    //   let hello = type recor(a: int)
    // In tha case, the parser parsers 'type recor' as a type expression, and then '(' as a FunApp.
    // Tt then thinks 'a: int' is a lambda function being being but misses the '->'.
    // That error message was far too confusing and since type expressions cannot be called  or projected,
    // we handle them here to fail early.
    ((tokType ~> tipe ~> ("(" | ".")) flatMap { _ => error("illegal type use") }) |
      tokType ~> tipe ^^ TypeExp
  }

  protected def ifThenElse: Parser[IfThenElse] = ifThenElseAttr

  final private lazy val ifThenElseAttr: Parser[IfThenElse] =
    tokIf ~> exp ~ (tokThen ~> exp) ~ (tokElse ~> exp) ^^ IfThenElse

  final protected lazy val nullConst: Parser[NullConst] = tokNull ^^^ NullConst()

  final protected lazy val boolConst: Parser[BoolConst] = tokTrue ^^^ BoolConst(true) | tokFalse ^^^ BoolConst(false)

  final protected lazy val stringConst: Parser[StringConst] = singleQuoteStringLit ^^ { s => StringConst(s) }

  final protected lazy val tripleQStringConst: Parser[TripleQuotedStringConst] = tripleQuoteStringLit ^^ { s =>
    TripleQuotedStringConst(s)
  }

  private def stringToNumberConst(v: String): NumberConst = v.last.toLower match {
    case 'b' => ByteConst(v.dropRight(1))
    case 's' => ShortConst(v.dropRight(1))
    case 'l' => LongConst(v.dropRight(1))
    case 'd' => DoubleConst(v.dropRight(1))
    case 'f' => FloatConst(v.dropRight(1))
    case 'q' => DecimalConst(v.dropRight(1))
    case _ =>
      if (isInt(v).isDefined) {
        IntConst(v)
      } else if (isLong(v).isDefined) {
        LongConst(v)
      } else {
        // Making the default double, but we should consider making it Decimal, for added precision.
        // If doing so, we will have to change the inferrer: InferTypes.getType to infer as decimal as well.
        DoubleConst(v)
      }
  }

  final protected lazy val numberConst: Parser[NumberConst] = unsignedNumericLit ^^ stringToNumberConst

  protected lazy val lists: Parser[Exp] = "[" ~> repsep(exp, ",") <~ "]" ^^ { atts =>
    ListPackageBuilder.Build(atts: _*)
  }

  protected lazy val records: Parser[Exp] = "{" ~> repsep(attr, ",") <~ "}" ^^ { atts =>
    RecordPackageBuilder.Build(atts.zipWithIndex.map {
      case (attr, idx) => attr match {
          case ParsedNamedAttribute(idn, e) => idn -> e
          case ParsedUnnamedAttribute(e) => s"_${idx + 1}" -> e
        }
    })
  }

  final protected lazy val attr: Parser[ParsedAttributed] = namedAttr | unnamedAttr

  final private lazy val namedAttr: Parser[ParsedNamedAttribute] = (identDef <~ ":") ~ exp ^^ ParsedNamedAttribute |
    ((identDef <~ "=") flatMap { _ => failure("use ':' instead of '=', e.g. 'x: 1'") })

  final private lazy val unnamedAttr: Parser[ParsedAttributed] = exp ^^ {
    case e @ Proj(_, idn) => ParsedNamedAttribute(idn, e)
    case e => ParsedUnnamedAttribute(e)

  }

}

sealed trait ParsedAttributed
final case class ParsedNamedAttribute(idn: String, e: Exp) extends ParsedAttributed
final case class ParsedUnnamedAttribute(e: Exp) extends ParsedAttributed

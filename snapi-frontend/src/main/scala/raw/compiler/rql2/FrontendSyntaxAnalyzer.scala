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

package raw.compiler.rql2

import org.bitbucket.inkytonik.kiama.parsing._
import org.bitbucket.inkytonik.kiama.util.Positions
import raw.compiler.base.SyntaxAnalyzer.identRegex
import raw.compiler.base.source.{BaseProgram, Type}
import raw.compiler.common
import raw.compiler.common.source.{Exp, IdnUse}
import raw.compiler.rql2.builtin.{ListPackageBuilder, RecordPackageBuilder}
import raw.compiler.rql2.source._

import scala.util.matching.Regex

object FrontendSyntaxAnalyzerTokens {

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
  val tokRegex = "regex\\b".r
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
}

/**
 * FrontendSyntaxAnalyzer - for user code.
 *
 * - Must be fast.
 * - Must not contain internal nodes.
 * - Must not contain internal types (e.g. types from common).
 */
class FrontendSyntaxAnalyzer(val positions: Positions)
    extends Parsers(positions)
    with common.SyntaxAnalyzer
    with Keywords {

  import FrontendSyntaxAnalyzerTokens._

  ///////////////////////////////////////////////////////////////////////////
  // Program vs Package
  ///////////////////////////////////////////////////////////////////////////

  override lazy val program: Parser[BaseProgram] = rep(rql2Method) ~ opt(exp) ^^ { case ms ~ me => Rql2Program(ms, me) }

  final protected lazy val rql2Method: Parser[Rql2Method] = idnDef ~ funProto ^^ { case i ~ p => Rql2Method(p, i) }

  ///////////////////////////////////////////////////////////////////////////
  // Types
  ///////////////////////////////////////////////////////////////////////////

  override protected def tipe: Parser[Type] = tipeAttr

  final private lazy val tipeAttr: Parser[Type] = rql2Type0 |
    failure("illegal type")

  final protected lazy val rql2Type0: Parser[Rql2Type] = rql2Type1 ~ rep(tokOr ~> rql2Type1) ^^ {
    case t1 ~ t2s =>
      if (t2s.isEmpty) t1
      else {
        val ts = t1 +: t2s
        Rql2OrType(ts, Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
      }
  }

  private lazy val rql2Type1: Parser[Rql2Type] = primitiveType |
    recordType |
    iterableType |
    listType |
    funType |
    packageType |
    packageEntryType |
    expType |
    undefinedType |
    typeAliasType

  private val defaultProps: Set[Rql2TypeProperty] = Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())

  final private lazy val primitiveType: Parser[Rql2PrimitiveType] =
    boolType | stringType | locationType | binaryType | numberType | temporalType

  final private lazy val boolType: Parser[Rql2BoolType] = tokBool ^^^ Rql2BoolType(defaultProps)

  final private lazy val stringType: Parser[Rql2StringType] = tokString ^^^ Rql2StringType(defaultProps)

  final private lazy val locationType: Parser[Rql2LocationType] = tokLocation ^^^ Rql2LocationType(defaultProps)

  final private lazy val binaryType: Parser[Rql2BinaryType] = tokBinary ^^^ Rql2BinaryType(defaultProps)

  final private lazy val numberType: Parser[Rql2NumberType] =
    byteType | shortType | intType | longType | floatType | doubleType | decimalType

  final private lazy val byteType: Parser[Rql2ByteType] = tokByte ^^^ Rql2ByteType(defaultProps)

  final private lazy val shortType: Parser[Rql2ShortType] = tokShort ^^^ Rql2ShortType(defaultProps)

  final private lazy val intType: Parser[Rql2IntType] = tokInt ^^^ Rql2IntType(defaultProps)

  final private lazy val longType: Parser[Rql2LongType] = tokLong ^^^ Rql2LongType(defaultProps)

  final private lazy val floatType: Parser[Rql2FloatType] = tokFloat ^^^ Rql2FloatType(defaultProps)

  final private lazy val doubleType: Parser[Rql2DoubleType] = tokDouble ^^^ Rql2DoubleType(defaultProps)

  final private lazy val decimalType: Parser[Rql2DecimalType] = tokDecimal ^^^ Rql2DecimalType(defaultProps)

  final private lazy val temporalType: Parser[Rql2TemporalType] = dateType | timeType | intervalType | timestampType

  final private lazy val dateType: Parser[Rql2DateType] = tokDate ^^^ Rql2DateType(defaultProps)

  final private lazy val timeType: Parser[Rql2TimeType] = tokTime ^^^ Rql2TimeType(defaultProps)

  final private lazy val intervalType: Parser[Rql2IntervalType] = tokInterval ^^^ Rql2IntervalType(defaultProps)

  final private lazy val timestampType: Parser[Rql2TimestampType] = tokTimestamp ^^^ Rql2TimestampType(defaultProps)

  final private lazy val recordType: Parser[Rql2RecordType] =
    tokRecord ~> ("(" ~> repsep(attrType, ",") <~ opt(",") <~ ")") ^^ {
      case atts => Rql2RecordType(atts, defaultProps)
    }

  final private lazy val attrType: Parser[Rql2AttrType] = (ident <~ ":") ~ tipe ^^ Rql2AttrType

  final private lazy val iterableType: Parser[Rql2IterableType] = tokCollection ~> ("(" ~> tipe <~ ")") ^^ {
    case t => Rql2IterableType(t, defaultProps)
  }

  final private lazy val listType: Parser[Rql2ListType] = tokList ~> ("(" ~> tipe <~ ")") ^^ {
    case t => Rql2ListType(t, defaultProps)
  }

  final private lazy val funType: PackratParser[FunType] = {
    // (msb): Note that the order "funOptTypeParam | tipe" is really important.
    // Parser combinators will not backtrack in | if the case matches.
    // So if we had "tipe | funOptTypeParam" and we had as input "x: int", then "x" would parse successfully as a typealias.
    // So that repsep case was handled. We'd then expect "," and since none found, we'd require ")".
    // By trying "funOptTypeParam" case first - the "longest case first" - we handle that issue.
    //
    // Wrap in parenthesis to disambiguate the type property annotations
    // e.g. ((int) -> string) @null @try
    ("(" ~> "(" ~> repsep(funOptTypeParam | tipe, ",") <~ ")") ~ ("->" ~> tipe <~ ")") ^^ {
      case ts ~ r =>
        FunType(ts.collect { case t: Type => t }, ts.collect { case p: FunOptTypeParam => p }, r, defaultProps)
    } |
      // e.g. (int) -> string
      ("(" ~> repsep(funOptTypeParam | tipe, ",") <~ ")") ~ ("->" ~> tipe) ^^ {
        case ts ~ r => FunType(
            ts.collect { case t: Type => t },
            ts.collect { case p: FunOptTypeParam => p },
            r,
            defaultProps
          )
      }
  }

  final private lazy val funOptTypeParam: Parser[FunOptTypeParam] = ident ~ (":" ~> tipe) ^^ FunOptTypeParam

  final private lazy val packageType: Parser[PackageType] = tokPackage ~> "(" ~> stringLit <~ ")" ^^ PackageType

  final private lazy val packageEntryType: Parser[PackageEntryType] =
    tokPackage ~> "(" ~> stringLit ~ ("," ~> stringLit <~ ")") ^^ PackageEntryType

  final private lazy val expType: Parser[ExpType] = tokType ~> tipe ^^ ExpType

  final private lazy val undefinedType: Parser[Rql2UndefinedType] = tokUndefined ^^^ Rql2UndefinedType(defaultProps)

  final private lazy val typeAliasType: Parser[TypeAliasType] = typeIdnUse ^^ TypeAliasType

  final private lazy val typeIdnUse: Parser[IdnUse] = typeIdent ^^ { idn => IdnUse(idn) }

  final private lazy val typeIdent: Parser[String] = escapedIdent | identRegex into { idn =>
    if (isReservedType(idn)) failure("reserved type keyword") else success(idn)
  }

  ///////////////////////////////////////////////////////////////////////////
  // Expressions
  ///////////////////////////////////////////////////////////////////////////

  final override protected lazy val exp: PackratParser[Exp] = exp1

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

  override protected def baseExp: PackratParser[Exp] = packageIdnExp |
    eval |
    baseExpAttr

  final private lazy val baseExpAttr: PackratParser[Exp] = {
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

  final private lazy val funProto: Parser[FunProto] = ("(" ~> repsep(funParam, ",") <~ ")") ~ opt(
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

  final private lazy val funProtoForFunAbsMultiParams: Parser[FunProto] = ("(" ~> repsep(funParam, ",") <~ ")") ~ opt(
    ":" ~> tipe
  ) ~ ("->" ~> funBody) ^^ {
    case ps ~ t ~ b => FunProto(ps, t, b)
  } |
    (("(" ~> repsep(funParam, ",") <~ ")") <~ opt(
      ":" <~ tipe
    ) <~ "=>" flatMap { _ => failure("use '->' instead of '=>', e.g. '(x: int) -> x + 1'") })

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

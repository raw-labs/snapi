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

import org.bitbucket.inkytonik.kiama.parsing._
import raw.compiler.base
import raw.compiler.base.CompilerProvider
import raw.compiler.base.source._
import raw.compiler.common.source._

import scala.util.matching.Regex

object SyntaxAnalyzer {
  private val kwError: Regex = "(?i)error\\b".r
}

trait SyntaxAnalyzer extends base.SyntaxAnalyzer with Keywords {

  self: ParsersBase with VectorRepetitionParsers =>

  import SyntaxAnalyzer._

  final protected lazy val bind: Parser[Bind] = idnDef ~ (":=" ~> exp) ^^ { case i ~ e => Bind(e, i) }

  final protected lazy val idnDef: Parser[IdnDef] = identDef ^^ { i => IdnDef(i) }

  protected def exp: PackratParser[Exp] = baseExp

  protected def baseExp: Parser[Exp] = eval |
    idnExp |
    "(" ~> exp <~ ")" |
    failure("expected expression")

  final protected lazy val eval: Parser[Eval] = method2("$$eval\\b".r, stringLit, stringLitEscaped) ^^ {
    case l ~ p =>
      val (program, _) = CompilerProvider.parse(l, p)
      Eval(RawBridgeImpl(l, program))
  }

  final protected lazy val programParam: Parser[SourceProgramParam] = idnDef ~ (":" ~> tipe) ^^ {
    case i ~ t => SourceProgramParam(i, t)
  }

  final protected lazy val identDef: Parser[String] = ident

  override protected def tipe: Parser[Type] = commonType | failure("illegal type")

  final protected lazy val commonType: Parser[CommonType] = anyType |
    nothingType |
    inputStreamType |
    outputStreamType |
    boolType |
    stringType |
    byteType |
    shortType |
    intType |
    longType |
    floatType |
    doubleType |
    decimalType |
    dateType |
    timeType |
    timestampType |
    intervalType |
    binaryType |
    recordType |
    iterableType |
    listType |
    generatorType |
    rddType |
    optionType |
    tryType |
    voidType |
    locationType |
    regexType |
    orType | errorType

  final protected lazy val errorType: Parser[ErrorType] = kwError ^^^ ErrorType()

  final private lazy val anyType: Parser[AnyType] = "any\\b".r ^^^ AnyType()

  final private lazy val nothingType: Parser[NothingType] = "nothing\\b".r ^^^ NothingType()

  final private lazy val inputStreamType: Parser[InputStreamType] = "inputstream\\b".r ^^^ InputStreamType()

  final private lazy val outputStreamType: Parser[OutputStreamType] = "outputstream\\b".r ^^^ OutputStreamType()

  final private lazy val boolType: Parser[BoolType] = "common\\b".r ~> "bool\\b".r ^^^ BoolType()

  final private lazy val stringType: Parser[StringType] = "common\\b".r ~> "string\\b".r ^^^ StringType()

  final private lazy val byteType: Parser[ByteType] = "common\\b".r ~> "byte\\b".r ^^^ ByteType()

  final private lazy val shortType: Parser[ShortType] = "common\\b".r ~> "short\\b".r ^^^ ShortType()

  final private lazy val intType: Parser[IntType] = "common\\b".r ~> "int\\b".r ^^^ IntType()

  final private lazy val longType: Parser[LongType] = "common\\b".r ~> "long\\b".r ^^^ LongType()

  final private lazy val floatType: Parser[FloatType] = "common\\b".r ~> "float\\b".r ^^^ FloatType()

  final private lazy val doubleType: Parser[DoubleType] = "common\\b".r ~> "double\\b".r ^^^ DoubleType()

  final private lazy val decimalType: Parser[DecimalType] = "common\\b".r ~> "decimal\\b".r ^^^ DecimalType()

  final private lazy val dateType: Parser[DateType] = "common\\b".r ~> "date\\b".r ^^^ DateType()

  final private lazy val timeType: Parser[TimeType] = "common\\b".r ~> "time\\b".r ^^^ TimeType()

  final private lazy val timestampType: Parser[TimestampType] = "common\\b".r ~> "timestamp\\b".r ^^^ TimestampType()

  final private lazy val intervalType: Parser[IntervalType] = "common\\b".r ~> "interval\\b".r ^^^ IntervalType()

  final private lazy val binaryType: Parser[BinaryType] = "common\\b".r ~> "binary\\b".r ^^^ BinaryType()

  final private lazy val recordType: Parser[RecordType] =
    "common\\b".r ~> "record\\b".r ~> opt("(" ~> repsep(attrType, ",") <~ ")") ^^ { matts =>
      RecordType(matts.getOrElse(Vector.empty))
    }

  final private lazy val attrType: Parser[AttrType] = (ident <~ ":") ~ tipe ^^ { case idn ~ t => AttrType(idn, t) }

  final private lazy val iterableType: Parser[IterableType] =
    "common\\b".r ~> "collection\\b".r ~> "(" ~> tipe <~ ")" ^^ IterableType

  final private lazy val listType: Parser[ListType] = "common\\b".r ~> "list\\b".r ~> "(" ~> tipe <~ ")" ^^ ListType

  final private lazy val generatorType: Parser[GeneratorType] =
    "common\\b".r ~> "generator\\b".r ~> "(" ~> tipe <~ ")" ^^ GeneratorType

  final private lazy val rddType: Parser[RDDType] = method1("rdd\\b".r, tipe) ^^ RDDType

  final private lazy val optionType: Parser[OptionType] = "option\\b".r ~> "(" ~> tipe <~ ")" ^^ OptionType

  final private lazy val tryType: Parser[TryType] = "try\\b".r ~> "(" ~> tipe <~ ")" ^^ TryType

  final private lazy val voidType: Parser[VoidType] = "void\\b".r ^^^ VoidType()

  final private lazy val locationType: Parser[LocationType] = "common\\b".r ~> "location\\b".r ^^^ LocationType()

  final private lazy val regexType: Parser[RegexType] = "common\\b".r ~> "regex\\b".r ~ "(" ~> "(\\d+)".r <~ ")" ^^ {
    n => RegexType(n.toInt)
  }

  final private lazy val orType: Parser[OrType] = "common\\b".r ~> "or\\b".r ~> "(" ~> rep1sep(tipe, ",") <~ ")" ^^ {
    ts => OrType(ts)
  }

  final protected lazy val idnExp: Parser[IdnExp] = idnUse ^^ { idn => IdnExp(idn) }

  final protected lazy val idnUse: Parser[IdnUse] = ident ^^ { idn => IdnUse(idn) }

}

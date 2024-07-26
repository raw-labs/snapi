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

package raw.compiler.rql2.builtin

import raw.compiler.base.source.Type
import raw.compiler.rql2._
import raw.compiler.rql2.api._
import raw.compiler.rql2.source._
import raw.client.api.{EntryDoc, PackageDoc}
import raw.compiler.common.source.Exp

class TestPackage extends PackageExtension {

  override def name: String = "TestPackage"

  override def docs: PackageDoc = ???

}

class MandatoryExpArgsEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "MandatoryExpArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx <= 2)
    Right(ExpParam(Rql2IntType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IntType())
  }

}

class MandatoryValueArgsEntry extends MandatoryExpArgsEntry {

  override def packageName: String = "TestPackage"

  override def entryName: String = "MandatoryValueArgs"

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(Rql2IntType()))
}

class OptionalExpArgsTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "OptionalExpArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(Rql2IntType()))

  override def optionalParams: Option[Set[String]] = Some(Set("x", "y"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    Right(ExpParam(Rql2IntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IntType())
  }

}

class OptionalValueArgSugar extends SugarEntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "OptionalValueArgSugar"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(Rql2IntType()))

  override def optionalParams: Option[Set[String]] = Some(Set("x"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    Right(ValueParam(Rql2IntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IntType())
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val mandatory = FunAppArg(mandatoryArgs.head.asInstanceOf[ExpArg].e, None)
    val optionalX = {
      val arg = optionalArgs.find(_._1 == "x").get._2.asInstanceOf[ValueArg].v.asInstanceOf[IntRql2Value]
      FunAppArg(IntConst(arg.v.toString), Some("x"))
    }
    val optionalY = FunAppArg(IntConst("10"), Some("y"))
    FunApp(Proj(PackageIdnExp("TestPackage"), "OptionalValueArgs"), Vector(mandatory, optionalX, optionalY))
  }
}

class OptionalValueArgsTestEntry extends OptionalExpArgsTestEntry {

  override def entryName: String = "OptionalValueArgs"

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    Right(ValueParam(Rql2IntType()))

}

class VarExpArgsTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarExpArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(Rql2IntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IntType())
  }

}

class VarValueArgsTestEntry extends VarExpArgsTestEntry {

  override def entryName: String = "VarValueArgs"

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(Rql2IntType()))

}

class VarValueArgSugarTestEntry extends SugarEntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarValueArgSugar"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(Rql2IntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2IntType())
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val varArg1 = FunAppArg(IntConst("1"), None)
    val varArg2 = FunAppArg(IntConst("2"), None)
    val valueArgs =
      varArgs.map { case ValueArg(v, _) => FunAppArg(IntConst(v.asInstanceOf[IntRql2Value].v.toString), None) }
    FunApp(Proj(PackageIdnExp("TestPackage"), "VarValueArgs"), Vector(varArg1, varArg2) ++ valueArgs)
  }

}

class VarNullableStringValueTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarValueNullableStringArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(Rql2StringType(Set(Rql2IsNullableTypeProperty()))))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2StringType())
  }

}

class VarNullableStringExpTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarExpNullableStringArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(Rql2StringType(Set(Rql2IsNullableTypeProperty()))))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2StringType(Set(Rql2IsNullableTypeProperty())))
  }

}

class StrictArgsTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "StrictArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(Rql2ListType(Rql2IntType())))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("r"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = idn match {
    case "r" =>
      Right(ExpParam(Rql2RecordType(Vector(Rql2AttrType("a", Rql2LongType()), Rql2AttrType("b", Rql2FloatType())))))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(Rql2FloatType())

}

class StrictArgsColPassThroughTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "StrictArgsColPassThrough"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(Rql2IterableType(Rql2IntType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(Rql2IterableType(Rql2IntType()))

}

class StrictArgsColConsumeTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "StrictArgsColConsume"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(Rql2IterableType(Rql2IntType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] =
    Right(Rql2ListType(Rql2IntType(), Set(Rql2IsTryableTypeProperty())))

}

abstract class ValueArgTestEntry(t: Type) extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1
  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ValueParam(t))
  }

  override def returnType(
      Args: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2RecordType(Vector(Rql2AttrType("arg", t))))
  }

}

class ByteValueArgTestEntry extends ValueArgTestEntry(Rql2ByteType()) {
  override def entryName: String = "ByteValueArg"
}

class ShortValueArgTestEntry extends ValueArgTestEntry(Rql2ShortType()) {
  override def entryName: String = "ShortValueArg"
}

class IntValueArgTestEntry extends ValueArgTestEntry(Rql2IntType()) {
  override def entryName: String = "IntValueArg"
}

class LongValueArgTestEntry extends ValueArgTestEntry(Rql2LongType()) {
  override def entryName: String = "LongValueArg"
}

class FloatValueArgTestEntry extends ValueArgTestEntry(Rql2FloatType()) {
  override def entryName: String = "FloatValueArg"
}

class DoubleValueArgTestEntry extends ValueArgTestEntry(Rql2DoubleType()) {
  override def entryName: String = "DoubleValueArg"
}

class StringValueArgTestEntry extends ValueArgTestEntry(Rql2StringType()) {
  override def entryName: String = "StringValueArg"
}

class BoolValueArgTestEntry extends ValueArgTestEntry(Rql2BoolType()) {
  override def entryName: String = "BoolValueArg"
}

class DateValueArgTestEntry extends ValueArgTestEntry(Rql2DateType()) {
  override def entryName: String = "DateValueArg"
}

class TimeValueArgTestEntry extends ValueArgTestEntry(Rql2TimeType()) {
  override def entryName: String = "TimeValueArg"
}

class TimestampValueArgTestEntry extends ValueArgTestEntry(Rql2TimestampType()) {
  override def entryName: String = "TimestampValueArg"
}

class IntervalValueArgTestEntry extends ValueArgTestEntry(Rql2IntervalType()) {
  override def entryName: String = "IntervalValueArg"
}

class RecordValueArgTestEntry
    extends ValueArgTestEntry(
      Rql2RecordType(Vector(Rql2AttrType("a", Rql2IntType()), Rql2AttrType("b", Rql2FloatType())))
    ) {
  override def entryName: String = "RecordValueArg"
}

class ListValueArgTestEntry
    extends ValueArgTestEntry(
      Rql2ListType(Rql2IntType())
    ) {
  override def entryName: String = "ListValueArg"
}

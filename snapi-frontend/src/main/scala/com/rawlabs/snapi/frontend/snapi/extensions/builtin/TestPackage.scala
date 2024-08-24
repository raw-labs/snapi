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

package com.rawlabs.snapi.frontend.snapi.extensions.builtin

import com.rawlabs.compiler.{EntryDoc, PackageDoc}
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions._
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.source.Exp

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
    Right(ExpParam(SnapiIntType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiIntType())
  }

}

class MandatoryValueArgsEntry extends MandatoryExpArgsEntry {

  override def packageName: String = "TestPackage"

  override def entryName: String = "MandatoryValueArgs"

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(SnapiIntType()))
}

class OptionalExpArgsTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "OptionalExpArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(SnapiIntType()))

  override def optionalParams: Option[Set[String]] = Some(Set("x", "y"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    Right(ExpParam(SnapiIntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiIntType())
  }

}

class OptionalValueArgSugar extends SugarEntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "OptionalValueArgSugar"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(SnapiIntType()))

  override def optionalParams: Option[Set[String]] = Some(Set("x"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    Right(ValueParam(SnapiIntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiIntType())
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
      val arg = optionalArgs.find(_._1 == "x").get._2.asInstanceOf[ValueArg].v.asInstanceOf[SnapiIntValue]
      FunAppArg(IntConst(arg.v.toString), Some("x"))
    }
    val optionalY = FunAppArg(IntConst("10"), Some("y"))
    FunApp(Proj(PackageIdnExp("TestPackage"), "OptionalValueArgs"), Vector(mandatory, optionalX, optionalY))
  }
}

class OptionalValueArgsTestEntry extends OptionalExpArgsTestEntry {

  override def entryName: String = "OptionalValueArgs"

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] =
    Right(ValueParam(SnapiIntType()))

}

class VarExpArgsTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarExpArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(SnapiIntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiIntType())
  }

}

class VarValueArgsTestEntry extends VarExpArgsTestEntry {

  override def entryName: String = "VarValueArgs"

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(SnapiIntType()))

}

class VarValueArgSugarTestEntry extends SugarEntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarValueArgSugar"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ValueParam(SnapiIntType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiIntType())
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
      varArgs.map { case ValueArg(v, _) => FunAppArg(IntConst(v.asInstanceOf[SnapiIntValue].v.toString), None) }
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
    Right(ValueParam(SnapiStringType(Set(SnapiIsNullableTypeProperty()))))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiStringType())
  }

}

class VarNullableStringExpTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "VarExpNullableStringArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(SnapiStringType(Set(SnapiIsNullableTypeProperty()))))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiStringType(Set(SnapiIsNullableTypeProperty())))
  }

}

class StrictArgsTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "StrictArgs"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(SnapiListType(SnapiIntType())))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("r"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = idn match {
    case "r" => Right(
        ExpParam(SnapiRecordType(Vector(SnapiAttrType("a", SnapiLongType()), SnapiAttrType("b", SnapiFloatType()))))
      )
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(SnapiFloatType())

}

class StrictArgsColPassThroughTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "StrictArgsColPassThrough"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(SnapiIterableType(SnapiIntType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = Right(SnapiIterableType(SnapiIntType()))

}

class StrictArgsColConsumeTestEntry extends EntryExtension {

  override def packageName: String = "TestPackage"

  override def entryName: String = "StrictArgsColConsume"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(SnapiIterableType(SnapiIntType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] =
    Right(SnapiListType(SnapiIntType(), Set(SnapiIsTryableTypeProperty())))

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
    Right(SnapiRecordType(Vector(SnapiAttrType("arg", t))))
  }

}

class ByteValueArgTestEntry extends ValueArgTestEntry(SnapiByteType()) {
  override def entryName: String = "ByteValueArg"
}

class ShortValueArgTestEntry extends ValueArgTestEntry(SnapiShortType()) {
  override def entryName: String = "ShortValueArg"
}

class IntValueArgTestEntry extends ValueArgTestEntry(SnapiIntType()) {
  override def entryName: String = "IntValueArg"
}

class LongValueArgTestEntry extends ValueArgTestEntry(SnapiLongType()) {
  override def entryName: String = "LongValueArg"
}

class FloatValueArgTestEntry extends ValueArgTestEntry(SnapiFloatType()) {
  override def entryName: String = "FloatValueArg"
}

class DoubleValueArgTestEntry extends ValueArgTestEntry(SnapiDoubleType()) {
  override def entryName: String = "DoubleValueArg"
}

class StringValueArgTestEntry extends ValueArgTestEntry(SnapiStringType()) {
  override def entryName: String = "StringValueArg"
}

class BoolValueArgTestEntry extends ValueArgTestEntry(SnapiBoolType()) {
  override def entryName: String = "BoolValueArg"
}

class DateValueArgTestEntry extends ValueArgTestEntry(SnapiDateType()) {
  override def entryName: String = "DateValueArg"
}

class TimeValueArgTestEntry extends ValueArgTestEntry(SnapiTimeType()) {
  override def entryName: String = "TimeValueArg"
}

class TimestampValueArgTestEntry extends ValueArgTestEntry(SnapiTimestampType()) {
  override def entryName: String = "TimestampValueArg"
}

class IntervalValueArgTestEntry extends ValueArgTestEntry(SnapiIntervalType()) {
  override def entryName: String = "IntervalValueArg"
}

class RecordValueArgTestEntry
    extends ValueArgTestEntry(
      SnapiRecordType(Vector(SnapiAttrType("a", SnapiIntType()), SnapiAttrType("b", SnapiFloatType())))
    ) {
  override def entryName: String = "RecordValueArg"
}

class ListValueArgTestEntry
    extends ValueArgTestEntry(
      SnapiListType(SnapiIntType())
    ) {
  override def entryName: String = "ListValueArg"
}

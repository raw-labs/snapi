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

import com.rawlabs.compiler.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.extensions.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  ValueParam
}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.source.{SnapiRecordType, _}

object RecordPackageBuilder {
  object Build {
    def apply(atts: Vector[(String, Exp)]): Exp = {
      FunApp(Proj(PackageIdnExp("Record"), "Build"), atts.map { case (idn, e) => FunAppArg(e, Some(idn)) }.to)
    }
    def apply(atts: Exp*): Exp = {
      FunApp(
        Proj(PackageIdnExp("Record"), "Build"),
        atts.zipWithIndex.map { case (e, idx) => FunAppArg(e, Some(s"_${idx + 1}")) }.to
      )
    }
    def unapply(e: Exp): Option[Vector[(String, Exp)]] = e match {
      case FunApp(Proj(PackageIdnExp("Record"), "Build"), atts) =>
        Some(atts.map { case FunAppArg(e, Some(idn)) => (idn, e) })
      case _ => None
    }
  }

  object AddField {
    def apply(r: Exp, e: Exp, name: String) =
      FunApp(Proj(PackageIdnExp("Record"), "AddField"), Vector(FunAppArg(r, None), FunAppArg(e, Some(name))))
  }

  object Concat {
    def apply(r1: Exp, r2: Exp) =
      FunApp(Proj(PackageIdnExp("Record"), "Concat"), Vector(FunAppArg(r1, None), FunAppArg(r2, None)))
  }

  object GetFieldByIndex {
    def apply(r: Exp, idx: Exp) =
      FunApp(Proj(PackageIdnExp("Record"), "GetFieldByIndex"), Vector(FunAppArg(r, None), FunAppArg(idx, None)))
  }
}

class RecordPackage extends PackageExtension {

  override def name: String = "Record"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the record type."
  )

}

class RecordBuildEntry extends EntryExtension {

  override def packageName: String = "Record"

  override def entryName: String = "Build"
  override def docs: EntryDoc = EntryDoc(
    summary = "Builds a record value.",
    description = None,
    examples = List(
      ExampleDoc("Record.Build(lat=46.518831258, lon=6.5593310)", result = Some("{lat: 46.518831258, lon: 6.5593310}"))
    ),
    params = List(
      ParamDoc(
        "",
        TypeDoc(List.empty),
        "Pairs of field name mapping to value.",
        customSyntax = Some("field=value, ...")
      )
    ),
    ret = Some(ReturnDoc("The record value.", retType = Some(TypeDoc(List("record")))))
  )

  override def nrMandatoryParams: Int = 0

  override def optionalParams: Option[Set[String]] = Some(Set.empty)

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    // TODO (msb): Limit types we can store in records?
    Right(ExpParam(AnythingType()))
  }

  override def allowRepeatedOptionalArguments: Boolean = true

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(
      SnapiRecordType(
        optionalArgs.map { case (i, arg: ExpArg) => SnapiAttrType(i, arg.t) }.to
      )
    )
  }

}

class RecordConcatEntry extends EntryExtension {

  override def packageName: String = "Record"

  override def entryName: String = "Concat"

  override def docs: EntryDoc = EntryDoc(
    summary = "Concatenates two records.",
    params = List(
      ParamDoc("record1", TypeDoc(List("record")), "First record to concatenate into one."),
      ParamDoc("record2", TypeDoc(List("record")), "Second record to concatenate into one.")
    ),
    examples = List(
      ExampleDoc(
        """let r1 = Record.Build(x=1, y=2),
          |    r2 = Record.Build(a="1", b="2")
          |in Record.Concat(r1, r2)""".stripMargin,
        result = Some("""{x: 1, y: 2, a: "1", b: "2"}""")
      )
    ),
    ret = Some(ReturnDoc("The concatenated record.", retType = Some(TypeDoc(List("record")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(ExpectedRecordType(Set.empty)))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val SnapiRecordType(atts1, props1) = mandatoryArgs(0).t
    val SnapiRecordType(atts2, props2) = mandatoryArgs(1).t
    Right(SnapiRecordType(atts1 ++ atts2, props1 ++ props2))
  }

}

class RecordFieldsEntry extends EntryExtension {

  override def packageName: String = "Record"

  override def entryName: String = "Fields"

  override def docs: EntryDoc = EntryDoc(
    summary = "Returns the field names of a record as a list of strings.",
    description = None,
    params = List(ParamDoc("record", TypeDoc(List("record")), "The record to obtain the fields from.")),
    examples = List(
      ExampleDoc(
        """let r = Record.Build(46.518831258, lon=6.5593310)
          |in Record.Fields(r)""".stripMargin,
        result = Some("""List.Build("lat", "lon")""")
      )
    ),
    ret = Some(ReturnDoc("The list of field names.", retType = Some(TypeDoc(List("list(string)")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(ExpectedRecordType(Set.empty)))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiListType(SnapiStringType()))
  }

}

class RecordAddFieldEntry extends EntryExtension {

  override def packageName: String = "Record"

  override def entryName: String = "AddField"

  override def docs: EntryDoc = EntryDoc(
    summary = "Adds a new field to a record.",
    description = None,
    params = List(
      ParamDoc("record", TypeDoc(List("record")), "The record to add the field to."),
      ParamDoc("", TypeDoc(List.empty), "Field name mapping to value.", customSyntax = Some("field=value"))
    ),
    examples = List(
      ExampleDoc(
        """let r = Record.Build(lat=46.518831258, lon=6.5593310)
          |in Record.AddField(r, name="EPFL")""".stripMargin,
        result = Some("""{ lat: 46.518831258, lon: 6.5593310, name: "EPFL" }""")
      )
    ),
    ret = Some(ReturnDoc("The record with the new field.", retType = Some(TypeDoc(List("record")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(ExpectedRecordType(Set.empty)))
  }

  override def optionalParams: Option[Set[String]] = Some(Set.empty)

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    val t = prevMandatoryArgs(0).asInstanceOf[ExpArg].t.asInstanceOf[SnapiRecordType]
    if (t.atts.exists(att => att.idn == idn)) {
      Left("field already exists in record")
    } else {
      // TODO (msb): Limit types we can store in records?
      Right(ExpParam(AnythingType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    if (optionalArgs.isEmpty) {
      Left("new field name and value must be provided")
    } else if (optionalArgs.size > 1) {
      Left("only one field can be added")
    } else {
      val SnapiRecordType(atts, _) = mandatoryArgs.head.t
      Right(SnapiRecordType(atts ++ optionalArgs.map(a => SnapiAttrType(a._1, a._2.t))))
    }
  }

}

class RecordRemoveFieldEntry extends EntryExtension {

  override def packageName: String = "Record"

  override def entryName: String = "RemoveField"

  override def docs: EntryDoc = EntryDoc(
    summary = "Removes a field from a record.",
    description = None,
    params = List(
      ParamDoc("record", TypeDoc(List("record")), "The record to remove the field from."),
      ParamDoc("field", TypeDoc(List("string")), "The name of the field to remove from the record.")
    ),
    examples = List(
      ExampleDoc(
        """let r = Record.Build(x=1, y=2, z=3)
          |in Record.RemoveField(r, "y")""".stripMargin,
        result = Some("""{ x: 1, z: 3 }""")
      )
    ),
    ret = Some(ReturnDoc("The record with the field removed.", retType = Some(TypeDoc(List("record")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(ExpectedRecordType(Set.empty)))
      case 1 => Right(ValueParam(SnapiStringType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val t = mandatoryArgs(0).asInstanceOf[ExpArg].t.asInstanceOf[SnapiRecordType]
    val idn = getStringValue(mandatoryArgs(1))
    if (!t.atts.exists(att => att.idn == idn)) {
      Left(s"field $idn not found")
    } else {
      Right(SnapiRecordType(t.atts.filter(att => att.idn != idn)))
    }
  }

}

class RecordGetFieldByIndexEntry extends EntryExtension {

  override def packageName: String = "Record"

  override def entryName: String = "GetFieldByIndex"

  override def docs: EntryDoc = EntryDoc(
    summary = "Gets a field from a record given an index.",
    description = None,
    params = List(
      ParamDoc("record", TypeDoc(List("record")), "The record to read the field from."),
      ParamDoc("index", TypeDoc(List("int")), "The index (aka position) of the field to get from the record.")
    ),
    examples = List(
      ExampleDoc(
        """let r = Record.Build(x=1, y=2, z=3)
          |in Record.GetFieldByIndex(r, 1)""".stripMargin,
        result = Some("2")
      )
    ),
    ret = Some(ReturnDoc("The value of the field at the given index.", retType = None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(ExpectedRecordType(Set.empty)))
      case 1 => Right(ValueParam(SnapiIntType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val t = mandatoryArgs(0).asInstanceOf[ExpArg].t.asInstanceOf[SnapiRecordType]
    val idx = getIntValue(mandatoryArgs(1))
    if (idx >= 1 && idx <= t.atts.length) {
      Right(t.atts(idx - 1).tipe)
    } else {
      Left(s"field at index $idx not found")
    }
  }

}

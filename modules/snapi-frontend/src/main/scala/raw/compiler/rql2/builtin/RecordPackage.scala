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

import raw.client.api._
import raw.compiler.base.source.{AnythingType, Type}
import raw.compiler.common.source._
import raw.compiler.rql2._
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpArg, ExpParam, PackageExtension, Param, ValueParam}
import raw.compiler.rql2.source._
import raw.compiler.rql2.source.{Rql2RecordType, _}

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
      Rql2RecordType(
        optionalArgs.map { case (i, arg: ExpArg) => Rql2AttrType(i, arg.t) }.to
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
    val Rql2RecordType(atts1, props1) = mandatoryArgs(0).t
    val Rql2RecordType(atts2, props2) = mandatoryArgs(1).t
    Right(Rql2RecordType(atts1 ++ atts2, props1 ++ props2))
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
    Right(Rql2ListType(Rql2StringType()))
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
    val t = prevMandatoryArgs(0).asInstanceOf[ExpArg].t.asInstanceOf[Rql2RecordType]
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
    val Rql2RecordType(atts, _) = mandatoryArgs.head.t
    Right(Rql2RecordType(atts ++ optionalArgs.map(a => Rql2AttrType(a._1, a._2.t))))
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
      case 1 => Right(ValueParam(Rql2StringType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val t = mandatoryArgs(0).asInstanceOf[ExpArg].t.asInstanceOf[Rql2RecordType]
    val idn = getStringValue(mandatoryArgs(1))
    if (!t.atts.exists(att => att.idn == idn)) {
      Left(s"field $idn not found")
    } else {
      Right(Rql2RecordType(t.atts.filter(att => att.idn != idn)))
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
      case 1 => Right(ValueParam(Rql2IntType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val t = mandatoryArgs(0).asInstanceOf[ExpArg].t.asInstanceOf[Rql2RecordType]
    val idx = getIntValue(mandatoryArgs(1))
    if (idx >= 1 && idx <= t.atts.length) {
      Right(t.atts(idx - 1).tipe)
    } else {
      Left(s"field at index $idx not found")
    }
  }

}

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

import com.rawlabs.compiler.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import raw.compiler.base.source.Type
import raw.compiler.rql2._
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, ShortEntryExtension, TypeParam}
import raw.compiler.rql2.source._

class EnvironmentPackage extends PackageExtension {

  override def name: String = "Environment"

  override def docs: PackageDoc = PackageDoc("Library of functions to access environment properties.")

}

class EnvironmentSecretEntry
    extends ShortEntryExtension(
      "Environment",
      "Secret",
      Vector(Rql2StringType()),
      Rql2StringType(Set(Rql2IsTryableTypeProperty())),
      EntryDoc(
        summary = "Returns the value of a secret registered in the credentials service.",
        examples = List(ExampleDoc("""Environment.Secret("my-secret-credential")""", result = Some("secret value"))),
        params = List(
          ParamDoc("secretName", TypeDoc(List("string")), "Name of the secret.")
        ),
        ret = Some(ReturnDoc("The value of the secret.", Some(TypeDoc(List("string")))))
      )
    )

class EnvironmentScopesEntry
    extends ShortEntryExtension(
      "Environment",
      "Scopes",
      Vector(),
      Rql2ListType(Rql2StringType()),
      EntryDoc(
        summary = "Returns the scopes for the current user.",
        examples = List(ExampleDoc("""Environment.Scopes()""", result = Some("""["sales", "marketing"]"""))),
        ret = Some(ReturnDoc("The scopes for the current user.", Some(TypeDoc(List("list(string)")))))
      )
    )

class EnvironmentParameterEntry extends EntryExtension {

  override def packageName: String = "Environment"

  override def entryName: String = "Parameter"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(TypeParam(anything))
      case 1 => Right(ExpParam(string))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(resetProps(mandatoryArgs(0).t, Set.empty))
  }

}

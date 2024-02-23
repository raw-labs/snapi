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
import raw.compiler.rql2.api.{Arg, EntryExtension, ExpParam, PackageExtension, Param, TypeArg, TypeParam}
import raw.compiler.rql2.source._

class TypePackage extends PackageExtension {

  override def name: String = "Type"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions that apply to types."
  )

}

class TypeCastEntry extends EntryExtension {

  override def packageName: String = "Type"

  override def entryName: String = "Cast"

  override def docs: EntryDoc = EntryDoc(
    "Casts an expression to a specific type.",
    params = List(
      ParamDoc("type", typeDoc = TypeDoc(List("type")), "The type to cast to."),
      ParamDoc("expression", typeDoc = TypeDoc(List("anything")), "The expression to cast.")
    ),
    examples = List(ExampleDoc("Type.Cast(type double, 1)", result = Some("1.0"))),
    ret = Some(ReturnDoc("The casted expression.", None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(TypeParam(AnythingType()))
      case 1 => Right(ExpParam(prevMandatoryArgs(0).t))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(t) = mandatoryArgs(0)
    Right(t)
  }

}

// Internal node that performs `cast` but catches unexpected nulls/errors hit while processing
// the nested objects. In case of error: replaces the expression by a null/error.
class TypeProtectCastEntry extends EntryExtension {

  override def packageName: String = "Type"

  override def entryName: String = "ProtectCast"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(TypeParam(AnythingType())) // from
      case 1 => Right(TypeParam(AnythingType())) // to
      case 2 => Right(ExpParam(prevMandatoryArgs(1).t)) // type, required to use the ImplicitCasts phase
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(source) = mandatoryArgs(0)
    val TypeArg(target) = mandatoryArgs(1)
    // run a comparison of source and target. The 'source' type has nullables and tryables in place 'target' doesn't.
    // * when nullables are found, output type gets Rql2IsNullableProperty
    // * when tryables are found, output type gets Rql2IsTryableProperty
    //
    // Possibly both are found. Flags are added to the top of target (the one without the unexpected nested nullables/tryables)
    Right(addProps(target, extraProps(target, source)))
  }

  // cloned from rql2.SemanticAnalyzer
  // TODO (bgaidioz) share the code somewhere?
  private def extraProps(target: Type, source: Type) = {

    def recurse(target: Type, source: Type): Set[Rql2TypeProperty] = (target, source) match {
      case (Rql2ListType(inner1, props1), Rql2ListType(inner2, props2)) => recurse(inner1, inner2) ++ (props2 &~ props1)
      case (Rql2IterableType(_, props1), Rql2IterableType(_, props2)) =>
        // inner types aren't checked because iterables aren't consumed in the moment they're passed to
        // the function. No exception will be raised under ProtectCast regarding an iterable's items.
        props2 &~ props1
      case (Rql2RecordType(atts1, props1), Rql2RecordType(atts2, props2)) =>
        val tipes1 = atts1.map(_.tipe)
        val tipes2 = atts2.map(_.tipe)
        assert(tipes1.size == tipes2.size)
        tipes1.zip(tipes2).flatMap { case (t1, t2) => recurse(t1, t2) }.toSet ++ (props2 &~ props1)
      case (t1: Rql2TypeWithProperties, t2: Rql2TypeWithProperties) => t2.props &~ t1.props
    }
    recurse(target, source)
  }

}

// Internal node that performs `cast` but catches unexpected nulls/errors hit while processing
// the nested objects. In case of error: replaces the expression by a null/error.
class TypeCastAnyEntry extends EntryExtension {

  override def packageName: String = "Type"

  override def entryName: String = "CastAny"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(TypeParam(AnythingType())) // the target type
      case 1 => Right(ExpParam(Rql2AnyType())) // an 'any' object
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(target) = mandatoryArgs(0)
    Right(addProp(target, Rql2IsTryableTypeProperty())) // type as tryable of the target
  }

}

class TypeEmptyEntry extends EntryExtension {

  override def packageName: String = "Type"

  override def entryName: String = "Empty"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(TypeParam(AnythingType()))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(t) = mandatoryArgs(0)
    Right(t)
  }

}

class TypeMatchEntry extends EntryExtension {

  override def packageName: String = "Type"

  override def entryName: String = "Match"

  override def docs: EntryDoc = EntryDoc(
    summary = "Processes a value depending of its potential types",
    description = Some("""Type.Match takes as a parameter an or-type of type X or Y or Z,
      |and a set of functions that apply to X, Y or Z. All functions should
      |return results of compatible types.""".stripMargin),
    examples = List(ExampleDoc("""Type.Match(v,
      |   x: int -> x, // returns an int
      |   l: list(int) -> List.Sum(l) // returns an int
      |)""".stripMargin)),
    params = List(
      ParamDoc("v", TypeDoc(List("any")), "The value to process."),
      ParamDoc(
        "f",
        TypeDoc(List("function")),
        "A function that applies to one of the value's potential types.",
        isOptional = true,
        isVarArg = true
      )
    ),
    ret = Some(ReturnDoc("The result of the function that applies to the value's type.", None)),
    warning = Some("""Functions should not be null or an error.
      |If any of the functions is null or an error, Type.Match evaluates to null/error.""".stripMargin)
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2OrType(Vector(AnythingType()))))
  }
  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    val Rql2OrType(options, _) = prevMandatoryArgs(0).t
    if (idx == 0) Right(ExpParam(OneOfType(options.map(o => FunType(Vector(o), Vector.empty, AnythingType())))))
    else {
      val FunType(_, _, outputType, _) = prevVarArgs(0).t
      Right(ExpParam(OneOfType(options.map(o => FunType(Vector(o), Vector.empty, MergeableType(outputType))))))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val typesMerger = new TypesMerger
    val Rql2OrType(options, _) = mandatoryArgs(0).t
    val varTypes: Vector[Type] = varArgs.map(_.t).collect { case FunType(Vector(t), _, _, _) => t }.toVector
    val distincted = varTypes.groupBy(identity).mapValues(v => v.length)
    if (distincted.exists(_._2 >= 2)) return Left("only one handler function per type is expected")
    val paramTypes = distincted.keys.to[collection.mutable.Set]
    options.foreach { expected =>
      val matches = paramTypes.filter(actual => typesMerger.propertyCompatible(actual, expected))
      if (matches.isEmpty) return Left("handler functions should be provided for all types")
      paramTypes.remove(matches.head)
    }
    val outputType = {
      val outputTypes = varArgs.map { arg =>
        val FunType(_, _, rType, _) = arg.t
        rType
      }
      typesMerger.mergeType(outputTypes: _*).get
    }
    Right(outputType)
  }

}

//class TypeMergeEntry extends NonExecutableEntryExtension {
//
//  override def docs: EntryDoc = ???
//
//  override def nrMandatoryParams: Int = 2
//
//  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
//    idx match {
//      case 0 => Right(TypeParam(AnythingType()))
//      case 1 => Right(TypeParam(AnythingType()))
//    }
//  }
//
//  override def returnType(
//      mandatoryArgs: Seq[Arg],
//      optionalArgs: Seq[(String, Arg)],
//      varArgs: Seq[Arg]
//  ): Either[String, Type] = {
//    val TypeArg(t1) = mandatoryArgs(0)
//    val TypeArg(t2) = mandatoryArgs(1)
//    // TODO (msb): Refactor out the mergeType function of the RQL2 SemanticAnalyzer and call it from here to do the
//    // type merge and return ExpType...
//    ???
//  }
//
//}

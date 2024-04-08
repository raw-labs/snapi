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
import raw.compiler.base.errors.ErrorCompilerMessage
import raw.compiler.base.source.{AnythingType, BaseNode, Type}
import raw.compiler.common.source._
import raw.compiler.rql2._
import raw.compiler.rql2.api.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  StringValue,
  SugarEntryExtension,
  TypeArg,
  TypeParam,
  ValueArg,
  ValueParam
}
import raw.compiler.rql2.errors.{
  InvalidOrderSpec,
  ItemsNotComparable,
  KeyNotComparable,
  OrderSpecMustFollowOrderingFunction
}
import raw.compiler.rql2.source._

class ListPackage extends PackageExtension {

  override def name: String = "List"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the list type."
  )

}

class EmptyListEntry extends EntryExtension {

  override def packageName: String = "List"

  override def entryName: String = "Empty"

  override def docs: EntryDoc = EntryDoc(
    "Creates an empty list.",
    params = List(
      ParamDoc("type", typeDoc = TypeDoc(List("type")), description = "The type of the elements of the empty list.")
    ),
    examples = List(ExampleDoc("List.Empty(type int)")),
    ret = Some(ReturnDoc("The empty list.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 1

  override def hasVarArgs: Boolean = false

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(TypeParam(AnythingType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val TypeArg(itemType) = mandatoryArgs.head
    Right(Rql2ListType(itemType))
  }

}

class BuildListEntry extends EntryExtension {

  override def packageName: String = "List"

  override def entryName: String = "Build"

  override def docs: EntryDoc = EntryDoc(
    "Builds a new list.",
    params = List(ParamDoc("values", TypeDoc(List("anything")), "Values to add to the list.", isVarArg = true)),
    examples = List(ExampleDoc("""List.Build(1,2,3,4,5)""", result = Some("[1,2,3,4,5]"))),
    ret = Some(ReturnDoc("The new list.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 0

  override def hasVarArgs: Boolean = true

  override def getVarParam(
      prevMandatoryArgs: Seq[Arg],
      prevVarArgs: Seq[Arg],
      idx: Int
  ): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(AnythingType()))
      case _ =>
        val ExpArg(_, t) = prevVarArgs.head
        Right(ExpParam(MergeableType(t)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    if (varArgs.isEmpty) {
      Right(Rql2ListType(Rql2UndefinedType()))
    } else {
      val typesMerger = new TypesMerger
      val t = typesMerger.mergeType(varArgs.map(_.t): _*).get
      Right(Rql2ListType(t))
    }
  }

}

class GetListEntry extends EntryExtension {

  override def packageName: String = "List"

  override def entryName: String = "Get"

  override def docs: EntryDoc = EntryDoc(
    "Selects an element from a list by index.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list to select the element from."),
      ParamDoc("index", TypeDoc(List("int")), "The index (starting from zero) of the element to select.")
    ),
    examples = List(
      ExampleDoc("""List.Get(List.Build(1,2,3,4,5), 0)""", result = Some("1")),
      ExampleDoc("""List.Get(List.Build(1,2,3,4,5), 2)""", result = Some("3")),
      ExampleDoc("""List.Get(List.Build(1,2,3,4,5), 100)""", result = Some("index out of bounds"))
    ),
    info = Some("""Selecting an element out of bounds returns an "index out of bounds" error."""),
    ret = Some(ReturnDoc("The selected element.", retType = Some(TypeDoc(List("anything")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 => Right(ExpParam(Rql2IntType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2ListType(itemType, _)) = mandatoryArgs.head
    Right(addProp(itemType, Rql2IsTryableTypeProperty()))
  }

}

trait ListToCollectionHint { this: EntryExtension =>

  override def getMandatoryParamHint(
      prevMandatoryArgs: Seq[Arg],
      idx: Int,
      actual: Type,
      expected: Type
  ): Option[String] = {
    idx match {
      case 0 => actual match {
          case _: Rql2IterableType => Some(s"did you mean Collection.$entryName?")
          case _ => None
        }
      case _ => None
    }
  }

}

class FilterListEntry extends EntryExtension with PredicateNormalization with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Filter"

  override def docs: EntryDoc = EntryDoc(
    "Selects all elements of a list that satisfy a predicate.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list to filter."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate, which receives an element of a list and must return true/false whether the element is to be seleected or not."
      )
    ),
    examples = List(
      ExampleDoc(
        """List.Filter(
          |  List.Build(1,2,3),
          |  v -> v >= 2
          |)""".stripMargin,
        result = Some("List.Build(2, 3)")
      )
    ),
    ret = Some(ReturnDoc("The filtered list.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, g) = mandatoryArgs.head
    Right(g)
  }

}

class TransformListEntry extends EntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Transform"

  override def docs: EntryDoc = EntryDoc(
    "Builds a new list by applying a function to each element of a list.",
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list to read."),
      ParamDoc(
        "function",
        TypeDoc(List("function")),
        "The mapping function, which receives an element of the list and returns a new element for the new list."
      )
    ),
    examples = List(
      ExampleDoc(
        """List.Transform(
          |  List.Build(1,2,3),
          |  v -> v * 10
          |)""".stripMargin,
        result = Some("List.Build(10, 20, 30)")
      )
    ),
    ret = Some(ReturnDoc("The transformed list.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
    case 1 =>
      val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
      assert(props.isEmpty, "Should have been handled as per arg 0 definition")
      Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val FunType(_, _, outType, props) = mandatoryArgs(1).t
    assert(props.isEmpty, "Should have been handled as per arg 1 definition")
    Right(Rql2ListType(outType))
  }

}

class TakeListEntry extends EntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Take"

  override def docs: EntryDoc = EntryDoc(
    "Selects first N elements of a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to select the first N elements from."),
      ParamDoc("n", typeDoc = TypeDoc(List("int")), "The number of elements to select from the list.")
    ),
    examples = List(ExampleDoc("List.Take(List.Build(3, 1, 2), 3)", result = Some("List.Build(3, 1)"))),
    ret = Some(ReturnDoc("The list of the first N elements.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 => Right(ExpParam(Rql2LongType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, g) = mandatoryArgs.head
    Right(g)
  }

}

abstract class AggregationListEntry(aggregation: Aggregation) extends AggregationEntry(aggregation) {

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(Rql2ListType(aggregation.innerTypeConstraint)))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val Rql2ListType(innerType, _) = mandatoryArgs.head.t
    aggregation.aggregationType(innerType)
  }

}

class SumListEntry extends AggregationListEntry(SumAggregation) with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Sum"

  override def docs: EntryDoc = EntryDoc(
    "Sums up all elements of a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to sum elements from.")
    ),
    examples = List(ExampleDoc("List.Sum(List.Build(3, 1, 2))", result = Some("6"))),
    ret = Some(ReturnDoc("The sum of all elements in the list.", retType = Some(TypeDoc(List("int")))))
  )

}

class MaxListEntry extends AggregationListEntry(MaxAggregation) with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Max"

  override def docs: EntryDoc = EntryDoc(
    "Finds the largest element in a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to find the largest element from.")
    ),
    examples = List(ExampleDoc("List.Max(List.Build(2, 3, 1))", result = Some("3"))),
    ret = Some(ReturnDoc("The largest element in the list.", retType = Some(TypeDoc(List("number")))))
  )

}

class MinListEntry extends AggregationListEntry(MinAggregation) with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Min"

  override def docs: EntryDoc = EntryDoc(
    "Finds the smallest element in a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to find the smallest element from.")
    ),
    examples = List(ExampleDoc("List.Min(List.Build(3, 1, 2))", result = Some("1"))),
    ret = Some(ReturnDoc("The smallest element in the list.", retType = Some(TypeDoc(List("number")))))
  )

}

class FirstListEntry extends AggregationListEntry(FirstAggregation) with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "First"

  override def docs: EntryDoc = EntryDoc(
    "Selects the first element of a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to select the first element from.")
    ),
    examples = List(ExampleDoc("List.First(List.Build(2, 3, 1))", result = Some("2"))),
    ret = Some(ReturnDoc("The first element in the list.", retType = None))
  )

}

class FindFirstListEntry extends SugarEntryExtension with PredicateNormalization with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "FindFirst"

  override def docs: EntryDoc = EntryDoc(
    "Returns the first element of a list that satisfies a predicate.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate to apply to the elements."
      )
    ),
    examples = List(
      ExampleDoc(
        """List.FindFirst(
          |  List.Build(1,2,3),
          |  v -> v >= 2
          |)""".stripMargin,
        result = Some("2")
      )
    ),
    ret = Some(ReturnDoc("The first element in the list that satisfies the predicate.", retType = None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2ListType(itemType, _)) = mandatoryArgs.head
    Right(addProp(itemType, Rql2IsNullableTypeProperty()))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    ListPackageBuilder.First(ListPackageBuilder.Filter(args.head.e, args(1).e))
  }

}

class LastListEntry extends AggregationListEntry(LastAggregation) with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Last"

  override def docs: EntryDoc = EntryDoc(
    "Selects the last element of a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to select the last element from.")
    ),
    examples = List(ExampleDoc("List.Last(List.Build(3, 1, 2))", result = Some("2"))),
    ret = Some(ReturnDoc("The last element in the list.", retType = None))
  )

}

class FindLastListEntry extends SugarEntryExtension with PredicateNormalization with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "FindLast"

  override def docs: EntryDoc = EntryDoc(
    "Returns the last element of a list that satisfies a predicate.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate to apply to the elements."
      )
    ),
    examples = List(
      ExampleDoc(
        """List.FindLast(
          |  List.Build(1,2,3),
          |  v -> v <= 2
          |)""".stripMargin,
        result = Some("2")
      )
    ),
    ret = Some(ReturnDoc("The last element in the list that satisfies the predicate.", retType = None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2ListType(itemType, _)) = mandatoryArgs.head
    Right(addProp(itemType, Rql2IsNullableTypeProperty()))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    ListPackageBuilder.Last(ListPackageBuilder.Filter(args.head.e, args(1).e))
  }

}

class CountListEntry extends AggregationListEntry(CountAggregation) with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Count"

  override def docs: EntryDoc = EntryDoc(
    "Counts the number of elements in a list.",
    params = List(ParamDoc("list", TypeDoc(List("list")), "The list to count elements.")),
    examples = List(
      ExampleDoc(
        """List.Count(
          |  List.Build(1,2,3)
          |)""".stripMargin,
        result = Some("3")
      )
    ),
    ret = Some(ReturnDoc("The number of elements in the list.", retType = Some(TypeDoc(List("long")))))
  )

}

class ExplodeListEntry extends SugarEntryExtension with RecordMerging with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Explode"

  override def docs: EntryDoc = EntryDoc(
    "Moves elements of a nested list into elements of the parent list",
    params = List(
      ParamDoc(
        "list",
        typeDoc = TypeDoc(List("list")),
        "The list to explode elements to."
      ),
      ParamDoc("nested", typeDoc = TypeDoc(List("list")), "The list to explode elements from.")
    ),
    examples = List(
      ExampleDoc(
        """
          |let data = [
          |  {
          |    title: "Less than 2",
          |    numbers: [{v: 0}, {v: 1}]
          |  },
          |  {
          |    title: "More than 2",
          |    numbers: [{v: 3}, {v: 4}]
          |  }
          |]
          |in
          |  List.Explode(data, r -> r.numbers)""".stripMargin,
        result = Some("""[
          |  {
          |    title: "Less than 2",
          |    numbers: [{v: 0}, {v: 1}],
          |    v: 0,
          |  },
          |  {
          |    title: "Less than 2",
          |    numbers: [{v: 0}, {v: 1}],
          |    v: 1,
          |  },
          |  {
          |    title: "More than 2",
          |    numbers: [{v: 3}, {v: 4}],
          |    v: 3
          |  },
          |  {
          |    title: "More than 2",
          |    numbers: [{v: 3}, {v: 4}],
          |    v: 4
          |  }
          |]""".stripMargin)
      )
    ),
    ret = Some(ReturnDoc("The list with elements exploded.", retType = None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(
          ExpParam(
            FunType(
              Vector(innerType),
              Vector.empty,
              OneOfType(
                Rql2ListType(AnythingType()),
                Rql2ListType(AnythingType(), Set(Rql2IsNullableTypeProperty())),
                Rql2ListType(AnythingType(), Set(Rql2IsTryableTypeProperty())),
                Rql2ListType(AnythingType(), Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
              )
            )
          )
        )
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2ListType(leftRowType, props)) = mandatoryArgs.head
    val ExpArg(_, FunType(_, _, Rql2ListType(rightRowType, _), _)) = mandatoryArgs(1)
    val outRowType = rql2JoinOutputRowType(leftRowType, rightRowType)
    Right(Rql2ListType(outRowType, props))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val Rql2ListType(leftRowType, _) = mandatoryArgs.head.t
    val left = CollectionPackageBuilder.From(args.head.e)
    val unnestFunction = {
      val idn = IdnDef()
      FunAbs(
        FunProto(
          Vector(FunParam(idn, Some(leftRowType), None)),
          None,
          FunBody(CollectionPackageBuilder.From(FunApp(args(1).e, Vector(FunAppArg(IdnExp(idn), None)))))
        )
      )
    }
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.Explode(left, unnestFunction))
  }

}

class UnnestListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Unnest"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(
          ExpParam(
            FunType(
              Vector(innerType),
              Vector.empty,
              OneOfType(
                Rql2ListType(AnythingType()),
                Rql2ListType(AnythingType(), Set(Rql2IsNullableTypeProperty())),
                Rql2ListType(AnythingType(), Set(Rql2IsTryableTypeProperty())),
                Rql2ListType(AnythingType(), Set(Rql2IsNullableTypeProperty(), Rql2IsTryableTypeProperty()))
              )
            )
          )
        )
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val Rql2ListType(_, props) = mandatoryArgs.head.t
    assert(props.isEmpty, "Should have been handled as per arg 1 definition")
    val FunType(_, _, Rql2ListType(outputRowType, _), _) = mandatoryArgs(1).t
    Right(Rql2ListType(outputRowType))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val Rql2ListType(leftRowType, _) = mandatoryArgs.head.t
    val left = CollectionPackageBuilder.From(args.head.e)
    val unnestFunction = {
      val idn = IdnDef()
      FunAbs(
        FunProto(
          Vector(FunParam(idn, Some(leftRowType), None)),
          None,
          FunBody(CollectionPackageBuilder.From(FunApp(args(1).e, Vector(FunAppArg(IdnExp(idn), None)))))
        )
      )
    }
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.Unnest(left, unnestFunction))
  }

}

class FromListEntry extends EntryExtension {

  override def packageName: String = "List"

  override def entryName: String = "From"

  override def docs: EntryDoc = EntryDoc(
    "Builds a list from the items of a collection.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collection to build the list from.")
    ),
    examples = List(ExampleDoc("List.From(Collection.Build(1, 2, 3))", result = Some("List.Build(1, 2, 3)"))),
    ret = Some(ReturnDoc("The list built from the collection.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2IterableType(AnythingType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2IterableType(itemType, _)) = mandatoryArgs.head
    Right(Rql2ListType(itemType, Set(Rql2IsTryableTypeProperty())))
  }

}

class UnsafeFromListEntry extends EntryExtension {

  override def packageName: String = "List"

  override def entryName: String = "UnsafeFrom"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(Rql2IterableType(AnythingType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2IterableType(itemType, _)) = mandatoryArgs.head
    Right(Rql2ListType(itemType))
  }

}

class GroupListEntry extends EntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "GroupBy"

  override def docs: EntryDoc = EntryDoc(
    "Partitions the input list according to a key function.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to partition."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The partition function, which receives an elements of the list and returns the key to partition it by."
      )
    ),
    ret = Some(
      ReturnDoc(
        "A list of pairs, where the first element is the key and the second element is the list of elements with that key.",
        retType = Some(TypeDoc(List("list")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val listType = mandatoryArgs.head.t
    val ExpArg(keyFunction, FunType(_, _, keyType, props)) = mandatoryArgs(1)
    assert(props.isEmpty, "Should have been handled as per arg 1 definition")
    if (isComparable(keyType)) Right(
      Rql2ListType(
        Rql2RecordType(
          Vector(
            Rql2AttrType("key", keyType),
            Rql2AttrType("group", listType)
          )
        )
      )
    )
    else Left(Seq(KeyNotComparable(keyFunction)))
  }

}

class JoinListEntry
    extends SugarEntryExtension
    with PredicateNormalization
    with RecordMerging
    with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Join"

  override def docs: EntryDoc = EntryDoc(
    "Joins two lists given a join condition, into a list of records that includes the fields from both input lists.",
    params = List(
      ParamDoc("first", TypeDoc(List("list")), "The first list to join."),
      ParamDoc("second", TypeDoc(List("list")), "The second list to join."),
      ParamDoc(
        "condition",
        TypeDoc(List("function")),
        "The join condition function, which applies to a pair of elements, one from each list, and returns true if they should be joined."
      )
    ),
    examples = List(
      ExampleDoc(
        """let
          |  first = List.Build( {v: 1}, {v: 2}, {v: 3} ),
          |  second = List.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
          |in
          |  List.Join(first, second, (row1, row2) -> row1.v == row2.n)""".stripMargin,
        result = Some("""[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""")
      ),
      ExampleDoc(
        """// items of a list that isn't a list of records, appear in the joined record
          |// with an automatically generated field name (here: _1).
          |let
          |  first = [1,2,3],
          |  second = [{n: 1, name: "One"}, {n: 2, name: "Two"}]
          |in
          |  List.Join(first, second, (v, row2) -> v == row2.n)""".stripMargin,
        result = Some("""[ { _1: 1, n: 1, name: "One" }, { _1: 2, n: 2, name: "Two" } ]""")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A new list of records that includes the fields from both input lists.",
        retType = Some(TypeDoc(List("list")))
      )
    )
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 | 1 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 2 =>
        val ExpArg(_, Rql2ListType(leftRowType, _)) = prevMandatoryArgs.head
        val ExpArg(_, Rql2ListType(rightRowType, _)) = prevMandatoryArgs(1)
        val outType = rql2JoinOutputRowType(leftRowType, rightRowType)
        Right(ExpParam(OneOfType(flexiblePredicateOn(outType), flexiblePredicateOn(Vector(leftRowType, rightRowType)))))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, Rql2ListType(leftRowType, _)) = mandatoryArgs.head
    val ExpArg(_, Rql2ListType(rightRowType, _)) = mandatoryArgs(1)
    val outType = rql2JoinOutputRowType(leftRowType, rightRowType)
    Right(Rql2ListType(outType))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val left = CollectionPackageBuilder.From(args.head.e)
    val right = CollectionPackageBuilder.From(args(1).e)
    val predicate = args(2).e
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.Join(left, right, predicate))
  }

}

class EquiJoinListEntry extends SugarEntryExtension with RecordMerging with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "EquiJoin"

  override def docs: EntryDoc = EntryDoc(
    "Joins two lists with an equality condition.",
    params = List(
      ParamDoc("first", TypeDoc(List("list")), "The first list to join."),
      ParamDoc("second", TypeDoc(List("list")), "The second list to join."),
      ParamDoc(
        "firstKey",
        TypeDoc(List("function")),
        "The join condition function, which receives a row with elements from the first list and returns the key to perform the equality join condition on."
      ),
      ParamDoc(
        "secondKey",
        TypeDoc(List("function")),
        "The join condition function, which receives a row with elements from the second list and returns the key to perform the equality join condition on."
      )
    ),
    examples = List(
      ExampleDoc(
        """let
          |  first = [ {v: 1}, {v: 2}, {v: 3} ],
          |  second = [ {n: 1, name: "One"}, {n: 2, name: "Two"} ]
          |in
          |  List.EquiJoin(first, second, a -> a.v, b -> b.n)""".stripMargin,
        result = Some("""[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""")
      )
    ),
    ret = Some(
      ReturnDoc("A new list built from joining both lists.", retType = Some(TypeDoc(List("list"))))
    )
  )

  override def nrMandatoryParams: Int = 4

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 | 1 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 2 =>
        val ExpArg(_, Rql2ListType(innerType, _)) = prevMandatoryArgs.head
        Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
      case 3 =>
        val ExpArg(_, Rql2ListType(innerType, _)) = prevMandatoryArgs(1)
        val ExpArg(_, FunType(_, _, kType, _)) = prevMandatoryArgs(2)
        Right(ExpParam(FunType(Vector(innerType), Vector.empty, MergeableType(kType))))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val ExpArg(_, Rql2ListType(leftRowType, _)) = mandatoryArgs.head
    val ExpArg(_, Rql2ListType(rightRowType, _)) = mandatoryArgs(1)
    val ExpArg(keyFunction1, FunType(_, _, keyType1, _)) = mandatoryArgs(2)
    val ExpArg(keyFunction2, FunType(_, _, keyType2, _)) = mandatoryArgs(3)
    if (isComparable(keyType1)) {
      if (isComparable(keyType2)) {
        val mergedRecordType = rql2JoinOutputRowType(leftRowType, rightRowType)
        Right(Rql2ListType(mergedRecordType))
      } else Left(Seq(KeyNotComparable(keyFunction2)))
    } else Left(Seq(KeyNotComparable(keyFunction1)))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val left = CollectionPackageBuilder.From(args.head.e)
    val right = CollectionPackageBuilder.From(args(1).e)
    val leftKey = args(2).e
    val rightKey = args(3).e
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.EquiJoin(left, right, leftKey, rightKey))
  }
}

class OrderByListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "OrderBy"

  override def docs: EntryDoc = EntryDoc(
    "Orders this list according to the key functions and orderings passed as parameters.",
    params = List(
      ParamDoc("list", TypeDoc(List("list")), description = "The list to order."),
      ParamDoc(
        "key",
        TypeDoc(List("function")),
        description = "The key ordering function, which receives an element of the list and returns the key.",
        isVarArg = true
      ),
      ParamDoc("order", TypeDoc(List("string")), description = """The order: "ASC" or "DESC".""", isVarArg = true)
    ),
    examples = List(ExampleDoc("""List.OrderBy(movies, m -> m.country, "ASC", m -> m.budget, "DESC")"""")),
    ret = Some(ReturnDoc("The ordered list.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(Rql2ListType(AnythingType())))
  }

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
    assert(props.isEmpty, "Should have been handled as per arg 0 definition")
    if (idx % 2 == 0) Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
    else Right(ValueParam(Rql2StringType()))
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val (orders, keyFunctions) = varArgs.partition(_.t.isInstanceOf[Rql2StringType])
    if (orders.size != keyFunctions.size) return Left(Seq(OrderSpecMustFollowOrderingFunction(node)))
    val keyErrors = for (
      ExpArg(arg, FunType(_, _, keyType, _)) <- keyFunctions
      if !isComparable(keyType)
    ) yield KeyNotComparable(arg)
    val orderErrors = for (
      ValueArg(value @ StringValue(order), _) <- orders
      if !Set("ASC", "DESC").contains(order.toUpperCase)
    ) yield InvalidOrderSpec(node, order)
    val errors = keyErrors ++ orderErrors
    if (errors.isEmpty) Right(mandatoryArgs.head.t)
    else Left(errors)
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val inputList = args.head.e
    val orderingSpecs = args.tail.map(_.e)
    ListPackageBuilder.UnsafeFrom(
      CollectionPackageBuilder.OrderBy(CollectionPackageBuilder.From(inputList), orderingSpecs.toVector)
    )
  }

}

class DistinctListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Distinct"

  override def docs: EntryDoc = EntryDoc(
    "Removes duplicate elements of a list.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list to remove duplicate elements from.")
    ),
    examples = List(
      ExampleDoc(
        """List.Distinct(
          |  List.Build(1,2,2,3,3,3)
          |)""".stripMargin,
        result = Some("[1, 2, 3]")
      )
    ),
    ret = Some(ReturnDoc("The new list.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 1

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val ExpArg(list, Rql2ListType(itemType, _)) = mandatoryArgs.head
    if (isComparable(itemType)) Right(mandatoryArgs.head.t)
    else Left(Seq(ItemsNotComparable(list)))
  }

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = Right(ExpParam(list))

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.Distinct(CollectionPackageBuilder.From(args.head.e)))
  }
}

class UnionListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Union"

  override def docs: EntryDoc = EntryDoc(
    "Merge the input lists into one.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The lists to union.", isVarArg = true)
    ),
    examples = List(
      ExampleDoc("List.Union([1, 2, 3], [4, 5, 6])", result = Some("[1, 2, 3, 4, 5, 6]"))
    ),
    ret = Some(ReturnDoc("The union of the lists.", retType = Some(TypeDoc(List("list")))))
  )

  override def nrMandatoryParams: Int = 2

  override def hasVarArgs: Boolean = true

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, t) = prevMandatoryArgs.head
        Right(ExpParam(MergeableType(t)))
    }
  }

  override def getVarParam(
      prevMandatoryArgs: Seq[Arg],
      prevVarArgs: Seq[Arg],
      idx: Int
  ): Either[String, Param] = {
    val ExpArg(_, t) = prevMandatoryArgs.head
    Right(ExpParam(MergeableType(t)))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val args = mandatoryArgs ++ varArgs
    val typesMerger = new TypesMerger
    val t = typesMerger.mergeType(args.map(_.t): _*).get
    Right(t)
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val iterableArgs = args.map(a => CollectionPackageBuilder.From(a.e))
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.Union(iterableArgs.toVector))
  }

}

class AvgListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Avg"

  override def docs: EntryDoc = EntryDoc(
    "Computes the average value of a list of numbers.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to compute the average on.", isVarArg = true)
    ),
    examples = List(
      ExampleDoc(
        """List.Avg(List.Build(1,2,3,4,5))""",
        result = Some("3")
      )
    ),
    ret = Some(ReturnDoc("The average value of the list.", retType = Some(TypeDoc(List("decimal")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(Rql2ListType(AvgAggregation.innerTypeConstraint)))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2DecimalType(Set(Rql2IsTryableTypeProperty(), Rql2IsNullableTypeProperty())))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val sumAndCount = IdnDef()
    Let(
      Vector(LetBind(CollectionPackageBuilder.TupleAvg(CollectionPackageBuilder.From(args.head.e)), sumAndCount, None)),
      BinaryExp(Div(), Proj(IdnExp(sumAndCount), "sum"), Proj(IdnExp(sumAndCount), "count"))
    )
  }
}

class ExistsListEntry extends EntryExtension with PredicateNormalization with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Exists"

  override def docs: EntryDoc = EntryDoc(
    "Tests whether a predicate holds for at least one element of a list.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate."
      )
    ),
    examples = List(
      ExampleDoc(
        """List.Exists(
          |  List.Build(1,2,3),
          |  v -> v >= 2
          |) // true""".stripMargin,
        result = Some("true")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A boolean indicating whether the predicate holds for at least one element of the list.",
        retType = Some(TypeDoc(List("bool")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, props)) = prevMandatoryArgs.head
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2BoolType())
  }
}

class ContainsListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "Contains"

  override def docs: EntryDoc = EntryDoc(
    "Tests if some value is contained in a list.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "The list."),
      ParamDoc(
        "value",
        TypeDoc(List("anything")),
        "The value to search for."
      )
    ),
    examples = List(ExampleDoc("""List.Contains(Lust.Build(1,2,3), 2)""", result = Some("true"))),
    ret = Some(
      ReturnDoc("True if the value is contained in the list, false otherwise.", retType = Some(TypeDoc(List("bool"))))
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(Rql2ListType(AnythingType())))
      case 1 =>
        val ExpArg(_, Rql2ListType(innerType, _)) = prevMandatoryArgs.head
        Right(ExpParam(innerType))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val ExpArg(list, Rql2ListType(itemType, _)) = mandatoryArgs.head
    if (isComparable(itemType)) Right(Rql2BoolType())
    else Left(Seq(ItemsNotComparable(list)))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val list = args.head.e
    val predicate = {
      val x = IdnDef()
      FunAbs(FunProto(Vector(FunParam(x, None, None)), None, FunBody(BinaryExp(Eq(), IdnExp(x), args(1).e))))
    }
    ListPackageBuilder.Exists(list, predicate)
  }
}

class ZipListEntry extends SugarEntryExtension with CollectionToListHint {

  override def packageName: String = "List"

  override def entryName: String = "Zip"

  override def docs: EntryDoc = EntryDoc(
    "Turns two lists into one by combining their corresponding elements in pairs until the shortest list is exhausted.",
    None,
    params = List(
      ParamDoc("list1", TypeDoc(List("list")), "A list."),
      ParamDoc("list2", TypeDoc(List("list")), "A list.")
    ),
    examples = List(
      ExampleDoc(
        """List.Zip(
          |  List.Build(1,2,3),
          |  List.Build("a", "b", "c")
          |)""".stripMargin,
        result = Some("""List.Build({1, "a"}, {2, "b"}, {3, "c"})""")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A list of pairs of elements from the two lists.",
        retType = Some(TypeDoc(List("list")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(list))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val Rql2ListType(itemType1, _) = mandatoryArgs.head.t
    val Rql2ListType(itemType2, _) = mandatoryArgs(1).t
    Right(Rql2ListType(Rql2RecordType(Vector(Rql2AttrType("_1", itemType1), Rql2AttrType("_2", itemType2)))))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val it1 = CollectionPackageBuilder.From(args.head.e)
    val it2 = CollectionPackageBuilder.From(args(1).e)
    ListPackageBuilder.UnsafeFrom(CollectionPackageBuilder.Zip(it1, it2))
  }

}

class MkStringListEntry extends SugarEntryExtension with ListToCollectionHint {

  override def packageName: String = "List"

  override def entryName: String = "MkString"

  override def docs: EntryDoc = EntryDoc(
    "Concatenates all elements of a list in a string using start, end, and separator strings.",
    None,
    params = List(
      ParamDoc("list", TypeDoc(List("list")), "A list."),
      ParamDoc("start", TypeDoc(List("string")), "The starting string.", isOptional = true),
      ParamDoc("sep", TypeDoc(List("string")), "The separator string.", isOptional = true),
      ParamDoc("end", TypeDoc(List("string")), "The ending string.", isOptional = true)
    ),
    examples = List(
      ExampleDoc(
        """List.MkString(
          |  List.Build("a", "b", "c"),
          |  start="(", sep=":", end=")")"""".stripMargin,
        result = Some(""""(a:b:c)"""")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A string containing all elements of the list separated by the separator string.",
        retType = Some(TypeDoc(List("string")))
      )
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    if (idx == 0) Right(ExpParam(Rql2ListType(Rql2StringType(Set(Rql2IsNullableTypeProperty())))))
    else Right(ExpParam(Rql2StringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("start", "sep", "end"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(Rql2StringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(Rql2StringType(Set(Rql2IsTryableTypeProperty())))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    // Turn the list into a collection and call Collection.MkString.
    val collection = FunAppArg(CollectionPackageBuilder.From(args.head.e), None)
    val fixedArgs = collection +: args.tail
    FunApp(Proj(PackageIdnExp("Collection"), "MkString"), fixedArgs.toVector)
  }

}

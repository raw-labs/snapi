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

import com.rawlabs.snapi.frontend.api.{EntryDoc, ExampleDoc, PackageDoc, ParamDoc, ReturnDoc, TypeDoc}
import com.rawlabs.snapi.frontend.base.errors.{ErrorCompilerMessage, InvalidSemantic}
import com.rawlabs.snapi.frontend.base.source.{AnythingType, BaseNode, Type}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi._
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.extensions.{
  Arg,
  EntryExtension,
  ExpArg,
  ExpParam,
  PackageExtension,
  Param,
  SugarEntryExtension,
  TypeArg,
  TypeParam,
  ValueArg,
  ValueParam
}
import com.rawlabs.snapi.frontend.snapi.errors.{
  InvalidOrderSpec,
  ItemsNotComparable,
  KeyNotComparable,
  OrderSpecMustFollowOrderingFunction
}

object CollectionPackageBuilder {
  object Transform {
    def apply(x: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Transform"), Vector(FunAppArg(x, None), FunAppArg(f, None)))
    }
  }

  object Filter {
    def apply(x: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Filter"), Vector(FunAppArg(x, None), FunAppArg(f, None)))
    }
  }

  object From {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "From"), Vector(FunAppArg(x, None)))
    }
  }

  object TupleAvg {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "TupleAvg"), Vector(FunAppArg(x, None)))
    }
  }

  object First {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "First"), Vector(FunAppArg(x, None)))
    }
  }

  object Last {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Last"), Vector(FunAppArg(x, None)))
    }
  }

  object Union {
    def apply(collections: Vector[Exp]): Exp = {
      val args = collections.map(x => FunAppArg(x, None))
      FunApp(Proj(PackageIdnExp("Collection"), "Union"), args)
    }
  }

  object Exists {
    def apply(list: Exp, predicate: Exp) = {
      FunApp(Proj(PackageIdnExp("Collection"), "Exists"), Vector(FunAppArg(list, None), FunAppArg(predicate, None)))
    }
  }

  object Distinct {
    def apply(x: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Distinct"), Vector(FunAppArg(x, None)))
    }
  }

  object Empty {
    def apply(t: Type): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Empty"), Vector(FunAppArg(TypeExp(t), None)))
    }
  }

  object EquiJoin {
    def apply(left: Exp, right: Exp, leftK: Exp, rightK: Exp): Exp = {
      val args = Vector(left, right, leftK, rightK).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "EquiJoin"), args)
    }
  }

  object Join {
    def apply(left: Exp, right: Exp, filter: Exp): Exp = {
      val args = Vector(left, right, filter).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "Join"), args)
    }
  }

  object InternalEquiJoin {
    def apply(left: Exp, right: Exp, leftK: Exp, rightK: Exp, remapF: Exp): Exp = {
      val args = Vector(left, right, leftK, rightK, remapF).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "InternalEquiJoin"), args)
    }
  }

  object InternalJoin {
    def apply(left: Exp, right: Exp, remap: Exp, filter: Exp): Exp = {
      val args = Vector(left, right, remap, filter).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "InternalJoin"), args)
    }
  }

  object Explode {
    def apply(left: Exp, right: Exp): Exp = {
      val args = Vector(left, right).map(v => FunAppArg(v, None))
      FunApp(Proj(PackageIdnExp("Collection"), "Explode"), args)
    }
  }

  object Unnest {
    def apply(in: Exp, f: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Unnest"), Vector(FunAppArg(in, None), FunAppArg(f, None)))
    }
  }

  object OrderBy {
    def apply(c: Exp, keysAndOrders: Vector[Exp]): Exp = {
      val args = (c +: keysAndOrders).map(e => FunAppArg(e, None))
      FunApp(Proj(PackageIdnExp("Collection"), "OrderBy"), args)
    }
  }

  object Zip {
    def apply(list1: Exp, list2: Exp): Exp = {
      FunApp(Proj(PackageIdnExp("Collection"), "Zip"), Vector(FunAppArg(list1, None), FunAppArg(list2, None)))
    }
  }

}

class CollectionPackage extends PackageExtension {

  override def name: String = "Collection"

  override def docs: PackageDoc = PackageDoc(
    description = "Library of functions for the collection type."
  )

}

class EmptyCollectionEntry extends EntryExtension {

  override def packageName: String = "Collection"

  override def entryName: String = "Empty"

  override def docs: EntryDoc = EntryDoc(
    "Creates an empty collection.",
    params = List(
      ParamDoc(
        "type",
        typeDoc = TypeDoc(List("type")),
        description = "The type of the elements of the empty collection."
      )
    ),
    examples = List(ExampleDoc("Collection.Empty(type int)")),
    ret = Some(ReturnDoc("The empty collection.", retType = Some(TypeDoc(List("collection")))))
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
    val TypeArg(t) = mandatoryArgs(0)
    Right(SnapiIterableType(t))
  }

}

class BuildCollectionEntry extends EntryExtension {

  override def packageName: String = "Collection"

  override def entryName: String = "Build"

  override def docs: EntryDoc = EntryDoc(
    "Builds a new collection.",
    params = List(ParamDoc("values", TypeDoc(List("anything")), "Values to add to the collection.", isVarArg = true)),
    examples = List(ExampleDoc("""Collection.Build(1,2,3,4,5)""", result = Some("[1,2,3,4,5]"))),
    ret = Some(ReturnDoc("The new collection.", retType = Some(TypeDoc(List("collection")))))
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
        val ExpArg(_, t) = prevVarArgs(0)
        Right(ExpParam(MergeableType(t)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    if (varArgs.isEmpty) {
      Right(SnapiIterableType(SnapiUndefinedType()))
    } else {
      val typesMerger = new TypesMerger
      val t = typesMerger.mergeType(varArgs.map(_.t): _*).get
      Right(SnapiIterableType(t))
    }
  }

}

trait CollectionToListHint { this: EntryExtension =>

  override def getMandatoryParamHint(
      prevMandatoryArgs: Seq[Arg],
      idx: Int,
      actual: Type,
      expected: Type
  ): Option[String] = {
    idx match {
      case 0 => actual match {
          case _: SnapiListType => Some(s"did you mean List.$entryName?")
          case _ => None
        }
      case _ => None
    }
  }

}

class FilterCollectionEntry extends EntryExtension with PredicateNormalization with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Filter"

  override def docs: EntryDoc = EntryDoc(
    "Selects all elements of a collection that satisfy a predicate.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection to filter."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate, which receives an element of a collection and must return true/false whether the element is to be selected or not."
      )
    ),
    examples = List(
      ExampleDoc(
        """Collection.Filter(
          |  Collection.Build(1,2,3),
          |  v -> v >= 2
          |)""".stripMargin,
        result = Some("[2,3]")
      )
    ),
    ret = Some(ReturnDoc("The filtered collection.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def getMandatoryParamHint(
      prevMandatoryArgs: Seq[Arg],
      idx: Int,
      actual: Type,
      expected: Type
  ): Option[String] = {
    idx match {
      case 0 => actual match {
          case _: SnapiListType => Some("did you mean List.Filter?")
          case _ => None
        }
      case _ => None
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, g) = mandatoryArgs(0)
    Right(g)
  }

}

class AvgCollectionEntry extends SugarEntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Avg"

  override def docs: EntryDoc = EntryDoc(
    "Computes the average value of a collection of numbers.",
    params = List(
      ParamDoc(
        "collection",
        typeDoc = TypeDoc(List("collection")),
        "The collection to compute the average on.",
        isVarArg = true
      )
    ),
    examples = List(
      ExampleDoc(
        """Collection.Avg(Collection.Build(1,2,3,4,5))""",
        result = Some("3")
      )
    ),
    ret = Some(ReturnDoc("The average value of the collection.", retType = Some(TypeDoc(List("decimal")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] =
    Right(ExpParam(SnapiIterableType(AvgAggregation.innerTypeConstraint)))

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiDecimalType(Set(SnapiIsTryableTypeProperty(), SnapiIsNullableTypeProperty())))
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
      Vector(LetBind(CollectionPackageBuilder.TupleAvg(args(0).e), sumAndCount, None)),
      BinaryExp(Div(), Proj(IdnExp(sumAndCount), "sum"), Proj(IdnExp(sumAndCount), "count"))
    )
  }
}

class OrderByCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "OrderBy"

  override def docs: EntryDoc = EntryDoc(
    "Orders this collection according to the key functions and orderings passed as parameters.",
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), description = "The collection to order."),
      ParamDoc(
        "key",
        TypeDoc(List("function")),
        description = "The key ordering function, which receives an element of the collection and returns the key.",
        isVarArg = true
      ),
      ParamDoc("order", TypeDoc(List("string")), description = """The order: "ASC" or "DESC".""", isVarArg = true)
    ),
    examples = List(ExampleDoc("""Collection.OrderBy(movies, m -> m.country, "ASC", m -> m.budget, "DESC")""")),
    ret = Some(ReturnDoc("The ordered collection.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(SnapiIterableType(AnythingType())))
  }

  override def hasVarArgs: Boolean = true

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
    assert(props.isEmpty, "Should have been handled as per arg 0 definition")
    if (idx % 2 == 0) Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
    else Right(ValueParam(SnapiStringType()))
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val (orders, keyFunctions) = varArgs.partition(_.t.isInstanceOf[SnapiStringType])
    if (orders.size != keyFunctions.size) return Left(Seq(OrderSpecMustFollowOrderingFunction(node)))
    val keyErrors = for (
      ExpArg(arg, FunType(_, _, keyType, _)) <- keyFunctions;
      if !isComparable(keyType)
    ) yield KeyNotComparable(arg)
    val orderErrors = for (
      ValueArg(value @ SnapiStringValue(order), _) <- orders;
      if !Set("ASC", "DESC").contains(order.toUpperCase)
    ) yield InvalidOrderSpec(node, order)
    val errors = keyErrors ++ orderErrors
    if (errors.isEmpty) Right(mandatoryArgs(0).t)
    else Left(errors)
  }

}

class TransformCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Transform"

  override def docs: EntryDoc = EntryDoc(
    "Builds a new collection by applying a function to each element of a collection.",
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection to read."),
      ParamDoc(
        "function",
        TypeDoc(List("function")),
        "The mapping function, which receives an element of the collection and returns a new element for the new collection."
      )
    ),
    examples = List(
      ExampleDoc(
        """Collection.Transform(
          |  Collection.Build(1,2,3),
          |  v -> v * 10
          |)""".stripMargin,
        result = Some("[10, 20, 30]")
      )
    ),
    ret = Some(ReturnDoc("The new collection.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = idx match {
    case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
    case 1 =>
      val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
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
    Right(SnapiIterableType(outType))
  }

}

class DistinctCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Distinct"

  override def docs: EntryDoc = EntryDoc(
    "Removes duplicate elements of a collection.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection to remove duplicate elements from.")
    ),
    examples = List(
      ExampleDoc(
        """Collection.Distinct(
          |  Collection.Build(1,2,2,3,3,3)
          |)""".stripMargin,
        result = Some("[1, 2, 3]")
      )
    ),
    ret = Some(ReturnDoc("The new collection.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getVarParam(prevMandatoryArgs: Seq[Arg], prevVarArgs: Seq[Arg], idx: Int): Either[String, Param] =
    super.getVarParam(prevMandatoryArgs, prevVarArgs, idx)

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiIterableType(AnythingType())))
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val ExpArg(list, SnapiIterableType(itemType, _)) = mandatoryArgs(0)
    if (isComparable(itemType)) Right(mandatoryArgs(0).t)
    else Left(Seq(ItemsNotComparable(list)))
  }

}

abstract class AggregationCollectionEntry(aggregation: Aggregation) extends AggregationEntry(aggregation) {

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    assert(idx == 0)
    Right(ExpParam(SnapiIterableType(aggregation.innerTypeConstraint)))
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    returnType(mandatoryArgs, optionalArgs, varArgs).left.map { reason =>
      val ExpArg(arg, _) = mandatoryArgs(0)
      Seq(InvalidSemantic(arg, reason))
    }

  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val SnapiIterableType(innerType, _) = mandatoryArgs.head.t
    aggregation.aggregationType(innerType).right.map(t => addProp(t, SnapiIsTryableTypeProperty()))
  }

}

class CountCollectionEntry extends AggregationCollectionEntry(CountAggregation) with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Count"

  override def docs: EntryDoc = EntryDoc(
    "Counts the number of elements in a collection.",
    params = List(ParamDoc("collection", TypeDoc(List("collection")), "The collection to count elements.")),
    examples = List(
      ExampleDoc(
        """Collection.Count(
          |  Collection.Build(1,2,3)
          |)""".stripMargin,
        result = Some("3")
      )
    ),
    ret = Some(ReturnDoc("The number of elements in the collection.", retType = Some(TypeDoc(List("long")))))
  )

}

class TupleAvgCollectionEntry extends AggregationCollectionEntry(AvgAggregation) {

  override def packageName: String = "Collection"

  override def entryName: String = "TupleAvg"

  override def docs: EntryDoc = ???

}

class MinCollectionEntry extends AggregationCollectionEntry(MinAggregation) with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Min"

  override def docs: EntryDoc = EntryDoc(
    "Finds the smallest element in a collection.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collection to find the smallest element from.")
    ),
    examples = List(ExampleDoc("Collection.Min(Collection.Build(3, 1, 2))", result = Some("1"))),
    ret = Some(ReturnDoc("The smallest element in the collection.", retType = Some(TypeDoc(List("number")))))
  )

}

class MaxCollectionEntry extends AggregationCollectionEntry(MaxAggregation) with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Max"

  override def docs: EntryDoc = EntryDoc(
    "Finds the largest element in a collection.",
    params = List(
      ParamDoc(
        "collection",
        typeDoc = TypeDoc(List("collection")),
        "The collection to find the largest element from."
      )
    ),
    examples = List(ExampleDoc("Collection.Max(Collection.Build(2, 3, 1))", result = Some("3"))),
    ret = Some(ReturnDoc("The largest element in the collection.", retType = Some(TypeDoc(List("number")))))
  )

}

class SumCollectionEntry extends AggregationCollectionEntry(SumAggregation) with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Sum"

  override def docs: EntryDoc = EntryDoc(
    "Sums all elements of a collection.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collection to sum elements from.")
    ),
    examples = List(ExampleDoc("Collection.Sum(Collection.Build(3, 1, 2))", result = Some("6"))),
    ret = Some(ReturnDoc("The sum of all elements in the collection.", retType = Some(TypeDoc(List("number")))))
  )

}

class FirstCollectionEntry extends AggregationCollectionEntry(FirstAggregation) with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "First"

  override def docs: EntryDoc = EntryDoc(
    "Selects the first element of a collection.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collection to select the first element from.")
    ),
    examples = List(ExampleDoc("Collection.First(Collection.Build(2, 3, 1))", result = Some("2"))),
    ret = Some(ReturnDoc("The first element in the collection.", retType = Some(TypeDoc(List("number")))))
  )

}

class FindFirstCollectionEntry extends SugarEntryExtension with PredicateNormalization with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "FindFirst"

  override def docs: EntryDoc = EntryDoc(
    "Returns the first element of a collection that satisfies a predicate.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate to apply to the elements."
      )
    ),
    examples = List(
      ExampleDoc(
        """Collection.FindFirst(
          |  Collection.Build(1,2,3),
          |  v -> v >= 2
          |)""".stripMargin,
        result = Some("2")
      )
    ),
    ret = Some(ReturnDoc("The first element in the collection that satisfies the predicate.", None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, SnapiIterableType(itemType, _)) = mandatoryArgs(0)
    Right(addProps(itemType, Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty())))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    CollectionPackageBuilder.First(CollectionPackageBuilder.Filter(args(0).e, args(1).e))
  }

}

class LastCollectionEntry extends AggregationCollectionEntry(LastAggregation) with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Last"

  override def docs: EntryDoc = EntryDoc(
    "Selects the last element of a collection.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collection to select the last element from.")
    ),
    examples = List(ExampleDoc("Collection.Last(Collection.Build(3, 1, 2))", result = Some("2"))),
    ret = Some(ReturnDoc("The last element in the collection.", None))
  )
}

class FindLastCollectionEntry extends SugarEntryExtension with PredicateNormalization with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "FindLast"

  override def docs: EntryDoc = EntryDoc(
    "Returns the last element of a collection that satisfies a predicate.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate to apply to the elements."
      )
    ),
    examples = List(
      ExampleDoc(
        """Collection.FindLast(
          |  Collection.Build(1,2,3),
          |  v -> v <= 2
          |)""".stripMargin,
        result = Some("2")
      )
    ),
    ret = Some(ReturnDoc("The last element in the collection that satisfies the predicate.", None))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(ExpParam(flexiblePredicateOn(innerType)))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, SnapiIterableType(itemType, _)) = mandatoryArgs(0)
    Right(addProps(itemType, Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty())))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    CollectionPackageBuilder.Last(CollectionPackageBuilder.Filter(args(0).e, args(1).e))
  }

}

class TakeCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Take"

  override def docs: EntryDoc = EntryDoc(
    "Selects first N elements of a collection.",
    params = List(
      ParamDoc(
        "collection",
        typeDoc = TypeDoc(List("collection")),
        "The collection to select the first N elements from."
      ),
      ParamDoc("n", typeDoc = TypeDoc(List("int")), "The number of elements to select from the collection.")
    ),
    examples = List(ExampleDoc("Collection.Take(Collection.Build(3, 1, 2), 3)", result = Some("[3, 1]"))),
    ret = Some(ReturnDoc("The first N elements in the collection.", Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 => Right(ExpParam(SnapiLongType()))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, g) = mandatoryArgs(0)
    Right(g)
  }

}

class ExplodeCollectionEntry extends SugarEntryExtension with RecordMerging with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Explode"

  /**
   * Documentation.
   */
  override def docs: EntryDoc = EntryDoc(
    "Moves elements of a nested collection into elements of the parent collection.",
    params = List(
      ParamDoc(
        "collection",
        typeDoc = TypeDoc(List("collection")),
        "The collection to explode elements to."
      ),
      ParamDoc("nested", typeDoc = TypeDoc(List("collection")), "The collection to explode elements from.")
    ),
    examples = List(
      ExampleDoc(
        """// Suppose you have the following JSON data:
          |// [
          |//   {
          |//     "title": "Less than 2",
          |//     "numbers": [{"v": 0}, {"v": 1}]
          |//   },
          |//   {
          |//     "title": "More than 2",
          |//     "numbers": [{"v": 3}, {"v": 4}]
          |//   }
          |// ]
          |//
          |let data = Json.Read("example.json")
          |in
          |  Collection.Explode(data, r -> r.numbers)""".stripMargin,
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
    ret = Some(
      ReturnDoc(
        "The collection with elements from the nested collection exploded into elements of the parent collection.",
        Some(TypeDoc(List("collection")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(
          ExpParam(
            FunType(
              Vector(innerType),
              Vector.empty,
              OneOfType(
                SnapiIterableType(AnythingType()),
                SnapiIterableType(AnythingType(), Set(SnapiIsNullableTypeProperty())),
                SnapiIterableType(AnythingType(), Set(SnapiIsTryableTypeProperty())),
                SnapiIterableType(AnythingType(), Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty()))
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
    val ExpArg(_, SnapiIterableType(leftRowType, props)) = mandatoryArgs(0)
    val ExpArg(_, FunType(_, _, SnapiIterableType(rightRowType, _), _)) = mandatoryArgs(1)
    val outRowType = snapiJoinOutputRowType(leftRowType, rightRowType)
    Right(SnapiIterableType(outRowType, props))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val ExpArg(in, SnapiIterableType(leftRowType, _)) = mandatoryArgs(0)
    val ExpArg(f, FunType(_, _, SnapiIterableType(rightRowType, _), _)) = mandatoryArgs(1)
    val leftIdn = IdnDef()
    val unnestedMerge = {
      val rightIdn = IdnDef()
      val userSpecifiedCollection = FunApp(f, Vector(FunAppArg(IdnExp(leftIdn), None)))
      CollectionPackageBuilder.Transform(
        userSpecifiedCollection,
        FunAbs(
          FunProto(
            Vector(FunParam(rightIdn, None, None)),
            None,
            FunBody(snapiJoinRowsConcatenation(IdnExp(leftIdn), leftRowType, IdnExp(rightIdn), rightRowType))
          )
        )
      )
    }
    val unnestFunction = FunAbs(FunProto(Vector(FunParam(leftIdn, None, None)), None, FunBody(unnestedMerge)))
    CollectionPackageBuilder.Unnest(in, unnestFunction)
  }

}

class UnnestCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Unnest"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(
          ExpParam(
            FunType(
              Vector(innerType),
              Vector.empty,
              OneOfType(
                SnapiIterableType(AnythingType()),
                SnapiIterableType(AnythingType(), Set(SnapiIsNullableTypeProperty())),
                SnapiIterableType(AnythingType(), Set(SnapiIsTryableTypeProperty())),
                SnapiIterableType(AnythingType(), Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty()))
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
    val SnapiIterableType(_, props) = mandatoryArgs(0).t
    assert(props.isEmpty, "Should have been handled as per arg 1 definition")
    val FunType(_, _, SnapiIterableType(outputRowType, _), _) = mandatoryArgs(1).t
    Right(SnapiIterableType(outputRowType))
  }

}

class FromCollectionEntry extends EntryExtension {

  override def packageName: String = "Collection"

  override def entryName: String = "From"

  override def docs: EntryDoc = EntryDoc(
    "Builds a collection from the items of a list.",
    params = List(
      ParamDoc("list", typeDoc = TypeDoc(List("list")), "The list to build the collection from.")
    ),
    examples = List(ExampleDoc("Collection.FromList(List.Build(1, 2, 3))", result = Some("Collection.Build(1, 2, 3)"))),
    ret = Some(ReturnDoc("The collection built from the list.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiListType(AnythingType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, SnapiListType(itemType, _)) = mandatoryArgs(0)
    Right(SnapiIterableType(itemType))
  }

}

class GroupCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "GroupBy"

  override def docs: EntryDoc = EntryDoc(
    "Partitions the input collection according to a key function.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collection to partition."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The partition function, which receives an elements of the collection and returns the key to partition it by."
      )
    ),
    ret = Some(
      ReturnDoc(
        "A collection of pairs, where the first element is the key and the second element is the collection of elements with that key.",
        retType = Some(TypeDoc(List("collection")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
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
    val listType = mandatoryArgs(0).t
    val ExpArg(keyFunction, FunType(_, _, keyType, props)) = mandatoryArgs(1)
    assert(props.isEmpty, "Should have been handled as per arg 1 definition")
    if (isComparable(keyType)) Right(
      SnapiIterableType(
        SnapiRecordType(
          Vector(
            SnapiAttrType("key", keyType),
            SnapiAttrType("group", listType)
          )
        )
      )
    )
    else Left(Seq(KeyNotComparable(keyFunction)))
  }

}

class JoinCollectionEntry
    extends SugarEntryExtension
    with PredicateNormalization
    with RecordMerging
    with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Join"

  override def docs: EntryDoc = EntryDoc(
    "Joins two collections given a join condition, into a collection of records that includes the fields from both input collections.",
    params = List(
      ParamDoc("first", TypeDoc(List("collection")), "The first collection to join."),
      ParamDoc("second", TypeDoc(List("collection")), "The second collection to join."),
      ParamDoc(
        "condition",
        TypeDoc(List("function")),
        "The join condition function, which applies to a pair of elements, one from each collection, and returns true if they should be joined."
      )
    ),
    examples = List(
      ExampleDoc(
        """let
          |  first = Collection.Build( {v: 1}, {v: 2}, {v: 3} ),
          |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
          |in
          |  Collection.Join(first, second, (row1, row2) -> row1.v == row2.n)""".stripMargin,
        result = Some("""[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""")
      ),
      ExampleDoc(
        """// items of a collection that isn't a collection of records, appear in the joined record
          |// with an automatically generated field name (here: _1).
          |let
          |  first = Collection.Build(1,2,3),
          |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
          |in
          |  Collection.Join(first, second, (v, row2) -> v == row2.n)""".stripMargin,
        result = Some("""[ { _1: 1, n: 1, name: "One" }, { _1: 2, n: 2, name: "Two" } ]""")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A new collection of records that includes the fields from both input collections.",
        retType = Some(TypeDoc(List("collection")))
      )
    )
  )

  override def nrMandatoryParams: Int = 3

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 | 1 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 2 =>
        val ExpArg(_, SnapiIterableType(leftRowType, _)) = prevMandatoryArgs(0)
        val ExpArg(_, SnapiIterableType(rightRowType, _)) = prevMandatoryArgs(1)
        val outType = snapiJoinOutputRowType(leftRowType, rightRowType)
        Right(ExpParam(OneOfType(flexiblePredicateOn(outType), flexiblePredicateOn(Vector(leftRowType, rightRowType)))))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, SnapiIterableType(leftRowType, _)) = mandatoryArgs(0)
    val ExpArg(_, SnapiIterableType(rightRowType, _)) = mandatoryArgs(1)
    val outType = snapiJoinOutputRowType(leftRowType, rightRowType)
    Right(SnapiIterableType(outType))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val remapF = {
      val SnapiIterableType(leftRowType, _) = mandatoryArgs(0).t
      val SnapiIterableType(rightRowType, _) = mandatoryArgs(1).t
      val leftIdn = IdnDef()
      val rightIdn = IdnDef()
      val concatenation = snapiJoinRowsConcatenation(IdnExp(leftIdn), leftRowType, IdnExp(rightIdn), rightRowType)
      FunAbs(
        FunProto(
          Vector(FunParam(leftIdn, None, None), FunParam(rightIdn, None, None)),
          None,
          FunBody(concatenation)
        )
      )
    }
    CollectionPackageBuilder.InternalJoin(args(0).e, args(1).e, remapF, args(2).e)
  }

}

class InternalJoinCollectionEntry extends EntryExtension with PredicateNormalization with RecordMerging {

  override def packageName: String = "Collection"

  override def entryName: String = "InternalJoin"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 4

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 | 1 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 2 =>
        val ExpArg(_, SnapiIterableType(leftRowType, _)) = prevMandatoryArgs(0)
        val ExpArg(_, SnapiIterableType(rightRowType, _)) = prevMandatoryArgs(1)
        Right(ExpParam(FunType(Vector(leftRowType, rightRowType), Vector.empty, AnythingType())))
      case 3 =>
        val ExpArg(_, SnapiIterableType(leftRowType, _)) = prevMandatoryArgs(0)
        val ExpArg(_, SnapiIterableType(rightRowType, _)) = prevMandatoryArgs(1)
        val ExpArg(_, FunType(_, _, outType, _)) = prevMandatoryArgs(2)
        Right(ExpParam(OneOfType(flexiblePredicateOn(outType), flexiblePredicateOn(Vector(leftRowType, rightRowType)))))
    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, FunType(_, _, outType, _)) = mandatoryArgs(2)
    Right(SnapiIterableType(outType))
  }

}

class EquiJoinCollectionEntry extends SugarEntryExtension with RecordMerging with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "EquiJoin"

  override def docs: EntryDoc = EntryDoc(
    "Joins two collections with an equality condition.",
    params = List(
      ParamDoc("first", TypeDoc(List("collection")), "The first collection to join."),
      ParamDoc("second", TypeDoc(List("collection")), "The second collection to join."),
      ParamDoc(
        "firstKey",
        TypeDoc(List("function")),
        "The join condition function, which receives a row with elements from the first collection and returns the key to perform the equality join condition on."
      ),
      ParamDoc(
        "secondKey",
        TypeDoc(List("function")),
        "The join condition function, which receives a row with elements from the second collection and returns the key to perform the equality join condition on."
      )
    ),
    info = Some("`EquiJoin` is a more efficient and scalable than `Join`, so use it when possible."),
    examples = List(
      ExampleDoc(
        """let
          |  first = Collection.Build( {v: 1}, {v: 2}, {v: 3} ),
          |  second = Collection.Build( {n: 1, name: "One"}, {n: 2, name: "Two"} )
          |in
          |  Collection.EquiJoin(first, second, a -> a.v, b -> b.n)""".stripMargin,
        result = Some("""[ { v: 1, n: 1, name: "One" }, { v: 2, n: 2, name: "Two" } ]""")
      )
    ),
    ret = Some(
      ReturnDoc("A new collection built from joining both collections.", retType = Some(TypeDoc(List("collection"))))
    )
  )

  override def nrMandatoryParams: Int = 4

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 | 1 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 2 =>
        val ExpArg(_, SnapiIterableType(innerType, _)) = prevMandatoryArgs(0)
        Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
      case 3 =>
        val ExpArg(_, SnapiIterableType(innerType, _)) = prevMandatoryArgs(1)
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
    val ExpArg(_, SnapiIterableType(leftRowType, _)) = mandatoryArgs(0)
    val ExpArg(_, SnapiIterableType(rightRowType, _)) = mandatoryArgs(1)
    val ExpArg(keyFunction1, FunType(_, _, keyType1, _)) = mandatoryArgs(2)
    val ExpArg(keyFunction2, FunType(_, _, keyType2, _)) = mandatoryArgs(3)
    if (isComparable(keyType1)) {
      if (isComparable(keyType2)) {
        val mergedRecordType = snapiJoinOutputRowType(leftRowType, rightRowType)
        Right(SnapiIterableType(mergedRecordType))
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
    val SnapiIterableType(leftRowType, _) = mandatoryArgs(0).t
    val SnapiIterableType(rightRowType, _) = mandatoryArgs(1).t
    val remapF = {
      val leftIdn = IdnDef()
      val rightIdn = IdnDef()
      val concatenation = snapiJoinRowsConcatenation(IdnExp(leftIdn), leftRowType, IdnExp(rightIdn), rightRowType)
      FunAbs(
        FunProto(
          Vector(FunParam(leftIdn, None, None), FunParam(rightIdn, None, None)),
          None,
          FunBody(concatenation)
        )
      )
    }
    val FunType(_, _, commonKeyType, _) = mandatoryArgs(3).t // rightKey, because it got merged with leftKey
    val leftKFunction = castKeyF(mandatoryArgs(2), commonKeyType)
    val rightKFunction = castKeyF(mandatoryArgs(3), commonKeyType)
    CollectionPackageBuilder.InternalEquiJoin(
      args(0).e,
      args(1).e,
      leftKFunction,
      rightKFunction,
      remapF
    )
  }

  private def castKeyF(arg: Arg, targetKeyType: Type) = {
    {
      val ExpArg(f, _) = arg
      val idn = IdnDef()
      FunAbs(
        FunProto(
          Vector(FunParam(idn, None, None)),
          None,
          FunBody(TypePackageBuilder.Cast(targetKeyType, FunApp(f, Vector(FunAppArg(IdnExp(idn), None)))))
        )
      )
    }
  }
}

class InternalEquiJoinCollectionEntry extends EntryExtension with RecordMerging {

  override def packageName: String = "Collection"

  override def entryName: String = "InternalEquiJoin"

  override def docs: EntryDoc = ???

  override def nrMandatoryParams: Int = 5

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 | 1 =>
        // input datasets
        Right(ExpParam(SnapiIterableType(AnythingType())))
      case 2 =>
        // left key function
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        Right(ExpParam(FunType(Vector(innerType), Vector.empty, AnythingType())))
      case 3 =>
        // right key function (output type = left key function output type)
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(1)
        val ExpArg(_, FunType(_, _, kType, _)) = prevMandatoryArgs(2)
        Right(ExpParam(FunType(Vector(innerType), Vector.empty, kType)))
      case 4 =>
        // remap function (in general concatenation of the two rows)
        val ExpArg(_, SnapiIterableType(leftRowType, _)) = prevMandatoryArgs(0)
        val ExpArg(_, SnapiIterableType(rightRowType, _)) = prevMandatoryArgs(1)
        Right(ExpParam(FunType(Vector(leftRowType, rightRowType), Vector.empty, AnythingType())))

    }
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val ExpArg(_, FunType(_, _, remapType, _)) = mandatoryArgs(4)
    Right(SnapiIterableType(remapType))
  }

}

class UnionCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Union"

  override def docs: EntryDoc = EntryDoc(
    "Merge, i.e. union, the input collections into one.",
    params = List(
      ParamDoc("collection", typeDoc = TypeDoc(List("collection")), "The collections to union.", isVarArg = true)
    ),
    examples = List(
      ExampleDoc("Collection.Union([1, 2, 3], [4, 5, 6])", result = Some("[1, 2, 3, 4, 5, 6]"))
    ),
    ret = Some(ReturnDoc("The union of the collections.", retType = Some(TypeDoc(List("collection")))))
  )

  override def nrMandatoryParams: Int = 2

  override def hasVarArgs: Boolean = true

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(rowType, _)) = prevMandatoryArgs(0)
        Right(ExpParam(SnapiIterableType(MergeableType(rowType))))
    }
  }

  override def getVarParam(
      prevMandatoryArgs: Seq[Arg],
      prevVarArgs: Seq[Arg],
      idx: Int
  ): Either[String, Param] = {
    val ExpArg(_, SnapiIterableType(rowType, _)) = prevMandatoryArgs(0)
    Right(ExpParam(SnapiIterableType(MergeableType(rowType))))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val argTypes = (mandatoryArgs ++ varArgs).map(_.t)
    val typesMerger = new TypesMerger
    val t = typesMerger.mergeType(argTypes: _*).get
    Right(t)
  }

}

trait RecordMerging {

  protected def snapiJoinRowsConcatenation(leftRow: Exp, leftRowType: Type, rightRow: Exp, rightRowType: Type): Exp = {
    def merge(leftRowType: Type, rightRowType: Type): Exp = (leftRowType, rightRowType) match {
      case (_: SnapiRecordType, _: SnapiRecordType) =>
        // Two records, concatenate the fields
        RecordPackageBuilder.Concat(leftRow, rightRow)
      case (rec: SnapiRecordType, _) =>
        // right isn't a record. Its value goes as rightmost field.
        val SnapiRecordType(atts, _) = snapiJoinOutputRowType(leftRowType, rightRowType)
        val idn = atts.last.idn
        RecordPackageBuilder.AddField(leftRow, rightRow, idn)
      case (_, rec: SnapiRecordType) =>
        // left isn't a record. Its value goes are leftmost field.
        val SnapiRecordType(atts, _) = snapiJoinOutputRowType(leftRowType, rightRowType)
        val idn = atts.head.idn
        RecordPackageBuilder.Concat(RecordPackageBuilder.Build(Vector((idn, leftRow))), rightRow)
      case _ =>
        // none are records: make a tuple (_1, _2)
        RecordPackageBuilder.Build(Vector(("_1", leftRow), ("_2", rightRow)))
    }
    merge(leftRowType, rightRowType)
  }
  protected def snapiJoinOutputRowType(leftRowType: Type, rightRowType: Type): SnapiRecordType =
    (leftRowType, rightRowType) match {
      case (SnapiRecordType(leftAtts, leftProps), SnapiRecordType(rightAtts, rightProps)) =>
        // both are records, merge attributes and properties (how Record.Concat behaves)
        SnapiRecordType(leftAtts ++ rightAtts, leftProps ++ rightProps)
      case (SnapiRecordType(atts, props), _) =>
        val others = atts.map(_.idn).toSet
        val idn = (1 to (others.size + 1)).map("_" + _).find(!others.contains(_)).head
        SnapiRecordType(atts :+ SnapiAttrType(idn, rightRowType), props)
      case (_, SnapiRecordType(atts, props)) =>
        // left isn't a record. Add it as a front extra column
        val others = atts.map(_.idn).toSet
        val idn = (1 to (others.size + 1)).map("_" + _).find(!others.contains(_)).head
        SnapiRecordType(SnapiAttrType(idn, leftRowType) +: atts, props)
      case _ => SnapiRecordType(Vector(SnapiAttrType("_1", leftRowType), SnapiAttrType("_2", rightRowType)))
    }

}

trait PredicateNormalization {

  protected def flexiblePredicateOn(outTypes: Vector[Type]): FunType = FunType(
    outTypes,
    Vector.empty,
    OneOfType(
      SnapiBoolType(),
      SnapiBoolType(Set(SnapiIsNullableTypeProperty())),
      SnapiBoolType(Set(SnapiIsTryableTypeProperty())),
      SnapiBoolType(Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty()))
    )
  )

  protected def flexiblePredicateOn(outType: Type): FunType = flexiblePredicateOn(Vector(outType))

}
class ExistsCollectionEntry extends EntryExtension with PredicateNormalization with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Exists"

  override def docs: EntryDoc = EntryDoc(
    "Tests whether a predicate holds for at least one element of a collection.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection."),
      ParamDoc(
        "predicate",
        TypeDoc(List("function")),
        "The function predicate."
      )
    ),
    examples = List(
      ExampleDoc(
        """Collection.Exists(
          |  Collection.Build(1,2,3),
          |  v -> v >= 2
          |)""".stripMargin,
        result = Some("true")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A boolean indicating whether the predicate holds for at least one element of the collection.",
        retType = Some(TypeDoc(List("bool")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, props)) = prevMandatoryArgs(0)
        assert(props.isEmpty, "Should have been handled as per arg 0 definition")
        Right(
          ExpParam(
            FunType(
              Vector(innerType),
              Vector.empty,
              OneOfType(
                SnapiBoolType(),
                SnapiBoolType(Set(SnapiIsNullableTypeProperty())),
                SnapiBoolType(Set(SnapiIsTryableTypeProperty())),
                SnapiBoolType(Set(SnapiIsNullableTypeProperty(), SnapiIsTryableTypeProperty()))
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
    Right(SnapiBoolType(Set(SnapiIsTryableTypeProperty())))
  }

}

class ContainsCollectionEntry extends SugarEntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Contains"

  override def docs: EntryDoc = EntryDoc(
    "Tests if some value is contained in a collection.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "The collection."),
      ParamDoc(
        "value",
        TypeDoc(List("anything")),
        "The value to search for."
      )
    ),
    examples = List(ExampleDoc("""Collection.Contains(Collection.Build(1,2,3), 2)""", result = Some("true"))),
    ret = Some(
      ReturnDoc(
        "True if the value is contained in the collection, false otherwise.",
        retType = Some(TypeDoc(List("bool")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    idx match {
      case 0 => Right(ExpParam(SnapiIterableType(AnythingType())))
      case 1 =>
        val ExpArg(_, SnapiIterableType(innerType, _)) = prevMandatoryArgs(0)
        Right(ExpParam(innerType))
    }
  }

  override def returnTypeErrorList(
      node: BaseNode,
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[Seq[ErrorCompilerMessage], Type] = {
    val ExpArg(items, SnapiIterableType(itemType, _)) = mandatoryArgs(0)
    if (isComparable(itemType)) Right(SnapiBoolType(Set(SnapiIsTryableTypeProperty())))
    else Left(Seq(ItemsNotComparable(items)))
  }

  override def desugar(
      t: Type,
      args: Seq[FunAppArg],
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Exp = {
    val list = args(0).e
    val predicate = {
      val x = IdnDef()
      FunAbs(FunProto(Vector(FunParam(x, None, None)), None, FunBody(BinaryExp(Eq(), IdnExp(x), args(1).e))))
    }
    CollectionPackageBuilder.Exists(list, predicate)
  }
}

class ZipCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "Zip"

  override def docs: EntryDoc = EntryDoc(
    "Turns two collections into one by combining their corresponding elements in pairs until the shortest collection is exhausted.",
    None,
    params = List(
      ParamDoc("collection1", TypeDoc(List("collection")), "A collection."),
      ParamDoc("collection2", TypeDoc(List("collection")), "A collection.")
    ),
    examples = List(
      ExampleDoc(
        """Collection.Zip(
          |  Collection.Build(1,2,3),
          |  Collection.Build("a", "b", "c")
          |)""".stripMargin,
        result = Some("""Collection.Build({1, "a"}, {2, "b"}, {3, "c"})""")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A collection of pairs of elements from the two collections.",
        retType = Some(TypeDoc(List("collection")))
      )
    )
  )

  override def nrMandatoryParams: Int = 2

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    Right(ExpParam(SnapiIterableType(AnythingType())))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    val SnapiIterableType(itemType1, _) = mandatoryArgs(0).t
    val SnapiIterableType(itemType2, _) = mandatoryArgs(1).t
    Right(SnapiIterableType(SnapiRecordType(Vector(SnapiAttrType("_1", itemType1), SnapiAttrType("_2", itemType2)))))
  }

}

class MkStringCollectionEntry extends EntryExtension with CollectionToListHint {

  override def packageName: String = "Collection"

  override def entryName: String = "MkString"

  override def docs: EntryDoc = EntryDoc(
    "Concatenates all elements of a collection in a string using start, end, and separator strings.",
    None,
    params = List(
      ParamDoc("collection", TypeDoc(List("collection")), "A collection."),
      ParamDoc("start", TypeDoc(List("string")), "The starting string.", isOptional = true),
      ParamDoc("sep", TypeDoc(List("string")), "The separator string.", isOptional = true),
      ParamDoc("end", TypeDoc(List("string")), "The ending string.", isOptional = true)
    ),
    examples = List(
      ExampleDoc(
        """Collection.MkString(
          |  Collection.Build("a", "b", "c"),
          |  start="(", sep=":", end=")")"""".stripMargin,
        result = Some(""""(a:b:c)"""")
      )
    ),
    ret = Some(
      ReturnDoc(
        "A string containing all elements of the collection separated by the separator string.",
        retType = Some(TypeDoc(List("string")))
      )
    )
  )

  override def nrMandatoryParams: Int = 1

  override def getMandatoryParam(prevMandatoryArgs: Seq[Arg], idx: Int): Either[String, Param] = {
    if (idx == 0) Right(ExpParam(SnapiIterableType(SnapiStringType(Set(SnapiIsNullableTypeProperty())))))
    else Right(ExpParam(SnapiStringType()))
  }

  override def optionalParams: Option[Set[String]] = Some(Set("start", "sep", "end"))

  override def getOptionalParam(prevMandatoryArgs: Seq[Arg], idn: String): Either[String, Param] = {
    Right(ExpParam(SnapiStringType()))
  }

  override def returnType(
      mandatoryArgs: Seq[Arg],
      optionalArgs: Seq[(String, Arg)],
      varArgs: Seq[Arg]
  )(implicit programContext: ProgramContext): Either[String, Type] = {
    Right(SnapiStringType(Set(SnapiIsTryableTypeProperty())))
  }

}

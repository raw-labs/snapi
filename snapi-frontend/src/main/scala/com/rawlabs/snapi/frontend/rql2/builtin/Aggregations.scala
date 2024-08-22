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

package com.rawlabs.snapi.frontend.rql2.builtin

import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.common.source._
import com.rawlabs.snapi.frontend.rql2.errors.ItemsNotComparable
import com.rawlabs.snapi.frontend.rql2.api.{EntryExtension, EntryExtensionHelper}
import com.rawlabs.snapi.frontend.rql2.source.{
  HasTypeProperties,
  IsNullable,
  Rql2AttrType,
  Rql2DecimalType,
  Rql2IsNullableTypeProperty,
  Rql2LongType,
  Rql2RecordType
}

abstract class Aggregation extends EntryExtensionHelper {

  val innerTypeConstraint: Type

  def aggregationType(t: Type): Either[String, Type]

}

object SumAggregation extends Aggregation {

  override val innerTypeConstraint: Type = OneOfType(number.tipes.map(addProp(_, Rql2IsNullableTypeProperty())))

  override def aggregationType(t: Type): Either[String, Type] = Right(t)

}

object MinAggregation extends Aggregation {

  override val innerTypeConstraint: Type = IsNullable()

  override def aggregationType(t: Type): Either[String, Type] = {
    if (isComparable(t)) Right(resetProps(t, Set(Rql2IsNullableTypeProperty())))
    else Left(ItemsNotComparable.message)
  }

}

object MaxAggregation extends Aggregation {

  override val innerTypeConstraint: Type = IsNullable()

  override def aggregationType(t: Type): Either[String, Type] = {
    if (isComparable(t)) Right(resetProps(t, Set(Rql2IsNullableTypeProperty())))
    else Left(ItemsNotComparable.message)
  }

}

object FirstAggregation extends Aggregation {

  override val innerTypeConstraint: Type = HasTypeProperties(Set(Rql2IsNullableTypeProperty()))

  override def aggregationType(t: Type): Either[String, Type] = Right(addProp(t, Rql2IsNullableTypeProperty()))

}

object LastAggregation extends Aggregation {

  override val innerTypeConstraint: Type = HasTypeProperties(Set(Rql2IsNullableTypeProperty()))

  override def aggregationType(t: Type): Either[String, Type] = Right(addProp(t, Rql2IsNullableTypeProperty()))

}

object CountAggregation extends Aggregation {

  override val innerTypeConstraint: Type = AnythingType()

  override def aggregationType(t: Type): Either[String, Type] = Right(Rql2LongType())

}

object AvgAggregation extends Aggregation {

  override val innerTypeConstraint: Type = OneOfType(number.tipes.map(t => addProp(t, Rql2IsNullableTypeProperty())))

  override def aggregationType(t: Type): Either[String, Type] = Right(
    Rql2RecordType(
      Vector(
        Rql2AttrType("sum", addProp(Rql2DecimalType(), Rql2IsNullableTypeProperty())),
        Rql2AttrType("count", Rql2LongType())
      )
    )
  )

}

abstract class AggregationEntry(aggregation: Aggregation) extends EntryExtension {

  override def nrMandatoryParams: Int = 1

}

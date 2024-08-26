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

import com.rawlabs.snapi.frontend.base.source.{AnythingType, Type}
import com.rawlabs.snapi.frontend.snapi.source._
import com.rawlabs.snapi.frontend.snapi.errors.ItemsNotComparable
import com.rawlabs.snapi.frontend.snapi.extensions.{EntryExtension, EntryExtensionHelper}
import com.rawlabs.snapi.frontend.snapi.source.{
  HasTypeProperties,
  IsNullable,
  SnapiAttrType,
  SnapiDecimalType,
  SnapiIsNullableTypeProperty,
  SnapiLongType,
  SnapiRecordType
}

abstract class Aggregation extends EntryExtensionHelper {

  val innerTypeConstraint: Type

  def aggregationType(t: Type): Either[String, Type]

}

object SumAggregation extends Aggregation {

  override val innerTypeConstraint: Type = OneOfType(number.tipes.map(addProp(_, SnapiIsNullableTypeProperty())))

  override def aggregationType(t: Type): Either[String, Type] = Right(t)

}

object MinAggregation extends Aggregation {

  override val innerTypeConstraint: Type = IsNullable()

  override def aggregationType(t: Type): Either[String, Type] = {
    if (isComparable(t)) Right(resetProps(t, Set(SnapiIsNullableTypeProperty())))
    else Left(ItemsNotComparable.message)
  }

}

object MaxAggregation extends Aggregation {

  override val innerTypeConstraint: Type = IsNullable()

  override def aggregationType(t: Type): Either[String, Type] = {
    if (isComparable(t)) Right(resetProps(t, Set(SnapiIsNullableTypeProperty())))
    else Left(ItemsNotComparable.message)
  }

}

object FirstAggregation extends Aggregation {

  override val innerTypeConstraint: Type = HasTypeProperties(Set(SnapiIsNullableTypeProperty()))

  override def aggregationType(t: Type): Either[String, Type] = Right(addProp(t, SnapiIsNullableTypeProperty()))

}

object LastAggregation extends Aggregation {

  override val innerTypeConstraint: Type = HasTypeProperties(Set(SnapiIsNullableTypeProperty()))

  override def aggregationType(t: Type): Either[String, Type] = Right(addProp(t, SnapiIsNullableTypeProperty()))

}

object CountAggregation extends Aggregation {

  override val innerTypeConstraint: Type = AnythingType()

  override def aggregationType(t: Type): Either[String, Type] = Right(SnapiLongType())

}

object AvgAggregation extends Aggregation {

  override val innerTypeConstraint: Type = OneOfType(number.tipes.map(t => addProp(t, SnapiIsNullableTypeProperty())))

  override def aggregationType(t: Type): Either[String, Type] = Right(
    SnapiRecordType(
      Vector(
        SnapiAttrType("sum", addProp(SnapiDecimalType(), SnapiIsNullableTypeProperty())),
        SnapiAttrType("count", SnapiLongType())
      )
    )
  )

}

abstract class AggregationEntry(aggregation: Aggregation) extends EntryExtension {

  override def nrMandatoryParams: Int = 1

}

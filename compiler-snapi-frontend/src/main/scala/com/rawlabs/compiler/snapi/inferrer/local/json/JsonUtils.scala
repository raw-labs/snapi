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

package com.rawlabs.compiler.snapi.inferrer.local.json

import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.compiler.snapi.inferrer.local._
import com.rawlabs.compiler.snapi.inferrer.api._

import scala.collection.JavaConverters._
import scala.collection.immutable

private[inferrer] trait JsonUtils extends MergeTypes with TextTypeInferrer with StrictLogging {

  protected val parserFeatures: immutable.Seq[Feature] = List(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature())

  protected def inferType(value: Any, currentType: SourceType): SourceType = {
    def inferList(x: Seq[Any], currentType: SourceType): SourceType = {

      val innerType: SourceType = currentType match {
        case SourceCollectionType(itemType, _) => itemType
        case _ => SourceNothingType()
      }

      val merged = x.foldLeft(innerType)((tipe, obj) => inferType(obj, tipe))
      SourceCollectionType(merged, false)
    }

    val inferred = value match {
      case _: Int => SourceIntType(false)
      case _: Long => SourceLongType(false)
      // on strings we try to find only  temporals or or-types
      case value: String => getType(value, currentType) match {
          case time: SourceTimeType => time
          case date: SourceDateType => date
          case timestamp: SourceTimestampType => timestamp
          case or: SourceOrType => or
          case _ => SourceStringType(false)
        }
      case _: Float => SourceFloatType(false)
      case _: Double => SourceDoubleType(false)
      case _: Boolean => SourceBoolType(false)
      case null => SourceNullType()
      case x: Seq[_] => inferList(x, currentType)
      case x: java.util.List[_] => inferList(x.asScala, currentType)
      case x: java.util.LinkedHashMap[String @unchecked, Object @unchecked] =>
        val tipeMap = x.asScala.toVector.map { x =>
          val currentAttType = currentType match {
            case SourceRecordType(atts, _) =>
              atts.find(att => att.idn == x._1).map(_.tipe).getOrElse(SourceNothingType())
            case _ => SourceNothingType()
          }
          SourceAttrType(x._1, inferType(x._2, currentAttType))
        }
        SourceRecordType(tipeMap, false)
      case _ => throw new LocalInferrerException(s"unsupported data type in JSON: $value")
    }
    maxOf(inferred, currentType)
  }

}

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

package com.rawlabs.snapi.compiler

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.{ClassTagExtensions, DefaultScalaModule}
import com.google.common.collect.HashMultiset
import com.rawlabs.snapi.frontend.base.source.Type
import com.rawlabs.snapi.frontend.rql2.FrontendSyntaxAnalyzer
import com.rawlabs.snapi.frontend.rql2.source._
import com.rawlabs.utils.core.{RawTestSuite, SettingsTestContext}
import org.bitbucket.inkytonik.kiama.util.Positions

import java.nio.file.Path
import scala.collection.JavaConverters._
import scala.math.BigDecimal.RoundingMode
import scala.util.control.NonFatal

trait SnapiOutputTestContext {
  this: RawTestSuite with SettingsTestContext =>

  def outputParser(
      queryResultPath: Path,
      tipe: String,
      ordered: Boolean = false,
      precision: Option[Int] = None,
      floatingPointAsString: Boolean = false
  ): Any = {

    val parser = new FrontendSyntaxAnalyzer(new Positions())
    val t = parser.parseType(tipe) match {
      case Right(t) => t
      case Left(err) => throw new AssertionError(err)
    }

    val mapper: ObjectMapper with ClassTagExtensions = {
      val om = new ObjectMapper() with ClassTagExtensions
      om.registerModule(DefaultScalaModule)
      // Don't close automatically file descriptors on read, which would trigger an extra "query.close()" call
      om.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
      om.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature())
      om
    }

    def recurse(n: JsonNode, t: Type): Any = {
      t match {
        case t: SnapiTypeWithProperties if t.props.contains(SnapiIsTryableTypeProperty()) =>
          if (n.isTextual && !t.isInstanceOf[SnapiDecimalType] /* */ ) n.asText()
          else recurse(n, t.cloneAndRemoveProp(SnapiIsTryableTypeProperty()))
        case t: SnapiTypeWithProperties if t.props.contains(SnapiIsNullableTypeProperty()) && n.isNull => null
        case _: SnapiBoolType if n.isBoolean => n.asBoolean
        case _: SnapiStringType if n.isTextual => n.asText
        case _: SnapiByteType | _: SnapiShortType | _: SnapiIntType if n.canConvertToInt => n.asInt
        case _: SnapiLongType if n.canConvertToLong => n.asLong
        case _: SnapiFloatType | _: SnapiDoubleType =>
          // TODO (msb): Validate it's the actual type complying with our format
          val v = {
            val double = n.asDouble
            precision match {
              case Some(p) =>
                val b = BigDecimal(double)
                b.setScale(p, RoundingMode.HALF_DOWN)
                b.doubleValue()
              case None => double
            }
          }
          if (floatingPointAsString) v.toString
          else v
        case _: SnapiDecimalType =>
          try {
            val decimal = BigDecimal(n.asText())
            precision match {
              case Some(p) => decimal.setScale(p, RoundingMode.HALF_DOWN)
              case None => decimal
            }
          } catch {
            case NonFatal(_) => n.asText() // in case it was tryable
          }
        case _: SnapiDateType if n.isTextual =>
          // TODO (msb): Validate it's the actual type complying with our format
          n.asText
        case _: SnapiTimeType if n.isTextual =>
          // TODO (msb): Validate it's the actual type complying with our format
          n.asText
        case _: SnapiTimestampType if n.isTextual =>
          // TODO (msb): Validate it's the actual type complying with our format
          n.asText
        case _: SnapiIntervalType if n.isTextual =>
          // TODO (msb): Validate it's the actual type complying with our format
          n.asText
        case _: SnapiBinaryType if n.isTextual =>
          // TODO (msb): Validate it's the actual type complying with our format
          n.asText
        //        case OrType(tipes) =>
        //          tipes.foreach { t =>
        //            try {
        //              return recurse(n, t)
        //            } catch {
        //              case NonFatal(_) =>
        //              // Try to parse with next one...
        //            }
        //          }
        //          throw new AssertionError("couldn't parse OrType with any parser")
        case SnapiRecordType(atts, _) if n.isObject =>
          val fields = n.fields().asScala.toVector
          val tipes = atts.map(_.tipe)
          assert(fields.length == tipes.length)
          fields.zip(tipes).map { case (p, t) => p.getKey -> recurse(p.getValue, t) }
        case _: SnapiListType | _: SnapiIterableType if n.isArray =>
          val inner = t match {
            case SnapiListType(inner, _) => inner
            case SnapiIterableType(inner, _) => inner
          }

          if (!ordered) {
            val bag = HashMultiset.create[Any]()
            n.iterator().asScala.foreach(n1 => bag.add(recurse(n1, inner)))
            bag
          } else {
            n.iterator().asScala.map(n1 => recurse(n1, inner)).toList
          }
        case SnapiOrType(tipes, _) => tipes.foreach { t =>
            tipes.foreach { t =>
              try {
                return recurse(n, t)
              } catch {
                case NonFatal(_) =>
                // Try to parse with next one...
              }
            }
            throw new AssertionError("couldn't parse OrType with any parser")
          }
      }
    }

    recurse(mapper.readTree(queryResultPath.toFile), t)
  }

}

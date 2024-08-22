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

package com.rawlabs.snapi.frontend.inferrer.local

import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.snapi.frontend.inferrer.api._
import scala.collection.mutable

private[inferrer] trait MergeTypes extends StrictLogging {

  private def addElementToOrType(or: SourceOrType, t: SourceNullableType) = {
    def findCompatible(t: SourceNullableType): Option[(SourceNullableType, SourceNullableType)] = {
      or.ors.foreach { typeInOr =>
        l4TypesMaxOf(t, typeInOr) match {
          case merged: SourceNullableType => return Some(typeInOr -> merged)
          case _ =>
        }
      }
      None
    }
    findCompatible(t) match {
      case Some((typeInOr, merged)) =>
        val mutableOrs: mutable.HashSet[SourceNullableType] = mutable.HashSet(or.ors.toSeq: _*)
        mutableOrs.remove(typeInOr)
        mutableOrs.add(merged)
        SourceOrType(mutableOrs.toSet)
      case None => SourceOrType(or.ors ++ Set(t))
    }
  }

  def maxOf(t1: SourceType, t2: SourceType): SourceType = {
    if (t1 == t2) {
      t1
    } else {
      (t1, t2) match {
        case (_: SourceAnyType, _) => SourceAnyType()
        case (_, _: SourceAnyType) => SourceAnyType()
        case (_: SourceNothingType, t) => t
        case (t, _: SourceNothingType) => t
        case (or1: SourceOrType, or2: SourceOrType) =>
          or1.ors.foldLeft(or2)((acc, elem) => addElementToOrType(acc, elem))
        case (or: SourceOrType, t: SourceNullableType) => addElementToOrType(or, t)
        case (t: SourceNullableType, or: SourceOrType) => addElementToOrType(or, t)
        case (t1: SourceNullableType, t2: SourceNullableType) => l4TypesMaxOf(t1, t2)
      }
    }
  }

  // TODO: The following doesn't handle temporal types since this hasn't been needed so far...
  protected def l4TypesMaxOf(t1: SourceNullableType, t2: SourceNullableType): SourceType = {
    (t1, t2) match {
      case (_: SourceNullType, _) => SourceNullableType.cloneAsNullable(t2)
      case (_, _: SourceNullType) => SourceNullableType.cloneAsNullable(t1)
      case (r1: SourceRecordType, r2: SourceRecordType) => maxOfRecord(r1, r2)
      case (_: SourceRecordType, _) | (_, _: SourceRecordType) => SourceOrType(Set(t1, t2))
      case (SourceCollectionType(i1, n1), SourceCollectionType(i2, n2)) => SourceCollectionType(maxOf(i1, i2), n1 || n2)
      case (_: SourceCollectionType, _) | (_, _: SourceCollectionType) => SourceOrType(Set(t1, t2))
      case (_: SourceStringType, _: SourcePrimitiveType) | (_: SourcePrimitiveType, _: SourceStringType) =>
        SourceStringType(t1.nullable || t2.nullable)
      case (_: SourceDecimalType, _: SourceNumberType) | (_: SourceNumberType, _: SourceDecimalType) =>
        SourceDecimalType(t1.nullable || t2.nullable)
      case (_: SourceDoubleType, _: SourceNumberType) | (_: SourceNumberType, _: SourceDoubleType) =>
        SourceDoubleType(t1.nullable || t2.nullable)
      case (_: SourceFloatType, _: SourceNumberType) | (_: SourceNumberType, _: SourceFloatType) =>
        SourceFloatType(t1.nullable || t2.nullable)
      case (_: SourceLongType, _: SourceNumberType) | (_: SourceNumberType, _: SourceLongType) =>
        SourceLongType(t1.nullable || t2.nullable)
      case (_: SourceIntType, _: SourceNumberType) | (_: SourceNumberType, _: SourceIntType) =>
        SourceIntType(t1.nullable || t2.nullable)
      case (_: SourceShortType, _: SourceNumberType) | (_: SourceNumberType, _: SourceShortType) =>
        SourceShortType(t1.nullable || t2.nullable)
      case (_: SourceByteType, _: SourceNumberType) | (_: SourceNumberType, _: SourceByteType) =>
        SourceByteType(t1.nullable || t2.nullable)
      case (SourceDateType(fmt1, n1), SourceDateType(fmt2, n2)) =>
        if (fmt1 == fmt2) SourceDateType(fmt1, n1 || n2)
        else SourceStringType(n1 || n2)
      case (SourceTimestampType(fmt1, n1), SourceTimestampType(fmt2, n2)) =>
        if (fmt1 == fmt2) SourceTimestampType(fmt1, n1 || n2)
        else SourceStringType(n1 || n2)
      case (SourceTimeType(fmt1, n1), SourceTimeType(fmt2, n2)) =>
        if (fmt1 == fmt2) SourceTimeType(fmt1, n1 || n2)
        else SourceStringType(n1 || n2)
      case (_: SourceTimestampType, _: SourceDateType) | (_: SourceDateType, _: SourceTimestampType) =>
        SourceStringType(t1.nullable || t2.nullable)
      case (_: SourceTimeType, _: SourceDateType) | (_: SourceDateType, _: SourceTimeType) =>
        SourceStringType(t1.nullable || t2.nullable)
      case (_: SourceTimestampType, _: SourceTimeType) | (_: SourceTimeType, _: SourceTimestampType) =>
        SourceStringType(t1.nullable || t2.nullable)
      case (_: SourceIntervalType, _: SourceIntervalType) => SourceIntervalType(t1.nullable || t2.nullable)
      case (_: SourceBoolType, _: SourceBoolType) => SourceBoolType(t1.nullable || t2.nullable)
      case _ => SourceOrType(Set(t1, t2))
    }
  }

  protected def maxOfRecord(rec1: SourceRecordType, rec2: SourceRecordType): SourceType = {
    val fields1 = rec1.atts
    val fields2 = rec2.atts
    val newList = mutable.ListBuffer[SourceAttrType]()
    var i = 0
    val n1 = fields1.size
    val n2 = fields2.size
    while (i < n1) {
      val SourceAttrType(idn1, tipe1) = fields1(i)
      var j = 0
      var searching = true
      while (searching && j < n2) {
        val attrType2 = fields2(j)
        if (attrType2.idn == idn1) {
          newList.append(SourceAttrType(idn1, maxOf(tipe1, attrType2.tipe)))
          searching = false
        }
        j += 1
      }
      if (searching) {
        newList.append(SourceAttrType(idn1, maxOf(tipe1, SourceNullType())))
      }
      i += 1
    }
    // Add all fields of fields2 that are not in fields1
    i = 0
    while (i < n2) {
      val SourceAttrType(idn2, tipe2) = fields2(i)
      var j = 0
      var searching = true
      while (searching && j < n1) {
        val attrType1 = fields1(j)
        if (attrType1.idn == idn2) {
          searching = false
        }
        j += 1
      }
      if (searching) {
        newList.append(SourceAttrType(idn2, maxOf(tipe2, SourceNullType())))
      }
      i += 1
    }
    SourceRecordType(newList.toVector, rec1.nullable || rec2.nullable)
  }

  def maxOfRow(r1: Seq[SourceType], r2: Seq[SourceType]): Array[SourceType] = {
    val m = Integer.max(r1.length, r2.length)
    val array = new Array[SourceType](m)
    var n = 0
    while (n < m) {
      val tipe =
        if (n >= r1.length) {
          maxOf(SourceNullType(), r2(n))
        } else if (n >= r2.length) {
          maxOf(SourceNullType(), r1(n))
        } else {
          maxOf(r1(n), r2(n))
        }
      array(n) = tipe
      n += 1
    }
    array
  }

}

object MergeTypes extends MergeTypes

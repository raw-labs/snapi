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

package raw.inferrer.local.xml

import raw.inferrer.{SourceNullableType, _}
import raw.inferrer.local._
import raw.inferrer.local.xml.InferrerXmlTypeReader.TEXT_FIELD_NAME

import scala.collection.mutable

private[inferrer] trait XmlMergeTypes extends MergeTypes {

  // Overrides the max of to allow merging single elements with collections.
  override protected def l4TypesMaxOf(t1: SourceNullableType, t2: SourceNullableType): SourceType = {
    (t1, t2) match {
      case (_: SourcePrimitiveType, _: SourceStringType) | (_: SourceStringType, _: SourcePrimitiveType) =>
        SourceStringType(t1.nullable || t2.nullable)
      case (_: SourceNumberType, _: SourceBoolType) | (_: SourceBoolType, _: SourceNumberType) =>
        SourceStringType(t1.nullable || t2.nullable)
      case (SourceCollectionType(i1, n1), SourceCollectionType(i2, n2)) => SourceCollectionType(maxOf(i1, i2), n1 || n2)
      case (SourceCollectionType(i1, n1), i2) => SourceCollectionType(maxOf(i1, i2), n1)
      case (i1, SourceCollectionType(i2, n2)) => SourceCollectionType(maxOf(i1, i2), n2)
      case (r: SourceRecordType, SourceNullType()) =>
        SourceRecordType(r.atts.map(a => SourceAttrType(a.idn, asIfNotFound(a.tipe))), r.nullable)
      case (SourceNullType(), r: SourceRecordType) =>
        SourceRecordType(r.atts.map(a => SourceAttrType(a.idn, asIfNotFound(a.tipe))), r.nullable)
      case (r: SourceRecordType, p: SourcePrimitiveType) =>
        maxOf(r, SourceRecordType(Vector(SourceAttrType(TEXT_FIELD_NAME, p)), false))
      case (p: SourcePrimitiveType, r: SourceRecordType) =>
        maxOf(r, SourceRecordType(Vector(SourceAttrType(TEXT_FIELD_NAME, p)), false))
      case _ => super.l4TypesMaxOf(t1, t2)
    }
  }

  override protected def maxOfRecord(rec1: SourceRecordType, rec2: SourceRecordType): SourceType = {
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
        val mergedType = asIfNotFound(tipe1)
        newList.append(SourceAttrType(idn1, mergedType))
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
        val mergedType = asIfNotFound(tipe2)
        newList.append(SourceAttrType(idn2, mergedType))
      }
      i += 1
    }
    SourceRecordType(newList.toVector, rec1.nullable || rec2.nullable)
  }

  private def asIfNotFound(sourceType: SourceType): SourceType = sourceType match {
    // When a field is found only in one record we consider it was null, which makes the field nullable.
    // If that field is known to be a collection, we consider the absence of the field is only an empty list.
    case n: SourceCollectionType => n
    case n: SourceNullableType => SourceNullableType.cloneAsNullable(n)
    case t => t
  }

}

private[inferrer] object XmlMergeTypes extends XmlMergeTypes

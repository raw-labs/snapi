/*
 * Copyright 2024 RAW Labs S.A.
 *
 * Use of this software is governed by the Business Source License
 * included in the file licenses/BSL.txt.
 *
 * As of the Change Date specified in that file, in accordance with
 * the Business Source License, use of this software will be governed
 * by the Apache License, Version 2.0, included in the file
 * licenses/APL.txt.
 */

package com.rawlabs.snapi.compiler.tests

import com.typesafe.scalalogging.StrictLogging
import org.graalvm.polyglot.Value

import java.math.{BigDecimal, RoundingMode}
import scala.collection.mutable

object TruffleValueComparer extends StrictLogging {

  /**
   * Compare two Truffle Values by structure and content.
   *
   * @param topLevelIteratorCompareToCollections
   *   if true, then at the top-level we treat "iterator" and "array" as comparable
   *   by comparing their elements. In nested structures, we keep the normal rule
   *   that "iterator != array" unless they are both truly arrays or both iterators.
   */
  def deepEquals(
      v1: Value,
      v2: Value,
      topLevelIteratorCompareToCollections: Boolean = true
  ): Boolean = {
    // Delegate to an internal function that also tracks whether we're at top-level or not.
    deepEqualsInternal(v1, v2, isTopLevel = true, topLevelIteratorCompareToCollections)
  }

  /**
   * Internal recursive comparison function.
   *
   * @param isTopLevel
   *   if true, we can unify array vs iterator if `topLevelIteratorCompareToCollections` is set.
   */
  private def deepEqualsInternal(
      v1: Value,
      v2: Value,
      isTopLevel: Boolean,
      topLevelIteratorCompareToCollections: Boolean
  ): Boolean = {
    logger.info(s"Comparing: $v1 vs $v2 (top-level: $isTopLevel)")

    // 0) Compare exceptions
    if (v1.isException && v2.isException) {
      val msg1 =
        try {
          v1.throwException()
          throw new IllegalStateException("Unreachable")
        } catch {
          case e: Exception => e.getMessage
        }
      val msg2 =
        try {
          v2.throwException()
          throw new IllegalStateException("Unreachable")
        } catch {
          case e: Exception => e.getMessage
        }
      val r = msg1 == msg2
      if (!r) logger.info(s"Exception message mismatch: $msg1 != $msg2")
      r

    } else if (v1.isException || v2.isException) {
      if (v1.isException) {
        try {
          v1.throwException()
          throw new IllegalStateException("Unreachable")
        } catch {
          case ex: Exception => logger.info(s"v1 message: ${ex.getMessage}")
        }
      }
      if (v2.isException) {
        try {
          v2.throwException()
          throw new IllegalStateException("Unreachable")
        } catch {
          case ex: Exception => logger.info(s"v2 message: ${ex.getMessage}")
        }
      }
      logger.info(s"One is exception, the other is not")
      false

      // 1) Compare null
    } else if (v1.isNull && v2.isNull) {
      true
    } else if (v1.isNull || v2.isNull) {
      logger.info("One is null, the other is not")
      false

      // 2) Compare numeric
    } else if (v1.isNumber && v2.isNumber) {
      val r = numericEquals(v1, v2)
      if (!r) logger.info(s"Numeric comparison failed: ${v1.toString} != ${v2.toString}")
      r
    } else if (v1.isNumber || v2.isNumber) {
      logger.info("One is number, the other is not")
      false

      // 2b) Compare booleans
    } else if (v1.isBoolean && v2.isBoolean) {
      val r = v1.asBoolean == v2.asBoolean
      if (!r) logger.info(s"Boolean comparison failed: ${v1.asBoolean} != ${v2.asBoolean}")
      r
    } else if (v1.isBoolean || v2.isBoolean) {
      logger.info("One is boolean, the other is not")
      false

      // 2c) Compare strings
    } else if (v1.isString && v2.isString) {
      val r = v1.asString == v2.asString
      if (!r) logger.info(s"String comparison failed: ${v1.asString} != ${v2.asString}")
      r
    } else if (v1.isString || v2.isString) {
      logger.info("One is string, the other is not")
      false

      // 3) Compare date/time/timestamp/duration/instant

      // --- 3a) Timestamps: both isDate & isTime
    } else if (
      v1.isDate && v1.isTime &&
      v2.isDate && v2.isTime
    ) {
      // We treat this as a "timestamp" => compare date & time
      val date1 = v1.asDate()
      val time1 = v1.asTime()
      val date2 = v2.asDate()
      val time2 = v2.asTime()
      val r = date1 == date2 && time1 == time2
      if (!r) logger.info(s"Timestamp comparison failed: $date1 $time1 != $date2 $time2")
      r

      // --- 3b) Both date only (and not time)
    } else if (
      v1.isDate && !v1.isTime &&
      v2.isDate && !v2.isTime
    ) {
      val d1 = v1.asDate()
      val d2 = v2.asDate()
      val r = d1 == d2
      if (!r) logger.info(s"Date comparison failed: $d1 != $d2")
      r

      // --- 3c) Both time only (and not date)
    } else if (
      v1.isTime && !v1.isDate &&
      v2.isTime && !v2.isDate
    ) {
      val t1 = v1.asTime()
      val t2 = v2.asTime()
      val r = t1 == t2
      if (!r) logger.info(s"Time comparison failed: $t1 != $t2")
      r

      // --- 3d) Both instant
    } else if (v1.isInstant && v2.isInstant) {
      val i1 = v1.asInstant()
      val i2 = v2.asInstant()
      val r = i1 == i2
      if (!r) logger.info(s"Instant comparison failed: $i1 != $i2")
      r

      // --- 3e) Both duration
    } else if (v1.isDuration && v2.isDuration) {
      val dur1 = v1.asDuration()
      val dur2 = v2.asDuration()
      val r = dur1 == dur2
      if (!r) logger.info(s"Duration comparison failed: $dur1 != $dur2")
      r

      // If one is date/time/duration/instant and the other is not => false
    } else if (
      v1.isDate || v1.isTime || v1.isInstant || v1.isDuration ||
      v2.isDate || v2.isTime || v2.isInstant || v2.isDuration
    ) {
      logger.info("One is date/time/duration/instant, the other is not")
      false

      // 4) Arrays (or top-level array vs iterator if flag is set)
    } else if (
      (v1.hasArrayElements || (v1.hasIterator && isTopLevel && topLevelIteratorCompareToCollections)) &&
      (v2.hasArrayElements || (v2.hasIterator && isTopLevel && topLevelIteratorCompareToCollections))
    ) {
      // Unify them at top-level if the flag is set, otherwise treat them as real arrays only
      val seq1 =
        if (v1.hasArrayElements) collectArrayElements(v1)
        else collectIteratorElements(v1.getIterator)
      val seq2 =
        if (v2.hasArrayElements) collectArrayElements(v2)
        else collectIteratorElements(v2.getIterator)

      if (seq1.size != seq2.size) {
        logger.info(s"Array/Iterator size mismatch: ${seq1.size} != ${seq2.size}")
        false
      } else {
        val r = seq1.zip(seq2).forall {
          case (a, b) => deepEqualsInternal(a, b, isTopLevel = false, topLevelIteratorCompareToCollections)
        }
        if (!r) logger.info("Array/Iterator element mismatch")
        r
      }

      // 5) Normal array vs array
    } else if (v1.hasArrayElements && v2.hasArrayElements) {
      if (v1.getArraySize != v2.getArraySize) {
        logger.info(s"Array size mismatch: ${v1.getArraySize} != ${v2.getArraySize}")
        false
      } else {
        val r = (0L until v1.getArraySize).forall { i =>
          val e1 = v1.getArrayElement(i)
          val e2 = v2.getArrayElement(i)
          deepEqualsInternal(e1, e2, isTopLevel = false, topLevelIteratorCompareToCollections)
        }
        if (!r) logger.info("Array element mismatch")
        r
      }

    } else if (v1.hasArrayElements || v2.hasArrayElements) {
      // One is array, the other is not => not equal
      logger.info("One is array, the other is not")
      false

      // 6) Iterators (normal case)
    } else if (v1.hasIterator && v2.hasIterator) {
      val seq1 = collectIteratorElements(v1.getIterator)
      val seq2 = collectIteratorElements(v2.getIterator)
      if (seq1.size != seq2.size) {
        logger.info(s"Iterator size mismatch: ${seq1.size} != ${seq2.size}")
        false
      } else {
        val r = seq1.zip(seq2).forall {
          case (a, b) => deepEqualsInternal(a, b, isTopLevel = false, topLevelIteratorCompareToCollections)
        }
        if (!r) logger.info("Iterator element mismatch")
        r
      }

    } else if (v1.hasIterator || v2.hasIterator) {
      logger.info("One is iterator, the other is not")
      false

      // 7) Hash entries (map/dict). In older Graal: each "entry" is array [key, value].
    } else if (v1.hasHashEntries && v2.hasHashEntries) {
      if (v1.getHashSize != v2.getHashSize) false
      else {
        val map1 = collectAllHashEntries(v1.getHashEntriesIterator)
        val map2 = collectAllHashEntries(v2.getHashEntriesIterator)
        if (map1.size != map2.size) {
          logger.info(s"Hash size mismatch: ${map1.size} != ${map2.size}")
          false
        } else {
          val r = map1.forall {
            case (k1, vVal1) => map2.get(k1) match {
                case Some(vVal2) => deepEqualsInternal(
                    vVal1,
                    vVal2,
                    isTopLevel = false,
                    topLevelIteratorCompareToCollections
                  )
                case None => false
              }
          }
          if (!r) logger.info("Hash entry mismatch")
          r
        }
      }

    } else if (v1.hasHashEntries || v2.hasHashEntries) {
      logger.info("One is hash, the other is not")
      false

      // 8) Object members
    } else if (v1.hasMembers && v2.hasMembers) {
      val keys1 = v1.getMemberKeys
      val keys2 = v2.getMemberKeys
      if (keys1.size != keys2.size) {
        logger.info(s"Member key size mismatch: ${keys1.size} != ${keys2.size}")
        false
      } else {
        val k1Set = toStringKeySet(keys1)
        val k2Set = toStringKeySet(keys2)
        if (k1Set != k2Set) {
          logger.info(s"Member key set mismatch: $k1Set != $k2Set")
          false
        } else {
          val r = k1Set.forall { kStr =>
            val m1 = v1.getMember(kStr)
            val m2 = v2.getMember(kStr)
            deepEqualsInternal(m1, m2, isTopLevel = false, topLevelIteratorCompareToCollections)
          }
          if (!r) logger.info("Member value mismatch")
          r
        }
      }

    } else if (v1.hasMembers || v2.hasMembers) {
      logger.info("One has members, the other does not")
      false

      // 9) Fallback: not equal for unhandled types (functions, host objects, etc.)
    } else {
      logger.info("Unhandled type")
      false
    }
  }

  /**
   * Compare numeric values carefully, with optional scale or epsilon.
   * - If both fit in long, compare as longs.
   * - Otherwise, parse as BigDecimal if possible.
   * - If that fails, fallback to double with optional epsilon.
   */
  def numericEquals(
      v1: Value,
      v2: Value,
      decimalScale: Int = 0,
      doubleEpsilon: Double = 0.0
  ): Boolean = {
    if (v1.fitsInLong && v2.fitsInLong) {
      v1.asLong == v2.asLong
    } else {
      try {
        val bd1 = new BigDecimal(v1.toString)
        val bd2 = new BigDecimal(v2.toString)
        if (decimalScale > 0) {
          val bd1Scaled = bd1.setScale(decimalScale, RoundingMode.HALF_UP)
          val bd2Scaled = bd2.setScale(decimalScale, RoundingMode.HALF_UP)
          val r = bd1Scaled.compareTo(bd2Scaled) == 0
          if (!r) {
            logger.info(s"BigDecimal comparison failed: $bd1Scaled != $bd2Scaled")
          }
          r
        } else {
          bd1.compareTo(bd2) == 0
        }
      } catch {
        case _: NumberFormatException =>
          logger.info("BigDecimal parse fail, fallback to double")
          if (v1.fitsInDouble && v2.fitsInDouble) {
            val d1 = v1.asDouble
            val d2 = v2.asDouble
            if (java.lang.Double.isNaN(d1) && java.lang.Double.isNaN(d2)) {
              true
            } else if (doubleEpsilon > 0.0) {
              math.abs(d1 - d2) <= doubleEpsilon
            } else {
              d1 == d2
            }
          } else {
            false
          }
      }
    }
  }

  /**
   * Collect array elements: from index 0 until size-1.
   */
  private def collectArrayElements(arrayValue: Value): List[Value] = {
    val size = arrayValue.getArraySize
    (0L until size).map(arrayValue.getArrayElement).toList
  }

  /**
   * Collect all elements from a guest "iterator object" using
   * hasIteratorNextElement() / getIteratorNextElement().
   */
  private def collectIteratorElements(iteratorValue: Value): List[Value] = {
    val buffer = mutable.ListBuffer.empty[Value]
    while (iteratorValue.hasIteratorNextElement) {
      buffer += iteratorValue.getIteratorNextElement
    }
    buffer.toList
  }

  /**
   * Collect all hash entries from a guest "hash iterator object". In older Graal,
   * each next element is an array [key, value]. We'll return them as a Map[Value, Value].
   */
  private def collectAllHashEntries(hashIterValue: Value): Map[Value, Value] = {
    val buffer = mutable.Map.empty[Value, Value]
    while (hashIterValue.hasIteratorNextElement) {
      val entry = hashIterValue.getIteratorNextElement
      if (entry.hasArrayElements && entry.getArraySize == 2) {
        val keyVal = entry.getArrayElement(0)
        val valVal = entry.getArrayElement(1)
        buffer.put(keyVal, valVal)
      }
    }
    buffer.toMap
  }

  /**
   * Convert a java.lang.Iterable[_] of keys to a Scala Set[String].
   */
  private def toStringKeySet(keys: java.lang.Iterable[_]): Set[String] = {
    val it = keys.iterator
    val s = mutable.Set.empty[String]
    while (it.hasNext) {
      s += it.next().toString
    }
    s.toSet
  }
}

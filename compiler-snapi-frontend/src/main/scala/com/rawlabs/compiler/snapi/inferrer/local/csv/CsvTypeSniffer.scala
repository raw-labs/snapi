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

package com.rawlabs.compiler.snapi.inferrer.local.csv

import com.typesafe.scalalogging.StrictLogging
import com.rawlabs.compiler.snapi.inferrer.api.{
  SourceAttrType,
  SourceCollectionType,
  SourceDoubleType,
  SourceNothingType,
  SourceNullType,
  SourceRecordType,
  SourceStringType,
  SourceType
}
import com.rawlabs.compiler.snapi.inferrer.local._

import scala.collection.mutable.ArrayBuffer

sealed private trait ParseState

private case object InQuote extends ParseState

private case object OffQuote extends ParseState

private case object EscapeQuote extends ParseState

private case object EscapeOffQuote extends ParseState

/** Parses CSV type while collecting stats at the same time. */
private[inferrer] class CsvTypeSniffer(
    val delimiter: Char,
    val quote: Option[Char],
    val escapeChar: Option[Char],
    nulls: Seq[String],
    nans: Seq[String],
    val stopEarly: Boolean = true
) extends TextTypeInferrer
    with MergeTypes
    with StrictLogging {

  private var rowNumber = 0
  private var tipes = Array[SourceType]()
  private var header: Seq[String] = Seq.empty
  private val currentToken = StringBuilder.newBuilder
  private val currentRow: ArrayBuffer[String] = ArrayBuffer.empty
  private var state: ParseState = OffQuote
  private var uniform = true
  // number of times the header values appear in the data
  private var nHeaderValues = 0
  private var quotedValues = 0
  private var nDelimiters = 0
  private var multiLine = false

  private var stopped = false

  def multiLineFields = multiLine

  def delimCount = nDelimiters

  def tokenCount = delimCount + rowNumber + 1

  def isUniform = uniform

  def quotedRatio = nQuoted.toDouble / tokenCount

  private val quoteDefined = quote.isDefined
  private val quoteChar = quote.getOrElse('.')
  private val escapeDefined = escapeChar.isDefined
  private val escape = escapeChar.getOrElse('.')

  private def dataTypes: Array[SourceType] = {
    // uses the current token if there is something there
    if (currentToken.nonEmpty) addNewToken()
    if (currentRow.length == header.length) {
      getTypes(currentRow, tipes)
    } else if (rowNumber == 1) {
      // in the case the csv has only one line
      Array.fill(header.length)(SourceNothingType())
    } else {
      tipes
    }
  }

  def nQuoted: Int = {
    // If reached EOF and last char is quoted, count it
    if (isQuoted(currentToken.toString())) {
      quotedValues + 1
    } else {
      quotedValues
    }
  }

  def infer(maybeHasHeader: Option[Boolean]) = {
    val headerTypes = getTypes(header, Seq.fill(header.length)(SourceNothingType()))

    // detects the header
    val (fields: Seq[String], hasHeader: Boolean) = maybeHasHeader match {
      case None => detectHeader match {
          case Some(h) => (h, true)
          case None => ((1 to header.length).map(n => s"_$n"), false)
        }
      case Some(true) => (header, true)
      case Some(false) => ((1 to header.length).map(n => s"_$n"), false)
    }
    // if it does not have an header it can now include the first row in the inferring
    val finalInf: Array[SourceType] =
      if (!hasHeader) {
        maxOfRow(dataTypes, headerTypes)
      } else {
        dataTypes
      }
    // It will replace NullType (undefined?) with string
    val noUndefined = finalInf.map {
      case _: SourceNullType => SourceStringType(true)
      case x => x
    }

    val atts = fields.indices.map(n => SourceAttrType(fields(n), noUndefined(n)))
    val tipe = SourceCollectionType(SourceRecordType(atts.toVector, false), false)
    val result = uniquifyTemporalFormats(tipe)
    (result, hasHeader)
  }

  private def getTypes(values: Seq[String], currentTypes: Seq[SourceType]) = {
    var n = 0
    val size = values.size
    val types = new Array[SourceType](size)
    while (n < size) {
      val value = values(n)
      val t = if (n < currentTypes.length) currentTypes(n) else SourceNothingType()
      // if the value is in the nullValues
      val tipe =
        if (nulls.contains(value)) {
          maxOf(SourceNullType(), t)
        } else if (nans.contains(value)) {
          maxOf(SourceDoubleType(false), t)
        } else {
          getType(value, t)
        }
      types(n) = tipe
      n += 1
    }
    types
  }

  def parse(s: String) = {
    if (!stopped) {
      val length = s.length
      var idx = 0
      while (idx < length) {
        if (!parseChar(s.charAt(idx))) {
          stopped = true
        }
        idx += 1
      }
    }
  }

  private def addNewToken() = {
    val value = currentToken.toString().trim

    currentRow += value
    if (isQuoted(value)) quotedValues += 1

    // Checking if header string appears in the values
    if (header.contains(value)) nHeaderValues += 1

    currentToken.clear
  }

  private def parseChar(c: Char): Boolean = {
    if (state == OffQuote) {
      if (quoteDefined && c == quoteChar) {
        currentToken += c
        state = InQuote
      } else if (c == delimiter) {
        addNewToken()
        nDelimiters += 1
      } else if (c == '\n') {
        addNewToken()
        if (rowNumber < 1) {
          // stores the header
          header = currentRow.map { s =>
            if (isQuoted(s)) s.substring(1, s.length - 1)
            else s
          }
        } else {
          if (currentRow.length != header.length) {
            uniform = false
            // If it is not uniform, stop now
            if (stopEarly) return false
          }

          tipes = getTypes(currentRow, tipes)
        }
        rowNumber += 1
        currentRow.clear
      } else if (escapeDefined && c == escape) {
        state = EscapeOffQuote
      } else {
        currentToken += c
      }
    } else if (state == EscapeOffQuote) {
      currentToken += replaceEscapedChars(c)
      state = OffQuote
    } else if (state == InQuote) {
      if (quoteDefined && c == quoteChar) {
        currentToken += c
        state = OffQuote
      } else if (c == '\n') {
        multiLine = true
      } else if (escapeDefined && c == escape) {
        state = EscapeQuote
      } else {
        currentToken += c
      }
    } else if (state == EscapeQuote) {
      currentToken += replaceEscapedChars(c)
      state = InQuote
    } else {
      currentToken += c
    }
    true
  }

  private def isQuoted(s: String): Boolean = quoteDefined && (s.nonEmpty && s.head == quoteChar && s.last == quoteChar)

  private def replaceEscapedChars(c: Char) = c match {
    case 'n' => '\n'
    case 't' => '\t'
    case 'r' => '\r'
    case _ => c
  }

  // Tries to detect the header
  private def detectHeader(): Option[Seq[String]] = {
    val headerTypes = header.map(s => getType(s, SourceNothingType()))
    // does the header have other types than strings
    if (
      headerTypes.exists {
        case _: SourceStringType => false
        case _ => true
      }
    ) {
      return None
    }

    //is any field of the header longer than 30 characters?
    if (header.exists(str => str.length > 30)) return None

    //is there any empty string
    if (header.exists(str => str.isEmpty)) return None

    // if header values appear more than 0.1% in the data (0.1% is a magic number)
    if (nHeaderValues.toDouble / tokenCount > 0.001) return None

    //does any field of the header have more than 3 words
    if (header.exists(str => str.split("\\s+").length > 3)) {
      return None
    }

    Some(header)
  }
}

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

package raw.client.sql

import raw.client.api.Pos

import scala.collection.mutable

case class SqlIdentifier(value: String, quoted: Boolean)

object SqlCodeUtils {
  def identifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_'

  // filter to recognize an identifier, possibly with dots, e.g. example.airports
  def fullIdentifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_' || c == '.' || c == '"'

  def compareSingleIdentifiers(v1: SqlIdentifier, v2: SqlIdentifier): Boolean = {
    // both quoted , case sensitive comparison, so we just compare the strings
    if (v1.quoted && v2.quoted) {
      v1.value == v2.value
    } else {
      // At least one is not quoted so we perform a case insensitive match
      v1.value.toLowerCase == v2.value.toLowerCase
    }
  }

  def compareIdentifiers(v1: Seq[SqlIdentifier], v2: Seq[SqlIdentifier]): Boolean = {
    if (v1.length != v2.length) return false
    v1.zip(v2).foreach { case (idn1, idn2) => if (!compareSingleIdentifiers(idn1, idn2)) return false }
    true
  }

  // Parses sql identifiers from a String.
  // It's used in auto completion so it will parse incomplete strings, e.g "schema"."tab
  def getIdentifiers(code: String): Seq[SqlIdentifier] = {
    val idns = mutable.ArrayBuffer[SqlIdentifier]()
    var idn = ""
    var quoted = false
    var state = "startIdn"
    code.foreach { char =>
      state match {
        case "startIdn" =>
          if (char == '"') {
            quoted = true
            state = "inQuote"
          } else {
            state = "outQuote"
            quoted = false
            idn += char
          }
        case "outQuote" =>
          if (identifierChar(char)) {
            idn += char
          } else if (char == '.') {
            idns += SqlIdentifier(idn, quoted)
            idn = ""
            state = "startIdn"
            quoted = false
          } else {
            // Here its not an identifier anymore so we exit.
            // Should we throw here?
            idns += SqlIdentifier(idn, quoted)
            return idns
          }
        case "inQuote" =>
          if (char == '"') {
            state = "checkQuote"
          } else {
            idn += char
          }
        case "checkQuote" =>
          if (char == '"') {
            idn += '"'
            state = "inQuote"
          } else {
            idns += SqlIdentifier(idn, quoted)
            idn = ""
            state = "startIdn"
            quoted = false
            if (char != '.') {
              // The only thing valid after finishing a quote is a dot, so we return the current value
              // Should we throw here?
              return idns
            }
          }
      }
    }
    // We reached the end of the string append what is left
    idns += SqlIdentifier(idn, quoted)
    idns.toSeq
  }

  def tokens(code: String): Seq[(String, Int)] = {
    val tokens = mutable.ArrayBuffer[(String, Int)]()
    var currentWord = ""
    var currentPos = 0
    var state = "idle"
    var quoteType: Char = '"'
    var pos = 1
    code.foreach { char =>
      state match {
        case "idle" =>
          if (char == '"' || char == '\'') {
            state = "inQuote"
            quoteType = char
            currentPos = pos
            currentWord += char
          } else if (!char.isWhitespace) {
            state = "inWord"
            currentWord += char
            currentPos = pos
          }
        case "inWord" =>
          if (char == '"' || char == '\'') {
            state = "inQuote"
            quoteType = char
            currentWord += char
          } else if (!char.isWhitespace) {
            currentWord += char
          } else {
            tokens.append((currentWord, currentPos))
            currentWord = ""
            currentPos = 0
            state = "idle"
          }
        case "inQuote" =>
          if (char == quoteType) {
            state = "checkQuote"
          } else {
            currentWord += char
          }
        case "checkQuote" =>
          if (char == quoteType) {
            currentWord += char
            state = "inQuote"
          } else if (!char.isWhitespace) {
            currentWord += quoteType
            state = "inWord"
            currentWord += char
          } else {
            currentWord += quoteType
            tokens.append((currentWord, currentPos))
            currentWord = ""
            currentPos = 0
            state = "idle"
          }
      }
      pos += 1
    }

    if (currentWord != "") tokens.append((currentWord, currentPos))
    tokens.toSeq
  }
}
class SqlCodeUtils(code: String) {
  import SqlCodeUtils._

  // This is getting the (dotted) identifier under the cursor,
  // going left and right until it finds a non-identifier character
  def getIdentifierUnder(p: Pos): String = {
    val lines = code.split("\n")
    val line = lines(p.line - 1)
    // go backwards while the token is a letter or digit or...
    var i = p.column - 1
    while (i >= 0 && fullIdentifierChar(line.charAt(i))) {
      i -= 1
    }
    // go forwards while the token is a letter or digit (not dots)
    var j = p.column
    while (j < line.length && identifierChar(line.charAt(j))) {
      j += 1
    }
    line.substring(i + 1, j)
  }

  // This gets the fraction of the (dotted) identifier, up to the position. This permits to get the
  // beginning of the dotted identifier, and perform a completion on it (airports.c => city or country)
  def getIdentifierUpTo(p: Pos): String = {
    val lines = code.split("\n")
    val line = lines(p.line - 1)
    // go backwards while the token is a letter or digit or...
    var i = p.column - 1
    while (i >= 0 && fullIdentifierChar(line.charAt(i))) {
      i -= 1
    }
    line.substring(i + 1, p.column)
  }
}

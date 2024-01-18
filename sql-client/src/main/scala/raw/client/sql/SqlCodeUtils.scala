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
// filter to recognize an identifier, e.g. a table name, a field name

object SqlCodeUtils {
  def identifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_'

  // filter to recognize an identifier, possibly with dots, e.g. example.airports
  def fullIdentifierChar(c: Char): Boolean = c.isLetterOrDigit || c == '_' || c == '.' || c == '"'
  def separateIdentifiers(code: String): Seq[SqlIdentifier] = {
    val idns = mutable.ArrayBuffer[SqlIdentifier]()
    var idn = ""
    var state = "startIdn"
    var quoted = false
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
            state = "checkDot"
          }
        case "checkDot" =>
          if (char == '.') {
            idn = ""
            state = "startIdn"
            quoted = false
          } else {
            // Here its not an identifier anymore so we exit.
            // Should we throw here?
            return idns
          }
      }
    }
    // We reached the end of the string append what is left
    idns += SqlIdentifier(idn, quoted)
    idns.toSeq
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

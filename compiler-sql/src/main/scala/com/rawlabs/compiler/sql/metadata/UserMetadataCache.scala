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

package com.rawlabs.compiler.sql.metadata

import com.google.common.cache.{CacheBuilder, CacheLoader}
import com.rawlabs.compiler.sql.antlr4.{SqlIdentifierNode, SqlIdnNode, SqlProjNode}
import com.rawlabs.compiler.sql.{SqlConnectionPool, SqlIdentifier}
import com.typesafe.scalalogging.StrictLogging

import java.time.Duration

import java.sql.SQLException

case class IdentifierInfo(name: Seq[SqlIdentifier], tipe: String)

/* This class is used to cache metadata info about the user's database.
 * It is used to provide completion for the SQL editor. Each user has its own instance.
 * The cache contains two completion caches:
 * - word completion: when the user types a word, it is used to find tables, columns, that match the beginning of a word.
 * - dot completion: when the user types a dot, it is used to find columns or tables that match the prefix items.
 * Entries in these two caches are fairly short-lived. They get deleted (and recomputed if needed) after a few seconds
 * so that the user can see new schemas, tables or columns that have been created in the database.
 */
class UserMetadataCache(jdbcUrl: String, connectionPool: SqlConnectionPool, maxSize: Int, expiry: Duration)
    extends StrictLogging {

  private val wordCompletionCache = {
    val loader = new CacheLoader[Seq[SqlIdentifier], Seq[IdentifierInfo]]() {
      override def load(idns: Seq[SqlIdentifier]): Seq[IdentifierInfo] = {
        try {
          val con = connectionPool.getConnection(jdbcUrl)
          try {
            val query = idns.size match {
              case 3 => WordSearchWithThreeItems
              case 2 => WordSearchWithTwoItems
              case 1 => WordSearchWithOneItem
            }
            val tokens = idns.map(idn => if (idn.quoted) idn.value else idn.value.toLowerCase)
            query.run(con, tokens)
          } finally {
            con.close()
          }
        } catch {
          case ex: SQLException if isConnectionFailure(ex) =>
            logger.warn("SqlConnectionPool connection failure", ex)
            Seq.empty
        }
      }
    }
    CacheBuilder
      .newBuilder()
      .maximumSize(maxSize)
      .expireAfterWrite(expiry)
      .build(loader)
  }

  def getWordCompletionMatches(idnNode: SqlIdnNode): Seq[(Seq[SqlIdentifier], String /* type */ )] = {
    val seq = idnNode match {
      case n: SqlIdentifierNode => Seq(SqlIdentifier(n.name, n.isQuoted))
      case n: SqlProjNode =>
        if (n.identifiers.forall(_.isInstanceOf[SqlIdentifierNode])) {
          // keep the one that's under the cursor
          n.identifiers.collect { case i: SqlIdentifierNode => SqlIdentifier(i.name, i.isQuoted) }
        } else Seq.empty
    }

    // on word completion we get a tokenized sequence of identifiers like [raw, airp] and should find potential
    // matches. In order to use the completion cache more often, instead of sending these tokens in a WHERE clause,
    // we replace the last item (here 'airp') and keep only one letter before issuing the search. This is a trick
    // that permits to reused the cache soon after, since any following keystroke will likely only append a new letter.
    //
    // [raw, airpo] => [raw, a]
    // [raw,airports,cit] => [raw,airports,c]
    // [air] => [a]
    //
    // when the user keeps typing example.ai, air, airp => we reuse that cached entry.

    seq.lastOption
      .map {
        case SqlIdentifier(lastIdn, lastIdnQuoted) =>
          val simpler = seq.take(seq.size - 1) :+ SqlIdentifier(lastIdn.take(1), lastIdnQuoted)
          val coarseSearch = wordCompletionCache.get(simpler) // we get too many matches here

          def filterMatches(identifierInfo: IdentifierInfo) = {
            val lastToken = identifierInfo.name.last.value
            if (lastIdnQuoted) lastToken.startsWith(lastIdn)
            else lastToken.startsWith(lastIdn.toLowerCase)
          }

          val r = coarseSearch
            .collect {
              case i if filterMatches(i) => (i.name, i.tipe)
            }
          r
      }
      .getOrElse(Seq.empty)
  }

  private val dotCompletionCache = {
    val loader = new CacheLoader[Seq[SqlIdentifier], Seq[IdentifierInfo]]() {
      override def load(idns: Seq[SqlIdentifier]): Seq[IdentifierInfo] = {
        try {
          val con = connectionPool.getConnection(jdbcUrl)
          try {
            val query = idns.size match {
              case 2 => DotSearchWithTwoItems
              case 1 => DotSearchWithOneItem
            }
            val tokens = idns.map(idn => if (idn.quoted) idn.value else idn.value.toLowerCase)
            query.run(con, tokens)
          } finally {
            con.close()
          }
        } catch {
          case ex: SQLException if isConnectionFailure(ex) =>
            logger.warn("SqlConnectionPool connection failure", ex)
            Seq.empty
        }
      }
    }
    CacheBuilder
      .newBuilder()
      .maximumSize(maxSize)
      .expireAfterWrite(expiry)
      .build(loader)
  }

  def getDotCompletionMatches(idnNode: SqlIdnNode): Seq[(Seq[SqlIdentifier], String)] = {
    val seq = idnNode match {
      case n: SqlIdentifierNode => Seq(SqlIdentifier(n.name, n.isQuoted))
      case n: SqlProjNode =>
        if (n.identifiers.forall(_.isInstanceOf[SqlIdentifierNode])) {
          // keep the one that's under the cursor
          n.identifiers.collect { case i: SqlIdentifierNode => SqlIdentifier(i.name, i.isQuoted) }
        } else Seq.empty
    }
    dotCompletionCache.get(seq).map(i => (i.name, i.tipe))
  }

  private def isConnectionFailure(ex: SQLException) = {
    val state = ex.getSQLState
    state != null && state.startsWith("08") // connection exception, SqlConnectionPool is full
  }
}

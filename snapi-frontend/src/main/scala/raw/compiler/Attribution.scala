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

package raw.compiler

import com.typesafe.scalalogging.StrictLogging
import org.bitbucket.inkytonik.kiama.attribution.Attribute
import org.bitbucket.inkytonik.kiama.util.Memoiser.makeIdMemoiser

class Attribution extends org.bitbucket.inkytonik.kiama.attribution.Attribution with StrictLogging {

  /**
   * Attribute that supports cycles: instead of throwing exception, caches and returns cycle.
   */
  class SafeCachedAttribute[T <: AnyRef, U](cycle: U)(f: T => U) extends Attribute[T, U]() {

    /**
     * Backing memo table.
     */
    val memo = makeIdMemoiser[T, Option[U]]

    /**
     * Return the value of this attribute for node `t`, raising an error if
     * it depends on itself.
     */
    def apply(t: T): U = {
      t.synchronized {
        memo.get(t) match {
          case Some(Some(u)) => u
          case Some(None) =>
            val u = cycle
            memo.put(t, Some(u))
            u
          case None =>
            memo.put(t, None)
            val u = f(t)
            memo.put(t, Some(u))
            u
        }
      }
    }

    /**
     * Has the value of this attribute at `t` already been computed or not?
     * If the table contains `Some (u)` then we've computed it and the value
     * was `u`. If the memo table contains `None` we are in the middle of
     * computing it. Otherwise the memo table contains no entry for `t`.
     */
    def hasBeenComputedAt(t: T): Boolean = memo.get(t) match {
      case Some(Some(_)) => true
      case _ => false
    }

    /**
     * Reset the cache for this attribute.
     */
    def reset() {
      memo.reset()
    }

  }

  def safeAttr[T <: AnyRef, U](s: U)(f: T => U): SafeCachedAttribute[T, U] = new SafeCachedAttribute(s)(f)

}

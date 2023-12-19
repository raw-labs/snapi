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

package raw.compiler.base

import org.bitbucket.inkytonik.kiama.util.Entity
import raw.compiler.base.source.BaseIdnNode

import scala.collection.mutable

class NormalizeMap(prefix: String = "") {

  private var cntIdnNode = 0
  private val mapIdnNode = mutable.HashMap[Entity, String]()

  def newIdnForIdnNode(entity: Entity, n: BaseIdnNode): String = {

    mapIdnNode.getOrElseUpdate(
      entity, {
        cntIdnNode += 1
        s"${prefix}_$$$cntIdnNode"
      }
    )
  }

  private var cntString = 0
  private val mapString = mutable.HashMap[String, String]()

  def newIdnForString(n: String): String = {
    mapString.getOrElseUpdate(
      n, {
        cntString += 1
        s"${prefix}_$$$cntString"
      }
    )
  }

}

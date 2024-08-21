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

package com.rawlabs.compiler.utils

import scala.collection.mutable

object RecordFieldsNaming {

  // Turn a vector of keys into a vector of distinct keys (renaming duplicates to _1, _2)
  def makeDistinct(keys: java.util.Vector[String]): java.util.Vector[String] = {
    val distinct = new java.util.Vector[String]
    val keySet = mutable.Map.empty[String, Boolean]
    keys.forEach((k: String) => keySet.put(k, false))
    // add all keys in the order they appear in the keys vector
    keys.forEach((key: String) => {
      var newKey: String = key
      if (keySet.getOrElse(newKey, false)) {
        // the key was seen already, find a new key by enumerating other keys.
        var n: Int = 1
        do {
          {
            newKey = key + '_' + {
              n += 1;
              n - 1
            }
          }
        } while (keySet.contains(newKey))
        keySet.put(newKey, true)
      } else {
        // else, first time we see the key. Keep the original name
        // and keep track of the fact that we saw it by storing 'true'
        keySet.put(newKey, true)
      }
      distinct.add(newKey)
    })
    distinct
  }

}

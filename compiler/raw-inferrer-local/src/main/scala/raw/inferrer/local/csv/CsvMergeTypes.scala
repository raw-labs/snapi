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

package raw.inferrer.local.csv

import raw.inferrer.{SourceAnyType, SourceAttrType, SourceNullableType, SourceRecordType, SourceType}
import raw.inferrer.local.MergeTypes

trait CsvMergeTypes extends MergeTypes {

  override protected def l4TypesMaxOf(t1: SourceNullableType, t2: SourceNullableType): SourceType = {
    (t1, t2) match {
      case (SourceRecordType(as1, n1), SourceRecordType(as2, n2)) =>
        if (as1.length == as2.length) {
          val atts =
            (as1, as2).zipped.map { case (att1, att2) => SourceAttrType(att1.idn, maxOf(att1.tipe, att2.tipe)) }
          SourceRecordType(atts, n1 || n2)
        } else {
          SourceAnyType()
        }
      case _ => super.l4TypesMaxOf(t1, t2)
    }
  }
}

private[inferrer] object CsvMergeTypes extends CsvMergeTypes

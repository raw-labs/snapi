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

package raw.runtime.truffle.runtime.aggregation.aggregator;

import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.option.EmptyOption;

@ExportLibrary(AggregatorLibrary.class)
public class SumAggregator {

  public SumAggregator() {}

  @ExportMessage
  public boolean isAggregator() {
    return true;
  }

  @ExportMessage(limit = "3")
  public Object merge(Object current, Object next, @Cached("create()") OperatorNodes.AddNode add) {
    return add.execute(current, next);
  }

  @ExportMessage(limit = "3")
  public Object zero() {
    return new EmptyOption();
  }
}

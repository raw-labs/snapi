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
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.library.ExportLibrary;
import com.oracle.truffle.api.library.ExportMessage;
import raw.runtime.truffle.runtime.operators.OperatorNodes;
import raw.runtime.truffle.runtime.option.EmptyOption;
import raw.runtime.truffle.runtime.option.OptionLibrary;

@ExportLibrary(AggregatorLibrary.class)
public class MaxAggregator {

  public MaxAggregator() {}

  @ExportMessage
  public boolean isAggregator() {
    return true;
  }

  @ExportMessage(limit = "3")
  public Object merge(
      Object current,
      Object next,
      @Cached("create()") OperatorNodes.CompareNode compare,
      @CachedLibrary(limit = "3") OptionLibrary options) {
    if (options.isDefined(current)) {
      if (options.isDefined(next)) {
        // if both are defined, pick the largest
        if (compare.execute(current, next) > 0) {
          return current;
        } else {
          return next;
        }
      } else {
        // if only current is defined, return it
        return current;
      }
    } else {
      // left is not defined, return right (perhaps not defined either, but fine)
      return next;
    }
  }

  @ExportMessage(limit = "3")
  public Object zero() {
    return new EmptyOption();
  }
}

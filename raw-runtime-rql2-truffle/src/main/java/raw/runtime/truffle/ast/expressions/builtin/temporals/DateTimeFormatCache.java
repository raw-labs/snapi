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

package raw.runtime.truffle.ast.expressions.builtin.temporals;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class DateTimeFormatCache {

  private static final ThreadLocal<HashMap<String, DateTimeFormatter>> formattersCache =
      new ThreadLocal<>();

  public static DateTimeFormatter get(String template) throws IllegalArgumentException {
    HashMap<String, DateTimeFormatter> formatters = formattersCache.get();
    if (formatters == null) {
      formatters = new HashMap<>();
      formattersCache.set(formatters);
    }
    DateTimeFormatter entry = formatters.get(template);
    if (entry == null) {
      entry = DateTimeFormatter.ofPattern(template);
      formatters.put(template, entry);
    }
    return entry;
  }
}

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

package raw.compiler.snapi.truffle.builtin.jdbc;

import raw.compiler.rql2.source.Rql2AttrType;

class AttrWithIndex {
  public final int index;
  public final Rql2AttrType attr;

  public AttrWithIndex(int index, Rql2AttrType attr) {
    this.index = index;
    this.attr = attr;
  }
}

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

package raw.compiler.snapi.truffle.builtin.list_extension;

import raw.compiler.rql2.builtin.MaxListEntry;
import raw.compiler.snapi.truffle.TruffleEntryExtension;

public class TruffleMaxListEntry extends MaxListEntry implements TruffleEntryExtension {}

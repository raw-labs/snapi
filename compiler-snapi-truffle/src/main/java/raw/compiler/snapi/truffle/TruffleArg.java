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

package raw.compiler.snapi.truffle;

import com.rawlabs.compiler.snapi.base.source.Type;
import raw.runtime.truffle.ExpressionNode;

public record TruffleArg(ExpressionNode exprNode, Type type, String identifier) {}

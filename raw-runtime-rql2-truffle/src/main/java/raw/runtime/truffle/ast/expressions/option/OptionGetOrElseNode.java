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

package raw.runtime.truffle.ast.expressions.option;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.library.CachedLibrary;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.runtime.option.OptionLibrary;

@NodeInfo(shortName = "Option.FlatMap")
@NodeChild("option")
@NodeChild("orElse")
public abstract class OptionGetOrElseNode extends ExpressionNode {

    @Specialization(
            guards = {"options.isOption(option)"},
            limit = "1")
    protected Object optionGetOrElse(
            Object option, Object orElse, @CachedLibrary("option") OptionLibrary options) {
        if (options.isDefined(option)) {
            return options.get(option);
        } else {
            return orElse;
        }
    }
}

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

package raw.runtime.truffle.ast.expressions.builtin.string_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.apache.commons.lang3.StringUtils;
import raw.runtime.truffle.ExpressionNode;

@NodeInfo(shortName = "String.Capitalize")
@NodeChild(value = "string")
public abstract class StringCapitalizeNode extends ExpressionNode {

    @Specialization
    protected String doCapitalize(String str) {
        // Based on StringUtils.capitalize() from Apache Commons Lang
        final int strLen = str.length();
        if (strLen == 0) {
            return str;
        }

        final int firstCodepoint = str.codePointAt(0);
        final int newCodePoint = Character.toTitleCase(firstCodepoint);
        boolean changed = (firstCodepoint != newCodePoint);

        final int[] newCodePoints = new int[strLen]; // cannot be longer than the char array
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint; // copy the first codepoint
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
            final int codepoint = str.codePointAt(inOffset);
            final int newCodepoint = Character.toLowerCase(codepoint);
            newCodePoints[outOffset++] = newCodepoint;
            changed = changed || (codepoint != newCodepoint);
            inOffset += Character.charCount(codepoint);
        }
        if (changed) {
            return new String(newCodePoints, 0, outOffset);
        } else {
            return str;
        }

    }
}

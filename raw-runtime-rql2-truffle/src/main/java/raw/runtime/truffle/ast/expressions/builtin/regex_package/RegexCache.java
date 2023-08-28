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

package raw.runtime.truffle.ast.expressions.builtin.regex_package;

import java.util.HashMap;
import java.util.regex.Pattern;

/** */
public class RegexCache {
    private static final ThreadLocal<HashMap<String, Pattern>> regexCache = new ThreadLocal<>();

    private static HashMap<String, Pattern> getMap() {
        if (regexCache.get() == null) {
            regexCache.set(new HashMap<>());
        }
        return regexCache.get();
    }

    private RegexCache() {}

    public static Pattern get(String s) {
        HashMap<String, Pattern> map = getMap();
        if (map.containsKey(s)) return map.get(s);

        Pattern pattern = Pattern.compile(s);
        map.put(s, pattern);
        return pattern;
    }
}

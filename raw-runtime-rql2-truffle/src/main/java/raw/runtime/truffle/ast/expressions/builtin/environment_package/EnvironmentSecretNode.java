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

package raw.runtime.truffle.ast.expressions.builtin.environment_package;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import raw.creds.Secret;
import raw.runtime.RuntimeContext;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.tryable.ObjectTryable;
import java.util.NoSuchElementException;

@NodeInfo(shortName = "Environment.Secret")
@NodeChild(value = "key")
public abstract class EnvironmentSecretNode extends ExpressionNode {

    @Specialization
    protected Object doSecret(String key) {
        RuntimeContext context = RawContext.get(this).getRuntimeContext();
        try {
            Secret v = context.sourceContext().credentialsService().getSecret(context.sourceContext().user(), key).get();
            return ObjectTryable.BuildSuccess(v.value());
        } catch (NoSuchElementException e) {
            return ObjectTryable.BuildFailure("could not find secret " + key);
        }
    }
}

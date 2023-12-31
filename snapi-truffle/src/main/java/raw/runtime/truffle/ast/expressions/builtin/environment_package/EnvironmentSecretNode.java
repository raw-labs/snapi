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
import java.util.NoSuchElementException;
import raw.creds.api.Secret;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawContext;
import raw.runtime.truffle.runtime.primitives.ErrorObject;

@NodeInfo(shortName = "Environment.Secret")
@NodeChild(value = "key")
public abstract class EnvironmentSecretNode extends ExpressionNode {

  @Specialization
  protected Object doSecret(String key) {
    try {
      Secret v = RawContext.get(this).getSecret(key);
      return v.value();
    } catch (NoSuchElementException e) {
      return new ErrorObject("could not find secret " + key);
    }
  }
}

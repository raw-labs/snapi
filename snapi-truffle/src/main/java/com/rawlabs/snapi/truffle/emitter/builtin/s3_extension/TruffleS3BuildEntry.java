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

package com.rawlabs.snapi.truffle.emitter.builtin.s3_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.builtin.S3BuildEntry;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.emitter.builtin.WithArgs;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.snapi.truffle.runtime.ast.expressions.builtin.location_package.LocationFromS3Node;
import java.util.List;

public class TruffleS3BuildEntry extends S3BuildEntry implements TruffleEntryExtension, WithArgs {

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode url = args.get(0).exprNode();

    ExpressionNode accessKey = arg(args, "accessKey").orElse(null);
    ExpressionNode secretKey = arg(args, "secretKey").orElse(null);
    ExpressionNode region = arg(args, "region").orElse(null);

    return new LocationFromS3Node(url, accessKey, secretKey, region);
  }
}

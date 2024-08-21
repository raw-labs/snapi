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

package raw.compiler.snapi.truffle.builtin.s3_extension;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.S3BuildEntry;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.compiler.snapi.truffle.builtin.WithArgs;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.builtin.location_package.LocationFromS3Node;

public class TruffleS3BuildEntry extends S3BuildEntry implements TruffleEntryExtension, WithArgs {

  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode url = args.get(0).exprNode();

    ExpressionNode accessKey = arg(args, "accessKey").orElse(null);
    ExpressionNode secretKey = arg(args, "secretKey").orElse(null);
    ExpressionNode region = arg(args, "region").orElse(null);

    return new LocationFromS3Node(url, accessKey, secretKey, region);
  }
}

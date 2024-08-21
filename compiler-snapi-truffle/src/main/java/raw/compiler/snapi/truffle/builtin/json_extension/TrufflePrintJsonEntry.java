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

package raw.compiler.snapi.truffle.builtin.json_extension;

import java.util.List;
import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.PrintJsonEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import raw.compiler.rql2output.truffle.builtin.JsonWriter;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.io.json.reader.JsonPrintNodeGen;

public class TrufflePrintJsonEntry extends PrintJsonEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    return JsonPrintNodeGen.create(
        args.getFirst().exprNode(),
        JsonWriter.recurse((Rql2TypeWithProperties) args.getFirst().type(), rawLanguage)
            .getCallTarget());
  }
}

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

import static raw.compiler.snapi.truffle.builtin.CompilerScalaConsts.tryable;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.ParseJsonEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import java.util.List;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.io.json.reader.JsonParseNode;
import raw.runtime.truffle.ast.io.json.reader.JsonParseNodeGen;
import raw.runtime.truffle.ast.io.json.reader.TryableTopLevelWrapper;

public class TruffleParseJsonEntry extends ParseJsonEntry
    implements TruffleEntryExtension, WithJsonArgs {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode timeFormat = timeFormat(args);
    ExpressionNode dateFormat = dateFormat(args);
    ExpressionNode timestampFormat = timestampFormat(args);
    ExpressionNode[] unnamedArgs = mandatoryArgs(args);

    JsonParser parser = new JsonParser(dateFormat, timeFormat, timestampFormat);

    JsonParseNode parseNode =
        JsonParseNodeGen.create(
            unnamedArgs[0],
            parser.recurse((Rql2TypeWithProperties) type, rawLanguage).getCallTarget());
    if (((Rql2TypeWithProperties) type).props().contains(tryable))
      return new TryableTopLevelWrapper(parseNode);
    else return parseNode;
  }
}

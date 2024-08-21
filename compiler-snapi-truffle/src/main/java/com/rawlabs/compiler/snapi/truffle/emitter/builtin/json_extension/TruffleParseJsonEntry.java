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

package com.rawlabs.compiler.snapi.truffle.emitter.builtin.json_extension;

import static com.rawlabs.compiler.snapi.truffle.emitter.builtin.CompilerScalaConsts.tryable;

import com.rawlabs.compiler.snapi.base.source.Type;
import com.rawlabs.compiler.snapi.rql2.builtin.ParseJsonEntry;
import com.rawlabs.compiler.snapi.rql2.source.Rql2TypeWithProperties;
import java.util.List;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.compiler.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.compiler.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.compiler.snapi.truffle.runtime.RawLanguage;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader.JsonParseNode;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader.JsonParseNodeGen;
import com.rawlabs.compiler.snapi.truffle.runtime.ast.io.json.reader.TryableTopLevelWrapper;

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

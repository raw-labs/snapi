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

package com.rawlabs.snapi.truffle.emitter.builtin.json_extension;

import java.util.List;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.snapi.extensions.builtin.ReadJsonEntry;
import com.rawlabs.snapi.frontend.snapi.source.SnapiIterableType;
import com.rawlabs.snapi.frontend.snapi.source.SnapiTypeWithProperties;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.ast.ExpressionNode;
import com.rawlabs.snapi.truffle.SnapiLanguage;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonReadCollectionNode;
import com.rawlabs.snapi.truffle.ast.io.json.reader.JsonReadValueNode;
import com.rawlabs.snapi.truffle.ast.io.json.reader.TryableTopLevelWrapper;

import static com.rawlabs.snapi.truffle.emitter.builtin.CompilerScalaConsts.*;

public class TruffleReadJsonEntry extends ReadJsonEntry
    implements TruffleEntryExtension, WithJsonArgs {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, SnapiLanguage rawLanguage) {
    ExpressionNode encoding = encoding(args);
    ExpressionNode timeFormat = timeFormat(args);
    ExpressionNode dateFormat = dateFormat(args);
    ExpressionNode timestampFormat = timestampFormat(args);
    ExpressionNode[] unnamedArgs = mandatoryArgs(args);
    JsonParser parser = new JsonParser(dateFormat, timeFormat, timestampFormat);
    if (type instanceof SnapiIterableType iterableType) {
      return new JsonReadCollectionNode(
          unnamedArgs[0],
          encoding,
          parser.recurse((SnapiTypeWithProperties) iterableType.innerType(), rawLanguage));
    } else {
      JsonReadValueNode parseNode =
          new JsonReadValueNode(
              unnamedArgs[0],
              encoding,
              parser.recurse((SnapiTypeWithProperties) type, rawLanguage).getCallTarget());
      if (((SnapiTypeWithProperties) type).props().contains(tryable))
        return new TryableTopLevelWrapper(parseNode);
      else return parseNode;
    }
  }
}

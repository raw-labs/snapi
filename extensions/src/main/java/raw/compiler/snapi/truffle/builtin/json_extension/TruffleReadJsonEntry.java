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

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.ReadJsonEntry;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.io.json.reader.JsonReadCollectionNode;
import raw.runtime.truffle.ast.io.json.reader.JsonReadValueNode;
import raw.runtime.truffle.ast.io.json.reader.TryableTopLevelWrapper;

import static raw.compiler.snapi.truffle.builtin.CompilerScalaConsts.*;

public class TruffleReadJsonEntry extends ReadJsonEntry implements TruffleEntryExtension, WithJsonArgs {

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    ExpressionNode encoding = encoding(args);
    ExpressionNode timeFormat = timeFormat(args);
    ExpressionNode dateFormat = dateFormat(args);
    ExpressionNode timestampFormat = timestampFormat(args);
    ExpressionNode[] unnamedArgs = unnamedArgs(args);
    JsonParser parser = new JsonParser(dateFormat, timeFormat, timestampFormat);
    if (type instanceof Rql2IterableType iterableType) {
      return new JsonReadCollectionNode(
          unnamedArgs[0],
          encoding,
          parser.recurse((Rql2TypeWithProperties) iterableType.innerType(), rawLanguage));
    } else {
      JsonReadValueNode parseNode =
          new JsonReadValueNode(
              unnamedArgs[0], encoding, parser.recurse((Rql2TypeWithProperties) type, rawLanguage));
      if (((Rql2TypeWithProperties) type).props().contains(tryable))
        return new TryableTopLevelWrapper(parseNode);
      else return parseNode;
    }
  }
}

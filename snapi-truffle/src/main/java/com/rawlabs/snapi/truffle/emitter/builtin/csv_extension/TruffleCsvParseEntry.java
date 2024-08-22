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

package com.rawlabs.snapi.truffle.emitter.builtin.csv_extension;

import com.rawlabs.snapi.frontend.base.source.Type;
import com.rawlabs.snapi.frontend.rql2.builtin.CsvParseEntry;
import com.rawlabs.snapi.frontend.rql2.source.Rql2TypeWithProperties;
import com.rawlabs.snapi.truffle.emitter.TruffleArg;
import com.rawlabs.snapi.truffle.emitter.TruffleEntryExtension;
import com.rawlabs.snapi.truffle.runtime.ExpressionNode;
import com.rawlabs.snapi.truffle.runtime.RawLanguage;
import java.util.List;

public class TruffleCsvParseEntry extends CsvParseEntry implements TruffleEntryExtension {
  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    CsvParser makeParser = new CsvParser(args);
    ExpressionNode url =
        args.stream().filter(a -> a.identifier() == null).findFirst().orElseThrow().exprNode();
    return makeParser.stringParser(url, (Rql2TypeWithProperties) type, rawLanguage);
  }
}

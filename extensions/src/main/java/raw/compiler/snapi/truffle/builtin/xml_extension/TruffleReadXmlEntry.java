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

package raw.compiler.snapi.truffle.builtin.xml_extension;

import raw.compiler.base.source.Type;
import raw.compiler.rql2.builtin.ReadXmlEntry;
import raw.compiler.rql2.source.Rql2IterableType;
import raw.compiler.rql2.source.Rql2ListType;
import raw.compiler.rql2.source.Rql2Type;
import raw.compiler.rql2.source.Rql2TypeWithProperties;
import raw.compiler.snapi.truffle.TruffleArg;
import raw.compiler.snapi.truffle.TruffleEntryExtension;
import raw.runtime.truffle.ExpressionNode;
import raw.runtime.truffle.RawLanguage;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFromNodeGen;
import raw.runtime.truffle.ast.expressions.iterable.list.ListFromUnsafeNodeGen;
import raw.runtime.truffle.ast.expressions.literals.StringNode;
import raw.runtime.truffle.ast.io.json.reader.TryableTopLevelWrapper;
import raw.runtime.truffle.ast.io.xml.parser.XmlReadCollectionNode;
import raw.runtime.truffle.ast.io.xml.parser.XmlReadValueNode;

import java.util.List;

public class TruffleReadXmlEntry extends ReadXmlEntry implements TruffleEntryExtension {

  private static ExpressionNode getArg(List<TruffleArg> namedArgs, String identifier, ExpressionNode defExp) {
    return namedArgs.stream().filter(arg -> arg.getIdentifier() != null && arg.getIdentifier().equals(identifier)).map(TruffleArg::getExprNode).findFirst().orElse(defExp);
  }

  private static final ExpressionNode defaultEncoding = new StringNode("utf-8");
  private static final ExpressionNode defaultTimestampFormat = new StringNode("yyyy-M-d['T'][ ]HH:mm[:ss[.SSS]]");
  private static final ExpressionNode defaultDateFormat = new StringNode("yyyy-M-d");
  private static final ExpressionNode defaultTimeFormat = new StringNode("HH:mm[:ss[.SSS]]");

  @Override
  public ExpressionNode toTruffle(Type type, List<TruffleArg> args, RawLanguage rawLanguage) {
    List<TruffleArg> unnamedArgs = args.stream().filter(arg -> arg.getIdentifier() == null).toList();
    List<TruffleArg> namedArgs = args.stream().filter(arg -> arg.getIdentifier() != null).toList();
    ExpressionNode encoding = getArg(namedArgs, "encoding", defaultEncoding);
    ExpressionNode timeFormatExp = getArg(namedArgs, "timeFormat", defaultTimeFormat);
    ExpressionNode dateFormatExp = getArg(namedArgs, "dateFormat", defaultDateFormat);
    ExpressionNode timestampFormatExp = getArg(namedArgs, "timestampFormat", defaultTimestampFormat);

    return switch (type) {
      case Rql2IterableType iterableType -> {
        ExpressionNode parseNode = new XmlReadCollectionNode(
            unnamedArgs.get(0).getExprNode(),
            encoding,
            dateFormatExp,
            timeFormatExp,
            timestampFormatExp,
            XmlRecurse
                .recurseXmlParser((Rql2TypeWithProperties) iterableType.innerType(), rawLanguage));
        if (XmlRecurse.isTryable(iterableType)) {
          // Probably will need to be either reused in json and xml or create a copy
          yield new TryableTopLevelWrapper(parseNode);
        } else {
          yield parseNode;
        }
      }
      case Rql2ListType listType -> {
        ExpressionNode parseNode = new XmlReadCollectionNode(
            unnamedArgs.get(0).getExprNode(),
            encoding,
            dateFormatExp,
            timeFormatExp,
            timestampFormatExp,
            XmlRecurse
                .recurseXmlParser((Rql2TypeWithProperties) listType.innerType(), rawLanguage));
        if (XmlRecurse.isTryable(listType)) {
          // Probably will need to be either reused in json and xml or create a copy
          yield ListFromNodeGen.create(parseNode, (Rql2Type) listType.innerType());
        } else {
          yield ListFromUnsafeNodeGen.create(parseNode, (Rql2Type) listType.innerType());
        }
      }
      case Rql2TypeWithProperties t -> {
        ExpressionNode parseNode = new XmlReadValueNode(
            unnamedArgs.get(0).getExprNode(),
            encoding,
            dateFormatExp,
            timeFormatExp,
            timestampFormatExp,
            XmlRecurse
                .recurseXmlParser(t, rawLanguage));
        if (XmlRecurse.isTryable(t)) {
          yield new TryableTopLevelWrapper(parseNode);
        } else {
          yield parseNode;
        }
      }
      default -> throw new IllegalStateException("Unexpected value: " + type);
    };

    }
  }
